package eu.thelair.medusabackend.task;

import com.google.inject.Inject;
import eu.thelair.medusabackend.model.Message;
import eu.thelair.medusabackend.model.Report;
import eu.thelair.medusabackend.repository.MessageRepository;
import eu.thelair.medusabackend.repository.ReportRepository;

import java.util.concurrent.TimeUnit;

public class FetchTask implements Runnable {

  @Inject
  private ReportRepository reportRepository;

  @Inject
  private MessageRepository messageRepository;

  @Override
  public void run() {
    while (true) {
      reportRepository.getTenMedusaReports().whenCompleteAsync(((reports, throwable) -> {
        for (Report report : reports) {
          reportRepository.insert(report);
          reportRepository.deleteMedusaReport(report.oldId());
        }
      }));

      messageRepository.getMessages().whenCompleteAsync(((messages, throwable) -> {
        for (Message message : messages) {
          messageRepository.insert(message);
          messageRepository.deleteMedusaReport(message.old_id());
        }
      }));


      try {
        Thread.sleep(TimeUnit.MINUTES.toMillis(5));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
