package hk.edu.cuhk.ie.iems5722.a2_1155152374;

import android.os.AsyncTask;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class send_MessagesTask extends AsyncTask<String, Void, String> {

    private String chatroom_id;
    private String user_id;
    private String name;
    private String message;
    private ListView lv;
    private ChatActivity.MyAdapter myAdapter;
    private ArrayList<Util.Msg> msgs;
    public send_MessagesTask(String chatroom_id, String user_id, String name, String message, ListView lv, ChatActivity.MyAdapter myAdapter, ArrayList<Util.Msg> msgs){
        this.chatroom_id = chatroom_id;
        this.user_id = user_id;
        this.name = name;
        this.message = message;
        this.lv = lv;
        this.myAdapter = myAdapter;
        this.msgs = msgs;
    }
    @Override
    protected String doInBackground(String... strings) {
        String result = Util.send_messages(chatroom_id, user_id, name, message);
        return result;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        long ms = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        String date = dateFormat.format(new Date(ms));
        //msgs.add(new Util.Msg(s, date, 0));
        //myAdapter.notifyDataSetChanged();


    }
}
