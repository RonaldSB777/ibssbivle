package com.ibscc.bible.models;

import java.io.Serializable;
import java.util.Calendar;

public class WeeklyEvent implements Serializable {
    private String id;
    private String title;
    private String description;
    private long dateTimeMillis;
    private int hour;
    private int minute;
    private String type;
    private String location;

    public WeeklyEvent() {
    }

    public WeeklyEvent(String title, String description, long dateTimeMillis, 
                      int hour, int minute, String type, String location) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.title = title;
        this.description = description;
        this.dateTimeMillis = dateTimeMillis;
        this.hour = hour;
        this.minute = minute;
        this.type = type;
        this.location = location;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getDateTimeMillis() { return dateTimeMillis; }
    public void setDateTimeMillis(long dateTimeMillis) { this.dateTimeMillis = dateTimeMillis; }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getFormattedTime() {
        return String.format("%02d:%02d", hour, minute);
    }

    public String getDayName() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateTimeMillis);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        
        String[] days = {"Domingo", "Segunda-feira", "Terça-feira", 
                        "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"};
        return days[dayOfWeek - 1];
    }

    public String getFormattedDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateTimeMillis);
        return String.format("%02d/%02d/%d", 
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR));
    }

    public boolean isToday() {
        Calendar eventCal = Calendar.getInstance();
        eventCal.setTimeInMillis(dateTimeMillis);
        
        Calendar today = Calendar.getInstance();
        
        return eventCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
               eventCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    public boolean isPast() {
        Calendar eventCal = Calendar.getInstance();
        eventCal.setTimeInMillis(dateTimeMillis);
        
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        
        return eventCal.before(today);
    }

    public String getIcon() {
        if (type == null) return "📅";
        
        switch (type) {
            case "culto": return "⛪";
            case "estudo": return "📖";
            case "oracao": return "🙏";
            case "jovens": return "👥";
            case "especial": return "⭐";
            default: return "📅";
        }
    }
}