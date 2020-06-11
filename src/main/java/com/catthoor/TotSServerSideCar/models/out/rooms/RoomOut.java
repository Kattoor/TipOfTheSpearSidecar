package com.catthoor.TotSServerSideCar.models.out.rooms;

import com.catthoor.TotSServerSideCar.models.out.PlayerInfoOut;

import java.util.List;

public class RoomOut {
    private int gameMode;
    private String region;
    private String sessionType;
    private String map;
    private String roomName;
    private List<PlayerInfoOut> blueTeam;
    private List<PlayerInfoOut> redTeam;
    private List<String> mapRotation;
    private int maxPlayer;
    private int gameLength;
    private int numOfBots;

    public RoomOut(int gameMode, String region, String sessionType, String map, String roomName, List<PlayerInfoOut> blueTeam, List<PlayerInfoOut> redTeam, List<String> mapRotation, int maxPlayer, int gameLength, int numOfBots) {
        this.gameMode = gameMode;
        this.region = region;
        this.sessionType = sessionType;
        this.map = map;
        this.roomName = roomName;
        this.blueTeam = blueTeam;
        this.redTeam = redTeam;
        this.mapRotation = mapRotation;
        this.maxPlayer = maxPlayer;
        this.gameLength = gameLength;
        this.numOfBots = numOfBots;
    }

    public RoomOut() {

    }

    public int getGameMode() {
        return gameMode;
    }

    public String getRegion() {
        return region;
    }

    public String getSessionType() {
        return sessionType;
    }

    public String getMap() {
        return map;
    }

    public String getRoomName() {
        return roomName;
    }

    public List<PlayerInfoOut> getBlueTeam() {
        return blueTeam;
    }

    public List<PlayerInfoOut> getRedTeam() {
        return redTeam;
    }

    public List<String> getMapRotation() {
        return mapRotation;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public int getGameLength() {
        return gameLength;
    }

    public int getNumOfBots() {
        return numOfBots;
    }
}
