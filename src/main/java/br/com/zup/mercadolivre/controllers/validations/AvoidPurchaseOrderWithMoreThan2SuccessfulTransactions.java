package br.com.zup.mercadolivre.controllers.validations;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.com.zup.mercadolivre.controllers.forms.PaymentForm;
import br.com.zup.mercadolivre.entities.Payment;

public class AvoidPurchaseOrderWithMoreThan2SuccessfulTransactions implements Validator {

	private EntityManager manager;

	public AvoidPurchaseOrderWithMoreThan2SuccessfulTransactions(EntityManager manager) {
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
				"SELECT p FROM Payment p WHERE p.purchaseOrder.id = :purchaseOrderId AND p.successfulPayment = TRUE",
				Payment.class);
		query.setParameter("purchaseOrderId", paymentForm.getPurchaseOrderId());
		List<Payment> payments = query.getResultList();

		if (payments.size() == 2) {
			errors.reject("purchaseOrderId", "A ordem de compra de ID " + paymentForm.getPurchaseOrderId()
					+ " já possuí 2 transações feitas com sucesso");
		}
	}

}
