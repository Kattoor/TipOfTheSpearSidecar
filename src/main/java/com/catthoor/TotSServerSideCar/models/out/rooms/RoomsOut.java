package com.catthoor.TotSServerSideCar.models.out.rooms;

import java.util.List;

public class RoomsOut {
    private List<RoomOut> rooms;

    public List<RoomOut> getRooms() {
        return rooms;
    }

    public RoomsOut(List<RoomOut> rooms) {
        this.rooms = rooms;
    }

    public RoomsOut() {

    }
}
