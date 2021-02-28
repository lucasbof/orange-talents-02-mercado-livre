package br.com.zup.mercadolivre.controllers.forms;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.lang.Assert;

public class ProductImagesForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@Size(min = 1)
	@NotNull
	private List<MultipartFile> images;

	public ProductImagesForm(@Size(min = 1) List<MultipartFile> images) {
		for(MultipartFile file : images) {
			Assert.state(!file.isEmpty(), "Arquivo não pode ser vazio");
		}
		this.images = images;
	}

	public List<MultipartFile> getImages() {
		return images;
	}

}
