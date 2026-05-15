package com.vocedelposto.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vocedelposto.app.model.AuthRequest;
import com.vocedelposto.app.model.AuthResponse;
import com.vocedelposto.app.network.RetrofitClient;
import com.vocedelposto.app.ui.PlacesActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvError = findViewById(R.id.tvError);

        btnLogin.setOnClickListener(v -> doLogin());
        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Inserisci email e password");
            return;
        }

        btnLogin.setEnabled(false);
        AuthRequest request = new AuthRequest(email, password);

        RetrofitClient.getInstance().getApiService()
                .login(request)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        btnLogin.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            saveTokenAndProceed(response.body());
                        } else {
                            showError("Email o password errati");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        btnLogin.setEnabled(true);
                        showError("Errore di rete: " + t.getMessage());
                    }
                });
    }

    private void doRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Inserisci email e password");
            return;
        }

        btnRegister.setEnabled(false);
        AuthRequest request = new AuthRequest(email, password);

        RetrofitClient.getInstance().getApiService()
                .register(request)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        btnRegister.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            saveTokenAndProceed(response.body());
                        } else {
                            showError("Registrazione fallita");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        btnRegister.setEnabled(true);
                        showError("Errore di rete: " + t.getMessage());
                    }
                });
    }

    private void saveTokenAndProceed(AuthResponse authResponse) {
        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        prefs.edit()
                .putString("token", authResponse.getToken())
                .putLong("userId", authResponse.getUserId())
                .putString("username", authResponse.getUsername())
                .apply();

        RetrofitClient.getInstance().setAuthToken(authResponse.getToken());

        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}