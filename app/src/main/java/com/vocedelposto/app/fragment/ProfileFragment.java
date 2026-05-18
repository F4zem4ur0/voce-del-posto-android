package com.vocedelposto.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vocedelposto.app.MainActivity;
import com.vocedelposto.app.R;
import com.vocedelposto.app.network.RetrofitClient;
import com.vocedelposto.app.ui.MyReviewsActivity;
import com.vocedelposto.app.ui.TagsActivity;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private static final int[] RADIUS_VALUES = {1, 2, 5, 10, 20};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences("voce_del_posto", MODE_PRIVATE);

        // username e email
        String username = prefs.getString("username", "");
        String email = prefs.getString("email", "");
        view.<TextView>findViewById(R.id.tvUsername).setText(username);
        view.<TextView>findViewById(R.id.tvEmail).setText(email);

        // raggio di ricerca
        SeekBar seekBar = view.findViewById(R.id.seekBarRadius);
        TextView tvRadius = view.findViewById(R.id.tvRadiusValue);
        int savedRadius = prefs.getInt("search_radius", 5);
        seekBar.setProgress(radiusToIndex(savedRadius));
        tvRadius.setText(savedRadius + " km");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int radius = RADIUS_VALUES[progress];
                tvRadius.setText(radius + " km");
                prefs.edit().putInt("search_radius", radius).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // salva username
        EditText etUsername = view.findViewById(R.id.etUsername);
        etUsername.setText(username);
        view.<Button>findViewById(R.id.btnSaveUsername).setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            if (newUsername.isEmpty()) {
                Toast.makeText(getContext(), "Username non può essere vuoto", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putString("username", newUsername).apply();
            view.<TextView>findViewById(R.id.tvUsername).setText(newUsername);
            Toast.makeText(getContext(), "Username aggiornato!", Toast.LENGTH_SHORT).show();
        });

        // gestisci tag
        view.<Button>findViewById(R.id.btnManageTags).setOnClickListener(v ->
                startActivity(new Intent(getContext(), TagsActivity.class)));

        // le mie recensioni
        view.<Button>findViewById(R.id.btnMyReviews).setOnClickListener(v ->
                startActivity(new Intent(getContext(), MyReviewsActivity.class)));

        // logout
        view.<Button>findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            RetrofitClient.getInstance().setAuthToken(null);
            startActivity(new Intent(getContext(), MainActivity.class));
            requireActivity().finish();
        });
    }

    private int radiusToIndex(int radius) {
        for (int i = 0; i < RADIUS_VALUES.length; i++) {
            if (RADIUS_VALUES[i] == radius) return i;
        }
        return 2;
    }
}