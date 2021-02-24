package br.com.zup.mercadolivre.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.mercadolivre.controllers.forms.LoginForm;
import br.com.zup.mercadolivre.controllers.responses.TokenResponse;
import br.com.zup.mercadolivre.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private AuthService authService;

	@PostMapping
	public ResponseEntity<TokenResponse> autenticar(@RequestBody @Valid LoginForm loginForm) {

		try {
			UsernamePasswordAuthenticationToken dadosLogin = loginForm.convert();
			Authentication auth = authManager.authenticate(dadosLogin);
			String token = authService.generateToken(auth);
			return ResponseEntity.ok(new TokenResponse(token, "Bearer"));
		} catch (AuthenticationException e) {
			return ResponseEntity.badRequest().build();
		}
	}
}


