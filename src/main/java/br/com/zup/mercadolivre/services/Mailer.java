package br.com.zup.mercadolivre.services;

public interface Mailer {

	void send(String body, String subject, String nameFrom, String from, String to);
}
