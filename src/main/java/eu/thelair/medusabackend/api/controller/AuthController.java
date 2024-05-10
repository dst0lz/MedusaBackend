package eu.thelair.medusabackend.api.controller;

import com.google.inject.Inject;
import eu.thelair.medusabackend.api.RestAPI;
import eu.thelair.medusabackend.jwt.JWTHandler;
import eu.thelair.medusabackend.model.User;
import eu.thelair.medusabackend.repository.UserRepository;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Optional;

public class AuthController implements Controller {
  @Inject
  private RestAPI restAPI;
  @Inject
  private UserRepository userRepository;
  @Inject
  private JWTHandler jwtHandler;

  private void login() {
    restAPI.app().post("/login", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      if (!request.containsKey("username")) {
        throw new BadRequestResponse("Username not found");
      }
      if (!request.containsKey("password")) {
        throw new BadRequestResponse("Password not found");
      }
      Optional<User> optionalUser = userRepository.findByUsername(request.get("username").toString());
      if (optionalUser.isEmpty()) {
        throw new ForbiddenResponse("User not found");
      }
      User user = optionalUser.get();
      if (!request.get("username").equals(user.name())) {
        throw new ForbiddenResponse("User not found");
      }
      if (!request.get("password").equals(user.password())) {
        throw new ForbiddenResponse("Wrong password");
      }

      JSONObject jsonUser = new JSONObject();
      jsonUser.put("id", user.id());
      jsonUser.put("username", user.name());

      JSONObject role = new JSONObject();
      role.put("color", user.role().getColor());
      role.put("realName", user.role().getName());
      role.put("name", user.role().name());

      jsonUser.put("role", role);

      JSONObject response = new JSONObject();
      response.put("user", jsonUser);
      response.put("token", jwtHandler.createToken(user.id(), user.role()));

      if (!userRepository.getLoggedInUsers().contains(user)) {
        userRepository.addUser(user);
      }

      ctx.json(response);
    });
  }

  @Override
  public void register() {
    login();
  }
}
