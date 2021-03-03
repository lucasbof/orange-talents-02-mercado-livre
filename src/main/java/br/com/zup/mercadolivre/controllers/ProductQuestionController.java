package br.com.zup.mercadolivre.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.mercadolivre.controllers.forms.ProductQuestionForm;
import br.com.zup.mercadolivre.entities.ProductQuestion;
import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.services.EmailService;

@RestController
@RequestMapping("/product/questions")
public class ProductQuestionController {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private EmailService emailService;
	
	@PostMapping
	@Transactional
	public ResponseEntity<List<String>> insert(@AuthenticationPrincipal User loggedUser, @RequestBody @Valid ProductQuestionForm questionForm) {
		ProductQuestion productQuestion = questionForm.toModel(manager, loggedUser);
		manager.persist(productQuestion);
		emailService.newProductQuestion(productQuestion);
		List<ProductQuestion> questions = manager.createQuery("SELECT q FROM ProductQuestion q", ProductQuestion.class).getResultList();
		return ResponseEntity.ok(questions.stream().map(q -> q.getTitle()).collect(Collectors.toList()));
	}
}
