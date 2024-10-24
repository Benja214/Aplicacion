package com.example.aplication.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.aplication.Perfil;
import com.example.aplication.R;
import com.example.aplication.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtener el ID del usuario actual
        String userId = auth.getCurrentUser().getUid();

        // Recuperar datos del usuario desde Firestore
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Mostrar los datos en la interfaz
                            //binding.tvNombre.setText(document.getString("nombre"));
                            //binding.tvApellido.setText(document.getString("apellido"));
                            //binding.tvRut.setText(document.getString("rut"));
                            //binding.tvPhone.setText(document.getString("telefono"));
                            //binding.tvEmail.setText(document.getString("email"));
                        } else {
                            Log.d("Perfil", "No se encontró el documento");
                            Toast.makeText(getContext(), "No se encontró el documento", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Perfil", "Error al recuperar los datos", task.getException());
                        Toast.makeText(getContext(), "Error al recuperar los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}