package br.com.zup.mercadolivre.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.Table;

import br.com.zup.mercadolivre.enums.PaymentGateway;
import br.com.zup.mercadolivre.enums.PurchaseOrderStatus;

@Entity
@Table(name = "tb_purchase_order")
public class PurchaseOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer quantity;
	
	@Enumerated(value = EnumType.STRING)
	private PaymentGateway gateway;
	
	@Enumerated(value = EnumType.STRING)
	private PurchaseOrderStatus status;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "buyer_id")
	private User buyer;
	
	@OneToMany(mappedBy = "purchaseOrder")
	private List<Payment> payments = new ArrayList<>();
	
	@Deprecated
	public PurchaseOrder() {
	}

	public PurchaseOrder(Integer quantity, PaymentGateway gateway, PurchaseOrderStatus status, Product product,
			User buyer) {
		this.quantity = quantity;
		this.gateway = gateway;
		this.status = status;
		this.product = product;
		this.buyer = buyer;
	}

	public Long getId() {
		return id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public PaymentGateway getGateway() {
		return gateway;
	}

	public PurchaseOrderStatus getStatus() {
		return status;
	}

	public Product getProduct() {
		return product;
	}

	public User getBuyer() {
		return buyer;
	}
	
	void updateStatus() {
		List<Payment> successfulList = this.payments.stream().filter(p -> p.getSuccessfulPayment() == true).collect(Collectors.toList());
		switch (successfulList.size()) {
		case 1:
			this.status = PurchaseOrderStatus.SUCCESS1;
			break;
		case 2:
			this.status = PurchaseOrderStatus.SUCCESS2;
			break;
		default:
			this.status = PurchaseOrderStatus.FAILURE;
			break;
		}
	}

	@PostPersist
	private void updateStock() {
		this.product.updateStock(this.quantity);		
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
		PurchaseOrder other = (PurchaseOrder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
