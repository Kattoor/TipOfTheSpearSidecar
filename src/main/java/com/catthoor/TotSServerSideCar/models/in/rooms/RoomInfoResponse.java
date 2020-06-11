package com.catthoor.TotSServerSideCar.models.in.rooms;

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
