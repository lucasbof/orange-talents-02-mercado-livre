package br.com.zup.mercadolivre.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import br.com.zup.mercadolivre.controllers.forms.ProductForm;
import br.com.zup.mercadolivre.controllers.forms.ProductOpinionForm;
import br.com.zup.mercadolivre.entities.Category;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.ProductOpinion;
import br.com.zup.mercadolivre.entities.User;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithUserDetails(value = "lucas@gmail.com")
class ProductOpinionControllerTest {

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
		manager.createQuery("DELETE FROM ProductOpinion").executeUpdate();
		productFormBuilder = new ProductFormBuilder();
		categoryFormBuilder = new CategoryFormBuilder();
	}
	
	@Test
	@DisplayName("insert should insert a new product opinion when the data is valid")
	void test1() throws Exception {
		Long productId = insertValidProduct();
		ProductOpinionForm opinionForm = new ProductOpinionForm(4.5, "Ótimo produto", "Um ótimo produto", productId);
		
		ResultActions result =
				mockMvc.perform(post("/product/opinions")
					.content(toJson(opinionForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		List<ProductOpinion> opinions = manager.createQuery("SELECT po FROM ProductOpinion po", ProductOpinion.class).getResultList();
		ProductOpinion opinion = opinions.get(0);
		assertEquals(1, opinions.size());
		assertEquals(opinionForm.getGrade(), opinion.getGrade());
		assertEquals(opinionForm.getTitle(), opinion.getTitle());
		assertEquals(opinionForm.getDescription(), opinion.getDescription());
		assertEquals(opinionForm.getProductId(), opinion.getProduct().getId());
	}
	
	@Test
	@DisplayName("insert should return 400 when the data is invalid")
	void test2() throws Exception {
		Long productId = insertValidProduct();
		ProductOpinionForm opinionForm = new ProductOpinionForm(7.8, null, null, productId);
		
		ResultActions result =
				mockMvc.perform(post("/product/opinions")
					.content(toJson(opinionForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
		result.andExpect(jsonPath("$.fieldErrors").exists());
		result.andExpect(jsonPath("$.globalErrors").exists());
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
