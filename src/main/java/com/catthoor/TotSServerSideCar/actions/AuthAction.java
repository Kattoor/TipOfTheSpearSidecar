package com.catthoor.TotSServerSideCar.actions;

import com.catthoor.TotSServerSideCar.Config;
import com.catthoor.TotSServerSideCar.Packet;

import java.net.Socket;
import java.util.Optional;

public class AuthAction implements PacketAction {

    @Override
    public Optional<String> execute(Packet packet, Socket socket) {
        /* Received a FromMS_SendCurrentState, reply with an authentication packet */
        Packet authPacket = new Packet(0, "{" +
                "\"secret\": \"" + Config.secret + "\"," +
                "\"userName\": \"" + Config.username + "\"," +
                "\"password\":\"" + Config.password + "\"}");
        authPacket.send(socket);
        System.out.println("Sent authentication request");
        return Optional.empty();
    }
}
