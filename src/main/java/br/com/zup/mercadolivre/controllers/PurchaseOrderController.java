package br.com.zup.mercadolivre.controllers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zup.mercadolivre.controllers.forms.PurchaseOrderForm;
import br.com.zup.mercadolivre.controllers.validations.AvoidNegativeStock;
import br.com.zup.mercadolivre.entities.PurchaseOrder;
import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.services.EmailService;

@RestController
@RequestMapping("/order")
public class PurchaseOrderController {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private EmailService emailService;
	
	@InitBinder
	public void init(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new AvoidNegativeStock(manager)); 
	}
	
	@PostMapping
	@Transactional
	public ResponseEntity<String> insert(@AuthenticationPrincipal User loggedUser, @RequestBody @Valid PurchaseOrderForm purchaseOrderForm, UriComponentsBuilder ucb) {
		PurchaseOrder purchaseOrder = purchaseOrderForm.toModel(manager, loggedUser);
		manager.persist(purchaseOrder);
		emailService.newPurchaseOrder(purchaseOrder);
		return ResponseEntity.status(HttpStatus.FOUND).body(purchaseOrder.getGateway().getReturnUrl(purchaseOrder, ucb));
	}
}
