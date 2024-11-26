package com.example.aplication.ui.applications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aplication.R;
import com.example.aplication.activities.MainActivity;
import com.example.aplication.adapters.ApplicationAdapter;
import com.example.aplication.databinding.DetailApplicationBinding;
import com.example.aplication.models.Application;
import com.example.aplication.models.User;
import com.example.aplication.utils.CircleTransform;
import com.example.aplication.utils.FCMService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DetailApplication extends Fragment {
    private DetailApplicationBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseApp storageApp;
    private StorageReference storageReference;
    private DocumentReference userDocRef;
    private static final int PICK_IMAGE_REQUEST = 1;


    private ImageView ivWorkerImage;
    private TextView tvName, tvEmail, tvPhone, tvMotivation, tvExperience, tvAvailability, tvLocation;
    private Button btnAccept, btnDecline;
    private ProgressBar progressBar;
    private LinearLayout actionLayout;

    private String applicationId;
    private Application application;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DetailApplicationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ivWorkerImage = binding.ivWorkerImage;
        tvName = binding.tvName;
        tvEmail = binding.tvEmail;
        tvPhone = binding.tvPhone;
        tvMotivation = binding.tvMotivation;
        tvExperience = binding.tvExperience;
        tvAvailability = binding.tvAvailability;
        tvLocation = binding.tvLocation;
        btnAccept = binding.acceptButton;
        btnDecline = binding.declineButton;
        progressBar = binding.progressBar;
        actionLayout = binding.actionLayout;

        if (getArguments() != null) {
            applicationId = getArguments().getString("applicationId");
        }

        loadData();

        btnAccept.setOnClickListener(v -> acceptApplication());
        btnDecline.setOnClickListener(v -> declineApplication());

        return root;
    }

    private void loadData() {
        db.collection("applications").document(applicationId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.getString("workerImage") != null) {
                                String imageUrl = document.getString("workerImage");
                                Picasso.get()
                                        .load(imageUrl)
                                        .transform(new CircleTransform())
                                        .into(ivWorkerImage);
                            }
                            tvEmail.setText("Correo: " + document.getString("workerEmail"));
                            application = document.toObject(Application.class);
                            db.collection("users").whereEqualTo("email", document.getString("workerEmail")).get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                                            tvName.setText("Nombre: " + userDocument.getString("nombre") + " " + userDocument.getString("apellido"));
                                            tvPhone.setText("Teléfono: " + userDocument.getString("telefono"));
                                            db.collection("detailApplications").document(applicationId).get()
                                                    .addOnSuccessListener(detailDocument -> {
                                                        if (detailDocument.exists()) {
                                                            tvMotivation.setText(detailDocument.getString("motivation"));
                                                            tvExperience.setText(detailDocument.getString("experience"));
                                                            tvAvailability.setText(detailDocument.getString("availability"));
                                                            tvLocation.setText(detailDocument.getString("location"));
                                                            progressBar.setVisibility(View.GONE);
                                                            actionLayout.setVisibility(View.VISIBLE);
                                                        } else {
                                                            Toast.makeText(getContext(), "Error al recuperar los datos", Toast.LENGTH_SHORT).show();
                                                            requireActivity().getSupportFragmentManager().popBackStack();
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "Error al recuperar los datos", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al recuperar los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void acceptApplication() {
        progressBar.setVisibility(View.VISIBLE);
        actionLayout.setVisibility(View.GONE);

        application.setStatus("Aceptado");
        db.collection("applications").document(applicationId).set(application).addOnSuccessListener(aVoid -> {
            db.collection("jobs").document(application.getJobId()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    int vacancies = documentSnapshot.getLong("vacancies").intValue();
                    if (vacancies > 0) {
                        int newVacancies = vacancies - 1;
                        db.collection("jobs").document(application.getJobId()).update("vacancies", newVacancies);
                    }
                    Toast.makeText(getContext(), "Postulación aceptada", Toast.LENGTH_SHORT).show();
                    workerNotification(application.getWorkerEmail(), "Postulación aceptada", "Tu postulación fue aceptada");
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            actionLayout.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Error al aceptar postulación", Toast.LENGTH_SHORT).show();
        });
    }

    public void declineApplication() {
        progressBar.setVisibility(View.VISIBLE);
        actionLayout.setVisibility(View.GONE);

        application.setStatus("Rechazado");
        db.collection("applications").document(applicationId).set(application).addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Postulación rechazada", Toast.LENGTH_SHORT).show();
            workerNotification(application.getWorkerEmail(), "Postulación rechazada", "Tu postulación fue rechazada");
            requireActivity().getSupportFragmentManager().popBackStack();
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            actionLayout.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Error al rechazar postulación", Toast.LENGTH_SHORT).show();
        });
    }

    public void workerNotification(String userEmail, String title, String message) {
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
                        Toast.makeText(getContext(), "Error al notificar al trabajador", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
