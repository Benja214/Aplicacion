package com.example.aplication.ui.jobs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.aplication.R;
import com.example.aplication.activities.Registro;
import com.example.aplication.databinding.CreateJobBinding;
import com.example.aplication.models.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateJob extends Fragment {
    private CreateJobBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText etTitle, etDescription, etSalary, etVacancies, etExpirationDate;
    private Spinner spnJobMode;
    private Button btnPostJob;

    private String jobMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = CreateJobBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etTitle = binding.etTitle;
        etDescription = binding.etDescription;
        etSalary = binding.etSalary;
        etVacancies = binding.etVacancies;
        etExpirationDate = binding.etExpirationDate;
        spnJobMode = binding.spnJobMode;
        btnPostJob = binding.btnPostJob;

        spnJobMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedJobMode = parent.getItemAtPosition(position).toString();
                if (selectedJobMode.equals("Full-time")) {
                    jobMode = "Full-time";
                } else if (selectedJobMode.equals("Part-time")) {
                    jobMode = "Part-time";
                } else if (selectedJobMode.equals("Por Horas")) {
                    jobMode = "Por Horas";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnPostJob.setOnClickListener(v -> postJob());

        return root;
    }

    private void postJob() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        String vacancies = etVacancies.getText().toString().trim();
        String expirationDate = etExpirationDate.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(salary) ||
                TextUtils.isEmpty(vacancies) || TextUtils.isEmpty(expirationDate)) {
            Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String jobId = UUID.randomUUID().toString();
        String companyEmail = auth.getCurrentUser().getEmail();

        Job job = new Job(jobId, companyEmail, title, description, expirationDate, Integer.parseInt(vacancies), jobMode, salary);
        db.collection("jobs").document(jobId).set(job)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Registro exitoso y datos guardados", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
