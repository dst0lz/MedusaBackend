package eu.thelair.medusabackend.model;

public record ProxyResponse(String reported, String editor,
                             String reportId, String system, String reason) {
}
