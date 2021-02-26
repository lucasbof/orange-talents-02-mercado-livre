package br.com.zup.mercadolivre.controllers.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.zup.mercadolivre.builders.CategoryFormBuilder;
import br.com.zup.mercadolivre.entities.Category;


@SpringBootTest
@Transactional
class CategoryFormTest {
	
	@PersistenceContext
	private EntityManager manager;
		
	private CategoryFormBuilder categoryFormBuilder = new CategoryFormBuilder();

	@Test
	@DisplayName("to model should return category without parent when ParentId is null")
	void toModelShouldReturnCategoryWithoutParentWhenParentIdIsNull() {
		String categoryName = "Eletrônicos";
		CategoryForm categoryForm = categoryFormBuilder
									.setName(categoryName)
									.setParentCategoryId(null)
									.build();
		Category cat = categoryForm.toModel(manager);
		
		assertNull(cat.getId());
		assertNull(cat.getParentCategory());
		assertEquals(categoryName, cat.getName());
	}
	
	@Test
	@DisplayName("to model should return category with parent when ParentId isn't null and exist")
	void toModelShouldReturnCategoryWithoutParentWhenParentIdIsNotNullAndExist() {
		String parentCategoryName = "Notebooks";
		String categoryName = "Eletrônicos";
		
		Category parentCategory = new Category(parentCategoryName);
		manager.persist(parentCategory);
		
		CategoryForm categoryForm = categoryFormBuilder
									.setName(categoryName)
									.setParentCategoryId(parentCategory.getId())
									.build();
		
		Category cat = categoryForm.toModel(manager);
		
		assertNull(cat.getId());
		assertNotNull(cat.getParentCategory());
		assertEquals(categoryName, cat.getName());
		assertEquals(parentCategoryName, cat.getParentCategory().getName());
		assertEquals(parentCategory.getId(), cat.getParentCategory().getId());
	}

}
