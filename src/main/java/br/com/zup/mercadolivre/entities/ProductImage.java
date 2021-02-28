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
@Table(name = "tb_product_image")
public class ProductImage implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String imgUrl;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Deprecated
	public ProductImage() {
	}

	public ProductImage(String imgUrl, Product product) {
		this.imgUrl = imgUrl;
		this.product = product;
	}

	public Long getId() {
		return id;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public Product getProduct() {
		return product;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imgUrl == null) ? 0 : imgUrl.hashCode());
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
		ProductImage other = (ProductImage) obj;
		if (imgUrl == null) {
			if (other.imgUrl != null)
				return false;
		} else if (!imgUrl.equals(other.imgUrl))
			return false;
		return true;
	}

}
