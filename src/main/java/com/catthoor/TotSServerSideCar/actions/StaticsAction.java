package com.catthoor.TotSServerSideCar.actions;

import com.catthoor.TotSServerSideCar.Packet;
import com.catthoor.TotSServerSideCar.models.in.statics.Statics;
import com.google.gson.Gson;

import java.net.Socket;
import java.util.Optional;

public class StaticsAction implements PacketAction {
    @Override
    public Optional<String> execute(Packet packet, Socket socket) {
        Statics statics = new Gson().fromJson(packet.getData(), Statics.class);
        return Optional.of(String.valueOf(statics.getConnectedPlayers()));
    }
}
