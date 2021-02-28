package br.com.zup.mercadolivre.controllers.forms;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.zup.mercadolivre.controllers.validations.ExistsId;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.ProductOpinion;
import br.com.zup.mercadolivre.entities.User;
import io.jsonwebtoken.lang.Assert;

public class ProductOpinionForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@DecimalMin(value = "1")
	@DecimalMax(value = "5")
	private Double grade;
	
	@NotBlank
	private String title;
	
	@NotBlank
	@Size(max = 500)
	private String description;
	
	@NotNull
	@ExistsId(domainClass = Product.class, fieldName = "id")
	private Long productId;

	public ProductOpinionForm(@NotNull @DecimalMin("1") @DecimalMax("5") Double grade, @NotBlank String title,
			@NotBlank @Size(max = 500) String description, @NotNull Long productId) {
		this.grade = grade;
		this.title = title;
		this.description = description;
		this.productId = productId;
	}

	public Double getGrade() {
		return grade;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Long getProductId() {
		return productId;
	}
	
	public ProductOpinion toModel(EntityManager manager, User author) {
		Product product = manager.find(Product.class, productId);
		Assert.notNull(product, "O ID do produto informado não existe!");
		
		return new ProductOpinion(grade, title, description, product, author);
	}
}
