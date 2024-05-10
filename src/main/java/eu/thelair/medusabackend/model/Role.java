package eu.thelair.medusabackend.model;

public enum Role {

  ADMIN("#AA0000", "Administrator"),
  DEVELOPER("#00AAAA", "Developer"),
  SRCONTENT("#00AAAA", "Senior-Content"),
  CONTENT("#00AAAA", "Content"),
  SRGUARDIAN("#FF5555", "Senior-Guardian"),
  GUARDIAN("#FF5555", "Guardian");

  private String color;
  private String name;

  Role(String color, String name) {
    this.color = color;
    this.name = name;
  }

  public static Role getById(int id) {
    for (Role role : Role.values()) {
      if (role.ordinal() == id) {
        return role;
      }
    }
    return null;
  }

  public String getColor() {
    return color;
  }

  public String getName() {
    return name;
  }
}
