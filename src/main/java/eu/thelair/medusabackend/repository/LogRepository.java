package eu.thelair.medusabackend.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.thelair.medusabackend.database.MySQL;
import eu.thelair.medusabackend.model.log.Log;

@Singleton
public class LogRepository {
  @Inject
  MySQL mySQL;

  public void insert(Log log) {
    String update = "INSERT INTO log (report_id, user, target, created_at, log_action, log_system) VALUES (?, ?, ?, ?, ?, ?)";
    mySQL.update(update,
            log.reportId(),
            log.user(),
            log.target(),
            log.createdAt(),
            log.action().name(),
            log.system().name()
    );
  }
}
