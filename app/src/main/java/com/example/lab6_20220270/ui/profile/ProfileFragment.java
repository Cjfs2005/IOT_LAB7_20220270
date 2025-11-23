package com.example.lab6_20220270.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.lab6_20220270.R;
import com.example.lab6_20220270.model.User;
import com.example.lab6_20220270.service.AuthService;
import com.example.lab6_20220270.service.CloudStorageService;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private ImageView ivProfileImage;
    private TextView tvProfileName, tvProfileDni, tvProfileEmail;
    private Button btnChangePhoto;
    private ProgressBar progressBar;
    private AuthService authService;
    private CloudStorageService storageService;
    private FirebaseFirestore db;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileDni = view.findViewById(R.id.tvProfileDni);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto);
        progressBar = view.findViewById(R.id.progressBarProfile);

        authService = AuthService.getInstance();
        storageService = CloudStorageService.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = authService.getCurrentUser().getUid();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        uploadProfileImage(uri);
                    }
                }
        );

        loadUserData();
        btnChangePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        return view;
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            tvProfileName.setText("Nombre: " + user.getName());
                            tvProfileDni.setText("DNI: " + user.getDni());
                            tvProfileEmail.setText("Correo: " + user.getEmail());
                            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                                Glide.with(requireContext())
                                        .load(user.getProfileImageUrl())
                                        .circleCrop()
                                        .into(ivProfileImage);
                            }
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadProfileImage(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);
        btnChangePhoto.setEnabled(false);

        storageService.uploadProfileImage(imageUri, userId, task -> {
            if (task.isSuccessful()) {
                storageService.getProfileImageUrl(userId, urlTask -> {
                    if (urlTask.isSuccessful()) {
                        Uri downloadUri = urlTask.getResult();
                        String imageUrl = downloadUri.toString();
                        updateProfileImageUrl(imageUrl);
                        Toast.makeText(getContext(), "URL: " + imageUrl, Toast.LENGTH_LONG).show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnChangePhoto.setEnabled(true);
                        Toast.makeText(getContext(), "Error al obtener URL", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                btnChangePhoto.setEnabled(true);
                Toast.makeText(getContext(), "Error al subir imagen: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateProfileImageUrl(String imageUrl) {
        db.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    btnChangePhoto.setEnabled(true);
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .circleCrop()
                            .into(ivProfileImage);
                    Toast.makeText(getContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnChangePhoto.setEnabled(true);
                    Toast.makeText(getContext(), "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
