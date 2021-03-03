package br.com.zup.mercadolivre.builders;

import br.com.zup.mercadolivre.controllers.forms.PaymentForm;

public class PaymentFormBuilder {

	private Long purchaseOrderId;
	private String paymentCode;
	private Long transactionId;

	public PaymentFormBuilder setPurchaseOrderId(Long purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
		return this;
	}

	public PaymentFormBuilder setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
		return this;
	}

	public PaymentFormBuilder setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	public PaymentForm build() {
		return new PaymentForm(purchaseOrderId, paymentCode, transactionId);
	}

}
