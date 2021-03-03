package br.com.zup.mercadolivre.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.zup.mercadolivre.entities.ProductQuestion;
import br.com.zup.mercadolivre.entities.PurchaseOrder;

@Service
public class EmailService {

	@Autowired
	private Mailer mailer;
	
	public void newProductQuestion(ProductQuestion question) {
		mailer.send("<html>...</html>","Nova pergunta...", question.getAuthor().getEmail() ,"novapergunta@nossomercadolivre.com",
				question.getProduct().getOwner().getEmail());
	}
	
	public void newPurchaseOrder(PurchaseOrder order) {
		mailer.send("<html>...</html>","Nova Compra...", order.getBuyer().getEmail() ,"novacompra@nossomercadolivre.com",
				order.getProduct().getOwner().getEmail());
	}

	public void newPurchaseOrderStatusResponse(PurchaseOrder order, boolean isSuccess) {
		if(isSuccess)
			mailer.send("<html>...</html>","Compra feita com sucesso", order.getProduct().getOwner().getEmail() ,"novacompra@nossomercadolivre.com",
				order.getBuyer().getEmail());
		else
			mailer.send("<html>...</html>","A compra falhou...", order.getProduct().getOwner().getEmail() ,"novacompra@nossomercadolivre.com",
					order.getBuyer().getEmail());
	}
}
