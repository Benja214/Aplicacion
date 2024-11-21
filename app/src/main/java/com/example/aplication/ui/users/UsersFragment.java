package com.example.aplication.ui.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aplication.adapters.UserAdapter;
import com.example.aplication.databinding.FragmentUsersBinding;
import com.example.aplication.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private FragmentUsersBinding binding;
    private FirebaseFirestore db;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        userList = new ArrayList<>();
        recyclerView = binding.recyclerView;
        progressBar = binding.progressBar;
        tvEmpty = binding.tvEmpty;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapter = new UserAdapter(getContext(), userList);
        recyclerView.setAdapter(userAdapter);

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        userList.clear();

                        if (task.getResult().size() > 1) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
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