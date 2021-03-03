package br.com.zup.mercadolivre.controllers;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.mercadolivre.controllers.forms.ProductImagesForm;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.services.UploaderFakeService;
import io.jsonwebtoken.lang.Assert;

@RestController
@RequestMapping("/product/images")
public class ProductImageController {
	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private UploaderFakeService uploaderFakeService;

	
	@PostMapping(value = "/{id}")
	@Transactional
	public ResponseEntity<String> uploadImage(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Long id, @Valid ProductImagesForm imagesForms) {
		Product product = manager.find(Product.class, id);
		Assert.notNull(product, "O ID do produto informado não existe");
		
		if(!product.getOwner().getEmail().equals(loggedUser.getEmail())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("O usuário" + loggedUser.getEmail() +"  não é propritário do produto de ID " + id );
		}
		
		Set<String> links = uploaderFakeService.uploadImages(imagesForms);
		product.bindImgUrls(links);
		
		manager.merge(product);
		
		return ResponseEntity.ok().build();
	}
}
