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
import java.util.ArrayList;

public class get_MessagesTask extends AsyncTask<String, Void, String> {

    private int id, page;
    private ListView lv;
    private ChatActivity.MyAdapter myAdapter;
    private ArrayList<Util.Msg> msgs;
    public get_MessagesTask(int id, int page, ListView lv, ChatActivity.MyAdapter myAdapter, ArrayList<Util.Msg> msgs){
        this.id = id;
        this.page = page;
        this.lv = lv;
        this.myAdapter = myAdapter;
        this.msgs = msgs;
    }
    @Override
    protected String doInBackground(String... strings) {
        String result = Util.get_messages(id, page);
        return result;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject json = new JSONObject(s);
            JSONObject data = json.getJSONObject("data");
            JSONArray array = data.getJSONArray("messages");
            Util.total_pages = data.getInt("total_pages");
            for(int i = array.length()-1; i>=0; i--){
                String message = array.getJSONObject(i).getString("message");
                String name = array.getJSONObject(i).getString("name");
                int user_id = array.getJSONObject(i).getInt("user_id");
                String time = array.getJSONObject(i).getString("message_time");
                String m = "User: "+name+"\n"+message;
                if(user_id == 1155152374){
                    msgs.add(new Util.Msg(m, time, 0));
                }
                else{
                    msgs.add(new Util.Msg(m, time, 1));
                }

            }

            myAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
