package br.com.zup.mercadolivre.controllers.responses;

import java.io.Serializable;

import br.com.zup.mercadolivre.entities.ProductOpinion;

public class OpinionsProductDetailsResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Double grade;
	private String title;
	private String description;
	private UserProductDetailsResponse author;

	public OpinionsProductDetailsResponse(ProductOpinion opinion) {
		this.grade = opinion.getGrade();
		this.title = opinion.getTitle();
		this.description = opinion.getDescription();
		this.author = new UserProductDetailsResponse(opinion.getAuthor());
	}

	public Double getGrade() {
		return grade;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public UserProductDetailsResponse getAuthor() {
		return author;
	}

}
