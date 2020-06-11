package com.catthoor.TotSServerSideCar.parsers;

import com.catthoor.TotSServerSideCar.Packet;
import com.catthoor.TotSServerSideCar.models.in.rooms.Rooms;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Optional;

public class PacketDataDeserializer {
    public static <T> Optional<T> deserialize(Packet packet, Class<T> t) {
        try {
            return Optional.of(new Gson().fromJson(packet.getData(), t));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
