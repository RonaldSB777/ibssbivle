package com.ibscc.bible.services;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibscc.bible.models.WeeklyEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseRESTService {
    
    // ⚠️ SUBSTITUIR PELA SUA URL DO FIREBASE
    private static final String FIREBASE_URL = "https://ibssbivle-default-rtdb.firebaseio.com";
    private static final String EVENTS_PATH = "/programacao_semanal.json";
    
    private Gson gson;
    private ExecutorService executor;
    private Handler mainHandler;

    public FirebaseRESTService() {
        this.gson = new Gson();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface OnEventsLoadedListener {
        void onEventsLoaded(List<WeeklyEvent> events);
        void onError(String error);
    }

    public interface OnCompleteListener {
        void onSuccess();
        void onError(String error);
    }

    // BUSCAR EVENTOS
    public void loadEvents(OnEventsLoadedListener listener) {
        executor.execute(() -> {
            try {
                URL url = new URL(FIREBASE_URL + EVENTS_PATH);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                    );
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    List<WeeklyEvent> events = parseEvents(response.toString());
                    mainHandler.post(() -> listener.onEventsLoaded(events));
                } else {
                    mainHandler.post(() -> 
                        listener.onError("Erro HTTP: " + responseCode)
                    );
                }
                conn.disconnect();

            } catch (Exception e) {
                Log.e("FirebaseREST", "Erro", e);
                mainHandler.post(() -> listener.onError(e.getMessage()));
            }
        });
    }
