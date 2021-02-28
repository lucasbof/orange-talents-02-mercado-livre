package br.com.zup.mercadolivre.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_product_opinion")
public class ProductOpinion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Double grade;
	private String title;
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "author_id")
	private User author;

	public ProductOpinion(Double grade, String title, String description, Product product, User author) {
		this.grade = grade;
		this.title = title;
		this.description = description;
		this.product = product;
		this.author = author;
	}

	public Long getId() {
		return id;
	}

	public Double getGrade() {
		return grade;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Product getProduct() {
		return product;
	}

	public User getAuthor() {
		return author;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductOpinion other = (ProductOpinion) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
