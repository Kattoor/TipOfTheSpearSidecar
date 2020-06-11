package com.catthoor.TotSServerSideCar.actions;

import com.catthoor.TotSServerSideCar.Packet;

import java.net.Socket;
import java.util.Optional;

public class AcceptConnectionAction implements PacketAction {
    @Override
    public Optional<String> execute(Packet packet, Socket socket) {
        System.out.println("Successfully authenticated");
        return Optional.empty();
    }
}
