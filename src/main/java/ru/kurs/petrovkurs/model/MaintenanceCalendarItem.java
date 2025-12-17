package ru.kurs.petrovkurs.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MaintenanceCalendarItem {
    private LocalDate date;
    private int overdueCount;
    private int todayCount;
    private int tomorrowCount;
    private int dayAfterTomorrowCount;
    private int futureCount;

    public MaintenanceCalendarItem(LocalDate date) {
        this.date = date;
    }

    public int getTotalCount() {
        return overdueCount + todayCount + tomorrowCount + dayAfterTomorrowCount + futureCount;
    }

    public String getColor() {
        if (overdueCount > 0) return "#ff5252";
        if (todayCount > 0) return "#ffeb3b";
        if (tomorrowCount > 0) return "#29b6f6";
        if (dayAfterTomorrowCount > 0) return "#66bb6a";
        if (futureCount > 0) return "#9fa8da";
        return "#f0f0f0";
    }

    public String getTooltipText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"));
        StringBuilder sb = new StringBuilder(date.format(formatter));

        if (getTotalCount() > 0) {
            sb.append("\n\nТехническое обслуживание:\n");
            if (overdueCount > 0) sb.append("• Просрочено: ").append(overdueCount).append("\n");
            if (todayCount > 0) sb.append("• Сегодня: ").append(todayCount).append("\n");
            if (tomorrowCount > 0) sb.append("• Завтра: ").append(tomorrowCount).append("\n");
            if (dayAfterTomorrowCount > 0) sb.append("• Послезавтра: ").append(dayAfterTomorrowCount).append("\n");
            if (futureCount > 0) sb.append("• Будущие: ").append(futureCount).append("\n");
        } else {
            sb.append("\n\nНет запланированного ТО");
        }

        return sb.toString();
    }

    // Геттеры и сеттеры
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getOverdueCount() { return overdueCount; }
    public void setOverdueCount(int overdueCount) { this.overdueCount = overdueCount; }

    public int getTodayCount() { return todayCount; }
    public void setTodayCount(int todayCount) { this.todayCount = todayCount; }

    public int getTomorrowCount() { return tomorrowCount; }
    public void setTomorrowCount(int tomorrowCount) { this.tomorrowCount = tomorrowCount; }

    public int getDayAfterTomorrowCount() { return dayAfterTomorrowCount; }
    public void setDayAfterTomorrowCount(int dayAfterTomorrowCount) { this.dayAfterTomorrowCount = dayAfterTomorrowCount; }

    public int getFutureCount() { return futureCount; }
    public void setFutureCount(int futureCount) { this.futureCount = futureCount; }
}