package com.catthoor.TotSServerSideCar.actions;

import com.catthoor.TotSServerSideCar.Packet;

import java.net.Socket;
import java.util.Optional;

public class PrintAction implements PacketAction {
    @Override
    public Optional<String> execute(Packet packet, Socket socket) {
        System.out.printf("Received packet [%d], %s\n", packet.getAction(), packet.getData().isEmpty() ? "empty packet" : packet.getData());
        return Optional.empty();
    }
}
