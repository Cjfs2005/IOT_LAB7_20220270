package com.example.lab6_20220270;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.lab6_20220270.service.AuthService;
import com.example.lab6_20220270.ui.auth.LoginActivity;
import com.example.lab6_20220270.ui.profile.ProfileFragment;
import com.example.lab6_20220270.ui.records.RecordsFragment;
import com.example.lab6_20220270.ui.summary.SummaryFragment;
import com.example.lab6_20220270.ui.vehicles.VehiclesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authService = AuthService.getInstance();
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_vehicles) {
                    selectedFragment = new VehiclesFragment();
                } else if (itemId == R.id.nav_records) {
                    selectedFragment = new RecordsFragment();
                } else if (itemId == R.id.nav_summary) {
                    selectedFragment = new SummaryFragment();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (itemId == R.id.nav_logout) {
                    logout();
                    return true;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new VehiclesFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_vehicles);
        }
    }

    private void logout() {
        authService.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}