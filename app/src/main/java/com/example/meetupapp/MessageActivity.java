package com.example.meetupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.meetupapp.Adapter.MessageAdapter;
import com.example.meetupapp.Model.Chat;
import com.example.meetupapp.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    TextView usernameMessageActivity;
    ImageView imageViewMessageActivity;

    RecyclerView recyclerView;
    EditText etMessage;
    ImageButton btnSend;

    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    Intent intent;
    String userid;

    RecyclerView messageRecyclerView;
    MessageAdapter messageAdapter;
    List<Chat> myChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        imageViewMessageActivity = findViewById(R.id.imageviewProfilePicture);
        usernameMessageActivity = findViewById(R.id.username);
        btnSend = findViewById(R.id.btn_send);
        etMessage = findViewById(R.id.EditText_send);

        // RecyclerView for messages
        messageRecyclerView = findViewById(R.id.recyclerViewMessage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userid = intent.getStringExtra("usersid");

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

                readMessages(firebaseUser.getUid(), userid, user.getImageURL());
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

        // Adds the latest messages between the user to the chat fragment.
        final DatabaseReference chatReference = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);

        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatReference.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

     private void readMessages(String myId, String userid, String imageurl) {
        myChat = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference("Chats");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myChat.clear();
                for (DataSnapshot snaps : snapshot.getChildren()) {
                    Chat chat = snaps.getValue(Chat.class);

                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myId)) {
                        myChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, myChat, imageurl);
                    messageRecyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
     }
}