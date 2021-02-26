package br.com.zup.mercadolivre.controllers.forms;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import br.com.zup.mercadolivre.controllers.validations.ExistsId;
import br.com.zup.mercadolivre.controllers.validations.UniqueValue;
import br.com.zup.mercadolivre.entities.Category;
import io.jsonwebtoken.lang.Assert;

public class CategoryForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@UniqueValue(domainClass = Category.class, fieldName = "name")
	private String name;

	@ExistsId(domainClass = Category.class, fieldName = "id")
	@Positive
	private Long parentCategoryId;

	public void setName(String name) {
		this.name = name;
	}

	public void setParentCategoryId(Long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public String getName() {
		return name;
	}

	public Long getParentCategoryId() {
		return parentCategoryId;
	}

	public Category toModel(EntityManager manager) {
		Category category = new Category(name);
		if (parentCategoryId != null) {
			Category parentCategory = manager.find(Category.class, parentCategoryId);
			Assert.notNull(parentCategory, "O id da categoria pai informado não existe");
			category.setParentCategory(parentCategory);
		}
		return category;
	}
}
