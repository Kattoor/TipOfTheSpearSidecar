package com.catthoor.TotSServerSideCar.models.in.rooms;

import com.catthoor.TotSServerSideCar.models.in.rooms.Room;

public class RoomInfoResponse {
    private boolean bSuccessful;
    private Room room;

    public boolean isbSuccessful() {
        return bSuccessful;
    }

    public Room getRoom() {
        return room;
    }
}
