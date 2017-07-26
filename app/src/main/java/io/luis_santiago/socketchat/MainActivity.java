package io.luis_santiago.socketchat;

import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.internal.Util;
import java.util.*;

import static android.R.attr.data;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private static String IP_URL = "https://mychatsocket.herokuapp.com/";
    private EditText editText;
    private Button button;
    private String messageFromServer = "";
    private String message;
    private ListView listview;
    private JSONObject data;
    private ArrayList <String> allMessages;
    private ArrayAdapter<String> allMessageAdapter;


    private Socket mSocket;{
        try{
            mSocket = IO.socket(IP_URL);
        }
        catch (URISyntaxException e){
            // There was an error
        }
    }

    private void init(){
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        listview = (ListView) findViewById(R.id.list_view);
        allMessages = new ArrayList<String>();
        allMessageAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, allMessages);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        mSocket.connect();
        mSocket.on("new message", onNewMessage);
        button.setOnClickListener(this);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("Main Activity", "JSON: "+ args[0]);
                    if(args.length != 0 && args[0]!=null) {
                        data = (JSONObject) args[0];
                    }
                    try{
                        messageFromServer = data.getString("mensaje");
                    }
                    catch (JSONException e){
                        Log.e("MAIN ACTIVITY", "THERE WAS AN ERROR");
                    }
                    finally {
                        Log.e("MAIN ACTIVITY", "TEXT"+ messageFromServer);
                        allMessages.add(messageFromServer);
                        listview.setAdapter(allMessageAdapter);
                    }
                }
            });
        }
    };
    @Override
    public void onClick(View view) {
        message = editText.getText().toString();
        editText.setText("");
        mSocket.emit("new message", message);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSocket.disconnect();
    }
}
