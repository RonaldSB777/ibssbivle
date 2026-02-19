package com.ibscc.bible;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;
import java.util.Random;
import java.util.List;

public class DailyNotificationHelper extends BroadcastReceiver {

    private static final String CHANNEL_ID = "bible_daily_verse";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        showDailyVerse(context);
    }

    public static void setupDailyNotification(Context context) {
        createNotificationChannel(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DailyNotificationHelper.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Configurar para 8:00 AM todos os dias
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Se já passou das 8:00 hoje, configurar para amanhã
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Repetir diariamente
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Versículo do Dia";
            String description = "Notificação diária com um versículo bíblico";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 250, 250, 250});
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showDailyVerse(Context context) {
        BibleHelper bibleHelper = new BibleHelper(context);
        
        // Obter versículo aleatório
        List<String> livros = bibleHelper.getBookAbbreviations();
        if (livros.isEmpty()) return;

        Random random = new Random();
        String abrev = livros.get(random.nextInt(livros.size()));
        int totalCap = bibleHelper.getChapterCount(abrev);
        
        if (totalCap == 0) return;

        int cap = random.nextInt(totalCap) + 1;
        List<Verse> verses = bibleHelper.getChapter(abrev, cap);
        
        if (verses.isEmpty()) return;
        
        Verse verse = verses.get(random.nextInt(verses.size()));
        String livroNome = bibleHelper.getBookName(abrev);
        
        // Criar notificação
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_agenda)
                .setContentTitle("📖 Versículo do Dia")
                .setContentText(livroNome + " " + cap + ":" + verse.getNumber())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(verse.getText())
                        .setSummaryText(livroNome + " " + cap + ":" + verse.getNumber()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 250, 250, 250})
                .setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManager notificationManager = (NotificationManager) 
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    public static void cancelDailyNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        
        Intent intent = new Intent(context, DailyNotificationHelper.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}