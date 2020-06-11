package com.catthoor.TotSServerSideCar;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Packet {
    public static String endDataString = "@end@";
    public boolean isValid = true;

    @SerializedName("act")
    public int action;

    @SerializedName("dat")
    public String data;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Packet() {

    }

    public Packet(int action, String data) {
        this.action = action;
        this.data = data;
    }

    public boolean send(Socket socket) {
        try {
            if (socket == null || !socket.isConnected())
                return false;

            final String json = new Gson().toJson(this);
            final String textToSend = json + Packet.endDataString;
            final byte[] bytesToSend = textToSend.getBytes(StandardCharsets.US_ASCII);

            socket.getOutputStream().write(bytesToSend);

            return bytesToSend.length > 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Packet(byte[] bytes) {
        String json = new String(bytes, StandardCharsets.US_ASCII);
        Packet packet = new Gson().fromJson(json, Packet.class);
        this.action = packet.action;
        this.data = packet.data;
    }
}
