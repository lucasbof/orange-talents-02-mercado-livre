package br.com.zup.mercadolivre.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;

	private UserForm newUserFormValid;

	@BeforeEach
	void setUp() throws Exception {
		UserFormBuilder userFormBuilder = new UserFormBuilder();
		this.newUserFormValid = userFormBuilder
							.setEmail("lucas@gmail.com")
							.setPassword("123456")
							.build();
	}
	
	@Test
	void insertShouldInsertUserWhenTheDataIsValid() throws Exception {
		ResultActions result =
				mockMvc.perform(post("/users")
					.content(toJson(newUserFormValid))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		List<User> list = userRepository.findAll();
		Assertions.assertEquals(1, list.size());
		
		Assertions.assertEquals(newUserFormValid.getEmail(), list.get(0).getEmail());
	}
	
	@Test
	void insertShouldReturn400WhenEmailIsNotUnique() throws Exception {
		userRepository.save(newUserFormValid.toModel());
		
		ResultActions result =
				mockMvc.perform(post("/users")
					.content(toJson(newUserFormValid))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
		
		long count = userRepository.count();
		
		Assertions.assertEquals(1L, count);
	}

	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

}
