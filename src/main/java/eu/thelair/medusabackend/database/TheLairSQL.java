package eu.thelair.medusabackend.database;

import eu.thelair.api.database.HikariAdapter;

public class TheLairSQL extends HikariAdapter {

  public TheLairSQL() {
    super("localhost", "3306", "thelair");
  }

  @Override
  public void createTables() {

  }
}
