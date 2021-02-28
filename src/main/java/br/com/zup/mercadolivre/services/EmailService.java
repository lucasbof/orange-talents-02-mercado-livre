package br.com.zup.mercadolivre.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.zup.mercadolivre.entities.ProductQuestion;

@Service
public class EmailService {

	@Autowired
	private Mailer mailer;
	
	public void newProductQuestion(ProductQuestion question) {
		mailer.send("<html>...</html>","Nova pergunta...", question.getAuthor().getEmail() ,"novapergunta@nossomercadolivre.com",
				question.getProduct().getOwner().getEmail());
	}
}
