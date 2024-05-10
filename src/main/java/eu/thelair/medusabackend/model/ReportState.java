package eu.thelair.medusabackend.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportState {
  OPEN("Nicht zugewiesen"),
  ASSIGNED("Zugewiesen"),
  CLOSED("Geschlossen");

  String name;

  ReportState(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }
}
