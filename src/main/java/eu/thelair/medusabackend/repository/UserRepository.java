package eu.thelair.medusabackend.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.thelair.medusabackend.database.MySQL;
import eu.thelair.medusabackend.model.Role;
import eu.thelair.medusabackend.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Singleton
public class UserRepository {
  @Inject
  private MySQL mySQL;

  private final Cache<String, User> userCache = CacheBuilder
          .newBuilder()
          .expireAfterWrite(Duration.of(1, ChronoUnit.HOURS))
          .build();

  public User findById(long id) {
    String query = "SELECT * FROM user WHERE id=?";
    try (ResultSet rs = mySQL.query(query, id)) {
      if (rs.next()) {
        User user = new User(rs.getLong("id"), rs.getString("username"), rs.getString("password"), Role.valueOf(rs.getString("role")));
        return user;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Optional<User> findByUsername(String name) {
    String query = "SELECT * FROM user WHERE username=?";
    try (ResultSet rs = mySQL.query(query, name)) {
      if (rs.next()) {
        User user = new User(rs.getLong("id"), rs.getString("username"), rs.getString("password"), Role.valueOf(rs.getString("role")));
        return Optional.of(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public void insert(User user) {
    String qry = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
    mySQL.update(qry, user.name(), user.password(), user.role().name());
  }

  public void addUser(User user) {
    userCache.put(user.name(), user);
  }

  public List<User> getLoggedInUsers() {
    List<User> users = new ArrayList<>(userCache.asMap().values());
    users.sort(Comparator.comparingInt(o -> o.role().ordinal()));
    return users;
  }
}
