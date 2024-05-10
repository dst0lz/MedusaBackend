package eu.thelair.medusabackend.utils;

import java.util.UUID;

public class HashGenerator {
  private HashGenerator() {
    // Util class
  }

  public static String getRandomId(int length) {
    char[] chars = UUID.randomUUID().toString().replaceAll("-", "").toCharArray();
    String res = "";

    for (int i = 0; i < length; i++) {
      res += chars[(int) (Math.random() * chars.length)];
    }

    return res;
  }
}
