package com.example.upamanyu.stanfordhealthpp;

import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RandomWebSocket extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS  = 1000;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);

        webSocket.send("Hello");
        webSocket.close(NORMAL_CLOSURE_STATUS, "BYE");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);

        Log.d("R", "Receiving" + text);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);

        webSocket.close(NORMAL_CLOSURE_STATUS, null);

        Log.d("C", "Closing");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);

        Log.d("F", "Failed");
    }
}
