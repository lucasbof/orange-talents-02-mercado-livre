package br.com.zup.mercadolivre.builders;

import br.com.zup.mercadolivre.controllers.forms.UserForm;

public class UserFormBuilder {

	private String email;
	private String password;
	
	public UserFormBuilder setEmail(String email) {
		this.email = email;
		return this;
	}
	
	public UserFormBuilder setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public UserForm build() {
		return new UserForm(email, password);
	}
}
