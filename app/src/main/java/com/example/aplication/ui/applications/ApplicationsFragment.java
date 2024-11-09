package com.example.aplication.ui.applications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplication.R;
import com.example.aplication.adapters.ApplicationAdapter;
import com.example.aplication.databinding.FragmentApplicationsBinding;
import com.example.aplication.models.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplicationsFragment extends Fragment {

    private FragmentApplicationsBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private RecyclerView recyclerView;
    private ApplicationAdapter applicationAdapter;
    private List<Application> applicationsList;
    private String userRole;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ApplicationsViewModel applicationsViewModel =
                new ViewModelProvider(this).get(ApplicationsViewModel.class);

        binding = FragmentApplicationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        applicationsList = new ArrayList<>();
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        applicationAdapter = new ApplicationAdapter(getContext(), applicationsList, "");
        recyclerView.setAdapter(applicationAdapter);

        String userEmail = auth.getCurrentUser().getEmail();

        db.collection("contacto")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userRole = document.getString("rol");
                            applicationAdapter = new ApplicationAdapter(getContext(), applicationsList, userRole);
                            recyclerView.setAdapter(applicationAdapter);

                            if (Objects.equals(userRole, "Empresa")) {
                                loadCompanyApplications(userEmail);
                            } else {
                                loadApplications(userEmail);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "No se encontró el rol para el email especificado", Toast.LENGTH_SHORT).show();
                    }
                });

        return root;
    }

    private void loadCompanyApplications(String email) {
        db.collection("applications")
                .whereEqualTo("companyEmail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        applicationsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Application application = document.toObject(Application.class);
                            applicationsList.add(application);
                        }
                        applicationAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error al obtener postulaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadApplications(String email) {
        db.collection("applications")
                .whereEqualTo("workerEmail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        applicationsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Application application = document.toObject(Application.class);
                            applicationsList.add(application);
                        }
                        applicationAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error al obtener postulaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}