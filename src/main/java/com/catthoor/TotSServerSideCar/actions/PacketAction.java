package com.catthoor.TotSServerSideCar.actions;

import com.catthoor.TotSServerSideCar.Packet;

import java.net.Socket;
import java.util.Optional;

public interface PacketAction {

    Optional<String> execute(Packet packet, Socket socket);
}
