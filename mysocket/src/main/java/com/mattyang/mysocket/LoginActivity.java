package com.mattyang.mysocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class LoginActivity extends Activity {
    EditText loginInput;
    Button loginButton;
    private Socket mSocket;
    String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatApplication chatApplication = (ChatApplication) getApplication();
        mSocket = chatApplication.getSocket();
        setContentView(R.layout.activity_login);
        loginInput = (EditText) findViewById(R.id.login_input);
        loginInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_NULL){
                    attempLogin();
                    return true;
                }
                return false;
            }
        });
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempLogin();
            }
        });
        mSocket.on("login",onLogin);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSocket.off("login",onLogin);
    }

    private void attempLogin(){
        username = loginInput.getText().toString();
        if(username.equalsIgnoreCase("")||username == null)
            return;
        mSocket.emit("add user",username);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("username",username);
            intent.putExtra("numUsers",numUsers);
            setResult(RESULT_OK,intent);
            finish();
        }
    };

}
