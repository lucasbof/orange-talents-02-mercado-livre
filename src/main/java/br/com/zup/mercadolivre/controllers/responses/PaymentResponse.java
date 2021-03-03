package br.com.zup.mercadolivre.controllers.responses;

import java.io.Serializable;

import br.com.zup.mercadolivre.entities.Payment;

public class PaymentResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long transactionId;
	private Boolean isSuccessfulTransaction;
	
	public PaymentResponse(Payment payment) {
		this.transactionId = payment.getTransactionId();
		this.isSuccessfulTransaction = payment.getSuccessfulPayment();
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public Boolean getIsSuccessfulTransaction() {
		return isSuccessfulTransaction;
	}
}
