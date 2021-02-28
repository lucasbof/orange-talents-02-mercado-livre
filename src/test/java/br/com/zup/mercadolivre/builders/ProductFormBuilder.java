package br.com.zup.mercadolivre.builders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.zup.mercadolivre.controllers.forms.CharacteristicForm;
import br.com.zup.mercadolivre.controllers.forms.ProductForm;

public class ProductFormBuilder {

	private String name;
	private BigDecimal price;
	private Integer quantity;
	private String description;
	private List<CharacteristicForm> characteristics = new ArrayList<>();
	private Long categoryId;
	
	public ProductForm createValidProductForm(Long categoryId) {
		this.name = "Samsung Galaxy";
		this.price = new BigDecimal(803.50);
		this.quantity = 132;
		this.description = "Um ótimo celular";
		this.characteristics = createValidcharacteristicsList();
		return new ProductForm(name, price, quantity, characteristics, description, categoryId);
	}

	public List<CharacteristicForm> createValidcharacteristicsList() {
		List<CharacteristicForm> list = new ArrayList<>();
		list.add(new CharacteristicForm("Qualidade", "Uma ótima qualidade"));
		list.add(new CharacteristicForm("Praticidade", "Uma ótima praticidade"));
		list.add(new CharacteristicForm("Utilidade", "Uma ótima utilidade"));
		return list;
	}

	public ProductFormBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public ProductFormBuilder setPrice(BigDecimal price) {
		this.price = price;
		return this;
	}

	public ProductFormBuilder setQuantity(Integer quatity) {
		this.quantity = quatity;
		return this;
	}

	public ProductFormBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public ProductFormBuilder setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public ProductFormBuilder addCharacteristiscForm(String name, String description) {
		this.characteristics.add(new CharacteristicForm(name, description));
		return this;
	}

	public ProductForm build() {
		ProductForm form = new ProductForm(name, price, quantity, characteristics, description, categoryId);
		return form;
	}
}
