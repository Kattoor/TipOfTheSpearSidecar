package com.catthoor.TotSServerSideCar;

import com.catthoor.TotSServerSideCar.actions.*;
import com.catthoor.TotSServerSideCar.models.in.PlayerInfo;
import com.catthoor.TotSServerSideCar.models.in.rooms.Room;
import com.catthoor.TotSServerSideCar.models.in.rooms.RoomInfoResponse;
import com.catthoor.TotSServerSideCar.models.in.rooms.Rooms;
import com.catthoor.TotSServerSideCar.models.out.PlayerInfoOut;
import com.catthoor.TotSServerSideCar.models.out.rooms.RoomOut;
import com.catthoor.TotSServerSideCar.models.out.rooms.RoomsOut;
import com.catthoor.TotSServerSideCar.parsers.PacketDataDeserializer;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
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

        String authenticationKey = loadAuthenticationKey().orElseGet(() -> {
            String generatedKey = generateAuthenticationKey();
            saveAuthenticationKey(generatedKey);
            return generatedKey;
        });

        Config.authenticationKey = authenticationKey;
        System.out.println("================ AUTHENTICATION KEY ================");
        System.out.println("| " + authenticationKey + " |");
        System.out.println("====================================================");

        /* FromMS_SyncData */
        Packet syncDataPacket = receivePacket(socket);
        handlePacket(syncDataPacket);

        startHttpServer();
    }

    private String generateAuthenticationKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return new BigInteger(1, bytes).toString();
    }

    private Optional<String> loadAuthenticationKey() {
        try {
            final Path filePath = Paths.get("authenticationKey");
            return Files.exists(filePath)
                    ? Optional.of(Files.readString(filePath))
                    : Optional.empty();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void saveAuthenticationKey(String authenticationKey) {
        try {
            final Path filePath = Paths.get("authenticationKey");
            Files.writeString(filePath, authenticationKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Packet receivePacket(Socket socket) throws IOException {
        byte[] buffer = new byte[socket.getSendBufferSize()];

        int bytesRead = socket.getInputStream().read(buffer);

        byte[] messageInBytes = new byte[bytesRead];
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
        String localIp = InetAddress.getLocalHost().getHostAddress();

        HttpServer server = HttpServer.create(new InetSocketAddress(localIp, 8080), 0);

        server.createContext("/", this::route);

        server.setExecutor(Executors.newSingleThreadExecutor());

        server.start();

        System.out.println("Listening for HTTP messages at " + localIp + ":8080");
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
            receivePacket(socket);

        String[] routeSplit = requestUri.split("\\?");
        String[] params = routeSplit.length >= 2 ? routeSplit[1].split("=") : new String[]{};
        String authKey = exchange.getRequestHeaders().get("authKey").get(0);

        if (!authKey.equals(Config.authenticationKey))
            return;

        switch (requestUri.split("\\?")[0]) {
            case "/playercount":
                out = new Packet(6, "");
                out.send(socket);

                in = receivePacket(socket);
                actionResponse = handlePacket(in);

                response = actionResponse.orElse("-1");
                break;
            case "/server":
                out = new Packet(7, "");
                out.send(socket);

                in = receivePacket(socket);

                Optional<Rooms> receivedRooms = PacketDataDeserializer.deserialize(in, Rooms.class);
                Optional<RoomsOut> outRooms = receivedRooms.map(inRooms ->
                        new RoomsOut(inRooms.getRooms().stream().map(inRoom ->
                                new RoomOut(
                                        inRoom.getGameMode(),
                                        inRoom.getRegion(),
                                        inRoom.getSessionType(),
                                        inRoom.getMap(),
                                        inRoom.getRoomName(),
                                        inRoom.getBlueTeam().stream().map(playerInfo -> new PlayerInfoOut(playerInfo.getDisplayName())).collect(Collectors.toList()),
                                        inRoom.getRedTeam().stream().map(playerInfo -> new PlayerInfoOut(playerInfo.getDisplayName())).collect(Collectors.toList()),
                                        inRoom.getMapRotation(),
                                        inRoom.getMaxPlayer(),
                                        inRoom.getGameLength(),
                                        inRoom.getNumOfBots())).collect(Collectors.toList())));
                response = new Gson().toJson(outRooms.orElse(null));
                break;
            case "/ping":
                response = "ok";

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
