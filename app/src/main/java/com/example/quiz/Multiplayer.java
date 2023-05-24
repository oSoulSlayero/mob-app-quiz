package com.example.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Multiplayer extends AppCompatActivity {

    Button button;
    String playerName = "";
    String roomName = "";
    String role = "";
    String message = "";
    FirebaseDatabase database;
    DatabaseReference messageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_multiplayer);

        button = findViewById(R.id.button);
        button.setEnabled(false);

        database = FirebaseDatabase.getInstance();
        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        playerName = preferences.getString("playerName", "");

        Bundle extras = getIntent().getExtras();
        if (extras !=null){
            roomName = extras.getString("roomName");
            if (roomName.equals(playerName)){
                role = "host";
            }else {
                role = "guest";
            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                message = role + "Poked!";
                messageRef.setValue(message);

            }
        });
        messageRef = database.getReference("rooms/"+roomName+"/message");
        message = role + "Poked!";
        messageRef.setValue(message);
        addRoomEventListener();
    }
    private void addRoomEventListener(){
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (role.equals("host")){
                    if (datasnapshot.getValue(String.class).contains("guest:")){
                        button.setEnabled(true);
                        Toast.makeText(Multiplayer.this, "" +
                                datasnapshot.getValue(String.class).replace("guest:",""), Toast.LENGTH_SHORT).show();
                    }else{
                        if (datasnapshot.getValue(String.class).contains("host:")){
                            button.setEnabled(true);
                            Toast.makeText(Multiplayer.this, "" +
                                    datasnapshot.getValue(String.class).replace("host:",""), Toast.LENGTH_SHORT).show();
                    }
                }
            }


        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messageRef.setValue(message);
            }
            });
    }
}
