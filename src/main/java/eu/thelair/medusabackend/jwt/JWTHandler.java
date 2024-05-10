package eu.thelair.medusabackend.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.thelair.medusabackend.model.Role;
import eu.thelair.medusabackend.model.User;
import eu.thelair.medusabackend.repository.UserRepository;

import java.util.Date;
import java.util.Optional;

@Singleton
public class JWTHandler {
  private static final Algorithm algorithmHS = Algorithm.HMAC512("mZuZkhzKN8LLvVC4kW3nCUN53YGFBvPuPEvaUndSvBPyuESuK5Wmqa7LenCrXvhdFZ8m9h2HKHeczgNsRGShbt2eZDwCT5mKvUHCxBPR2hFeK9QYSfXkkPMKfAqhdzV4QKUf4eDPR7gKFuEmAALhqQq4tEkdH5Qe7zdPdC9aKewp3rE8Ehh6TPvDNcr4eap2qaRBaxSaK4M86ZEnRP5RLHpK5hEdXpJqudJC5GwRsYssvCF9Zqnu9hVnFgfbk9RE");
  private static final long JWT_TOKEN_VALIDITY = 60 * 60;
  private static final String ISSUER = "THELAIR";

  private final UserRepository userRepository;

  @Inject
  public JWTHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public String createToken(long id, Role role) {
    return JWT.create()
            .withSubject("" + id)
            .withIssuer(ISSUER)
            .withClaim("role", role.name())
            .withIssuedAt(new Date(System.currentTimeMillis()))
            .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
            .sign(algorithmHS);
  }

  public boolean validateToken(String token) {
    if (decodeToken(token).isPresent()) {
      DecodedJWT jwt = decodeToken(token).get();
      return jwt.getExpiresAt().after(new Date(System.currentTimeMillis()));
    }
    return false;
  }

  private Optional<DecodedJWT> decodeToken(String token) {
    JWTVerifier verifier = JWT.require(algorithmHS).withIssuer(ISSUER).build();
    DecodedJWT decodedJWT = null;
    try {
      decodedJWT = verifier.verify(token);
    } catch (Exception e) {
    }
    return Optional.ofNullable(decodedJWT);
  }


}
