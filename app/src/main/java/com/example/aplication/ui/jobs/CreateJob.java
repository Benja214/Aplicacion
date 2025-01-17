package com.example.aplication.ui.jobs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.aplication.R;
import com.example.aplication.databinding.CreateJobBinding;
import com.example.aplication.models.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.UUID;

public class CreateJob extends Fragment {
    private CreateJobBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText etTitle, etDescription, etSalary, etVacancies, etExpirationDate;
    private Spinner spnJobMode;
    private Button btnPostJob;
    private ProgressBar progressBar;

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
        progressBar = binding.progressBar;

        etExpirationDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            String currentDate = etExpirationDate.getText().toString();
            if (!currentDate.isEmpty()) {
                try {
                    String[] dateParts = currentDate.split("/");
                    int day = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]) - 1;
                    int year = Integer.parseInt(dateParts[2]);

                    calendar.set(year, month, day);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    R.style.CustomDatePickerTheme,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        etExpirationDate.setText(formattedDate);
                    },
                    year,
                    month,
                    day
            );

            datePickerDialog.show();
        });

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
        progressBar.setVisibility(View.VISIBLE);
        btnPostJob.setVisibility(View.GONE);

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        String vacancies = etVacancies.getText().toString().trim();
        String expirationDate = etExpirationDate.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(salary) ||
                TextUtils.isEmpty(vacancies) || TextUtils.isEmpty(expirationDate)) {
            Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            btnPostJob.setVisibility(View.VISIBLE);
            return;
        }

        String jobId = UUID.randomUUID().toString();
        String companyEmail = auth.getCurrentUser().getEmail();

        db.collection("users")
                .whereEqualTo("email", companyEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String companyImage = document.getString("imageUrl");
                            Job job = new Job(jobId, companyImage, companyEmail, title, description, expirationDate, Integer.parseInt(vacancies), jobMode, salary);
                            db.collection("jobs").document(jobId).set(job)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Registro exitoso y datos guardados", Toast.LENGTH_SHORT).show();
                                        requireActivity().getSupportFragmentManager().popBackStack();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        btnPostJob.setVisibility(View.VISIBLE);
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "No se encontró el rol para el email especificado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
