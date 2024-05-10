package eu.thelair.medusabackend.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import eu.thelair.medusabackend.api.RestAPI;
import eu.thelair.medusabackend.database.MedusaSQL;
import eu.thelair.medusabackend.model.Report;
import eu.thelair.medusabackend.model.log.Log;
import eu.thelair.medusabackend.model.log.LogAction;
import eu.thelair.medusabackend.model.log.LogSystem;
import eu.thelair.medusabackend.repository.LogRepository;
import eu.thelair.medusabackend.repository.ReportRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ReportController implements Controller {
  @Inject
  private RestAPI restAPI;
  @Inject
  private MedusaSQL sql;

  @Inject
  private ReportRepository repository;
  @Inject
  private LogRepository logRepository;

  private DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

  public void reportCount() {
    restAPI.app().post("/report/count", ctx -> {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("count", repository.countReports());
      ctx.json(jsonObject);
    });
  }

  public void reportById() {
    restAPI.app().post("/report", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String id = (String) request.get("id");
      Report report = repository.findById(id);
      ctx.json(replaceReportTime(report));
    });
  }

  public void randomReport() {
    restAPI.app().post("/report/random", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String username = (String) request.get("username");
      Report report = repository.findOpenReport();
      if (report == null) {
        JSONObject response = new JSONObject();
        response.put("reportId", null);
        ctx.json(response);
        return;
      }
      repository.asignReport(report.reportId(), username);

      Log log = new Log(report.reportId(), username, "Medusa", Timestamp.valueOf(LocalDateTime.now()), LogAction.ASSIGN_FROM, LogSystem.MEDUSA);
      logRepository.insert(log);

      ctx.json(replaceReportTime(report));
    });
  }

  public void finishReport() {
    restAPI.app().post("/report/finish", ctx -> {
      JSONObject request = (JSONObject) new JSONParser().parse(ctx.body());
      String reportId = (String) request.get("reportId");
      String system = (String) request.get("system");
      String reason = (String) request.get("reason");
      String username = (String) request.get("username");
      repository.finishReport(reportId, system, reason);
      JSONObject response = new JSONObject();
      response.put("success", true);

      Log log = new Log(reportId, username, "", Timestamp.valueOf(LocalDateTime.now()), LogAction.FINISH, LogSystem.MEDUSA);
      logRepository.insert(log);

      ctx.json(response);
    });
  }

  @Override
  public void register() {
    reportCount();
    reportById();
    randomReport();
    finishReport();
  }

  private JSONObject replaceReportTime(Report report) throws JsonProcessingException, ParseException {
    ObjectMapper objectMapper = new ObjectMapper();
    JSONParser parser = new JSONParser();
    JSONObject response = (JSONObject) parser.parse(objectMapper.writeValueAsString(report));
    long reportMillis = (long) response.get("reportTime");
    ZoneId zoneId = ZoneId.of("Europe/Berlin");
    LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(reportMillis), zoneId);
    response.put("reportTime", format.format(date));
    return response;
  }
}
