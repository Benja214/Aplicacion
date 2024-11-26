package com.example.aplication.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aplication.R;
import com.example.aplication.models.Application;
import com.example.aplication.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private List<Application> applicationList;
    private String userRole;

    public ApplicationAdapter(List<Application> applicationList, String userRole) {
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
            holder.acceptButton.setVisibility(View.VISIBLE);
            String status = application.getStatus();
            if (Objects.equals(status, "Postulado")) {
                holder.acceptButton.setText("Ver postulación");
                holder.acceptButton.setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("applicationId", application.getApplicationId());
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_nav_applications_to_detail_application, bundle);
                });
            } else if (Objects.equals(status, "Aceptado")) {
                holder.acceptButton.setText("Aceptado");
                holder.acceptButton.setEnabled(false);
            } else if (Objects.equals(status, "Rechazado")) {
                holder.acceptButton.setText("Rechazado");
                holder.acceptButton.setEnabled(false);
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

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        ImageView applicationImage;
        TextView jobTitleText, statusText, applicationDate, companyText;
        Button acceptButton;
        ProgressBar progressBar;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            applicationImage = itemView.findViewById(R.id.applicationImage);
            jobTitleText = itemView.findViewById(R.id.jobTitleText);
            statusText = itemView.findViewById(R.id.statusText);
            applicationDate = itemView.findViewById(R.id.applicationDate);
            companyText = itemView.findViewById(R.id.companyText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}