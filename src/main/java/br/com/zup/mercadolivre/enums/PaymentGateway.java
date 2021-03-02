package br.com.zup.mercadolivre.enums;

import org.springframework.web.util.UriComponentsBuilder;

import br.com.zup.mercadolivre.entities.PurchaseOrder;

public enum PaymentGateway {

	PAYPAL("1", "0") {
		@Override
		public String getReturnUrl(PurchaseOrder order, UriComponentsBuilder uriComponentsBuilder) {
			String urlRetornoPaypal = uriComponentsBuilder.path("/retorno-paypal/{id}").buildAndExpand(order.getId())
					.toString();

			return "paypal.com/" + order.getId() + "?redirectUrl=" + urlRetornoPaypal;
		}
	},
	PAGSEGURO("SUCESSO", "ERRO") {
		@Override
		public String getReturnUrl(PurchaseOrder order, UriComponentsBuilder uriComponentsBuilder) {
			String urlPagseguro = uriComponentsBuilder.path("/retorno-pagseguro/{id}").buildAndExpand(order.getId())
					.toString();

			return "pagseguro.com/" + order.getId() + "?redirectUrl=" + urlPagseguro;
		}
	};

	private String successCodePayment;
	private String failureCodePayment;

	private PaymentGateway(String successCodePayment, String failureCodePayment) {
		this.successCodePayment = successCodePayment;
		this.failureCodePayment = failureCodePayment;
	}

	public String getSuccessCodePayment() {
		return successCodePayment;
	}

	public String getFailureCodePayment() {
		return failureCodePayment;
	}

	public abstract String getReturnUrl(PurchaseOrder order, UriComponentsBuilder uriComponentsBuilder);

	public boolean isSuccessCode(String paymentCode) {
		if (this.successCodePayment.equals(paymentCode))
			return true;
		else if (this.failureCodePayment.equals(paymentCode))
			return false;
		throw new IllegalStateException("O status de pagamento " + paymentCode + " é desconhecido");
	}
}
