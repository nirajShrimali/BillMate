package com.example.billmate;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bootomNavView);
        floatingActionButton = findViewById(R.id.floatingActionBtn);

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new ScannerFragment());
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.scanFragment){
                    loadFragment(new ScannerFragment());
                }else if(id == R.id.customersFragment){
                    loadFragment(new CustomersFragment());
                }else if(id == R.id.dataFragment){
                    loadFragment(new DataFragment());
                }else{
                    loadFragment(new AccountFragment());
                }
                return true;
            }
        });

        floatingActionButton.setOnClickListener(view -> {
            loadFragment(new ScannerFragment());
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
