package com.vocedelposto.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vocedelposto.app.MainActivity;
import com.vocedelposto.app.R;
import com.vocedelposto.app.network.RetrofitClient;
import com.vocedelposto.app.ui.TagsActivity;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

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
        String username = prefs.getString("username", "");
        String email = prefs.getString("email", "");

        view.<TextView>findViewById(R.id.tvUsername).setText(username);
        view.<TextView>findViewById(R.id.tvEmail).setText(email);

        view.<Button>findViewById(R.id.btnManageTags).setOnClickListener(v ->
                startActivity(new Intent(getContext(), TagsActivity.class)));

        view.<Button>findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            RetrofitClient.getInstance().setAuthToken(null);
            startActivity(new Intent(getContext(), MainActivity.class));
            requireActivity().finish();
        });
    }
}