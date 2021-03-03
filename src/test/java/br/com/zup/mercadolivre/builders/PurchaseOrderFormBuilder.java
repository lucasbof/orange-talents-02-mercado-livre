package br.com.zup.mercadolivre.builders;

import br.com.zup.mercadolivre.controllers.forms.PurchaseOrderForm;
import br.com.zup.mercadolivre.enums.PaymentGateway;

public class PurchaseOrderFormBuilder {

	private Integer quantity;
	private PaymentGateway gateway;
	private Long productId;
	
	public PurchaseOrderForm createValidPurchaseOrderForm(Long productId) {
		this.quantity = 132;
		this.gateway = PaymentGateway.PAYPAL;
		this.productId = productId;
		return build();
	}
	
	public PurchaseOrderFormBuilder setQuantity(Integer quantity) {
		this.quantity = quantity;
		return this;
	}
	public PurchaseOrderFormBuilder setGateway(PaymentGateway gateway) {
		this.gateway = gateway;
		return this;
	}
	public PurchaseOrderFormBuilder setProductId(Long productId) {
		this.productId = productId;
		return this;
	}
	
	public PurchaseOrderForm build() {
		return new PurchaseOrderForm(quantity, gateway, productId);
	}
	
	
}
