package com.example.billmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.billmate.databinding.ActivityRegistrationBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registration extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.register.setOnClickListener(view -> registerUser());
        binding.googleSignIn.setOnClickListener(view -> signInWithGoogle());
        binding.txtLoginNow.setOnClickListener(view -> navigateTo(Login.class));
    }

    private void registerUser() {
        String email = binding.email.getText().toString().trim();
        String username = binding.username.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String confirmPassword = binding.confirmPassword.getText().toString().trim();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        String sanitizedEmail = email.replace(".", ",");

        databaseReference.child(sanitizedEmail).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Toast.makeText(this, "Email is already registered!", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, String> userData = new HashMap<>();
                userData.put("username", username);
                userData.put("email", email);
                userData.put("password", password);

                databaseReference.child(sanitizedEmail).setValue(userData).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        navigateTo(Login.class);
                    } else {
                        Toast.makeText(this, "Failed to register. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } else {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("id", user.getUid());
                    userData.put("name", user.getDisplayName());
                    userData.put("profile", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");

                    databaseReference.child(user.getUid()).setValue(userData);

                    Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show();
                    navigateTo(MainActivity.class);
                }
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateTo(Class<?> targetClass) {
        Intent intent = new Intent(this, targetClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
    }
}
