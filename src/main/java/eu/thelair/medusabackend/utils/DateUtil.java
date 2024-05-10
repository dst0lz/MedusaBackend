package eu.thelair.medusabackend.utils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class DateUtil {
  private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC");

  public static LocalDateTime startOfDay() {
    return LocalDateTime.now(DEFAULT_ZONE_ID).with(LocalTime.MIN);
  }

  public static LocalDateTime endOfDay() {
    return LocalDateTime.now(DEFAULT_ZONE_ID).with(LocalTime.MAX);
  }

  public static boolean belongsToCurrentDay(final LocalDateTime localDateTime) {
    return localDateTime.isAfter(startOfDay()) && localDateTime.isBefore(endOfDay());
  }

  //note that week starts with Monday
  public static LocalDateTime startOfWeek() {
    return LocalDateTime.now(DEFAULT_ZONE_ID)
            .with(LocalTime.MIN)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
  }

  //note that week ends with Sunday
  public static LocalDateTime endOfWeek() {
    return LocalDateTime.now(DEFAULT_ZONE_ID)
            .with(LocalTime.MAX)
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
  }

  public static boolean belongsToCurrentWeek(final LocalDateTime localDateTime) {
    return localDateTime.isAfter(startOfWeek()) && localDateTime.isBefore(endOfWeek());
  }

  public static LocalDateTime startOfMonth() {
    return LocalDateTime.now(DEFAULT_ZONE_ID)
            .with(LocalTime.MIN)
            .with(TemporalAdjusters.firstDayOfMonth());
  }

  public static LocalDateTime endOfMonth() {
    return LocalDateTime.now(DEFAULT_ZONE_ID)
            .with(LocalTime.MAX)
            .with(TemporalAdjusters.lastDayOfMonth());
  }

  public static boolean belongsToCurrentMonth(final LocalDateTime localDateTime) {
    return localDateTime.isAfter(startOfMonth()) && localDateTime.isBefore(endOfMonth());
  }

  public static long toMills(final LocalDateTime localDateTime) {
    return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
  }

  public static Date toDate(final LocalDateTime localDateTime) {
    return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
  }

  public static String toString(final LocalDateTime localDateTime) {
    return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
  }
}
