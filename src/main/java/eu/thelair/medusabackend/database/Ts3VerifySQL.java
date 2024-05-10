package eu.thelair.medusabackend.database;

import eu.thelair.api.database.HikariAdapter;

public class Ts3VerifySQL extends HikariAdapter {

  public Ts3VerifySQL() {
    super("localhost", "3306", "tsverify");
  }

  @Override
  public void createTables() {

  }
}
