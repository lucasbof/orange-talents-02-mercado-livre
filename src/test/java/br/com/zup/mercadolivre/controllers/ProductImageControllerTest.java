package br.com.zup.mercadolivre.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import br.com.zup.mercadolivre.builders.CategoryFormBuilder;
import br.com.zup.mercadolivre.builders.ProductFormBuilder;
import br.com.zup.mercadolivre.controllers.forms.CategoryForm;
import br.com.zup.mercadolivre.controllers.forms.ProductForm;
import br.com.zup.mercadolivre.controllers.forms.ProductImagesForm;
import br.com.zup.mercadolivre.entities.Category;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.ProductImage;
import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.services.UploaderFakeService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductImageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@PersistenceContext
	private EntityManager manager;
	
	@MockBean
	private UploaderFakeService uploaderFakeService;

	private ProductFormBuilder productFormBuilder;

	private CategoryFormBuilder categoryFormBuilder;
	
	private String fileName1;
	private String fileName2;
	
	private String urlSavedImage1;
	private String urlSavedImage2;

	@BeforeEach
	void setUp() throws Exception {
		fileName1 = "test-image.png";
		fileName2 = "test-image2.png";
		
		urlSavedImage1 = "bucket.io://" + fileName1;
		urlSavedImage2 = "bucket.io://" + fileName2;
		
		when(uploaderFakeService.uploadImages(ArgumentMatchers.any(ProductImagesForm.class)))
		.thenReturn(new HashSet<String>(Arrays.asList(urlSavedImage1, urlSavedImage2)));
		
		productFormBuilder = new ProductFormBuilder();
		categoryFormBuilder = new CategoryFormBuilder();
	}

	@Test
	@DisplayName("insert should return 200 when the data is valid")
	@WithUserDetails(value = "lucas@gmail.com")
	public void test1() throws Exception {
		Long productId = insertValidProduct();

		String pathToImage = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "static" + File.separator + "images" + File.separator;

		MockMultipartFile firstFile = new MockMultipartFile("images", fileName1, "image/png",
				extractBytes(pathToImage + fileName1));
		MockMultipartFile secondFile = new MockMultipartFile("images", fileName2, "image/png",
				extractBytes(pathToImage + fileName2));

		ResultActions result = mockMvc
			.perform(MockMvcRequestBuilders.multipart("/product/images/{id}", productId)
			.file(firstFile)
			.file(secondFile));
		result.andExpect(status().isOk());
		
		// verify if this mocked method was called one time
		verify(uploaderFakeService, times(1)).uploadImages(ArgumentMatchers.any(ProductImagesForm.class));
		
		// verify if the images urls was saved 
		Product product = manager.find(Product.class, productId);
		assertNotNull(product);
		Set<ProductImage> imagesObj = product.getImages();
		Set<String> images = imagesObj.stream().map(i -> i.getImgUrl()).collect(Collectors.toSet());
		
		assertEquals(2, images.size());
		assertTrue(images.contains(urlSavedImage1));
		assertTrue(images.contains(urlSavedImage2));
		

	}
	
	@Test
	@DisplayName("insert should return 403 when the logged user is not the product owner")
	@WithUserDetails(value = "maria@gmail.com")
	public void test2() throws Exception {
		Long productId = insertValidProduct();

		String pathToImage = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "static" + File.separator + "images" + File.separator;

		MockMultipartFile firstFile = new MockMultipartFile("images", fileName1, "image/png",
				extractBytes(pathToImage + fileName1));
		MockMultipartFile secondFile = new MockMultipartFile("images", fileName2, "image/png",
				extractBytes(pathToImage + fileName2));

		mockMvc
			.perform(MockMvcRequestBuilders.multipart("/product/images/{id}", productId)
			.file(firstFile)
			.file(secondFile))
			.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("insert should return 400 when the data is invalid")
	@WithUserDetails(value = "maria@gmail.com")
	public void test3() throws Exception {
		String pathToImage = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "static" + File.separator + "images" + File.separator;

		MockMultipartFile firstFile = new MockMultipartFile("images", fileName1, "image/png",
				extractBytes(pathToImage + fileName1));
		MockMultipartFile secondFile = new MockMultipartFile("images", fileName2, "image/png",
				extractBytes(pathToImage + fileName2));

		mockMvc
			.perform(MockMvcRequestBuilders.multipart("/product/images/{id}", 10000)
			.file(firstFile)
			.file(secondFile))
			.andExpect(status().isBadRequest());
	}

	private Long insertValidProduct() {
		User owner = manager.createQuery("SELECT u FROM User u WHERE u.email = 'lucas@gmail.com'", User.class)
				.getSingleResult();
		CategoryForm categoryForm = categoryFormBuilder.setName("Eletrônicos").build();
		Category cat = categoryForm.toModel(manager);
		manager.persist(cat);

		ProductForm productForm = productFormBuilder.createValidProductForm(cat.getId());

		Product product = new Product(productForm.getName(), productForm.getPrice(), productForm.getQuantity(),
				productForm.getDescription(), owner, cat, productForm.getCharacteristics());
		
		manager.persist(product);
		
		return product.getId();
	}

	private byte[] extractBytes(String ImageName) throws Exception {
		File imgPath = new File(ImageName);
		BufferedImage bufferedImage = ImageIO.read(imgPath);
		WritableRaster raster = bufferedImage.getRaster();
		DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
		return data.getData();
	}

}
