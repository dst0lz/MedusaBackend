package eu.thelair.medusabackend.database;

import com.google.inject.Singleton;
import eu.thelair.api.database.HikariAdapter;

@Singleton
public final class MySQL extends HikariAdapter {

  public MySQL() {
    super("localhost", "3306", "medusapanel");
  }

  @Override
  public void createTables() {
    update("CREATE TABLE IF NOT EXISTS user(" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "username VARCHAR(20)," +
            "password TEXT," +
            "role VARCHAR(50))"
    );
    update("CREATE TABLE IF NOT EXISTS report(" +
            "report_id VARCHAR(36) PRIMARY KEY,"
            + "reported_uuid VARCHAR(36),"
            + "report_uuid VARCHAR(36),"
            + "reported_name VARCHAR(16),"
            + "report_name VARCHAR(16),"
            + "report_time TIMESTAMP,"
            + "reason TEXT,"
            + "replay TEXT,"
            + "server TEXT,"
            + "assigned_user TEXT,"
            + "selected_system TEXT,"
            + "selected_reason TEXT,"
            + "report_state TEXT)"
    );

    update("CREATE TABLE IF NOT EXISTS message ("
            + "message_id VARCHAR(36) PRIMARY KEY,"
            + "sender_uuid VARCHAR(36),"
            + "sender_name VARCHAR(16),"
            + "message_time TIMESTAMP,"
            + "server TEXT,"
            + "message TEXT,"
            + "assigned_user TEXT,"
            + "selected_system TEXT,"
            + "selected_reason TEXT,"
            + "report_state TEXT)"
    );

    update("""
            CREATE TABLE IF NOT EXISTS log(
            log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
            report_id VARCHAR(36),
            user VARCHAR(16),
            target TEXT,
            created_at TIMESTAMP,
            log_action TEXT,
            log_system TEXT);
            """
    );
  }

}
