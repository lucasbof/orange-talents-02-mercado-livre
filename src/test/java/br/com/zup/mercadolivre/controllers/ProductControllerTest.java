package br.com.zup.mercadolivre.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.mercadolivre.builders.CategoryFormBuilder;
import br.com.zup.mercadolivre.builders.ProductFormBuilder;
import br.com.zup.mercadolivre.controllers.forms.CategoryForm;
import br.com.zup.mercadolivre.controllers.forms.CharacteristicForm;
import br.com.zup.mercadolivre.controllers.forms.ProductForm;
import br.com.zup.mercadolivre.entities.Category;
import br.com.zup.mercadolivre.entities.Characteristic;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.User;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithUserDetails(value = "lucas@gmail.com")
class ProductControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ProductFormBuilder productFormBuilder;

	private CategoryFormBuilder categoryFormBuilder;

	@BeforeEach
	void setUp() throws Exception {
		productFormBuilder = new ProductFormBuilder();
		categoryFormBuilder = new CategoryFormBuilder();
	}

	@Test
	@DisplayName("insert should insert product and their characteristcs when the data is valid")
	void test1() throws Exception {
		CategoryForm categoryForm = categoryFormBuilder
				.setName("Eletrônicos")
				.build();
		Category cat = categoryForm.toModel(manager);
		manager.persist(cat);
		
		ProductForm productForm = productFormBuilder.createValidProductForm(cat.getId());
		
		ResultActions result =
				mockMvc.perform(post("/products")
					.content(toJson(productForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		TypedQuery<Product> query = manager.createQuery("SELECT p FROM Product p WHERE p.name = :name", Product.class);
		query.setParameter("name", productForm.getName());
		Product product = query.getSingleResult();
		
		assertNotNull(product);
		assertEquals(productForm.getName(), product.getName());
		assertEquals(productForm.getPrice(), product.getPrice());
		assertEquals(productForm.getQuantity(), product.getQuantity());
		assertEquals(productForm.getDescription(), product.getDescription());
		
		assertNotNull(product.getCategory());
		assertEquals(productForm.getCategoryId(), product.getCategory().getId());
		
		assertNotNull(product.getCharacteristics());
		assertEquals(productForm.getCharacteristics().size(), product.getCharacteristics().size());
		for(Characteristic carac : product.getCharacteristics()) {
			assertNotNull(carac.getId());
			List<CharacteristicForm> filteredCaracters = productForm.getCharacteristics().stream().filter(cf -> cf.getName().equals(carac.getName())).collect(Collectors.toList());
			assertTrue(filteredCaracters.size() == 1);
			assertEquals(filteredCaracters.get(0).getDescription(), carac.getDescription());
		}
	}
	
	@Test
	@DisplayName("insert should return 400 with error message when the data is invalid")
	void test2() throws Exception {
		// Empty productForm
		ProductForm productForm = productFormBuilder.build();
		
		ResultActions result =
				mockMvc.perform(post("/products")
					.content(toJson(productForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
		
		result.andExpect(jsonPath("$.fieldErrors").exists());
		result.andExpect(jsonPath("$.fieldErrors").isArray());
		
		result.andExpect(jsonPath("$.globalErrors").exists());
		result.andExpect(jsonPath("$.globalErrors").isArray());
		
	}
	
	@Test
	@DisplayName("getById should return product details when the productId exists")
	void test3() throws Exception {
		Long productId = insertValidProduct();
		
		ResultActions result =
				mockMvc.perform(get("/products/{id}", productId)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.grade.average").exists());
		result.andExpect(jsonPath("$.grade.totalNumber").exists());
		result.andExpect(jsonPath("$.grade.totalNumber").exists());
		
		result.andExpect(jsonPath("$.characteristics").exists());
		result.andExpect(jsonPath("$.characteristics").isArray());
		
		result.andExpect(jsonPath("$.imgUrls").exists());
		result.andExpect(jsonPath("$.imgUrls").isArray());
		
		result.andExpect(jsonPath("$.questions").exists());
		result.andExpect(jsonPath("$.questions").isArray());
		
		result.andExpect(jsonPath("$.opinions").exists());
		result.andExpect(jsonPath("$.opinions").isArray());
	}
	
	@Test
	@DisplayName("getById should return 404 when the productId does not exist")
	void test4() throws Exception {
		Long productId = 100000L;
		
		ResultActions result =
				mockMvc.perform(get("/products/{id}", productId)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
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

	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}
}
