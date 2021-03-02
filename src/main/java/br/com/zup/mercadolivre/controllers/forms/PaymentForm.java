package br.com.zup.mercadolivre.controllers.forms;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.mercadolivre.controllers.validations.ExistsId;
import br.com.zup.mercadolivre.entities.Payment;
import br.com.zup.mercadolivre.entities.PurchaseOrder;
import io.jsonwebtoken.lang.Assert;

public class PaymentForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@ExistsId(domainClass = PurchaseOrder.class, fieldName = "id")
	private Long purchaseOrderId;

	@NotBlank
	private String paymentCode;

	@NotNull
	private Long transactionId;

	public PaymentForm(@NotNull Long purchaseOrderId, @NotBlank String paymentCode, @NotNull Long transactionId) {
		this.purchaseOrderId = purchaseOrderId;
		this.paymentCode = paymentCode;
		this.transactionId = transactionId;
	}

	public Long getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public String getPaymentCode() {
		return paymentCode;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public Payment toModel(EntityManager manager) {
		PurchaseOrder order = manager.find(PurchaseOrder.class, purchaseOrderId);
		Assert.notNull(order, "O ID do pedido de compra informado não existe");

		boolean successfulPayment = order.getGateway().isSuccessCode(paymentCode);
		return new Payment(order, successfulPayment, transactionId);
	}
}
