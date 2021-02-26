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
class ExistsIdValidatorTest {

	@Mock
	private ExistsId existIdAnn;

	@PersistenceContext
	private EntityManager manager;

	private ExistsIdValidator validator;

	@BeforeEach
	void setUp() throws Exception {
		Mockito.<Class<?>>when(existIdAnn.domainClass()).thenReturn(Category.class);
		when(existIdAnn.fieldName()).thenReturn("id");
		
		validator = new ExistsIdValidator(manager);
		validator.initialize(existIdAnn);
	}

	@Test
	void isValidShouldReturnTrueWhenIdIsNull() throws Exception {
		boolean valid = validator.isValid(null, null);
		
		assertTrue(valid);
	}

	@Test
	void isValidShouldReturnTrueWhenIdIsNotNullAndExists() throws Exception {
		Category cat = new Category("Eletrônicos");
		manager.persist(cat);

		boolean valid = validator.isValid(cat.getId(), null);

		assertTrue(valid);
	}

	@Test
	void isValidShouldReturnFalseWhenIdIsNotNullAndDoesNotExist() throws Exception {
		boolean valid = validator.isValid(100000L, null);
		
		assertFalse(valid);
	}

}
