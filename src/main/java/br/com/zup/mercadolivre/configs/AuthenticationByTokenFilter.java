package br.com.zup.mercadolivre.configs;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.repositories.UserRepository;
import br.com.zup.mercadolivre.services.AuthService;

public class AuthenticationByTokenFilter extends OncePerRequestFilter {

	private AuthService authService;

	private UserRepository userRepository;

	public AuthenticationByTokenFilter(AuthService authService, UserRepository userRepository) {
		this.authService = authService;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = retrieveToken(request);
		boolean valido = authService.isValidToken(token);
		if (valido) {
			authUser(token);
		}
		filterChain.doFilter(request, response);

	}

	private void authUser(String token) {
		Long userId = authService.getUserId(token);
		User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Dados inválidos!"));
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

	}

	private String retrieveToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token == null || token.isBlank() || !token.startsWith("Bearer ")) {
			return null;
		}
		return token.substring(7, token.length());
	}
}
