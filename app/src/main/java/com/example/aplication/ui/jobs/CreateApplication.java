package com.example.aplication.ui.jobs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.aplication.databinding.CreateApplicationBinding;
import com.example.aplication.models.Application;
import com.example.aplication.models.DetailApplication;
import com.example.aplication.models.User;
import com.example.aplication.utils.FCMService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CreateApplication extends Fragment {
    private CreateApplicationBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText etMotivation, etExperience, etAvailability, etLocation;
    private Button btnPostApplication;
    private ProgressBar progressBar;

    private String jobId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = CreateApplicationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etMotivation = binding.etMotivation;
        etExperience = binding.etExperience;
        etAvailability = binding.etAvailability;
        etLocation = binding.etLocation;
        btnPostApplication = binding.btnPostApplication;
        progressBar = binding.progressBar;

        if (getArguments() != null) {
            jobId = getArguments().getString("jobId");
        }

        btnPostApplication.setOnClickListener(v -> postApplication());

        return root;
    }

    private void postApplication() {
        String motivation = etMotivation.getText().toString().trim();
        String experience = etExperience.getText().toString().trim();
        String availability = etAvailability.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (motivation.isEmpty() || experience.isEmpty() || availability.isEmpty() || location.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnPostApplication.setVisibility(View.GONE);

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
                        for (QueryDocumentSnapshot userDocument : task.getResult()) {
                            String workerImage = userDocument.getString("imageUrl");

                            db.collection("jobs").document(jobId).get().addOnSuccessListener(document -> {
                                if (document.exists()) {
                                    String companyImage = document.getString("companyImage");
                                    String title = document.getString("title");
                                    String companyEmail = document.getString("companyEmail");

                                    Application application = new Application(applicationId, jobId, workerImage, companyImage, title, workerEmail, companyEmail, formattedDate, "Postulado");
                                    db.collection("applications").document(applicationId).set(application)
                                            .addOnSuccessListener(aVoid -> {
                                                DetailApplication detailApplication = new DetailApplication(applicationId, motivation, experience, availability, location);
                                                db.collection("detailApplications").document(applicationId).set(detailApplication)
                                                        .addOnSuccessListener(dVoid -> {
                                                            Toast.makeText(getContext(), "Aplicaste al empleo: " + title, Toast.LENGTH_SHORT).show();
                                                            companyNotification(companyEmail, "Nueva postulación", "Se ha realizado una nueva postulación al empleo: " + title);
                                                            requireActivity().getSupportFragmentManager().popBackStack();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            progressBar.setVisibility(View.GONE);
                                                            btnPostApplication.setVisibility(View.VISIBLE);
                                                            Toast.makeText(getContext(), "Error al postular", Toast.LENGTH_SHORT).show();
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                progressBar.setVisibility(View.GONE);
                                                btnPostApplication.setVisibility(View.VISIBLE);
                                                Toast.makeText(getContext(), "Error al postular", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnPostApplication.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "No se encontró el rol para el email especificado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void companyNotification(String userEmail, String title, String message) {
        FCMService fcmService = new FCMService(getContext());
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
                        Toast.makeText(getContext(), "Error al notificar a la empresa", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}