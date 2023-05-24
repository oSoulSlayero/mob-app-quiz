package com.example.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

public class EnterName extends AppCompatActivity {

    Button submit;
    EditText nameInput;
    String playerName = "";
    FirebaseDatabase database;
    DatabaseReference playerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_enter_name);

        nameInput = findViewById(R.id.nameInput);
        submit = findViewById(R.id.submit);
        database = FirebaseDatabase.getInstance();
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        if (!playerName.equals("")) {
            playerRef = database.getReference("players/" + playerName);
            addEventListener();
            playerRef.setValue("");
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerName = nameInput.getText().toString();
                nameInput.setText("");
                if (!playerName.equals("")) {
                    submit.setEnabled(false);
                    playerRef = database.getReference("players/" + playerName);
                    addEventListener();
                    playerRef.setValue("");
                }

            }
        });
    }
    private void addEventListener(){
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!playerName.equals("")){
                    SharedPreferences preferences = getSharedPreferences("PREFS",0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();

                    startActivity(new Intent(getApplicationContext(), GroupQuiz.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                submit.setEnabled(true);
                Toast.makeText(EnterName.this, "Error!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}