package com.example.aplication.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.aplication.R;
import com.example.aplication.activities.MainActivity;
import com.example.aplication.databinding.DeleteAccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAccount extends Fragment {
    private DeleteAccountBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference userDocRef;

    private EditText etPassword;
    private Button btnDeleteAccount;
    private ProgressBar progressBar;
    private boolean isPasswordVisible;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DeleteAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etPassword = binding.etPassword;
        btnDeleteAccount = binding.btnDeleteAccount;
        progressBar = binding.progressBar;

        isPasswordVisible = false;

        etPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - (etPassword.getCompoundDrawables()[2].getBounds().width() * 2))) {
                    if (isPasswordVisible) {
                        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye, 0);
                    } else {
                        etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye_off, 0);
                    }
                    isPasswordVisible = !isPasswordVisible;

                    etPassword.setSelection(etPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        btnDeleteAccount.setOnClickListener(v -> deleteAccount());

        return root;
    }

    private void deleteAccount() {
        progressBar.setVisibility(View.VISIBLE);
        btnDeleteAccount.setVisibility(View.GONE);

        String email = auth.getCurrentUser().getEmail();
        String password = etPassword.getText().toString().trim();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        userDocRef = db.collection("users").document(userId);
                        userDocRef.delete().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            requireActivity().finish();
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            btnDeleteAccount.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnDeleteAccount.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}