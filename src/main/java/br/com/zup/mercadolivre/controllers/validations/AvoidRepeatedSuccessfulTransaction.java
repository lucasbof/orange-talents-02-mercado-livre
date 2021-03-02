package br.com.zup.mercadolivre.controllers.validations;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.com.zup.mercadolivre.controllers.forms.PaymentForm;
import br.com.zup.mercadolivre.entities.Payment;
import io.jsonwebtoken.lang.Assert;

public class AvoidRepeatedSuccessfulTransaction implements Validator {

	private EntityManager manager;

	public AvoidRepeatedSuccessfulTransaction(EntityManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return PaymentForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (errors.hasErrors())
			return;
		PaymentForm paymentForm = (PaymentForm) target;
		TypedQuery<Payment> query = manager.createQuery(
				"SELECT p FROM Payment p WHERE p.transactionId = :transactionId AND p.successfulPayment = TRUE",
				Payment.class);
		query.setParameter("transactionId", paymentForm.getTransactionId());
		List<Payment> payments = query.getResultList();
		
		Assert.state(payments.size() <= 1, "Há mais de uma mesma transação cadastrada com sucesso na base");

		if(payments.size() == 1) {
			errors.reject("transactionId",
					"A transação de ID " + paymentForm.getTransactionId() + " já possui uma operação feita com sucesso!");
		}
	}

}
