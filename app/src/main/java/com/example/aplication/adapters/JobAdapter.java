package com.example.aplication.adapters;

import android.content.Context;
import android.os.Bundle;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aplication.R;
import com.example.aplication.models.Application;
import com.example.aplication.models.Job;
import com.example.aplication.models.User;
import com.example.aplication.utils.CircleTransform;
import com.example.aplication.utils.FCMService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

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
            holder.actionButton.setText("Editar");
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.actionButton.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("jobId", job.getJobId());
                bundle.putString("companyImage", job.getCompanyImage());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_nav_jobs_to_edit_job, bundle);
            });
            holder.deleteButton.setOnClickListener(view -> {
                deleteJob(job.getJobId(), position, holder);
            });
        } else if ("Administrador".equals(userRole)) {
            Picasso.get()
                    .load(job.getCompanyImage())
                    .transform(new CircleTransform())
                    .into(holder.jobImage);
            holder.jobImage.setVisibility(View.VISIBLE);
            holder.jobEmail.setVisibility(View.VISIBLE);
            holder.jobEmail.setText("Publicado por: " + job.getCompanyEmail());
            holder.actionButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(view -> {
                deleteJob(job.getJobId(), position, holder);
            });
        } else {
            Picasso.get()
                    .load(job.getCompanyImage())
                    .transform(new CircleTransform())
                    .into(holder.jobImage);
            holder.jobImage.setVisibility(View.VISIBLE);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            try {
                Date fecha = sdf.parse(job.getExpirationDate());
                Date hoy = new Date();

                if (fecha.before(hoy)) {
                    holder.actionButton.setText("Vencido");
                    holder.actionButton.setEnabled(false);
                } else {
                    holder.actionButton.setText("Postular");
                    holder.deleteButton.setVisibility(View.GONE);
                    holder.actionButton.setOnClickListener(view -> {
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.actionButton.setVisibility(View.GONE);

                        auth = FirebaseAuth.getInstance();
                        db = FirebaseFirestore.getInstance();

                        String applicationId = UUID.randomUUID().toString();
                        String workerEmail = auth.getCurrentUser().getEmail();
                        Date currentDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = dateFormat.format(currentDate);

                        db.collection("users")
                                .whereEqualTo("email", workerEmail)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String workerImage = document.getString("imageUrl");

                                            Application application = new Application(applicationId, job.getJobId(), workerImage, job.getCompanyImage(), job.getTitle(), workerEmail, job.getCompanyEmail(), formattedDate, "Postulado");
                                            db.collection("applications").document(applicationId).set(application)
                                                    .addOnSuccessListener(aVoid -> {
                                                        holder.actionButton.setText("Postulado");
                                                        holder.actionButton.setEnabled(false);
                                                        holder.progressBar.setVisibility(View.GONE);
                                                        holder.actionButton.setVisibility(View.VISIBLE);
                                                        Toast.makeText(context, "Aplicaste al empleo: " + job.getTitle(), Toast.LENGTH_SHORT).show();
                                                        companyNotification(job.getCompanyEmail(), "Nueva postulación", "Se ha realizado una nueva postulación al empleo: " + job.getTitle());
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        holder.progressBar.setVisibility(View.GONE);
                                                        holder.actionButton.setVisibility(View.VISIBLE);
                                                        Toast.makeText(context, "Error al postular", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    } else {
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder.actionButton.setVisibility(View.VISIBLE);
                                        Toast.makeText(context, "No se encontró el rol para el email especificado", Toast.LENGTH_SHORT).show();
                                    }
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

    private void deleteJob(String jobId, int position, @NonNull JobViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.actionLayout.setVisibility(View.GONE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("applications")
                .whereEqualTo("jobId", jobId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnCompleteListener(applicationDeleteTask -> {});
                        }

                        db.collection("jobs").document(jobId)
                                .delete()
                                .addOnCompleteListener(jobTask -> {
                                    if (jobTask.isSuccessful()) {
                                        jobList.remove(position);
                                        notifyItemRemoved(position);
                                        Toast.makeText(context, "Trabajo eliminado", Toast.LENGTH_SHORT).show();
                                    } else {
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder.actionLayout.setVisibility(View.VISIBLE);
                                        Toast.makeText(context, "Error al eliminar el trabajo", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.actionLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "Error al eliminar las aplicaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        ImageView jobImage;
        TextView jobTitle, jobDescription, jobSalary, jobVacancies, jobMode, jobExpirationDate, jobEmail;
        Button actionButton, deleteButton;
        ProgressBar progressBar;
        LinearLayout actionLayout;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobImage = itemView.findViewById(R.id.jobImage);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobDescription = itemView.findViewById(R.id.jobDescription);
            jobSalary = itemView.findViewById(R.id.jobSalary);
            jobVacancies = itemView.findViewById(R.id.jobVacancies);
            jobMode = itemView.findViewById(R.id.jobMode);
            jobExpirationDate = itemView.findViewById(R.id.jobExpirationDate);
            jobEmail = itemView.findViewById(R.id.jobEmail);
            actionButton = itemView.findViewById(R.id.actionButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            progressBar = itemView.findViewById(R.id.progressBar);
            actionLayout = itemView.findViewById(R.id.actionLayout);
        }
    }

    public void companyNotification(String userEmail, String title, String message) {
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
                        Toast.makeText(context, "Error al notificar a la empresa", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}