package br.com.zup.mercadolivre.builders;

import br.com.zup.mercadolivre.controllers.forms.CategoryForm;

public class CategoryFormBuilder {

	private String name;
	private Long parentCategoryId;
	
	public CategoryFormBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public CategoryFormBuilder setParentCategoryId(Long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
		return this;
	}
	
	public CategoryForm build() {
		CategoryForm form = new CategoryForm();
		form.setName(name);
		form.setParentCategoryId(parentCategoryId);
		return form;
	}
}
