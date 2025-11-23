package com.example.lab6_20220270.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lab6_20220270.MainActivity;
import com.example.lab6_20220270.R;
import com.example.lab6_20220270.api.RetrofitClient;
import com.example.lab6_20220270.model.RegistroRequest;
import com.example.lab6_20220270.model.RegistroResponse;
import com.example.lab6_20220270.model.User;
import com.example.lab6_20220270.service.AuthService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etDni, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private AuthService authService;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etRegisterName);
        etDni = findViewById(R.id.etRegisterDni);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvGoToLogin);
        progressBar = findViewById(R.id.progressBarRegister);

        authService = AuthService.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || dni.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(ProgressBar.VISIBLE);
        btnRegister.setEnabled(false);

        RegistroRequest request = new RegistroRequest(dni, email);
        RetrofitClient.getInstance().getApi().validarRegistro(request).enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegistroResponse registroResponse = response.body();
                    if (registroResponse.isExito()) {
                        createFirebaseUser(name, dni, email, password);
                    } else {
                        progressBar.setVisibility(ProgressBar.GONE);
                        btnRegister.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, registroResponse.getMensaje(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressBar.setVisibility(ProgressBar.GONE);
                    btnRegister.setEnabled(true);
                    try {
                        String errorBody = response.errorBody().string();
                        RegistroResponse errorResponse = new com.google.gson.Gson().fromJson(errorBody, RegistroResponse.class);
                        if (errorResponse != null && errorResponse.getMensaje() != null) {
                            Toast.makeText(RegisterActivity.this, errorResponse.getMensaje(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error en la validación", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Error en la validación", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                progressBar.setVisibility(ProgressBar.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createFirebaseUser(String name, String dni, String email, String password) {
        authService.registerUser(email, password, task -> {
            if (task.isSuccessful()) {
                String uid = authService.getCurrentUser().getUid();
                User user = new User(name, dni, email);
                db.collection("users").document(uid).set(user)
                        .addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(ProgressBar.GONE);
                            Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(ProgressBar.GONE);
                            btnRegister.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                progressBar.setVisibility(ProgressBar.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Error al crear usuario: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
