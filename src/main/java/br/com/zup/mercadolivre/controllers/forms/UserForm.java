package br.com.zup.mercadolivre.controllers.forms;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.zup.mercadolivre.entities.User;

public class UserForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Length(min = 6)
	private String password;

	public UserForm(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public User toModel() {
		return new User(this.email, new BCryptPasswordEncoder().encode(this.password));
	}
}
