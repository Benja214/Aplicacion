package com.example.aplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aplication.R;
import com.example.aplication.models.User;
import com.example.aplication.utils.CircleTransform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        Picasso.get()
                .load(user.getImageUrl())
                .transform(new CircleTransform())
                .into(holder.userImage);
        holder.userImage.setVisibility(View.VISIBLE);
        holder.tvUsername.setText(user.getNombre() + " " + user.getApellido());
        holder.tvRol.setText("Rol: " + user.getRol());
        holder.tvPhone.setText("Teléfono: " + user.getTelefono());
        holder.tvEmail.setText("Correo: " + user.getEmail());

        if ("Administrador".equals(user.getRol())) {
            holder.btnDeleteUser.setEnabled(false);
        } else {
            holder.btnDeleteUser.setOnClickListener(view -> {
                deleteUser(user.getEmail(), position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void deleteUser(String email, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();

                            db.collection("users").document(documentId).delete()
                                    .addOnSuccessListener(unused -> {
                                        userList.remove(position);
                                        notifyItemRemoved(position);
                                        Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show();

                                        deleteJobs(email);
                                        deleteApplications(email);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(context, "No se encontró usuario con ese correo", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteJobs(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("jobs")
                .whereEqualTo("companyEmail", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();

                            db.collection("jobs").document(documentId).delete()
                                    .addOnSuccessListener(unused -> {})
                                    .addOnFailureListener(e -> {});
                        }
                    }
                })
                .addOnFailureListener(e -> {});
    }

    private void deleteApplications(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("applications")
                .whereEqualTo("companyEmail", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();

                            db.collection("applications").document(documentId).delete()
                                    .addOnSuccessListener(unused -> {})
                                    .addOnFailureListener(e -> {});
                        }
                    }
                })
                .addOnFailureListener(e -> {});

        db.collection("applications")
                .whereEqualTo("workerEmail", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();

                            db.collection("applications").document(documentId).delete()
                                    .addOnSuccessListener(unused -> {})
                                    .addOnFailureListener(e -> {});
                        }
                    }
                })
                .addOnFailureListener(e -> {});
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView tvUsername, tvRol, tvPhone, tvEmail;
        Button btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvRol = itemView.findViewById(R.id.tvRol);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}