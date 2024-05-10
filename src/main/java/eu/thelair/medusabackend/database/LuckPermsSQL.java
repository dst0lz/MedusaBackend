package eu.thelair.medusabackend.database;

import eu.thelair.api.database.HikariAdapter;

public class LuckPermsSQL extends HikariAdapter {

  public LuckPermsSQL() {
    super("localhost", "3306", "luckperms");
  }


  @Override
  public void createTables() {

  }
}
