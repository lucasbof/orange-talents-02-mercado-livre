package br.com.zup.mercadolivre.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import br.com.zup.mercadolivre.builders.CategoryFormBuilder;
import br.com.zup.mercadolivre.builders.ProductFormBuilder;
import br.com.zup.mercadolivre.controllers.forms.CategoryForm;
import br.com.zup.mercadolivre.controllers.forms.ProductForm;
import br.com.zup.mercadolivre.entities.Category;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.User;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithUserDetails(value = "lucas@gmail.com")
class ProductImageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@PersistenceContext
	private EntityManager manager;

	private ProductFormBuilder productFormBuilder;

	private CategoryFormBuilder categoryFormBuilder;

	@BeforeEach
	void setUp() throws Exception {
		productFormBuilder = new ProductFormBuilder();
		categoryFormBuilder = new CategoryFormBuilder();
	}

	@Test
	public void test() throws Exception {
		Long productId = insertValidProduct();

		String pathToImage = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "static" + File.separator + "images" + File.separator;
		String fileName1 = "test-image.png";
		String fileName2 = "test-image2.png";

		MockMultipartFile firstFile = new MockMultipartFile("images", "test-image.png", "image/png",
				extractBytes(pathToImage + fileName1));
		MockMultipartFile secondFile = new MockMultipartFile("images", "test-image2.png", "image/png",
				extractBytes(pathToImage + fileName2));

		mockMvc
			.perform(MockMvcRequestBuilders.multipart("/product/images/{id}", productId)
			.file(firstFile)
			.file(secondFile))
			.andExpect(status().isOk());
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
