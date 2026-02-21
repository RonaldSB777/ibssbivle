package com.ibscc.bible;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibscc.bible.models.WeeklyEvent;
import com.ibscc.bible.services.FirebaseRESTService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminProgramacaoActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private Button btnAddEvent;
    
    private FirebaseRESTService firebaseService;
    private AdminEventsAdapter adapter;
    private List<WeeklyEvent> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_programacao);

        firebaseService = new FirebaseRESTService();
        
        initViews();
        loadEvents();
    }

    private void initViews() {
        rvEvents = findViewById(R.id.rv_admin_events);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        btnAddEvent = findViewById(R.id.btn_add_event);

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminEventsAdapter();
        rvEvents.setAdapter(adapter);

        btnAddEvent.setOnClickListener(v -> showAddEventDialog());

        // Botão voltar
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadEvents() {
        progressBar.setVisibility(View.VISIBLE);
        
        firebaseService.loadEvents(new FirebaseRESTService.OnEventsLoadedListener() {
            @Override
            public void onEventsLoaded(List<WeeklyEvent> loadedEvents) {
                progressBar.setVisibility(View.GONE);
                
                events = loadedEvents;
                adapter.updateEvents(events);
                
                if (events.isEmpty()) {
                    rvEvents.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                } else {
                    rvEvents.setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminProgramacaoActivity.this, 
                    "Erro: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEventDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null);
        
        EditText etTitle = dialogView.findViewById(R.id.et_title);
        EditText etDescription = dialogView.findViewById(R.id.et_description);
        EditText etLocation = dialogView.findViewById(R.id.et_location);
        Spinner spinnerType = dialogView.findViewById(R.id.spinner_type);
        Button btnSelectDate = dialogView.findViewById(R.id.btn_select_date);
        Button btnSelectTime = dialogView.findViewById(R.id.btn_select_time);

        // Spinner de tipos
        String[] types = {"Culto", "Estudo Bíblico", "Oração", "Jovens", "Especial"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, types
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        final Calendar selectedDateTime = Calendar.getInstance();
        final int[] selectedHour = {19};
        final int[] selectedMinute = {0};

        btnSelectDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, day);
                btnSelectDate.setText(String.format("%02d/%02d/%d", day, month + 1, year));
            }, 
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH),
            selectedDateTime.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        btnSelectTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hour, minute) -> {
                selectedHour[0] = hour;
                selectedMinute[0] = minute;
                btnSelectTime.setText(String.format("%02d:%02d", hour, minute));
            },
            selectedHour[0],
            selectedMinute[0],
            true
            ).show();
        });

        new AlertDialog.Builder(this)
            .setTitle("➕ Adicionar Evento")
            .setView(dialogView)
            .setPositiveButton("Salvar", (dialog, which) -> {
                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                String location = etLocation.getText().toString();
                String typeText = spinnerType.getSelectedItem().toString();
                
                if (title.isEmpty()) {
                    Toast.makeText(this, "Preencha o título!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String type = convertType(typeText);

                WeeklyEvent event = new WeeklyEvent(
                    title, 
                    description,
                    selectedDateTime.getTimeInMillis(),
                    selectedHour[0],
                    selectedMinute[0],
                    type,
                    location
                );

                saveEvent(event);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private String convertType(String typeText) {
        switch (typeText) {
            case "Culto": return "culto";
            case "Estudo Bíblico": return "estudo";
            case "Oração": return "oracao";
            case "Jovens": return "jovens";
            case "Especial": return "especial";
            default: return "culto";
        }
    }

    private void saveEvent(WeeklyEvent event) {
        progressBar.setVisibility(View.VISIBLE);
        
        firebaseService.addEvent(event, new FirebaseRESTService.OnCompleteListener() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminProgramacaoActivity.this, 
                    "✅ Evento adicionado com sucesso!", 
                    Toast.LENGTH_SHORT).show();
                loadEvents();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminProgramacaoActivity.this, 
                    "❌ Erro ao adicionar: " + error, 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteEvent(WeeklyEvent event) {
        new AlertDialog.Builder(this)
            .setTitle("🗑️ Remover Evento")
            .setMessage("Deseja remover \"" + event.getTitle() + "\"?")
            .setPositiveButton("Remover", (dialog, which) -> deleteEvent(event))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteEvent(WeeklyEvent event) {
        progressBar.setVisibility(View.VISIBLE);
        
        firebaseService.deleteEvent(event.getId(), new FirebaseRESTService.OnCompleteListener() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminProgramacaoActivity.this, 
                    "✅ Evento removido!", 
                    Toast.LENGTH_SHORT).show();
                loadEvents();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminProgramacaoActivity.this, 
                    "❌ Erro ao remover: " + error, 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ADAPTER
    class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.ViewHolder> {
        
        private List<WeeklyEvent> eventsList = new ArrayList<>();

        public void updateEvents(List<WeeklyEvent> newEvents) {
            this.eventsList = newEvents;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_event, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WeeklyEvent event = eventsList.get(position);
            
            holder.tvTitle.setText(event.getTitle());
            holder.tvDescription.setText(event.getDescription());
            holder.tvDateTime.setText(event.getDayName() + " • " + event.getFormattedTime());
            holder.tvLocation.setText("📍 " + (event.getLocation() != null ? event.getLocation() : "Sem local"));
            
            holder.btnDelete.setOnClickListener(v -> confirmDeleteEvent(event));
        }

        @Override
        public int getItemCount() {
            return eventsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDescription, tvDateTime, tvLocation;
            ImageButton btnDelete;

            ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_event_title);
                tvDescription = itemView.findViewById(R.id.tv_event_description);
                tvDateTime = itemView.findViewById(R.id.tv_event_datetime);
                tvLocation = itemView.findViewById(R.id.tv_event_location);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }
        }
    }
}