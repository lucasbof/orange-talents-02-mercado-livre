package br.com.zup.mercadolivre.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.zup.mercadolivre.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByEmail(String email);
}
