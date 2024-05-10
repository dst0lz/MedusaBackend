package eu.thelair.medusabackend.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import eu.thelair.medusabackend.api.RestAPI;
import eu.thelair.medusabackend.model.Message;
import eu.thelair.medusabackend.model.log.Log;
import eu.thelair.medusabackend.model.log.LogAction;
import eu.thelair.medusabackend.model.log.LogSystem;
import eu.thelair.medusabackend.repository.LogRepository;
import eu.thelair.medusabackend.repository.MessageRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MessageController implements Controller {
  @Inject
  private RestAPI restAPI;

  @Inject
  private MessageRepository repository;

  @Inject
  private LogRepository logRepository;

  private DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

  public void messageCount() {
    restAPI.app().post("/message/count", ctx -> {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("count", repository.countMessages());
      ctx.json(jsonObject);
    });
  }

  public void messageById() {
    restAPI.app().post("/message", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String id = (String) request.get("id");
      Message message = repository.findById(id);
      ctx.json(replaceTimes(message));
    });
  }

  public void randomMessage() {
    restAPI.app().post("/message/random", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String username = (String) request.get("username");
      Message message = repository.findOpenMessage();
      if (message == null) {
        JSONObject response = new JSONObject();
        response.put("messageId", null);
        ctx.json(response);
        return;
      }
      repository.assignMessage(message.messageId(), username);

      Log log = new Log(message.messageId(), username, "Poseidon", Timestamp.valueOf(LocalDateTime.now()), LogAction.ASSIGN_FROM, LogSystem.POSEIDON);
      logRepository.insert(log);

      ctx.json(replaceTimes(message));
    });
  }

  public void finishMessage() {
    restAPI.app().post("/message/finish", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String messageId = (String) request.get("messageId");
      String system = (String) request.get("system");
      String reason = (String) request.get("reason");
      String username = (String) request.get("username");
      repository.finishMessage(messageId, system, reason);
      JSONObject response = new JSONObject();
      response.put("success", true);

      Log log = new Log(messageId, username, "", Timestamp.valueOf(LocalDateTime.now()), LogAction.FINISH, LogSystem.POSEIDON);
      logRepository.insert(log);

      ctx.json(response);
    });
  }

  public void getMesssage() {
    restAPI.app().post("/message/tzeuz", ctx -> {
      repository.fetchMessages();
    });
  }

  @Override
  public void register() {
    messageCount();
    messageById();
    randomMessage();
    finishMessage();
    getMesssage();
  }

  private JSONObject replaceTimes(Message message) throws JsonProcessingException, ParseException {
    ObjectMapper objectMapper = new ObjectMapper();
    JSONParser parser = new JSONParser();
    JSONObject response = (JSONObject) parser.parse(objectMapper.writeValueAsString(message));
    long reportMillis = (long) response.get("messageTime");
    ZoneId zoneId = ZoneId.of("Europe/Berlin");
    LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(reportMillis), zoneId);
    response.put("messageTime", format.format(date));
    return response;
  }
}
