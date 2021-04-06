package hk.edu.cuhk.ie.iems5722.a2_1155152374;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Util {

    public static int total_pages;

    public static class chatroom{
        public int id;
        public String name;
        public chatroom(int id, String name){
            this.id = id;
            this.name = name;
        }
        public String getName(){
            return name;
        }
    }

    public static class Msg {
        public String content;
        public String time;
        public int msg_type;
        public Msg(String content, String time, int msg_type) {
            this.content = content;
            this.time = time;
            this.msg_type = msg_type;
        }
    }

    public static String get_chatrooms() {
        InputStream is = null;

        try{
            URL url = new URL("http://18.222.138.26/api/a3/get_chatrooms");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            is= conn.getInputStream();

            String result = "";
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line=br.readLine())!=null){
                result += line;
                System.out.print(result);

            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return "no";
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static String get_messages(int id, int page) {
        InputStream is = null;
        String s = "http://18.222.138.26/api/a3/get_messages?chatroom_id="+id+"&page="+page;
        try{
            URL url = new URL(s);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            is= conn.getInputStream();

            String result = "";
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line=br.readLine())!=null){
                result += line;
                System.out.print(result);

            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return "no";
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String send_messages(String chatroom_id, String user_id, String name, String message){
        try{
            URL url = new URL("http://18.222.138.26/api/a3/send_message");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            Uri.Builder builder = new Uri.Builder();
            ArrayList<String> para_names = new ArrayList<String>();
            para_names.add("chatroom_id");
            para_names.add("user_id");
            para_names.add("name");
            para_names.add("message");
            ArrayList<String> para_values = new ArrayList();
            para_values.add(chatroom_id);
            para_values.add(user_id);
            para_values.add(name);
            para_values.add(message);
            for ( int i = 0; i < para_names.size(); i++) {
                builder.appendQueryParameter(para_names.get(i), para_values.get(i));
            }
            String query = builder.build().getEncodedQuery();
            writer.write (query ) ;
            writer.flush() ;
            writer.close() ;
            os.close();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
               // Log.e("msg", conn.getHeaderField("json") );
            }
            return message;
        }catch (Exception e){
            e.printStackTrace();
            return "no";
        }

    }
}
