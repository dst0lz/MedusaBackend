package eu.thelair.medusabackend;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import eu.thelair.medusabackend.api.RestAPI;
import eu.thelair.medusabackend.database.MedusaSQL;
import eu.thelair.medusabackend.database.MySQL;
import eu.thelair.medusabackend.repository.UserRepository;
import eu.thelair.medusabackend.task.FetchTask;
import eu.thelair.medusabackend.utils.DateUtil;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class MedusaBackend {
  private Injector injector;

  @Inject
  private RestAPI restAPI;
  @Inject
  private MySQL mySQL;
  @Inject
  private MedusaSQL medusaSQL;
  @Inject
  private FetchTask fetchTask;

  public MedusaBackend() {
    System.out.println("Injecting classes ...");
    MedusaBackendModule medusaBackendModule = new MedusaBackendModule(this);
    this.injector = Guice.createInjector(medusaBackendModule);
    this.injector.injectMembers(this);
    System.out.println("Classes injected ...");

    System.out.println("Starting API ...");
    this.restAPI.start();
    System.out.println("API started");

    System.out.println("Database setup successfully");

    new Thread(fetchTask).start();

    System.out.println("MedusaBackend started successfully!");
  }

  public static void main(String[] args) {
    new MedusaBackend();
  }
}
