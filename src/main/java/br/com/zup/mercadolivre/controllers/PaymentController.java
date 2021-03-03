package br.com.zup.mercadolivre.controllers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.mercadolivre.controllers.forms.PaymentForm;
import br.com.zup.mercadolivre.controllers.responses.PaymentResponse;
import br.com.zup.mercadolivre.controllers.validations.AvoidPurchaseOrderWithMoreThan2SuccessfulTransactions;
import br.com.zup.mercadolivre.controllers.validations.AvoidRepeatedSuccessfulTransaction;
import br.com.zup.mercadolivre.entities.Payment;
import br.com.zup.mercadolivre.services.EmailService;
import br.com.zup.mercadolivre.services.RestRequestService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private EmailService emailService;

	@Autowired
	private RestRequestService restRequestService;

	@InitBinder
	public void init(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new AvoidRepeatedSuccessfulTransaction(manager),
				new AvoidPurchaseOrderWithMoreThan2SuccessfulTransactions(manager));
	}

	@PostMapping
	@Transactional
	public ResponseEntity<PaymentResponse> insert(@RequestBody @Valid PaymentForm paymentForm) {
		Payment payment = paymentForm.toModel(manager);
		manager.persist(payment);
		if (payment.getSuccessfulPayment()) {
			restRequestService.executeGet("http://localhost:8080/invoice/report/{purchaseOrderId}/{buyerId}",
					payment.getPurchaseOrder().getId(), payment.getPurchaseOrder().getBuyer().getId());
			restRequestService.executeGet("http://localhost:8080/ranking/report/{purchaseOrderId}/{sellerId}",
					payment.getPurchaseOrder().getId(), payment.getPurchaseOrder().getProduct().getOwner().getId());
			emailService.newPurchaseOrderStatusResponse(payment.getPurchaseOrder(), true);
		}
		else {
			emailService.newPurchaseOrderStatusResponse(payment.getPurchaseOrder(), false);
		}
		return ResponseEntity.ok(new PaymentResponse(payment));
	}
}
