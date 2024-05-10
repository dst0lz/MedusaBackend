package eu.thelair.medusabackend.model.log;

import java.sql.Timestamp;

public record Log(String reportId, String user, String target, Timestamp createdAt,
                  LogAction action, LogSystem system) {

}
