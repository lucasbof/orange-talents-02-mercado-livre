package br.com.zup.mercadolivre.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.mercadolivre.controllers.forms.LoginForm;
import br.com.zup.mercadolivre.services.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private AuthService authService;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void authenticateShouldReturn200WithValidCredentials() throws Exception {
		LoginForm loginForm = new LoginForm("lucas@gmail.com", "123456");
		
		ResultActions result =
				mockMvc.perform(post("/auth")
					.content(toJson(loginForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		result.andExpect(jsonPath("$.token").exists());
		result.andExpect(jsonPath("$.type").exists());
		
		String response = result.andReturn().getResponse().getContentAsString();
		Map<String, String> map = objectMapper.readValue(response, new TypeReference<Map<String, String>>() {});
		
		assertTrue(authService.isValidToken(map.get("token")));
	}
	
	@Test
	void authenticateShouldReturn400WithInvalidCredentials() throws Exception {
		LoginForm loginForm = new LoginForm("hyhyh@juj.com", null);
		
		ResultActions result =
				mockMvc.perform(post("/auth")
					.content(toJson(loginForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
	}
	
	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

}
