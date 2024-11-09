package com.example.aplication.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aplication.R;
import com.example.aplication.models.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private Context context;
    private List<Application> applicationList;
    private String userRole;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public ApplicationAdapter(Context context, List<Application> applicationList, String userRole) {
        this.context = context;
        this.applicationList = applicationList;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_card, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = applicationList.get(position);
        holder.jobTitleText.setText("Empleo: " + application.getJobTitle());
        holder.statusText.setText("Estado: " + application.getStatus());
        holder.applicationDate.setText("Fecha de aplicación: " + application.getApplicationDate());

        if ("Empresa".equals(userRole)) {
            holder.companyText.setText("Trabajador: " + application.getWorkerEmail());
            holder.buttons.setVisibility(View.VISIBLE);
            String status = application.getStatus();
            if (Objects.equals(status, "Postulado")) {
                holder.acceptButton.setOnClickListener(view -> {
                    acceptApplication(holder, application);
                });
                holder.declineButton.setOnClickListener(view -> {
                    declineApplication(holder, application);
                });
            } else if (Objects.equals(status, "Aceptado")) {
                holder.declineButton.setVisibility(View.GONE);
                holder.acceptButton.setText("Aceptado");
                holder.acceptButton.setEnabled(false);
            } else if (Objects.equals(status, "Rechazado")) {
                holder.acceptButton.setVisibility(View.GONE);
                holder.declineButton.setText("Rechazado");
                holder.declineButton.setEnabled(false);
            }
        } else {
            holder.companyText.setText("Empresa: " + application.getCompanyEmail());
        }
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public void acceptApplication(@NonNull ApplicationViewHolder holder, Application application) {
        application.setStatus("Aceptado");
        db = FirebaseFirestore.getInstance();
        db.collection("applications").document(application.getApplicationId()).set(application).addOnSuccessListener(aVoid -> {
            holder.declineButton.setVisibility(View.GONE);
            holder.acceptButton.setText("Aceptado");
            holder.acceptButton.setEnabled(false);
            Toast.makeText(context, "Postulación aceptada", Toast.LENGTH_SHORT).show();
            db.collection("jobs").document(application.getJobId()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    int vacancies = documentSnapshot.getLong("vacancies").intValue();
                    if (vacancies > 0) {
                        int newVacancies = vacancies - 1;
                        db.collection("jobs").document(application.getJobId()).update("vacancies", newVacancies);
                    }
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Error al aceptar postulación", Toast.LENGTH_SHORT).show();
        });
    }

    public void declineApplication(@NonNull ApplicationViewHolder holder, Application application) {
        application.setStatus("Rechazado");
        db.collection("applications").document(application.getApplicationId()).set(application).addOnSuccessListener(aVoid -> {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setText("Rechazado");
            holder.declineButton.setEnabled(false);
            Toast.makeText(context, "Postulación rechazada", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Error al rechazar postulación", Toast.LENGTH_SHORT).show();
        });
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleText, statusText, applicationDate, companyText;
        Button acceptButton, declineButton;
        LinearLayout buttons;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleText = itemView.findViewById(R.id.jobTitleText);
            statusText = itemView.findViewById(R.id.statusText);
            applicationDate = itemView.findViewById(R.id.applicationDate);
            companyText = itemView.findViewById(R.id.companyText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
            buttons = itemView.findViewById(R.id.buttons);
        }
    }
}