package br.com.zup.mercadolivre.enums;

import org.springframework.web.util.UriComponentsBuilder;

import br.com.zup.mercadolivre.entities.PurchaseOrder;

public enum PaymentGateway {

	PAYPAL {
		@Override
		public String getReturnUrl(PurchaseOrder order, UriComponentsBuilder uriComponentsBuilder) {
			String urlRetornoPaypal = uriComponentsBuilder
					.path("/retorno-paypal/{id}")
					.buildAndExpand(order.getId())
					.toString();

			return "paypal.com/" + order.getId() + "?redirectUrl=" + urlRetornoPaypal;
		}
	},
	PAGSEGURO {
		@Override
		public String getReturnUrl(PurchaseOrder order, UriComponentsBuilder uriComponentsBuilder) {
			String urlPagseguro = uriComponentsBuilder
					.path("/retorno-pagseguro/{id}")
					.buildAndExpand(order.getId())
					.toString();

			return "pagseguro.com/" + order.getId() + "?redirectUrl=" + urlPagseguro;
		}
	};

	public abstract String getReturnUrl(PurchaseOrder order, UriComponentsBuilder uriComponentsBuilder);
}
