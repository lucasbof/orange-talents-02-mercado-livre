package br.com.zup.mercadolivre.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestRequestService {

	@Autowired
	private RestTemplate restTemplate;
	
	public String executeGet(String url, Object... pathVars) {

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<String> entity = new HttpEntity<>(headers);

		HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathVars);

		return response.getBody();
	}
}
