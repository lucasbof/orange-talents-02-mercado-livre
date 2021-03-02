package br.com.zup.mercadolivre.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ranking")
public class SellersRankingFakeController {

	@GetMapping(value = "/report/{purchaseOrderId}/{sellerId}")
	public ResponseEntity<Void> reportInvoice(
			@PathVariable(value = "purchaseOrderId") Long purchaseOrderId,
			@PathVariable(value = "sellerId") Long sellerId
	) {
		if(purchaseOrderId == -1 || sellerId == -1) {
			return ResponseEntity.badRequest().build();
		}
		System.out.println("O ranking de vendedores recebeu o report");
		System.out.println("purchaseOrderId: " + purchaseOrderId);
		System.out.println("sellerId " + sellerId);
		return ResponseEntity.ok().build();
	}
}
