package br.com.zup.mercadolivre.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import br.com.zup.mercadolivre.controllers.forms.CharacteristicForm;
import io.jsonwebtoken.lang.Assert;

@Entity
@Table(name = "tb_product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private BigDecimal price;
	private Integer quantity;
	private String description;
	private LocalDate createdAt;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User owner;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
	private Set<Characteristic> characteristics = new HashSet<>();

	public Product(String name, BigDecimal price, Integer quantity, String description, User owner,
			Category category, Collection<CharacteristicForm> characteristics) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.description = description;
		this.owner = owner;
		this.category = category;
		this.characteristics.addAll(
				characteristics.stream()
				.map(c -> c.toModel(this))
				.collect(Collectors.toSet())
		);
		
		Assert.isTrue(this.characteristics.size() >= 3, "Todo produto deve ter no minímo 3 características");
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public String getDescription() {
		return description;
	}

	public Category getCategory() {
		return category;
	}

	public Set<Characteristic> getCharacteristics() {
		return characteristics;
	}

	public User getOwner() {
		return owner;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDate.now();
	}

}
