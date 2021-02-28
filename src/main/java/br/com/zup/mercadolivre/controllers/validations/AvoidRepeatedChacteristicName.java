package br.com.zup.mercadolivre.controllers.validations;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.com.zup.mercadolivre.controllers.forms.ProductForm;

public class AvoidRepeatedChacteristicName implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ProductForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ProductForm productForm = (ProductForm) target;
		if(productForm.hasRepeatedChacteristicName()) {
			errors.reject("characteristics",  "Os nomes das características devem ser únicos!");
		}
	}

}
