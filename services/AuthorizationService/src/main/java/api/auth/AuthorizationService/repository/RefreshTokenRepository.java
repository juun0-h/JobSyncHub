package api.auth.AuthorizationService.repository;

import api.auth.AuthorizationService.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByEmail(String email);
}
