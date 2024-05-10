package eu.thelair.medusabackend.database;

import eu.thelair.api.database.HikariAdapter;

public class MedusaSQL extends HikariAdapter {

  public MedusaSQL() {
    super("localhost", "3306", "medusa");
  }

  @Override
  public void createTables() {

  }
}
