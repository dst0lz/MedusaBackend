package eu.thelair.medusabackend.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.thelair.medusabackend.api.RestAPI;
import eu.thelair.medusabackend.model.ProxyResponse;
import eu.thelair.medusabackend.repository.MessageRepository;
import eu.thelair.medusabackend.repository.ReportRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Singleton
public class ProxyController implements Controller {
  @Inject
  private RestAPI restAPI;

  @Inject
  private MessageRepository messageRepository;

  @Inject
  private ReportRepository reportRepository;

  public void proxy() {
    ObjectMapper objectMapper = new ObjectMapper();
    restAPI.app().post("/proxy", ctx -> {
      JSONArray repsonse = new JSONArray();
      for (ProxyResponse proxyResponse : messageRepository.getProxyResponses()) {
        JSONObject object = new JSONObject();
        object.put("reported", proxyResponse.reported());
        object.put("editor", proxyResponse.editor());
        object.put("reportId", proxyResponse.reportId());
        object.put("system", proxyResponse.system());
        object.put("reason", proxyResponse.reason());
        repsonse.add(object);
      }

      for (ProxyResponse proxyResponse : reportRepository.getProxyResponses()) {
        JSONObject object = new JSONObject();
        object.put("reported", proxyResponse.reported());
        object.put("editor", proxyResponse.editor());
        object.put("reportId", proxyResponse.reportId());
        object.put("system", proxyResponse.system());
        object.put("reason", proxyResponse.reason());
        repsonse.add(object);
      }

      ctx.json(repsonse);

      messageRepository.getProxyResponses().clear();
      reportRepository.getProxyResponses().clear();
    });
  }

  @Override
  public void register() {
    proxy();
  }
}
