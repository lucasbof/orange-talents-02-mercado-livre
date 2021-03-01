package br.com.zup.mercadolivre.controllers.forms;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import br.com.zup.mercadolivre.controllers.validations.ExistsId;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.PurchaseOrder;
import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.enums.PaymentGateway;
import br.com.zup.mercadolivre.enums.PurchaseOrderStatus;
import io.jsonwebtoken.lang.Assert;

public class PurchaseOrderForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@Positive
	@NotNull
	private Integer quantity;

	@NotNull
	private PaymentGateway gateway;

	@NotNull
	@ExistsId(domainClass = Product.class, fieldName = "id")
	private Long productId;

	public PurchaseOrderForm(Integer quantity, PaymentGateway gateway, Long productId) {
		this.quantity = quantity;
		this.gateway = gateway;
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public PaymentGateway getGateway() {
		return gateway;
	}

	public Long getProductId() {
		return productId;
	}

	public PurchaseOrder toModel(EntityManager manager, User buyer) {
		Product product = manager.find(Product.class, productId);
		Assert.notNull(product, "O ID do produto informado não existe!");
		
		return new PurchaseOrder(quantity, gateway, PurchaseOrderStatus.STARTED, product, buyer);
	}

}
