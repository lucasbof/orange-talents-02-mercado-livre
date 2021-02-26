package br.com.zup.mercadolivre.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToOne
	@JoinColumn(name = "parent_category_id")
	private Category parentCategory;

	@Deprecated
	public Category() {
	}

	public Category(String name) {
		this.name = name;
	}

	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Category getParentCategory() {
		return parentCategory;
	}
}
