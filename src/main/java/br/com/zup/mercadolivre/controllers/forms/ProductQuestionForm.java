package br.com.zup.mercadolivre.controllers.forms;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.mercadolivre.controllers.validations.ExistsId;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.ProductQuestion;
import br.com.zup.mercadolivre.entities.User;
import io.jsonwebtoken.lang.Assert;

public class ProductQuestionForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotBlank
	private String title;
	
	@NotNull
	@ExistsId(domainClass = Product.class, fieldName = "id")
	private Long productId;

	public ProductQuestionForm(String title, @NotNull Long productId) {
		this.title = title;
		this.productId = productId;
	}

	public String getTitle() {
		return title;
	}

	public Long getProductId() {
		return productId;
	}
	
	public ProductQuestion toModel(EntityManager manager, User author) {
		Product product = manager.find(Product.class, productId);
		Assert.notNull(product, "O ID do produto informado não existe!");
		
		return new ProductQuestion(title, product, author);
	}
	
	

}
