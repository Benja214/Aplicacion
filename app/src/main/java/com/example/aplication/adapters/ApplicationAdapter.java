package com.example.aplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aplication.R;
import com.example.aplication.models.Application;
import com.example.aplication.models.User;
import com.example.aplication.utils.CircleTransform;
import com.example.aplication.utils.FCMService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private Context context;
    private List<Application> applicationList;
    private String userRole;

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
            Picasso.get()
                    .load(application.getWorkerImage())
                    .transform(new CircleTransform())
                    .into(holder.applicationImage);
            holder.applicationImage.setVisibility(View.VISIBLE);
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
            Picasso.get()
                    .load(application.getCompanyImage())
                    .transform(new CircleTransform())
                    .into(holder.applicationImage);
            holder.applicationImage.setVisibility(View.VISIBLE);
            holder.companyText.setText("Empresa: " + application.getCompanyEmail());
        }
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public void acceptApplication(@NonNull ApplicationViewHolder holder, Application application) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.buttons.setVisibility(View.GONE);

        application.setStatus("Aceptado");
        db = FirebaseFirestore.getInstance();
        db.collection("applications").document(application.getApplicationId()).set(application).addOnSuccessListener(aVoid -> {
            holder.declineButton.setVisibility(View.GONE);
            holder.acceptButton.setText("Aceptado");
            holder.acceptButton.setEnabled(false);
            holder.progressBar.setVisibility(View.GONE);
            holder.buttons.setVisibility(View.VISIBLE);
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
            workerNotification(application.getWorkerEmail(), "Postulación aceptada", "Tu postulación fue aceptada");
        }).addOnFailureListener(e -> {
            holder.progressBar.setVisibility(View.GONE);
            holder.buttons.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Error al aceptar postulación", Toast.LENGTH_SHORT).show();
        });
    }

    public void declineApplication(@NonNull ApplicationViewHolder holder, @NonNull Application application) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.buttons.setVisibility(View.GONE);

        application.setStatus("Rechazado");
        db = FirebaseFirestore.getInstance();
        db.collection("applications").document(application.getApplicationId()).set(application).addOnSuccessListener(aVoid -> {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setText("Rechazado");
            holder.declineButton.setEnabled(false);
            holder.progressBar.setVisibility(View.GONE);
            holder.buttons.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Postulación rechazada", Toast.LENGTH_SHORT).show();
            workerNotification(application.getWorkerEmail(), "Postulación rechazada", "Tu postulación fue rechazada");
        }).addOnFailureListener(e -> {
            holder.progressBar.setVisibility(View.GONE);
            holder.buttons.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Error al rechazar postulación", Toast.LENGTH_SHORT).show();
        });
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        ImageView applicationImage;
        TextView jobTitleText, statusText, applicationDate, companyText;
        Button acceptButton, declineButton;
        LinearLayout buttons;
        ProgressBar progressBar;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            applicationImage = itemView.findViewById(R.id.applicationImage);
            jobTitleText = itemView.findViewById(R.id.jobTitleText);
            statusText = itemView.findViewById(R.id.statusText);
            applicationDate = itemView.findViewById(R.id.applicationDate);
            companyText = itemView.findViewById(R.id.companyText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
            buttons = itemView.findViewById(R.id.buttons);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public void workerNotification(String userEmail, String title, String message) {
        FCMService fcmService = new FCMService(context);
        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            String token = user.getFcmToken();
                            fcmService.sendNotification(token, title, message);
                        }
                    } else {
                        Toast.makeText(context, "Error al notificar al trabajador", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}