package eu.thelair.medusabackend.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.thelair.medusabackend.database.*;
import eu.thelair.medusabackend.model.Message;
import eu.thelair.medusabackend.model.ProxyResponse;
import eu.thelair.medusabackend.model.ReportState;
import eu.thelair.medusabackend.utils.HashGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
public class MessageRepository {
  @Inject
  MySQL mySQL;
  @Inject
  MedusaSQL medusaSQL;
  @Inject
  LuckPermsSQL luckPermsSQL;
  @Inject
  TheLairSQL theLairSQL;
  @Inject
  ChatLogSQL chatLogSQL;

  private List<ProxyResponse> proxyResponses = new ArrayList<>();

  public void insert(Message message) {
    String qry = "INSERT INTO message VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    mySQL.update(qry,
            message.messageId(),
            message.senderUUID(),
            message.senderName(),
            message.messageTime(),
            message.server(),
            message.message(),
            message.assignedUser(),
            message.selectedSystem(),
            message.selectedReason(),
            message.reportState().name()
    );
  }

  public int countMessages() {
    String qry = "SELECT count(*) AS count FROM message WHERE report_state = ?";
    try (ResultSet rs = mySQL.query(qry, ReportState.OPEN.name())) {
      if (rs.next()) {
        return rs.getInt("count");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;

  }

  public Message findById(String id) {
    String qry = "SELECT * FROM message WHERE message_id = ?";
    try (ResultSet rs = mySQL.query(qry, id)) {
      if (rs.next()) {
        String messageId = rs.getString("message_id");
        String senderUUID = rs.getString("sender_uuid");
        String senderName = rs.getString("sender_name");
        Timestamp messageTime = rs.getTimestamp("message_time");
        String server = rs.getString("server");
        String message = rs.getString("message");
        String assignedUser = rs.getString("assigned_user");
        String selectedSystem = rs.getString("selected_system");
        String selectedReason = rs.getString("selected_reason");
        ReportState reportState = ReportState.valueOf(rs.getString("report_state"));
        String rank = getRankByUUID(senderUUID);
        return new Message(messageId, senderUUID, senderName, messageTime, server, message, assignedUser, selectedSystem, selectedReason, reportState, rank, 0);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public CompletableFuture<List<Message>> getMessages() {
    String qry = "SELECT * FROM message LIMIT 30";
    List<Message> messages = new ArrayList<>();
    return CompletableFuture.supplyAsync(() -> {
      try (ResultSet rs = medusaSQL.query(qry)) {
        while (rs.next()) {
          String messageId = HashGenerator.getRandomId(24);
          String senderUUID = rs.getString("sender_user_uuid");
          String senderName = rs.getString("sender_user_name");
          Timestamp messageTime = rs.getTimestamp("message_time");
          String server = rs.getString("server");
          String messageString = rs.getString("message");
          String rank = getRankByUUID(senderUUID);
          long oldId = rs.getLong("message_id");
          Message message = new Message(messageId, senderUUID, senderName, messageTime, server, messageString, "", "", "", ReportState.OPEN, rank, oldId);
          messages.add(message);
        }
        return messages;
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return messages;
    });
  }

  public String getRankByUUID(String uuid) {
    String qry = "SELECT primary_group FROM luckperms_players WHERE uuid = ?";
    try (ResultSet rs = luckPermsSQL.query(qry, uuid)) {
      if (rs.next()) {
        String group = rs.getString("primary_group");
        if (group.equals("default")) group = "Spieler";
        return group.substring(0, 1).toUpperCase() + group.substring(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "Spieler";
  }

  public Message findOpenMessage() {
    String qry = "SELECT message_id FROM message WHERE report_state=? ORDER BY rand() LIMIT 1";
    try (ResultSet rs = mySQL.query(qry, ReportState.OPEN.name())) {
      if (rs.next()) {
        String id = rs.getString("message_id");
        return findById(id);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void deleteMedusaReport(long id) {
    String delete = "DELETE FROM message WHERE message_id = ?";
    medusaSQL.update(delete, id);
  }

  public void assignMessage(String messageId, String username) {
    String update = "UPDATE message SET assigned_user = ?, report_state = ? WHERE message_id = ?";
    mySQL.update(update, username, ReportState.ASSIGNED.name(), messageId);
  }

  public void finishMessage(String messageId, String system, String reason) {
    String update = "UPDATE message SET selected_system = ?, selected_reason = ?, report_state = ? WHERE message_id = ?";
    mySQL.update(update, system, reason, ReportState.CLOSED.name(), messageId);
    addProxyResponse(messageId, system, reason);
  }

  public void addProxyResponse(String messageId, String system, String reason) {
    String qry = "SELECT sender_name, assigned_user FROM message WHERE message_id=?";
    try (ResultSet rs = mySQL.query(qry, messageId)) {
      if (rs.next()) {
        String reported = rs.getString("sender_name");
        String editor = rs.getString("assigned_user");
        ProxyResponse response = new ProxyResponse(reported, editor, messageId, system, reason);
        proxyResponses.add(response);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<ProxyResponse> getProxyResponses() {
    return proxyResponses;
  }

  public void fetchMessages() {
    String qry = "SELECT server, name, message, timestamp FROM messages ORDER BY RAND() LIMIT 2000";
    try (ResultSet rs = chatLogSQL.query(qry)) {
      while (rs.next()) {
        String messageId = HashGenerator.getRandomId(24);
        String name = rs.getString("name");
        String server = rs.getString("server");
        String messageString = rs.getString("message");
        long millis = rs.getLong("timestamp");
        Timestamp timestamp = Timestamp.from(Instant.ofEpochMilli(millis));
        Message message = new Message(messageId, UUID.randomUUID().toString(), name, timestamp, server, messageString, "", "", "", ReportState.OPEN, getRankByName(name), 0);
        insert(message);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public String getRankByName(String name) {
    String qry = "SELECT primary_group FROM luckperms_players WHERE username = ?";
    try (ResultSet rs = luckPermsSQL.query(qry, name)) {
      if (rs.next()) {
        String group = rs.getString("primary_group");
        if (group.equals("default")) group = "Spieler";
        return group.substring(0, 1).toUpperCase() + group.substring(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "Spieler";
  }

}
