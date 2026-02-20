package com.ibscc.bible;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ibscc.bible.models.WeeklyEvent;
import com.ibscc.bible.services.FirebaseRESTService;

import java.util.ArrayList;
import java.util.List;

public class ProgramacaoFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvEvents;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    
    private FirebaseRESTService firebaseService;
    private EventsAdapter adapter;
    private List<WeeklyEvent> events = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_programacao, container, false);
        
        firebaseService = new FirebaseRESTService();
        
        initViews(view);
        loadEvents();
        
        return view;
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        rvEvents = view.findViewById(R.id.rv_events);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyState = view.findViewById(R.id.empty_state);

        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventsAdapter();
        rvEvents.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadEvents);
    }

    private void loadEvents() {
    if (!swipeRefresh.isRefreshing()) {
        progressBar.setVisibility(View.VISIBLE);
    }
    
    // ⭐ LOG DE DEBUG
    Toast.makeText(getContext(), "🔄 Carregando eventos...", Toast.LENGTH_SHORT).show();
    
    firebaseService.loadWeekEvents(new FirebaseRESTService.OnEventsLoadedListener() {
        @Override
        public void onEventsLoaded(List<WeeklyEvent> loadedEvents) {
            progressBar.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
            
            // ⭐ LOG DE SUCESSO
            Toast.makeText(getContext(), 
                "✅ " + loadedEvents.size() + " eventos carregados!", 
                Toast.LENGTH_LONG).show();
            
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
            swipeRefresh.setRefreshing(false);
            
            // ⭐ LOG DE ERRO
            if (getContext() != null) {
                Toast.makeText(getContext(), 
                    "❌ ERRO: " + error, 
                    Toast.LENGTH_LONG).show();
            }
        }
    });
}

    // ADAPTER
    class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
        
        private List<WeeklyEvent> eventsList = new ArrayList<>();

        public void updateEvents(List<WeeklyEvent> newEvents) {
            this.eventsList = newEvents;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WeeklyEvent event = eventsList.get(position);
            
            holder.tvIcon.setText(event.getIcon());
            holder.tvTitle.setText(event.getTitle());
            holder.tvDescription.setText(event.getDescription());
            holder.tvDay.setText(event.getDayName());
            holder.tvTime.setText(event.getFormattedTime());
            
            if (event.getLocation() != null && !event.getLocation().isEmpty()) {
                holder.tvLocation.setVisibility(View.VISIBLE);
                holder.tvLocation.setText("📍 " + event.getLocation());
            } else {
                holder.tvLocation.setVisibility(View.GONE);
            }

            // Diminuir opacidade se for evento passado
            float alpha = event.isPast() ? 0.5f : 1.0f;
            holder.itemView.setAlpha(alpha);
        }

        @Override
        public int getItemCount() {
            return eventsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvIcon, tvTitle, tvDescription, tvDay, tvTime, tvLocation;

            ViewHolder(View itemView) {
                super(itemView);
                tvIcon = itemView.findViewById(R.id.tv_event_icon);
                tvTitle = itemView.findViewById(R.id.tv_event_title);
                tvDescription = itemView.findViewById(R.id.tv_event_description);
                tvDay = itemView.findViewById(R.id.tv_event_day);
                tvTime = itemView.findViewById(R.id.tv_event_time);
                tvLocation = itemView.findViewById(R.id.tv_event_location);
            }
        }
    }
}