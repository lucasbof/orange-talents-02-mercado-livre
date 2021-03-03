package br.com.zup.mercadolivre.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import br.com.zup.mercadolivre.controllers.forms.ProductQuestionForm;
import br.com.zup.mercadolivre.entities.Category;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.ProductQuestion;
import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.services.EmailService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithUserDetails(value = "lucas@gmail.com")
class ProductQuestionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private EmailService emailService;

	private ProductFormBuilder productFormBuilder;

	private CategoryFormBuilder categoryFormBuilder;
	
	@BeforeEach
	void setUp() throws Exception {
		Mockito.doNothing().when(emailService).newProductQuestion(ArgumentMatchers.any(ProductQuestion.class));
		manager.createQuery("DELETE FROM ProductQuestion").executeUpdate();
		productFormBuilder = new ProductFormBuilder();
		categoryFormBuilder = new CategoryFormBuilder();
	}

	@Test
	@DisplayName("insert should insert a question when the data is valid")
	void test1() throws Exception {
		Long productId = insertValidProduct();
		ProductQuestionForm questionForm = new ProductQuestionForm("Ótimo produto", productId);
		
		ResultActions result =
				mockMvc.perform(post("/product/questions")
					.content(toJson(questionForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		List<ProductQuestion> questions = manager.createQuery("SELECT pq FROM ProductQuestion pq", ProductQuestion.class).getResultList();
		ProductQuestion question = questions.get(0);
		assertEquals(1, questions.size());
		assertEquals(questionForm.getTitle(), question.getTitle());
		assertEquals(questionForm.getProductId(), question.getProduct().getId());
		
		// verify if the send email method was called one time
		verify(emailService, times(1)).newProductQuestion(question);
	}
	
	@Test
	@DisplayName("insert should return 400 when the data is invalid")
	void test2() throws Exception {
		Long productId = insertValidProduct();
		ProductQuestionForm questionForm = new ProductQuestionForm(null, productId);
		
		ResultActions result =
				mockMvc.perform(post("/product/questions")
					.content(toJson(questionForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
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
