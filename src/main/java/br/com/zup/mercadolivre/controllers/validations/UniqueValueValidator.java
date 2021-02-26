package br.com.zup.mercadolivre.controllers.validations;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class UniqueValueValidator implements ConstraintValidator<UniqueValue, String> {

	private String fieldName;
	private Class<?> domainClass;
	
	/*
		 Este obj será injetado pelo Spring se a anotação estiver fora da entidade, pois estaria no contexto do Spring, e assim funcionaria normalmente
		 Caso esteja na entidade, o Hibernate que faria a injeção, mas ele não conseguiria fazer esta injeção, por não estar no contexto do Spring
		 e assim, este objeto será nulo podendo lançar NullPointerException em isValid
	 */
	private EntityManager manager;
	
	@Autowired
	public UniqueValueValidator(EntityManager manager) {
		this.manager = manager;
	}

	@Override
	public void initialize(UniqueValue params) {
		this.fieldName = params.fieldName();
		this.domainClass = params.domainClass();	
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		Query query = manager.createQuery("SELECT 1 FROM " + domainClass.getName() + " WHERE LOWER(" + fieldName + ") = LOWER(:value)");
		
		query.setParameter("value", value);
		List<?> resultList = query.getResultList();
		
		Assert.state(resultList.size() <= 1, "Foi encontrado mais de um " + domainClass + "com o atributo " + fieldName );
		
		return resultList.isEmpty();
	}
	
	



}
