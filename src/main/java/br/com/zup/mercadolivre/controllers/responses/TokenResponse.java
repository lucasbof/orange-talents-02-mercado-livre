package br.com.zup.mercadolivre.controllers.responses;

import java.io.Serializable;

public class TokenResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private String token;
	@SuppressWarnings("unused")
	private String tipo;
	
	public TokenResponse(String token, String tipo) {
		this.token = token;
		this.tipo = tipo;
	}

	
}
