package br.com.zup.mercadolivre.entities;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "tb_payment")
public class Payment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "purchase_order_id")
	private PurchaseOrder purchaseOrder;

	private Boolean successfulPayment;

	private Long transactionId;

	private Instant createdAt;

	@Deprecated
	public Payment() {
	}

	public Payment(PurchaseOrder purchaseOrder, Boolean successfulPayment, Long transactionId) {
		this.purchaseOrder = purchaseOrder;
		this.successfulPayment = successfulPayment;
		this.transactionId = transactionId;
	}

	public Long getId() {
		return id;
	}

	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}

	public Boolean getSuccessfulPayment() {
		return successfulPayment;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = Instant.now();
	}

	@PostPersist
	private void updateOrderStatus() {
		this.purchaseOrder.updateStatus();
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
		Payment other = (Payment) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
