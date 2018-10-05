package com.mattyang.mysocket;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatApplication extends Application {
    public static final String CHAT_SERVER_URL = "https://socket-io-chat.now.sh/";


    private io.socket.client.Socket mSocket;

    {
        try {
            mSocket = IO.socket(CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket(){
        return mSocket;
    }

}
