package eu.thelair.medusabackend.model;

import java.sql.Timestamp;

public record Report(String reportId, String reportedUUID,
                     String reportUUID, String reportedName,
                     String reportName, Timestamp reportTime,
                     String reason, String replay,
                     String server, String assignedUser,
                     String selectedSystem, String selectedReason,
                     ReportState reportState, String rank, String onlineState, long oldId) {

}
