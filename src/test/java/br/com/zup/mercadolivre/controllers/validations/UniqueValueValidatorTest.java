package br.com.zup.mercadolivre.controllers.validations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.zup.mercadolivre.entities.Category;

@SpringBootTest
@Transactional
class UniqueValueValidatorTest {
	
	@Mock
	private UniqueValue uniqueValueAnn;
	
	@PersistenceContext
	private EntityManager manager;
	
	private UniqueValueValidator validator;

	@BeforeEach
	void setUp() throws Exception {
		Mockito.<Class<?>>when(uniqueValueAnn.domainClass()).thenReturn(Category.class);
		when(uniqueValueAnn.fieldName()).thenReturn("name");
		validator = new UniqueValueValidator(manager);
		validator.initialize(uniqueValueAnn);
	}

	@Test
	void isValidShouldReturnTrueWhenValueIsUnique() {		
		boolean valid = validator.isValid("Eletrônicos", null);
		assertTrue(valid);
	}
	
	@Test
	void isValidShouldReturnFalseWhenValueIsNotUnique() {
		String categoryName = "Eletrônicos";
		Category category = new Category(categoryName);
		
		manager.persist(category);
		
		boolean valid = validator.isValid(categoryName, null);
		assertFalse(valid);
	}
	
	@Test
	void isValidShouldReturnFalseWhenValueIsNotUniqueIgnoreCase() {
		Category category = new Category("eletrônicos");
		
		manager.persist(category);
		
		boolean valid = validator.isValid("ElEtRôNiCoS", null);
		assertFalse(valid);
	}

}
