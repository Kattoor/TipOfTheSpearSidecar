package com.catthoor.TotSServerSideCar.models.in.statics;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Statics {
    @SerializedName("DSCs")
    private List<DSCStaticsItem> dscs;
    private int connectedPlayers;

    public List<DSCStaticsItem> getDscs() {
        return dscs;
    }

    public int getConnectedPlayers() {
        return connectedPlayers;
    }
}
