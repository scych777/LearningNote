package com.mattyang.learningdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Activity {
    private ServerSocket mServerSocket;
    Handler mHandler;
    public static final int PORT = 6000;
    TextView textView;
    Thread serverThread = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_activity);
        textView = (TextView) findViewById(R.id.SERVER_DISPLAY);
        mHandler = new Handler();
        serverThread = new Thread(new ServerThread());
        serverThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    class ServerThread implements Runnable{
        @Override
        public void run() {
            Socket socket = null;
            try {
                mServerSocket = new ServerSocket(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()){
                try {
                    socket = mServerSocket.accept();
                    CommunicationThread communicationThread = new CommunicationThread(socket);
                    new Thread(communicationThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable{
        private Socket clientSocket;
        private BufferedReader input;
        public CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                try {
                    final String read = input.readLine();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("Client says : " + read + "\n");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
