package eu.thelair.medusabackend.database;

import eu.thelair.api.database.HikariAdapter;

public class Ts3SQL extends HikariAdapter {

  public Ts3SQL() {
    super("116.202.31.224", "3306", "ts3bot");
  }

  @Override
  public void createTables() {

  }
}
