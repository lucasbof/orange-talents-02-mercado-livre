package br.com.zup.mercadolivre.controllers.exceptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidationError implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<FieldMessage> fieldErrors = new ArrayList<>();
	private List<FieldMessage> globalErrors = new ArrayList<>();

	public List<FieldMessage> getFieldErrors() {
		return fieldErrors;
	}

	public List<FieldMessage> getGlobalErrors() {
		return globalErrors;
	}

	public void addFieldError(String fieldName, String message) {
		fieldErrors.add(new FieldMessage(fieldName, message));
	}
	
	public void addGlobalError(String fieldName, String message) {
		globalErrors.add(new FieldMessage(fieldName, message));
	}
}
