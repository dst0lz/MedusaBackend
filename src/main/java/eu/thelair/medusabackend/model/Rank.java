package eu.thelair.medusabackend.model;

public enum Rank {
  ADMIN("#AA0000", "Administrator"),
  DEVELOPER("#00AAAA", "Developer"),
  SRCONTENT("#00AAAA", "Senior-Content"),
  CONTENT("#00AAAA", "Content"),
  SRGUARDIAN("#FF5555", "Senior-Guardian"),
  GURADIAN("#FF5555", "Guardian"),
  YOUTUBER("#AA00AA", "YouTuber"),
  VIP_PLUS("#FFFF55", "VIP+"),
  VIP("#55FF55", "VIP"),
  PREMIUM("#FFAA00", "Premium"),
  SPIELER("#5555FF", "Spieler");

  private String color;
  private String name;

  Rank(String color, String name) {
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
