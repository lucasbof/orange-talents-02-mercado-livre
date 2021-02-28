package br.com.zup.mercadolivre.controllers.forms;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import br.com.zup.mercadolivre.controllers.validations.ExistsId;
import br.com.zup.mercadolivre.controllers.validations.UniqueValue;
import br.com.zup.mercadolivre.entities.Category;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.User;
import io.jsonwebtoken.lang.Assert;

public class ProductForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@UniqueValue(domainClass = Product.class, fieldName = "name")
	private String name;

	@NotNull
	@Positive
	private BigDecimal price;

	@NotNull
	@DecimalMin(value = "0")
	private Integer quantity;

	@Size(min = 3)
	@Valid
	private List<CharacteristicForm> characteristics = new ArrayList<>();

	@Size(max = 1000)
	private String description;

	@NotNull
	@ExistsId(domainClass = Category.class, fieldName = "id")
	private Long categoryId;

	public ProductForm(@NotBlank String name, @NotNull @Positive BigDecimal price,
			@NotNull @DecimalMin("0") Integer quantity, @Size(min = 3) List<CharacteristicForm> characteristics,
			@Size(max = 1000) String description, @NotNull Long categoryId) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.characteristics.addAll(characteristics);
		this.description = description;
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public List<CharacteristicForm> getCharacteristics() {
		return characteristics;
	}

	public String getDescription() {
		return description;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public Product toModel(EntityManager manager, User owner) {
		Category cat = manager.find(Category.class, categoryId);
		Assert.notNull(cat, "O ID da categoria informado não existe!");
		
		Product product = new Product(name, price, quantity, description, owner, cat, characteristics);
		return product;
	}

	public boolean hasRepeatedChacteristicName() {
		List<String> names = this.characteristics.stream().map(x -> x.getName().toLowerCase()).collect(Collectors.toList());
		return names.size() != new HashSet<String>(names).size();
	}
}
