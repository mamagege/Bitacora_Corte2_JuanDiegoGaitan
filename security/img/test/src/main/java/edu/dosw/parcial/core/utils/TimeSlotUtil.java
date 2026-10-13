package edu.dosw.parcial.core.utils;

/**
 * Utilidad para el procesamiento y parsing de franjas horarias tipo "Martes 10:00 AM"
 * a día ("Martes") y hora en formato 24h (10).
 */
public final class TimeSlotUtil {

    private TimeSlotUtil() {}

    public static String extractDay(String timeSlot) {
        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            return "";
        }
        return timeSlot.trim().split("\\s+")[0];
    }

    public static int extractHour24(String timeSlot) {
        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            return -1;
        }
        String[] parts = timeSlot.trim().split("\\s+");
        if (parts.length < 3) {
            return -1;
        }
        String[] hourMinute = parts[1].split(":");
        int hour = Integer.parseInt(hourMinute[0]);
        String meridiem = parts[2].toUpperCase();

        if (meridiem.equals("PM") && hour != 12) {
            hour += 12;
        } else if (meridiem.equals("AM") && hour == 12) {
            hour = 0;
        }
        return hour;
    }
}
