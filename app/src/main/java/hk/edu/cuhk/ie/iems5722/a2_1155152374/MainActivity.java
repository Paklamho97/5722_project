package hk.edu.cuhk.ie.iems5722.a2_1155152374;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private static final int ACTION_CONNECTED = 1;
    private static final int ACTION_JOIN = 2;
    private static final int ACTION_UPDATE = 3;
    private static final int ACTION_BROADCAST = 4;
    private static final int ACTION_LEAVE = 5;
    private static final int ACTION_MYEVENT = 6;
    private MainHandler handler = new MainHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createNotificationChannel();
        ListView lv = findViewById(R.id.main_lv);
        ChatRoomAdapter mchatroomadapter;
        ArrayList<Util.chatroom> chatrooms = new ArrayList<>();
        mchatroomadapter = new ChatRoomAdapter(this, chatrooms);
        lv.setAdapter(mchatroomadapter);

        get_ChatroomsTask c = new get_ChatroomsTask(mchatroomadapter, chatrooms);
        c.execute();

        try {
            socket = IO.socket("http://18.222.138.26:8001/");
            socket.on(Socket.EVENT_CONNECT, onConnectSuccess);
            socket.on("myevent", onMyevent);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(MainActivity.this, ChatActivity.class);
                        i.putExtra("id", chatrooms.get(position).id);
                        i.putExtra("name", chatrooms.get(position).name);
                        startActivity(i);
                    }
                });


    }



    public class ChatRoomAdapter  extends BaseAdapter{

        private Context mContext;
        private ArrayList<Util.chatroom> chatrooms;
        private LayoutInflater layoutInflater;

        public ChatRoomAdapter(Context mContext, ArrayList<Util.chatroom> chatrooms){
            this.chatrooms = chatrooms;
            this.mContext = mContext;
            layoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return chatrooms.size();
        }

        @Override
        public Object getItem(int position) {
            return chatrooms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewholder holder;
            if(convertView==null){
                holder = new viewholder();
                convertView = layoutInflater.inflate(R.layout.chatroomlistview, null);
                holder.tv = (TextView) convertView.findViewById(R.id.chatroomlist_tv);
                convertView.setTag(holder);
            }
            else{
                holder = (viewholder) convertView.getTag();
            }
            Util.chatroom c = chatrooms.get(position);
            holder.tv.setText(c.getName());
            return convertView;
        }

        private class viewholder{
            TextView tv;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "hello";
            String description = "hello";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Emitter.Listener onConnectSuccess = new Emitter.Listener() {
        @Override
        public void call ( Object... args) {
            Message msg = handler.obtainMessage(ACTION_CONNECTED, "Connected");
            msg.sendToTarget();
        }
    } ;



    private Emitter.Listener onMyevent = new Emitter.Listener() {
        @Override
        public void call ( Object... args) {
            try {
                JSONObject data = (JSONObject) args [0];
                String text = data.getString("message" );
                int chatroom_id = data.getInt("chatroom_id" );
                Message msg = handler.obtainMessage(ACTION_MYEVENT, data );
                msg.sendToTarget();
            } catch (JSONException e) {
                e.printStackTrace();
            } }
    } ;

    @Override
    protected void onDestroy() {
        socket.disconnect( ) ;
        socket.off();
        super.onDestroy();
    }

}