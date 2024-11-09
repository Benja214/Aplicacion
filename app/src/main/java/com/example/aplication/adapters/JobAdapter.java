package com.example.aplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aplication.R;
import com.example.aplication.models.Application;
import com.example.aplication.models.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private Context context;
    private List<Job> jobList;
    private String userRole;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public JobAdapter(Context context, List<Job> jobList, String userRole) {
        this.context = context;
        this.jobList = jobList;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.job_card, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.jobTitle.setText(job.getTitle());
        holder.jobDescription.setText(job.getDescription());
        holder.jobSalary.setText("Sueldo: $" + job.getSalary());
        holder.jobVacancies.setText("Vacantes: " + job.getVacancies());
        holder.jobMode.setText("Modalidad: " + job.getJobMode());
        holder.jobExpirationDate.setText("Vence: " + job.getExpirationDate());

        if ("Empresa".equals(userRole)) {
            Log.d("JobAdapter", "Setting action button to 'Editar'");
            holder.actionButton.setText("Editar");
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.actionButton.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("jobId", job.getJobId());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_nav_jobs_to_edit_job, bundle);
            });
            holder.deleteButton.setOnClickListener(view -> {
                deleteJob(job.getJobId(), position);
            });
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            try {
                Date fecha = sdf.parse(job.getExpirationDate());
                Date hoy = new Date();

                if (fecha.before(hoy)) {
                    holder.actionButton.setText("Vencido");
                    holder.actionButton.setEnabled(false);
                } else {
                    Log.d("JobAdapter", "Setting action button to 'Postular'");
                    holder.actionButton.setText("Postular");
                    holder.deleteButton.setVisibility(View.GONE);
                    holder.actionButton.setOnClickListener(view -> {
                        auth = FirebaseAuth.getInstance();
                        db = FirebaseFirestore.getInstance();

                        String applicationId = UUID.randomUUID().toString();
                        String workerEmail = auth.getCurrentUser().getEmail();
                        Date currentDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = dateFormat.format(currentDate);

                        Application application = new Application(applicationId, job.getJobId(), job.getTitle(), workerEmail, job.getCompanyEmail(), formattedDate, "Postulado");
                        db.collection("applications").document(applicationId).set(application)
                                .addOnSuccessListener(aVoid -> {
                                    holder.actionButton.setText("Postulado");
                                    holder.actionButton.setEnabled(false);
                                    Toast.makeText(context, "Aplicaste al empleo: " + job.getTitle(), Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error al postular", Toast.LENGTH_SHORT).show();
                                });
                    });
                }
            } catch (ParseException e) {
                Toast.makeText(context, "Formato de fecha inválido: " + job.getExpirationDate(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    private void deleteJob(String jobId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("jobs").document(jobId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        jobList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Trabajo eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al eliminar el trabajo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobDescription, jobSalary, jobVacancies, jobMode, jobExpirationDate;
        Button actionButton, deleteButton;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobDescription = itemView.findViewById(R.id.jobDescription);
            jobSalary = itemView.findViewById(R.id.jobSalary);
            jobVacancies = itemView.findViewById(R.id.jobVacancies);
            jobMode = itemView.findViewById(R.id.jobMode);
            jobExpirationDate = itemView.findViewById(R.id.jobExpirationDate);
            actionButton = itemView.findViewById(R.id.actionButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}