package br.com.zup.mercadolivre.controllers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.mercadolivre.controllers.forms.ProductOpinionForm;
import br.com.zup.mercadolivre.entities.ProductOpinion;
import br.com.zup.mercadolivre.entities.User;

@RestController
@RequestMapping("/product/opinions")
public class ProductOpinionController {

	@PersistenceContext
	private EntityManager manager;
	
	@PostMapping
	@Transactional
	public ResponseEntity<Void> insert(@AuthenticationPrincipal User loggedUser, @RequestBody @Valid ProductOpinionForm opinionForm) {
		ProductOpinion productOpinion = opinionForm.toModel(manager, loggedUser);
		manager.persist(productOpinion);
		return ResponseEntity.ok().build();
	}
}
