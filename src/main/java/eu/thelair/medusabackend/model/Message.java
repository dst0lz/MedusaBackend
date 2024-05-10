package eu.thelair.medusabackend.model;

import java.sql.Timestamp;

public record Message(String messageId, String senderUUID, String senderName,
                      Timestamp messageTime, String server, String message,
                      String assignedUser, String selectedSystem, String selectedReason,
                      ReportState reportState, String rank, long old_id) {
}
