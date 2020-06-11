package com.catthoor.TotSServerSideCar;

import com.catthoor.TotSServerSideCar.actions.*;
import com.catthoor.TotSServerSideCar.models.PlayerInfo;
import com.catthoor.TotSServerSideCar.models.in.rooms.Room;
import com.catthoor.TotSServerSideCar.models.in.rooms.RoomInfoResponse;
import com.catthoor.TotSServerSideCar.models.in.rooms.Rooms;
import com.catthoor.TotSServerSideCar.parsers.PacketDataDeserializer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java -jar sidecar.jar username password ip");
            return;
        }

        Config.username = args[0];
        Config.password = args[1];
        Config.ip = args[2];

        new Main();
    }

    private enum PacketType {
        SendCurrentState,
        Response,
        Statics,
        RoomsInfo,
        SyncData,
        AcceptConnection,
        CreateBattleRoomResponse,
        SearchPlayerResponse,
        GetBanListResponse,
        BanPlayerResponse,
        UnBanPlayerResponse,
        SendDSCConfig,
        NotifyUpdateConfig,
        EMPTY_13,
        NotifyUpdateDSRuntimeConfig,
        SkipMapResponse,
        GetRoomInfoResponse,
        KickResponse,
        RemovePlayerFromBlacklistResponse,
        ReceiveAdminList,
        InsertAdminResponse,
        GetHistoryResponse
    }

    private final Map<PacketType, PacketAction> packetActions = Map.ofEntries(
            Map.entry(PacketType.SendCurrentState, new AuthAction()),
            Map.entry(PacketType.AcceptConnection, new AcceptConnectionAction()),
            Map.entry(PacketType.SyncData, new SyncDataAction()),
            Map.entry(PacketType.Statics, new StaticsAction()));

    private final Socket socket;

    private Main() throws IOException {
        socket = new Socket(Config.ip, Config.port);

        if (socket.isConnected())
            System.out.println("Successfully connected to Master Server");

        /* FromMS_SendCurrentState */
        Packet sendCurrentStatePacket = receivePacket(socket);
        /* ToMS_SendCurrentState */
        handlePacket(sendCurrentStatePacket);

        /* FromMS_AcceptConnection */
        Packet acceptConnectionPacket = receivePacket(socket);
        handlePacket(acceptConnectionPacket);

        /* FromMS_SyncData */
        Packet syncDataPacket = receivePacket(socket);
        handlePacket(syncDataPacket);

        startHttpServer();
    }

    private Packet receivePacket(Socket socket) throws IOException {
        byte[] buffer = new byte[socket.getSendBufferSize()];

        int bytesRead = socket.getInputStream().read(buffer);

        byte[] messageInBytes = new byte[bytesRead];
        if (messageInBytes.length >= 0)
            System.arraycopy(buffer, 0, messageInBytes, 0, messageInBytes.length);

        final String message = new String(messageInBytes, StandardCharsets.US_ASCII);
        String json = message.split(Packet.endDataString)[0];

        return new Packet(json.getBytes(StandardCharsets.US_ASCII));
    }

    private Optional<String> handlePacket(Packet packet) {
        PacketType packetType = PacketType.values()[packet.action];
        return packetActions.getOrDefault(packetType, new PrintAction()).execute(packet, socket);
    }

    private void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("192.168.10.132", 8080), 0);

        server.createContext("/", this::route);

        server.setExecutor(Executors.newSingleThreadExecutor());

        server.start();

        System.out.println("Listening for HTTP messages on port 8080");
    }

    private void route(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET"))
            return;

        System.out.println("HTTP GET: " + exchange.getRequestURI());

        Packet out;
        Packet in;
        Optional<String> actionResponse;

        OutputStream outputStream = exchange.getResponseBody();
        String response = "";

        String requestUri = exchange.getRequestURI().toString();

        /* Clear the inputsStream so a receivePacket call does not retrieve old data still in the buffer */
        if (socket.getInputStream().available() > 0)
            socket.getInputStream().readAllBytes();

        String[] routeSplit = requestUri.split("\\?");
        String route = routeSplit.length >= 1 ? routeSplit[0] : "";
        String[] params = routeSplit.length >= 2 ? routeSplit[1].split("=") : new String[]{};

        switch (requestUri.split("\\?")[0]) {
            case "/playercount":
                out = new Packet(6, "");
                out.send(socket);

                in = receivePacket(socket);
                actionResponse = handlePacket(in);

                response = actionResponse.orElse("-1");
                break;
            case "/room-ids":
                out = new Packet(7, "");
                out.send(socket);

                in = receivePacket(socket);
                actionResponse = PacketDataDeserializer.deserialize(in, Rooms.class)
                        .map(rooms -> rooms.getRooms()
                                .stream()
                                .map(room -> room.getRoomName() + "\t" + room.getRoomId())
                                .collect(Collectors.joining(", ")));

                response = actionResponse.orElse("No rooms");
                break;
            case "/current-map":
                out = new Packet(19, "{\"room\": \"" + params[1] + "\"}");
                out.send(socket);

                in = receivePacket(socket);
                actionResponse =
                        PacketDataDeserializer.deserialize(in, RoomInfoResponse.class)
                                .map(roomInfo -> roomInfo.getRoom().getMap());

                response = actionResponse.orElse("Can't get current map");
                break;
            case "/players":
                out = new Packet(19, "{\"room\": \"" + params[1] + "\"}");
                out.send(socket);

                in = receivePacket(socket);
                actionResponse = PacketDataDeserializer.deserialize(in, RoomInfoResponse.class)
                        .map(roomInfo -> {
                            Room room = roomInfo.getRoom();
                            List<PlayerInfo> players = new ArrayList<>(room.getBlueTeam());
                            players.addAll(room.getRedTeam());
                            return players.size() > 0
                                    ? players.stream().map(PlayerInfo::getDisplayName).collect(Collectors.joining(", "))
                                    : null;
                        });

                response = actionResponse.orElse("No players");
                break;
        }

        exchange.sendResponseHeaders(200, response.length());
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
