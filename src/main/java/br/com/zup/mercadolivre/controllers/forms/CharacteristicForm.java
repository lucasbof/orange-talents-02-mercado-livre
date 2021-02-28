package br.com.zup.mercadolivre.controllers.forms;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import br.com.zup.mercadolivre.entities.Characteristic;
import br.com.zup.mercadolivre.entities.Product;

public class CharacteristicForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String description;

	public CharacteristicForm(@NotBlank String name, @NotBlank String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}	
	
	public Characteristic toModel(Product product) {
		return new Characteristic(name, description, product);
	}

}
