package eu.thelair.medusabackend.database;

import eu.thelair.api.database.HikariAdapter;

public class ChatLogSQL extends HikariAdapter {

  public ChatLogSQL() {
    super("localhost", "3306", "chatlog");
  }

  @Override
  public void createTables() {

  }
}
