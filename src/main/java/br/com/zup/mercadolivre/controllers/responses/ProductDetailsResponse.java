package br.com.zup.mercadolivre.controllers.responses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.zup.mercadolivre.entities.Product;

public class ProductDetailsResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private BigDecimal price;
	private String description;
	private GradesProductDetailsResponse grade;
	private List<CharacteristcsProductDetailsResponse> characteristics = new ArrayList<>();

	private Set<String> imgUrls = new HashSet<>();
	private List<QuestionsProductDetailsResponse> questions = new ArrayList<>();
	private List<OpinionsProductDetailsResponse> opinions = new ArrayList<>();

	public ProductDetailsResponse(Product product) {
		this.name = product.getName();
		this.price = product.getPrice();
		this.description = product.getDescription();
		this.grade = new GradesProductDetailsResponse(product.getOpinions());
		
		if(product.getCharacteristics().size() != 0)
			this.characteristics = product.getCharacteristics().stream()
					.map(c -> new CharacteristcsProductDetailsResponse(c)).collect(Collectors.toList());
		
		if(product.getImages().size() != 0)
			this.imgUrls = product.getImages().stream().map(i -> i.getImgUrl()).collect(Collectors.toSet());
		
		if(product.getQuestions().size() != 0)
			this.questions = product.getQuestions().stream().map(q -> new QuestionsProductDetailsResponse(q))
					.collect(Collectors.toList());
		
		if(product.getOpinions().size() != 0)
			this.opinions = product.getOpinions().stream().map(o -> new OpinionsProductDetailsResponse(o))
					.collect(Collectors.toList());
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getDescription() {
		return description;
	}

	public GradesProductDetailsResponse getGrade() {
		return grade;
	}

	public List<CharacteristcsProductDetailsResponse> getCharacteristics() {
		return characteristics;
	}

	public Set<String> getImgUrls() {
		return imgUrls;
	}

	public List<QuestionsProductDetailsResponse> getQuestions() {
		return questions;
	}

	public List<OpinionsProductDetailsResponse> getOpinions() {
		return opinions;
	}

}
