package hk.edu.cuhk.ie.iems5722.a2_1155152374;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainHandler extends Handler {
    private static final int ACTION_CONNECTED = 1;
    private static final int ACTION_JOIN = 2;
    private static final int ACTION_UPDATE = 3;
    private static final int ACTION_BROADCAST = 4;
    private static final int ACTION_LEAVE = 5;
    private static final int ACTION_MYEVENT = 6;
    private static final String CHANNEL_ID = "1";
    private final WeakReference<MainActivity> mainActivity;

    MainHandler(MainActivity activity) {
        this.mainActivity = new WeakReference<>(activity);
    }
    @Override
    public void handleMessage(Message msg) {
        MainActivity activity = mainActivity.get();
        long ms = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        String date = dateFormat.format(new Date(ms));
        switch (msg.what) {
            case ACTION_CONNECTED:
                Log.e("here", (String)msg.obj );
                //Toast.makeText(activity, (String)msg.obj,Toast.LENGTH_SHORT).show();
                break;
            case ACTION_MYEVENT:
                JSONObject d = (JSONObject) msg.obj;
                try {
                    String text = d.getString("message" );
                    int chatroom_id = d.getInt("chatroom_id" );
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, CHANNEL_ID)
                            .setSmallIcon(R.drawable.refresh_btn)
                            .setContentTitle("Chatroom "+chatroom_id)
                            .setContentText(text)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);
                    notificationManager.notify(1, builder.build() ) ;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                super.handleMessage(msg);
        } }


}