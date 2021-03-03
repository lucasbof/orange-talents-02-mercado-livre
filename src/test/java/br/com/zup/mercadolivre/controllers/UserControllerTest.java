package br.com.zup.mercadolivre.controllers;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

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

import br.com.zup.mercadolivre.builders.UserFormBuilder;
import br.com.zup.mercadolivre.controllers.forms.UserForm;
import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithUserDetails(value = "lucas@gmail.com")
class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	private UserFormBuilder userFormBuilder = new UserFormBuilder();

	@BeforeEach
	void setUp() throws Exception {
		userRepository.deleteAll();
	}
	
	@Test
	@DisplayName("insert method should insert user and return 200 when the data form is valid")
	void insertShouldInsertUserWhenTheDataIsValid() throws Exception {
		String email = "bob@gmail.com";
		UserForm userForm = userFormBuilder
				.setEmail(email)
				.setPassword("123456")
				.build();
		
		ResultActions result =
				mockMvc.perform(post("/users")
					.content(toJson(userForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		Optional<User> user = userRepository.findByEmail(email);
		
		assertTrue(user.isPresent());
		assertEquals(email, user.get().getEmail());
	}
	
	@Test
	@DisplayName("insert method shouldn't insert user and should return 400 with error message when the email is not unique")
	void insertShouldReturn400WhenEmailIsNotUnique() throws Exception {
		UserForm userForm = userFormBuilder
				.setEmail("bob@gmail.com")
				.setPassword("123456")
				.build();

		userRepository.save(userForm.toModel());
		
		ResultActions result =
				mockMvc.perform(post("/users")
					.content(toJson(userForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
		
		result.andExpect(jsonPath("$.fieldErrors").isArray());
		result.andExpect(jsonPath("$.fieldErrors[*].fieldName", hasItem("email")));
	}

	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

}