// BUSCAR EVENTOS DA SEMANA (MODIFICADO - SEM FILTRO)
public void loadWeekEvents(OnEventsLoadedListener listener) {
    loadEvents(listener); // Carrega TODOS os eventos sem filtrar
}

    // BUSCAR EVENTOS DE HOJE
    public void loadTodayEvents(OnEventsLoadedListener listener) {
        loadEvents(new OnEventsLoadedListener() {
            @Override
            public void onEventsLoaded(List<WeeklyEvent> allEvents) {
                List<WeeklyEvent> todayEvents = new ArrayList<>();
                for (WeeklyEvent event : allEvents) {
                    if (event.isToday()) {
                        todayEvents.add(event);
                    }
                }
                listener.onEventsLoaded(todayEvents);
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
            }
        });
    }

    // ADICIONAR EVENTO
    public void addEvent(WeeklyEvent event, OnCompleteListener listener) {
        executor.execute(() -> {
            try {
                String eventId = String.valueOf(System.currentTimeMillis());
                event.setId(eventId);

                URL url = new URL(FIREBASE_URL + EVENTS_PATH);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PATCH");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                Map<String, WeeklyEvent> eventMap = new HashMap<>();
                eventMap.put(eventId, event);
                String jsonEvent = gson.toJson(eventMap);

                OutputStream os = conn.getOutputStream();
                os.write(jsonEvent.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                conn.disconnect();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    mainHandler.post(listener::onSuccess);
                } else {
                    mainHandler.post(() -> 
                        listener.onError("Erro: " + responseCode)
                    );
                }

            } catch (Exception e) {
                mainHandler.post(() -> listener.onError(e.getMessage()));
            }
        });
    }

    // DELETAR EVENTO
    public void deleteEvent(String eventId, OnCompleteListener listener) {
        executor.execute(() -> {
            try {
                URL url = new URL(FIREBASE_URL + "/programacao_semanal/" + eventId + ".json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

                int responseCode = conn.getResponseCode();
                conn.disconnect();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    mainHandler.post(listener::onSuccess);
                } else {
                    mainHandler.post(() -> 
                        listener.onError("Erro: " + responseCode)
                    );
                }

            } catch (Exception e) {
                mainHandler.post(() -> listener.onError(e.getMessage()));
            }
        });
    }

    // GERAR TEMPLATE PADRÃO
    public void generateWeeklyTemplate(OnCompleteListener listener) {
        executor.execute(() -> {
            try {
                Map<String, WeeklyEvent> eventsMap = new HashMap<>();
                
                Calendar now = Calendar.getInstance();
                Calendar startOfWeek = (Calendar) now.clone();
                startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                
                long baseTime = startOfWeek.getTimeInMillis();
                
                // Domingo manhã
                WeeklyEvent dom1 = new WeeklyEvent(
                    "Culto de Celebração",
                    "Culto dominical pela manhã",
                    baseTime,
                    9, 0, "culto", "Templo Principal"
                );
                dom1.setId("dom_manha");
                eventsMap.put(dom1.getId(), dom1);

                // Domingo noite
                WeeklyEvent dom2 = new WeeklyEvent(
                    "Culto da Noite",
                    "Culto dominical noturno",
                    baseTime,
                    19, 0, "culto", "Templo Principal"
                );
                dom2.setId("dom_noite");
                eventsMap.put(dom2.getId(), dom2);

                // Terça
                WeeklyEvent terca = new WeeklyEvent(
                    "Culto de Oração",
                    "Reunião de oração e intercessão",
                    baseTime + (2 * 24 * 60 * 60 * 1000L),
                    19, 30, "oracao", "Salão de Oração"
                );
                terca.setId("terca");
                eventsMap.put(terca.getId(), terca);

                // Quarta
                WeeklyEvent quarta = new WeeklyEvent(
                    "Estudo Bíblico",
                    "Estudo aprofundado da Palavra",
                    baseTime + (3 * 24 * 60 * 60 * 1000L),
                    19, 30, "estudo", "Sala de Estudos"
                );
                quarta.setId("quarta");
                eventsMap.put(quarta.getId(), quarta);

                // Sexta
                WeeklyEvent sexta = new WeeklyEvent(
                    "Culto de Jovens",
                    "Reunião da juventude",
                    baseTime + (5 * 24 * 60 * 60 * 1000L),
                    20, 0, "jovens", "Salão de Eventos"
                );
                sexta.setId("sexta");
                eventsMap.put(sexta.getId(), sexta);

                URL url = new URL(FIREBASE_URL + EVENTS_PATH);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String json = gson.toJson(eventsMap);

                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                conn.disconnect();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    mainHandler.post(listener::onSuccess);
                } else {
                    mainHandler.post(() -> 
                        listener.onError("Erro: " + responseCode)
                    );
                }

            } catch (Exception e) {
                mainHandler.post(() -> listener.onError(e.getMessage()));
            }
        });
    }

    // HELPERS
    private List<WeeklyEvent> parseEvents(String json) {
        List<WeeklyEvent> events = new ArrayList<>();

        if (json == null || json.equals("null") || json.trim().isEmpty()) {
            return events;
        }

        try {
            Type type = new TypeToken<Map<String, WeeklyEvent>>(){}.getType();
            Map<String, WeeklyEvent> eventsMap = gson.fromJson(json, type);

            if (eventsMap != null) {
                for (Map.Entry<String, WeeklyEvent> entry : eventsMap.entrySet()) {
                    WeeklyEvent event = entry.getValue();
                    if (event.getId() == null) {
                        event.setId(entry.getKey());
                    }
                    events.add(event);
                }
            }

            Collections.sort(events, new Comparator<WeeklyEvent>() {
                @Override
                public int compare(WeeklyEvent e1, WeeklyEvent e2) {
                    return Long.compare(e1.getDateTimeMillis(), e2.getDateTimeMillis());
                }
            });

        } catch (Exception e) {
            Log.e("FirebaseREST", "Erro ao parsear", e);
        }

        return events;
    }

    private List<WeeklyEvent> filterWeekEvents(List<WeeklyEvent> allEvents) {
        List<WeeklyEvent> weekEvents = new ArrayList<>();
        
        Calendar now = Calendar.getInstance();
        Calendar startOfWeek = (Calendar) now.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        startOfWeek.set(Calendar.HOUR_OF_DAY, 0);
        startOfWeek.set(Calendar.MINUTE, 0);
        
        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.add(Calendar.DAY_OF_YEAR, 6);
        endOfWeek.set(Calendar.HOUR_OF_DAY, 23);
        endOfWeek.set(Calendar.MINUTE, 59);
        
        for (WeeklyEvent event : allEvents) {
            Calendar eventCal = Calendar.getInstance();
            eventCal.setTimeInMillis(event.getDateTimeMillis());
            
            if (!eventCal.before(startOfWeek) && !eventCal.after(endOfWeek)) {
                weekEvents.add(event);
            }
        }
        
        return weekEvents;
    }
}