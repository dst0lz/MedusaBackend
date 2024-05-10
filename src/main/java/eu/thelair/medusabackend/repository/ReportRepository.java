package eu.thelair.medusabackend.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.thelair.medusabackend.database.LuckPermsSQL;
import eu.thelair.medusabackend.database.MedusaSQL;
import eu.thelair.medusabackend.database.MySQL;
import eu.thelair.medusabackend.database.TheLairSQL;
import eu.thelair.medusabackend.model.ProxyResponse;
import eu.thelair.medusabackend.model.Report;
import eu.thelair.medusabackend.model.ReportState;
import eu.thelair.medusabackend.utils.HashGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
public class ReportRepository {
  @Inject
  MySQL mySQL;
  @Inject
  MedusaSQL medusaSQL;
  @Inject
  LuckPermsSQL luckPermsSQL;
  @Inject
  TheLairSQL theLairSQL;

  private List<ProxyResponse> proxyResponses = new ArrayList<>();

  public void insert(Report report) {
    String qry = "INSERT INTO report VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    mySQL.update(qry,
            report.reportId(),
            report.reportedUUID(),
            report.reportUUID(),
            report.reportedName(),
            report.reportName(),
            report.reportTime(),
            report.reason(),
            report.replay(),
            report.server(),
            report.assignedUser(),
            report.selectedSystem(),
            report.selectedReason(),
            report.reportState().name());
  }

  public int countReports() {
    String qry = "SELECT count(*) AS count FROM report WHERE report_state = ?";
    try (ResultSet rs = mySQL.query(qry, ReportState.OPEN.name())) {
      if (rs.next()) {
        return rs.getInt("count");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;

  }

  public Report findById(String id) {
    String qry = "SELECT * FROM report WHERE report_id = ?";
    try (ResultSet rs = mySQL.query(qry, id)) {
      if (rs.next()) {
        String reportId = rs.getString("report_id");
        String reportedUUID = rs.getString("reported_uuid");
        String reportUUID = rs.getString("report_uuid");
        String reportedName = rs.getString("reported_name");
        String reportName = rs.getString("report_name");
        Timestamp reportTime = rs.getTimestamp("report_time");
        String reason = rs.getString("reason");
        String replay = rs.getString("replay");
        String server = rs.getString("server");
        String assignedUser = rs.getString("assigned_user");
        String selectedSystem = rs.getString("selected_system");
        String selected_reason = rs.getString("selected_reason");
        ReportState reportState = ReportState.valueOf(rs.getString("report_state"));
        String rank = getRankByUUID(reportedUUID);
        String onlineState = getOnlineState(reportedUUID);
        return new Report(reportId, reportedUUID, reportUUID, reportedName, reportName, reportTime, reason, replay, server, assignedUser, selectedSystem, selected_reason, reportState, rank, onlineState, 0);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public CompletableFuture<List<Report>> getTenMedusaReports() {
    String qry = "SELECT * FROM report LIMIT 10";
    List<Report> reports = new ArrayList<>();
    return CompletableFuture.supplyAsync(() -> {
      try (ResultSet rs = medusaSQL.query(qry)) {
        while (rs.next()) {
          String reportId = HashGenerator.getRandomId(12);
          String reportedUUID = rs.getString("reported_user_uuid");
          String reportUUID = rs.getString("report_user_uuid");
          String reportedName = rs.getString("reported_user_name");
          String reportName = rs.getString("report_user_name");
          Timestamp reportTime = rs.getTimestamp("report_time");
          String reason = rs.getString("reason");
          String replay = rs.getString("replay");
          String server = rs.getString("server");
          String rank = getRankByUUID(reportedUUID);
          String onlineState = getOnlineState(reportedUUID);
          long oldId = rs.getLong("report_id");
          Report report = new Report(reportId, reportedUUID, reportUUID, reportedName, reportName, reportTime, reason, replay, server, "", "", "", ReportState.OPEN, rank, onlineState, oldId);
          reports.add(report);
        }
        return reports;
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return reports;
    });
  }

  public void deleteMedusaReport(long id) {
    String delete = "DELETE FROM report WHERE report_id = ?";
    medusaSQL.update(delete, id);
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

  public String getOnlineState(String uuid) {
    String qry = "SELECT online_state FROM thelairplayer WHERE uuid = ?";
    try (ResultSet rs = theLairSQL.query(qry, uuid)) {
      if (rs.next()) {
        return rs.getString("online_state");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "Nicht abrufbar";
  }

  public Report findOpenReport() {
    String qry = "SELECT report_id FROM report WHERE report_state=? ORDER BY rand() LIMIT 1";
    try (ResultSet rs = mySQL.query(qry, ReportState.OPEN.name())) {
      if (rs.next()) {
        String id = rs.getString("report_id");
        return findById(id);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void asignReport(String reportId, String username) {
    String update = "UPDATE report SET assigned_user = ?, report_state = ? WHERE report_id = ?";
    mySQL.update(update, username, ReportState.ASSIGNED.name(), reportId);
  }

  public void finishReport(String reportId, String system, String reason) {
    String update = "UPDATE report SET selected_system = ?, selected_reason = ?, report_state = ? WHERE report_id = ?";
    mySQL.update(update, system, reason, ReportState.CLOSED.name(), reportId);
    addProxyResponse(reportId, system, reason);
  }

  public void addProxyResponse(String reportId, String system, String reason) {
    String qry = "SELECT reported_name, assigned_user FROM report WHERE report_id = ?";
    try (ResultSet rs = mySQL.query(qry, reportId)) {
      if (rs.next()) {
        String reported = rs.getString("reported_name");
        String editor = rs.getString("assigned_user");
        ProxyResponse response = new ProxyResponse(reported, editor, reportId, system, reason);
        proxyResponses.add(response);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<ProxyResponse> getProxyResponses() {
    return proxyResponses;
  }
}
