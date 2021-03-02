package br.com.zup.mercadolivre.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoice")
public class InvoiceFakeController {

	@GetMapping(value = "/report/{purchaseOrderId}/{buyerId}")
	public ResponseEntity<Void> reportInvoice(
			@PathVariable(value = "purchaseOrderId") Long purchaseOrderId,
			@PathVariable(value = "buyerId") Long buyerId
	) {
		if(purchaseOrderId == null || buyerId == null) {
			return ResponseEntity.badRequest().build();
		}
		System.out.println("Nota fiscal recebeu o report...");
		System.out.println("purchaseOrderId " + purchaseOrderId);
		System.out.println("buyerId " + buyerId);
		return ResponseEntity.ok().build();
	}
}
