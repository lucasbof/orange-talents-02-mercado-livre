package br.com.zup.mercadolivre.controllers.responses;

import java.io.Serializable;

import br.com.zup.mercadolivre.entities.User;

public class UserProductDetailsResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;

	public UserProductDetailsResponse(User author) {
		this.email = author.getEmail();
	}

	public String getEmail() {
		return email;
	}
}
