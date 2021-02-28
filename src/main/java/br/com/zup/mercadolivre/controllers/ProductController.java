package br.com.zup.mercadolivre.controllers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.mercadolivre.controllers.forms.ProductForm;
import br.com.zup.mercadolivre.controllers.validations.AvoidRepeatedChacteristicName;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.User;

@RestController
@RequestMapping("/products")
public class ProductController {

	@PersistenceContext
	private EntityManager manager;
	
	@InitBinder
	public void init(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new AvoidRepeatedChacteristicName()); 
	}
	
	@PostMapping
	@Transactional
	public ResponseEntity<Void> insert(@AuthenticationPrincipal User loggedUser, @RequestBody @Valid ProductForm productForm) {
		Product product = productForm.toModel(manager, loggedUser);
		manager.persist(product);
		return ResponseEntity.ok().build();
	}
}
