package com.example.meetupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.meetupapp.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {
    TextView usernameMessageActivity;
    ImageView imageViewMessageActivity;

    RecyclerView recyclerView;
    EditText etMessage;
    ImageButton btnSend;

    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        imageViewMessageActivity = findViewById(R.id.imageviewProfilePicture);
        usernameMessageActivity = findViewById(R.id.username);
        btnSend = findViewById(R.id.btn_send);
        etMessage = findViewById(R.id.EditText_send);

        intent = getIntent();
        String userid = intent.getStringExtra("usersid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                usernameMessageActivity.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {
                    imageViewMessageActivity.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MessageActivity.this)
                            .load(user.getImageURL())
                            .into(imageViewMessageActivity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etMessage.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(firebaseUser.getUid(),userid,msg);
                } else {
                    Toast.makeText(MessageActivity.this, "Cannot Send an Empty Message",Toast.LENGTH_SHORT);
                }

                // after msg is sent, clear out the EditTest field
                etMessage.setText("");
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        reference.child("Chats").push().setValue(hashMap);
    }
}