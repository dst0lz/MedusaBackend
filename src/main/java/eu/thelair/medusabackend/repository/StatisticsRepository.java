package eu.thelair.medusabackend.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.thelair.medusabackend.database.MySQL;
import eu.thelair.medusabackend.database.Ts3SQL;
import eu.thelair.medusabackend.database.Ts3VerifySQL;
import eu.thelair.medusabackend.model.ReportState;
import eu.thelair.medusabackend.model.log.LogAction;
import eu.thelair.medusabackend.model.log.LogSystem;
import eu.thelair.medusabackend.utils.DateUtil;
import eu.thelair.medusabackend.utils.uuidfetcher.UUIDFetcher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class StatisticsRepository {
  @Inject
  private MySQL mySQL;

  @Inject
  private Ts3SQL ts3SQL;

  @Inject
  private Ts3VerifySQL ts3VerifySQL;

  public int findLastWeek(String username, LogSystem logSystem) {
    String qry = "SELECT count(*) as count FROM log WHERE user = ? AND log_system = ? AND log_action = ? AND created_at BETWEEN ? AND ?";
    try (ResultSet rs = mySQL.query(qry,
            username,
            logSystem.name(),
            LogAction.FINISH.name(),
            Timestamp.valueOf(DateUtil.startOfWeek()),
            Timestamp.valueOf(DateUtil.endOfWeek()))) {
      if (rs.next()) {
        return rs.getInt("count");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public int findAllTime(String username) {
    String qry = "SELECT count(*) as count FROM report WHERE assigned_user = ? AND report_state = ?";
    try (ResultSet rs = mySQL.query(qry, username, ReportState.CLOSED.name())) {
      if (rs.next()) {
        return rs.getInt("count");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public int findAllTimePoseidon(String username) {
    String qry = "SELECT count(*) as count FROM message WHERE assigned_user = ? AND report_state = ?";
    try (ResultSet rs = mySQL.query(qry, username, ReportState.CLOSED.name())) {
      if (rs.next()) {
        return rs.getInt("count");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public String findTeamspeakTimeByName(String name) {
    String tsId = findTeamspeakIdByName(name);
    if (tsId.equals("")) return "Error";
    String qry = "SELECT talkTime FROM client WHERE uniqueId = ?";
    try (ResultSet rs = ts3SQL.query(qry, tsId)) {
      if (rs.next()) {
        return String.valueOf(TimeUnit.MILLISECONDS.toHours(rs.getLong("talkTime")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "Error";
  }

  public String findTeamspeakIdByName(String name) {
    UUID uuid = UUIDFetcher.getUUID(name);
    String qry = "SELECT id FROM teamSpeak WHERE uuid = ?";
    try (ResultSet rs = ts3VerifySQL.query(qry, uuid.toString())) {
      if (rs.next()) {
        return rs.getString("id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "";
  }
}
