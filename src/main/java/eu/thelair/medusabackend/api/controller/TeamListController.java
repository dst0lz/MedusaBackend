package eu.thelair.medusabackend.api.controller;

import com.google.inject.Inject;
import eu.thelair.medusabackend.api.RestAPI;
import eu.thelair.medusabackend.model.Role;
import eu.thelair.medusabackend.model.User;
import eu.thelair.medusabackend.repository.UserRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;

public class TeamListController implements Controller {
  @Inject
  private RestAPI restAPI;
  @Inject
  private UserRepository userRepository;

  public void getTeamList() {
    restAPI.app().post("/teamlist", ctx -> {
      JSONArray array = new JSONArray();
      for (User user : userRepository.getLoggedInUsers()) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", user.name());

        JSONObject role = new JSONObject();
        role.put("color", user.role().getColor());
        role.put("realName", user.role().getName());
        role.put("name", user.role().name());
        role.put("sortId", user.role().ordinal());

        jsonObject.put("role", role);
        array.add(jsonObject);
      }

      ctx.json(array);
    });
  }

  @Override
  public void register() {
    getTeamList();
  }

  public List<User> getRandomUser() {
    User user1 = new User(4, "SirupDev", "123", Role.DEVELOPER);
    User user2 = new User(5, "Honeyyymoon", "123", Role.ADMIN);
    User user3 = new User(6, "Miiiind", "123", Role.SRGUARDIAN);
    User user4 = new User(7, "DerStandard", "123", Role.ADMIN);
    //userRepository.getLoggedInUsers().clear();
    userRepository.getLoggedInUsers().addAll(Arrays.asList(user1, user2, user3, user4));
    return userRepository.getLoggedInUsers();
  }

}
