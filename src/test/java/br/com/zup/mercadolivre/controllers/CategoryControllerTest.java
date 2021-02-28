package br.com.zup.mercadolivre.controllers;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import br.com.zup.mercadolivre.controllers.forms.CategoryForm;
import br.com.zup.mercadolivre.entities.Category;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithUserDetails(value = "lucas@gmail.com")
class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@PersistenceContext
	private EntityManager manager;
		
	private CategoryFormBuilder categoryFormBuilder = new CategoryFormBuilder();
	
	@Test
	@DisplayName("insert method should insert category without parent and return 200 when the data form is valid")
	void insertShouldInsertCategoryWithoutParentWhenTheDataIsValid() throws Exception {
		CategoryForm categoryForm = categoryFormBuilder
				.setName("Eletrônicos")
				.build();
		
		ResultActions result =
				mockMvc.perform(post("/categories")
					.content(toJson(categoryForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		TypedQuery<Category> query = manager.createQuery("SELECT c FROM Category c WHERE c.name = :name", Category.class);
		query.setParameter("name", categoryForm.getName());
		Category category = query.getSingleResult();
		
		assertNotNull(category);
		assertEquals(categoryForm.getName(), category.getName());
		assertNull(category.getParentCategory());
	}
	
	@Test
	@DisplayName("insert method should insert category with parent and return 200 when the data form is valid")
	void insertShouldInsertCategoryWithParentWhenTheDataIsValid() throws Exception {
		Category cat = new Category("Eletrônicos");
		manager.persist(cat);
		
		CategoryForm categoryForm = categoryFormBuilder
				.setName("Notebooks")
				.setParentCategoryId(cat.getId())
				.build();
		
		ResultActions result =
				mockMvc.perform(post("/categories")
					.content(toJson(categoryForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		TypedQuery<Category> query = manager.createQuery("SELECT c FROM Category c WHERE c.name = :name", Category.class);
		query.setParameter("name", categoryForm.getName());
		Category category = query.getSingleResult();
				
		assertNotNull(category);
		assertEquals(categoryForm.getName(), category.getName());
		assertEquals(categoryForm.getParentCategoryId(), category.getParentCategory().getId());
	}
	
	@ParameterizedTest
	@CsvSource(value = {"''", "'     '", ","})
	@DisplayName("insert method return 400 when the name is blank")
	void insertShouldReturn400WithErrorMessageWhenTheNameIsBlank(String categoryName) throws Exception {
		CategoryForm categoryForm = categoryFormBuilder
				.setName(categoryName)
				.build();
		
		ResultActions result =
				mockMvc.perform(post("/categories")
					.content(toJson(categoryForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
				
		result.andExpect(jsonPath("$.fieldErrors").isArray());
		result.andExpect(jsonPath("$.fieldErrors[*].fieldName", hasItem("name")));
	}
	
	@Test
	@DisplayName("insert method should return 400 with error message when name is not unique")
	void insertShouldReturn400WhenEmailIsNotUnique() throws Exception {
		String categoryName = "Eletrônicos";
		Category cat = new Category(categoryName);
		manager.persist(cat);
		
		CategoryForm categoryForm = categoryFormBuilder
				.setName(categoryName)
				.build();
		
		ResultActions result =
				mockMvc.perform(post("/categories")
					.content(toJson(categoryForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
		
		result.andExpect(jsonPath("$.fieldErrors").isArray());
		result.andExpect(jsonPath("$.fieldErrors[*].fieldName", hasItem("name")));
	}
	
	@ParameterizedTest
	@CsvSource(value = {"-1", "-50", "100000", "100700"})
	@DisplayName("insert method should return 400 with error message when parentCategoryId isn't exist or is negative")
	void insertShouldReturn400WhenParentCategoryIdIsNotExistOrIsNegative(Long parentCategoryId) throws Exception {	
		CategoryForm categoryForm = categoryFormBuilder
				.setName("Eletrônicos")
				.setParentCategoryId(parentCategoryId)
				.build();
		
		ResultActions result =
				mockMvc.perform(post("/categories")
					.content(toJson(categoryForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
		
		result.andExpect(jsonPath("$.fieldErrors").isArray());
		result.andExpect(jsonPath("$.fieldErrors[*].fieldName", hasItem("parentCategoryId")));
	}
	
	
	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}
}
