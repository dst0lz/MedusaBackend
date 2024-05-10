package eu.thelair.medusabackend.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.thelair.medusabackend.api.controller.*;
import eu.thelair.medusabackend.jwt.JWTHandler;
import eu.thelair.medusabackend.repository.UserRepository;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;

@Singleton
public class RestAPI {
  private Javalin app;
  @Inject
  private UserRepository userRepository;
  @Inject
  private JWTHandler jwtHandler;

  @Inject
  private AuthController authController;
  @Inject
  private ReportController reportController;
  @Inject
  private TeamListController teamListController;
  @Inject
  private MessageController messageController;
  @Inject
  private ProxyController proxyController;
  @Inject
  private StatisticsController statisticsController;

  public void start() {
    app = Javalin.create(javalinConfig -> {
      javalinConfig.defaultContentType = "application/json";
      javalinConfig.enableCorsForAllOrigins();
    });
    app.start(8888);
    app._conf.accessManager((handler, ctx, permittedRoles) -> {

      if (ctx.path().contains("login")) {
        handler.handle(ctx);
        return;
      }
      if (ctx.path().contains("proxy")) {
        if (ctx.ip().equals("193.187.255.86")) {
          handler.handle(ctx);
          return;
        }
      }
      String token = ctx.req.getHeader("Authorization");
      if (token != null) {
        if (!jwtHandler.validateToken(token)) {
          throw new UnauthorizedResponse("Token expired!");
        }
        handler.handle(ctx);
      } else {
        throw new UnauthorizedResponse("Not authenticated!");
      }
    });
    this.registerControllers();
  }

  private void registerControllers() {
    this.authController.register();
    this.reportController.register();
    this.teamListController.register();
    this.messageController.register();
    this.proxyController.register();
    this.statisticsController.register();
  }

  public void stop() {
    app.stop();
  }

  public Javalin app() {
    return app;
  }
}
