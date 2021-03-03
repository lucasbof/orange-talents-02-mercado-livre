package br.com.zup.mercadolivre.controllers.responses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import br.com.zup.mercadolivre.entities.ProductOpinion;

public class GradesProductDetailsResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal average;
	private Integer totalNumber;

	public GradesProductDetailsResponse(List<ProductOpinion> opinions) {
		if(opinions.size() != 0) {
			List<Double> grades = opinions.stream().map(op -> op.getGrade()).collect(Collectors.toList());
			this.totalNumber = grades.size();
			this.average = calculateAvarage(grades);
		}
		else {
			this.average = new BigDecimal(0);
			this.totalNumber = 0;
		}
	}

	private BigDecimal calculateAvarage(List<Double> grades) {
		double sum = 0.0;
		for (Double grade : grades) {
			sum += grade.doubleValue();
		}
		double avg = sum / grades.size();
		return new BigDecimal(avg).setScale(1, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getAverage() {
		return average;
	}

	public Integer getTotalNumber() {
		return totalNumber;
	}
}
