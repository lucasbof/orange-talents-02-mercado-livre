package br.com.zup.mercadolivre.controllers.responses;

import java.io.Serializable;

import br.com.zup.mercadolivre.entities.Characteristic;

public class CharacteristcsProductDetailsResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;

	public CharacteristcsProductDetailsResponse(Characteristic c) {
		this.name = c.getName();
		this.description = c.getDescription();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

}
