package com.example.aplication.ui.jobs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.aplication.R;
import com.example.aplication.databinding.EditJobBinding;
import com.example.aplication.models.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class EditJob extends Fragment {
    private EditJobBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText etTitle, etDescription, etSalary, etVacancies, etExpirationDate;
    private Spinner spnJobMode;
    private Button btnEditJob;
    private ProgressBar progressBar;

    private String jobId;
    private String companyImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = EditJobBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etTitle = binding.etTitle;
        etDescription = binding.etDescription;
        etSalary = binding.etSalary;
        etVacancies = binding.etVacancies;
        etExpirationDate = binding.etExpirationDate;
        spnJobMode = binding.spnJobMode;
        btnEditJob = binding.btnEditJob;
        progressBar = binding.progressBar;

        if (getArguments() != null) {
            jobId = getArguments().getString("jobId");
            companyImage = getArguments().getString("companyImage");
            loadJobDetails(jobId);
        }

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


        btnEditJob.setOnClickListener(v -> editJob());

        return root;
    }

    private void loadJobDetails(String jobId) {
        db.collection("jobs").document(jobId).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                etTitle.setText(document.getString("title"));
                etDescription.setText(document.getString("description"));
                etSalary.setText(document.getString("salary"));
                etVacancies.setText(String.valueOf(document.getLong("vacancies")));
                etExpirationDate.setText(document.getString("expirationDate"));

                String jobMode = document.getString("jobMode");

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.job_mode_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnJobMode.setAdapter(adapter);

                int position = adapter.getPosition(jobMode);
                spnJobMode.setSelection(position);
            }
        });
    }

    private void editJob() {
        progressBar.setVisibility(View.VISIBLE);
        btnEditJob.setVisibility(View.GONE);

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        String vacanciesStr = etVacancies.getText().toString().trim();
        String expirationDate = etExpirationDate.getText().toString().trim();
        String jobMode = spnJobMode.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty() || salary.isEmpty() || vacanciesStr.isEmpty() || expirationDate.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Job updatedJob = new Job(jobId, companyImage, auth.getCurrentUser().getEmail(), title, description, expirationDate, Integer.parseInt(vacanciesStr), jobMode, salary);

        db.collection("jobs").document(jobId)
                .set(updatedJob)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Trabajo actualizado con éxito", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnEditJob.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Error al actualizar el trabajo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}