package br.com.zup.mercadolivre.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthService implements UserDetailsService {

	@Value("${mercadolivre.jwt.expiration}")
	private String expiration;

	@Value("${mercadolivre.jwt.secret}")
	private String secret;

	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> usuario = repository.findByEmail(username);
		return usuario.orElseThrow(() -> new UsernameNotFoundException("Dados inválidos!"));
	}

	public String generateToken(Authentication auth) {
		User user = (User) auth.getPrincipal();
		Date hoje = new Date();
		Date dataExpiracao = new Date(hoje.getTime() + Long.parseLong(expiration));
		return Jwts.builder().setIssuer("API do Mercado Livre").setSubject(user.getId().toString()).setIssuedAt(hoje)
				.setExpiration(dataExpiracao).signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	public boolean isValidToken(String token) {
		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public Long getUserId(String token) {
		Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
		return Long.parseLong(claims.getSubject());
	}

}
