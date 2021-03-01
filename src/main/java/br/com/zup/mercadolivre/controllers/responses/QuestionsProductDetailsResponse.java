package br.com.zup.mercadolivre.controllers.responses;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.zup.mercadolivre.entities.ProductQuestion;

public class QuestionsProductDetailsResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String title;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate createdAt;

	private UserProductDetailsResponse author;

	public QuestionsProductDetailsResponse(ProductQuestion question) {
		this.title = question.getTitle();
		this.createdAt = question.getCreatedAt();

		this.author = new UserProductDetailsResponse(question.getAuthor());
	}

	public String getTitle() {
		return title;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public UserProductDetailsResponse getAuthor() {
		return author;
	}

}
