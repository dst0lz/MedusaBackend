package eu.thelair.medusabackend.model;

import com.google.common.base.Objects;

public record User(long id, String name, String password, Role role) {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return id == user.id && Objects.equal(name, user.name) && role == user.role;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, name, role);
  }
}
