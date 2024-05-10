package eu.thelair.medusabackend.api.controller;

import com.google.inject.Inject;
import eu.thelair.medusabackend.api.RestAPI;
import eu.thelair.medusabackend.model.log.LogSystem;
import eu.thelair.medusabackend.repository.StatisticsRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class StatisticsController implements Controller {
  @Inject
  private RestAPI restAPI;

  @Inject
  private StatisticsRepository repository;

  public void reportLastWeekCount() {
    restAPI.app().post("/statistics/report/week", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String username = (String) request.get("username");

      JSONObject response = new JSONObject();
      response.put("count", repository.findLastWeek(username, LogSystem.MEDUSA));
      ctx.json(response);
    });
  }

  public void poseidonLastWeekCount() {
    restAPI.app().post("/statistics/poseidon/week", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String username = (String) request.get("username");

      JSONObject response = new JSONObject();
      response.put("count", repository.findLastWeek(username, LogSystem.POSEIDON));
      ctx.json(response);
    });
  }

  public void reportCount() {
    restAPI.app().post("/statistics/report/alltime", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String username = (String) request.get("username");

      JSONObject response = new JSONObject();
      response.put("count", repository.findAllTime(username));
      ctx.json(response);
    });
  }

  public void poseidonCount() {
    restAPI.app().post("/statistics/poseidon/alltime", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String username = (String) request.get("username");

      JSONObject response = new JSONObject();
      response.put("count", repository.findAllTimePoseidon(username));
      ctx.json(response);
    });
  }

  public void teamspeakCount() {
    restAPI.app().post("/statistics/teamspeak/alltime", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String username = (String) request.get("username");

      JSONObject response = new JSONObject();
      response.put("count", repository.findTeamspeakTimeByName(username));
      ctx.json(response);
    });
  }

  @Override
  public void register() {
    reportLastWeekCount();
    poseidonLastWeekCount();
    reportCount();
    poseidonCount();
    teamspeakCount();
  }
}
