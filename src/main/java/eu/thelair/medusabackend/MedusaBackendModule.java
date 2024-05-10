package eu.thelair.medusabackend;

import com.google.inject.AbstractModule;

public class MedusaBackendModule extends AbstractModule {
  private final MedusaBackend medusaBackend;

  public MedusaBackendModule(MedusaBackend medusaBackend) {
    this.medusaBackend = medusaBackend;
  }

  @Override
  protected void configure() {
    bind(MedusaBackend.class).toInstance(medusaBackend);
  }
}
