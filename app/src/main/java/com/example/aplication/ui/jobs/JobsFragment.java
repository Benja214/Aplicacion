package com.example.aplication.ui.jobs;

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
import com.example.aplication.adapters.JobAdapter;
import com.example.aplication.databinding.FragmentJobsBinding;
import com.example.aplication.models.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JobsFragment extends Fragment {

    private FragmentJobsBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private List<Job> jobList;
    private String userRole;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        JobsViewModel worksViewModel = new ViewModelProvider(this).get(JobsViewModel.class);

        binding = FragmentJobsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        jobList = new ArrayList<>();
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        jobAdapter = new JobAdapter(getContext(), jobList, "");
        recyclerView.setAdapter(jobAdapter);

        String companyEmail = auth.getCurrentUser().getEmail();

        db.collection("contacto")
                .whereEqualTo("email", companyEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userRole = document.getString("rol");
                            jobAdapter = new JobAdapter(getContext(), jobList, userRole);
                            recyclerView.setAdapter(jobAdapter);

                            if (Objects.equals(userRole, "Empresa")) {
                                binding.btnCrear.setVisibility(View.VISIBLE);
                                binding.btnCrear.setOnClickListener(v -> {
                                    NavController navController = Navigation.findNavController(v);
                                    navController.navigate(R.id.action_nav_jobs_to_create_job);
                                });
                                loadCompanyJobs(companyEmail);
                            } else {
                                loadJobs();
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "No se encontró el rol para el email especificado", Toast.LENGTH_SHORT).show();
                    }
                });

        return root;
    }

    private void loadCompanyJobs(String email) {
        db.collection("jobs")
                .whereEqualTo("companyEmail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        jobList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Job job = document.toObject(Job.class);
                            jobList.add(job);
                        }
                        jobAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error al obtener trabajos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadJobs() {
        db.collection("jobs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        jobList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Job job = document.toObject(Job.class);
                            jobList.add(job);
                        }
                        jobAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error al obtener trabajos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}