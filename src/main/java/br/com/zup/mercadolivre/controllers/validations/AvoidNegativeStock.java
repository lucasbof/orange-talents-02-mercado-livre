package br.com.zup.mercadolivre.controllers.validations;

import javax.persistence.EntityManager;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.com.zup.mercadolivre.controllers.forms.PurchaseOrderForm;
import br.com.zup.mercadolivre.entities.Product;

public class AvoidNegativeStock implements Validator {

	private EntityManager manager;

	public AvoidNegativeStock(EntityManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return PurchaseOrderForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (errors.hasErrors())
			return;
		PurchaseOrderForm orderForm = (PurchaseOrderForm) target;
		Product product = manager.find(Product.class, orderForm.getProductId());
		if ((product.getQuantity() - orderForm.getQuantity()) < 0) {
			errors.reject("quantity", "A quantidade informada (" + orderForm.getQuantity()
					+ ") é maior que o disponível no estoque (" + product.getQuantity() + ")");
		}
	}

}
