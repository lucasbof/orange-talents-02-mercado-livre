package br.com.zup.mercadolivre.controllers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.mercadolivre.controllers.forms.CategoryForm;
import br.com.zup.mercadolivre.entities.Category;

@RestController
@RequestMapping("/categories")
public class CategoryController {

	@PersistenceContext
	private EntityManager manager;
	
	@PostMapping
	@Transactional
	public ResponseEntity<Void> insert(@RequestBody @Valid CategoryForm categoryForm) {
		Category category = categoryForm.toModel(manager);
		manager.persist(category);
		return ResponseEntity.ok().build();
	}
}
