package br.com.zup.mercadolivre.controllers.forms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.zup.mercadolivre.builders.ProductFormBuilder;

class ProductFormTest {
	
	private ProductFormBuilder productFormBuilder;

	@BeforeEach
	void setUp() throws Exception {
		productFormBuilder = new ProductFormBuilder();
	}

	@Test
	void hasRepeatedCharacteristicNameShouldReturnTrueWhenThereAreRepeatedCharacteristicNames() {
		String characteristicName = "qualidade";
		ProductForm productForm = productFormBuilder
									.addCharacteristiscForm(characteristicName, "")
									.addCharacteristiscForm(characteristicName, "")
									.addCharacteristiscForm("limpeza", "")
									.build();
		boolean result = productForm.hasRepeatedChacteristicName();
		
		assertTrue(result);
	}
	
	@Test
	void hasRepeatedCharacteristicNameShouldReturnTrueWhenThereAreRepeatedCharacteristicNamesIgnoreCase() {
		String characteristicName = "qualidade";
		ProductForm productForm = productFormBuilder
									.addCharacteristiscForm(characteristicName.toUpperCase(), "")
									.addCharacteristiscForm(characteristicName.toLowerCase(), "")
									.addCharacteristiscForm("limpeza", "")
									.build();
		boolean result = productForm.hasRepeatedChacteristicName();
		
		assertTrue(result);
	}
	
	@Test
	void hasRepeatedCharacteristicNameShouldReturnFalseWhenThereAreNotRepeatedCharacteristicNames() {
		ProductForm productForm = productFormBuilder
									.addCharacteristiscForm("durabilidade", "")
									.addCharacteristiscForm("utilidade", "")
									.addCharacteristiscForm("limpeza", "")
									.build();
		boolean result = productForm.hasRepeatedChacteristicName();
		
		assertFalse(result);
	}

}
