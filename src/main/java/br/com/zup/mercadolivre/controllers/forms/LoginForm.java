package br.com.zup.mercadolivre.controllers.forms;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class LoginForm {

	private String email;
	private String senha;
	
	public LoginForm(String email, String senha) {
		this.email = email;
		this.senha = senha;
	}

	public String getEmail() {
		return email;
	}

	public String getSenha() {
		return senha;
	}

	public UsernamePasswordAuthenticationToken convert() {
		return new UsernamePasswordAuthenticationToken(this.email, this.senha);
	}
}
