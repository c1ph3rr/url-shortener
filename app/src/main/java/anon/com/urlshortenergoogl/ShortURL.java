package anon.com.urlshortenergoogl;

import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class ShortURL {

    private static String KEY = "AIzaSyBE1AVba-qcsATJgSaPbkf0Rt1Yjx_hkbg";                                              //unique google api key
    private static String URL = "https://www.googleapis.com/urlshortener/v1/url?shortUrl=http://goo.gl/fbsS&key=";

    private static ShortUrlListener _listener = null;

    public abstract static interface ShortUrlListener {
        public abstract void OnFinish(String url);
    }


    public static String makeShort(final String longUrl) {

        String url = longUrl;
        String json = connect(longUrl);

        try{

            JSONObject jsonObj = new JSONObject(json);

            if(jsonObj != null && jsonObj.has("id")){
                String id = jsonObj.getString("id");
                if(id != null && 0 < id.length()) {
                    url = id;
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return url;
    }


    public static void makeShortUrl(final String longUrl, ShortUrlListener listener) {

        _listener = listener;

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = longUrl;
                String json = connect(longUrl);

                try{

                    JSONObject jsonObj = new JSONObject(json);

                    if(jsonObj != null && jsonObj.has("id")){
                        String id = jsonObj.getString("id");
                        if(id != null && 0 < id.length()) {
                            url = id;
                        }
                    }

                } catch(Exception e) {

                }

                Message msg = new Message();
                msg.what = 100;
                msg.obj = url;
                mHandler.sendMessage(msg);

            }
        }).start();

    }

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    if(_listener != null){
                        _listener.OnFinish((String)msg.obj);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private static String connect(String longUrl)
    {
        String res = "";

        try
        {
            URLConnection conn = new URL(URL+KEY).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("{\"longUrl\":\"" + longUrl + "\"}");
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = rd.readLine()) != null)
            {
                s = s.append(sResponse);
            }

            res = s.toString();

            wr.close();
            rd.close();
        } catch (Exception e) {

        }

        return res;
    }

}
