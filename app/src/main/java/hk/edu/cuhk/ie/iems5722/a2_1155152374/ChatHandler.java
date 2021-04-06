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

public class ChatHandler extends Handler {
    private static final int ACTION_CONNECTED = 1;
    private static final int ACTION_JOIN = 2;
    private static final int ACTION_UPDATE = 3;
    private static final int ACTION_BROADCAST = 4;
    private static final int ACTION_LEAVE = 5;
    private static final int ACTION_MYEVENT = 6;
    private static final String CHANNEL_ID = "1";
    private final WeakReference<ChatActivity> chatActivity;

    ChatHandler(ChatActivity activity) {
        this.chatActivity = new WeakReference<>(activity);
    }
    @Override
    public void handleMessage(Message msg) {
        ChatActivity activity = chatActivity.get();
        long ms = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        String date = dateFormat.format(new Date(ms));
        switch (msg.what) {
            case ACTION_CONNECTED:
                Log.e("here", (String)msg.obj );
                //Toast.makeText(activity, (String)msg.obj,Toast.LENGTH_SHORT).show();
                break;
            case ACTION_UPDATE:
                //activity.output.setText((String) msg.obj);
                break;
            case ACTION_JOIN:
                activity.msgs.add(new Util.Msg(msg.obj.toString(), date, 0));
                activity.myAdapter.notifyDataSetChanged();
                break;
            case ACTION_BROADCAST:
                JSONObject data = (JSONObject) msg.obj;
                try {
                    String text = data.getString("message" );
                    int chatroom_id = data.getInt("chatroom_id" );
                    activity.msgs.add(new Util.Msg(text, date, 0));
                    activity.myAdapter.notifyDataSetChanged();
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
            case ACTION_LEAVE:
                activity.msgs.add(new Util.Msg(msg.obj.toString(), date, 0));
                activity.myAdapter.notifyDataSetChanged();
                break;
            default:
                super.handleMessage(msg);
        } }


}