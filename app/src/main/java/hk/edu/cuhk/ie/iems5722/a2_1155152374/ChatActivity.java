package hk.edu.cuhk.ie.iems5722.a2_1155152374;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    public ArrayList<Util.Msg> msgs;
    private EditText input;
    public MyAdapter myAdapter;
    private ListView lv;
    private ImageButton send;
    private int id;
    private String name;
    private int page = 1;
    private boolean toNextPage;
    private Socket socket;
    private static final int ACTION_CONNECTED = 1;
    private static final int ACTION_JOIN = 2;
    private static final int ACTION_UPDATE = 3;
    private static final int ACTION_BROADCAST = 4;
    private static final int ACTION_LEAVE = 5;
    private ChatHandler handler = new ChatHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        name = extras.getString("name");
        actionBar.setTitle(name);

        try {
            socket = IO.socket("http://18.222.138.26:8001/");
            socket.on(Socket.EVENT_CONNECT, onConnectSuccess);
            socket.on("join", onJoin);
            socket.on("broadcast", onBroadcast);
            socket.on("leave", onLeave);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        try {
            json.put ("chatroom_id", id ) ;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("join", json);

        send = findViewById(R.id.send);
        input = findViewById(R.id.input);
        lv = (ListView)findViewById(R.id.lv);
        msgs = new ArrayList<>();

        myAdapter = new MyAdapter(this, msgs);

        lv.setAdapter(myAdapter);

        get_MessagesTask a = new get_MessagesTask(id, 1, lv, myAdapter, msgs);
        a.execute();


        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE){
                    if(toNextPage == true & page < Util.total_pages){
                        msgs.clear();
                        page++;
                        get_MessagesTask a = new get_MessagesTask(id, page, lv, myAdapter, msgs);
                        a.execute();

                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                toNextPage = true;


            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = input.getText().toString();
                if(!content.isEmpty()){
                    String chatroom_id = String.valueOf(id);
                    send_MessagesTask smt = new send_MessagesTask(chatroom_id, "1155152374", "He Bailin", content, lv, myAdapter, msgs);
                    smt.execute();
                    input.setText("");
                  /*  msgs.clear();
                    get_MessagesTask a = new get_MessagesTask(id, 1, lv, myAdapter, msgs);
                    a.execute();*/
                }


               /* new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String content = input.getText().toString();
                        long ms = System.currentTimeMillis();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                        String date = dateFormat.format(new Date(ms));

                        lv.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!content.isEmpty()){
                                    msgs.add(new Util.Msg(content, date, 0));
                                    myAdapter.notifyDataSetChanged();
                                    input.setText("");
                                }
                                lv.setStackFromBottom(true);
                            }
                        });
                    }
                }).start();*/



            }
        });


    }


    public class MyAdapter extends BaseAdapter {

       // private List<Chat> list;
        private LayoutInflater inflater;
        private Context mContext;
        private ArrayList<Util.Msg> Msgs;

        public MyAdapter(Context context, ArrayList<Util.Msg> Msgs)
        {
            this.Msgs = Msgs;
            inflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return msgs.size();
        }

        @Override
        public Object getItem(int position) {
            return msgs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            Util.Msg msg = (Util.Msg) getItem(position);
            return msg.msg_type;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            ViewHolder holder;
            Util.Msg msg = (Util.Msg) getItem(position);
            if(convertView == null)
            {
                if(getItemViewType(position) == 0){
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.send_view, null);
                    holder.content = (TextView) convertView.findViewById(R.id.send_content);
                    holder.time = (TextView) convertView.findViewById(R.id.send_time);

                }
                else{
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.receive_view, null);
                    holder.content = (TextView) convertView.findViewById(R.id.receive_content);
                    holder.time = (TextView) convertView.findViewById(R.id.receive_time);

                }

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.content.setText(msg.content);
            holder.time.setText(msg.time);

            return convertView;

        }

        private class ViewHolder {
            TextView content;
            TextView time;

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                JSONObject json = new JSONObject();
                try {
                    json.put ("chatroom_id", id ) ;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("leave", json);
                finish();
                return true;


            case R.id.refresh_btn:
                msgs.clear();
                get_MessagesTask a = new get_MessagesTask(id, 1, lv, myAdapter, msgs);
                a.execute();
                Toast.makeText(this, "refresh", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.meun_chat, menu);
        return true;
    }




    private Emitter.Listener onConnectSuccess = new Emitter.Listener() {
        @Override
        public void call ( Object... args) {
            Message msg = handler.obtainMessage(ACTION_CONNECTED, "Connected");
            msg.sendToTarget();
        }
    } ;

    private Emitter.Listener onJoin = new Emitter.Listener() {
        @Override
        public void call ( Object... args) {
            try {
                JSONObject data = (JSONObject) args [0];
                String text = data.getString("chatroom_id" );
                Message msg = handler.obtainMessage(ACTION_JOIN, text );
                msg.sendToTarget();
            } catch (JSONException e) {
                e.printStackTrace();
            } }
    } ;

    private Emitter.Listener onBroadcast = new Emitter.Listener() {
        @Override
        public void call ( Object... args) {
            try {
                JSONObject data = (JSONObject) args [0];
                String text = data.getString("message" );
                int chatroom_id = data.getInt("chatroom_id" );
                Message msg = handler.obtainMessage(ACTION_BROADCAST, data );
                msg.sendToTarget();
            } catch (JSONException e) {
                e.printStackTrace();
            } }
    } ;

    private Emitter.Listener onLeave = new Emitter.Listener() {
        @Override
        public void call ( Object... args) {
            try {
                JSONObject data = (JSONObject) args [0];
                String text = data.getString("chatroom_id" );
                Message msg = handler.obtainMessage(ACTION_LEAVE, text );
                msg.sendToTarget();
            } catch (JSONException e) {
                e.printStackTrace();
            } }
    } ;
    private Emitter.Listener onTextUpdate = new Emitter.Listener() {
        @Override
        public void call ( Object... args) {
            try {
                JSONObject data = (JSONObject) args [0];
                String text = data.getString(" text " );
                Message msg = handler.obtainMessage(ACTION_UPDATE, text );
                msg.sendToTarget();
            } catch (JSONException e) {
                e.printStackTrace();
            } }
    } ;

    @Override
    protected void onDestroy() {
        if (socket != null) {
            socket.disconnect( ) ;
            socket.off();
        }
        super.onDestroy();
    }



}