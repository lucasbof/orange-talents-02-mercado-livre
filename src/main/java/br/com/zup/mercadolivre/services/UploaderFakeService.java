package br.com.zup.mercadolivre.services;

import java.util.Set;
import java.util.stream.Collectors;

import br.com.zup.mercadolivre.controllers.forms.ProductImagesForm;

public class UploaderFakeService {

	public static Set<String> uploadImages(ProductImagesForm imagesForm) {
		return imagesForm.getImages().stream().map(image -> "bucket://" + Math.random() + image.getOriginalFilename())
				.collect(Collectors.toSet());
	}

}
