package br.com.zup.mercadolivre.controllers;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.mercadolivre.controllers.forms.UserForm;
import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.repositories.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserRepository repository;
	
	@PostMapping
	@Transactional
	public ResponseEntity<Void> insert(@RequestBody @Valid UserForm userForm) {
		User user = userForm.toModel();
		user = repository.save(user);
		return ResponseEntity.ok().build();
	}
}
