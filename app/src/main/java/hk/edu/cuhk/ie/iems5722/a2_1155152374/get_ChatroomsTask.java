package hk.edu.cuhk.ie.iems5722.a2_1155152374;

import android.os.AsyncTask;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class get_ChatroomsTask extends AsyncTask<String, Void, String> {

    private MainActivity.ChatRoomAdapter myAdapter;
    private ArrayList<Util.chatroom> chatrooms;
    public get_ChatroomsTask(MainActivity.ChatRoomAdapter myAdapter, ArrayList<Util.chatroom> chatrooms){

        this.myAdapter = myAdapter;
        this.chatrooms = chatrooms;
    }
    @Override
    protected String doInBackground(String... strings) {

        return Util.get_chatrooms();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        JSONObject json = null;
        try {
            json = new JSONObject(s);
            JSONArray array = json.getJSONArray("data");
            for(int i=0; i<array.length(); i++){
                String name = array.getJSONObject(i).getString("name");
                int id = array.getJSONObject(i).getInt("id");
                chatrooms.add(new Util.chatroom(id, name));
                myAdapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
