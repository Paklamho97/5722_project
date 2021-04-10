package com.example.chatapp.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.chatapp.GroupMessageActivity;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");
        String title = remoteMessage.getData().get("title");
        String[] parts = title.split("-");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");
        String currentGroup = preferences.getString("currentgroup", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && sented.equals(firebaseUser.getUid())) {
            if (title.contains("New Group Message")) {
                if (!currentGroup.equals(parts[1].trim())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOreoNupNotification(remoteMessage);
                    } else {
                        sendNotification(remoteMessage);
                    }
                }
            }
            else {
                if (!currentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOreoNupNotification(remoteMessage);
                    } else {
                        sendNotification(remoteMessage);
                    }
                }
            }
        }
    }

    private void sendOreoNupNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String time = remoteMessage.getData().get("time");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j;
        try {
            j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        } catch (NumberFormatException ex) {
            j = Integer.MAX_VALUE;
        }
        Intent intent;
        Bundle bundle = new Bundle();

        if (title.contains("New Group Message")) {
            String[] parts = title.split("-");
            intent = new Intent(this, GroupMessageActivity.class);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("groupTitle").getValue().equals(parts[1].trim())) {
                            bundle.putString("groupId", (String) snapshot.child("groupId").getValue());
                            bundle.putString("groupTitle", (String) snapshot.child("groupTitle").getValue());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            intent = new Intent(this, MessageActivity.class);
            bundle.putString("userid", user);
        }
        while ((bundle.getString("groupId") == null || bundle.getString("groupTitle") == null) && bundle.getString("userid") == null);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNupNotifications oreoNupNotifications = new OreoNupNotifications(this);
        Notification.Builder builder = oreoNupNotifications.getOreoNupNotification(title, body, pendingIntent, defaultSound, icon, time);

        int i = 0;

        if (j > 0) {
            i = j;
        }

        oreoNupNotifications.getManager().notify(i, builder.build());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(time);
        int finalI = i;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (chat != null && chat.isIsseen()) {
                    oreoNupNotifications.getManager().cancel(finalI);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String time = remoteMessage.getData().get("time");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j;
        try {
            j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        } catch (NumberFormatException ex) {
            j = Integer.MAX_VALUE;
        }
        Intent intent;
        Bundle bundle = new Bundle();

        if (title.contains("New Group Message")) {
            String[] parts = title.split("-");
            intent = new Intent(this, GroupMessageActivity.class);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("groupTitle").getValue().equals(parts[1].trim())) {
                            bundle.putString("groupId", (String) snapshot.child("groupId").getValue());
                            bundle.putString("groupTitle", (String) snapshot.child("groupTitle").getValue());
                            System.out.println(bundle.getString("groupTitle"));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
           intent = new Intent(this, MessageActivity.class);
           bundle.putString("userid", user);
        }
        while ((bundle.getString("groupId") == null || bundle.getString("groupTitle") == null) && bundle.getString("userid") == null);

        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(Integer.parseInt(icon))
                                             .setContentTitle(title).setContentText(body).setAutoCancel(true).setSound(defaultSound).setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        int i = 0;

        if (j > 0) {
            i = j;
        }

        noti.notify(i, builder.build());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(time);
        int finalI = i;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (chat != null && chat.isIsseen()) {
                    noti.cancel(finalI);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
