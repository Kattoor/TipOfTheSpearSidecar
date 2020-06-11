package com.catthoor.TotSServerSideCar.models.in.rooms;

import com.catthoor.TotSServerSideCar.models.PlayerInfo;
import com.catthoor.TotSServerSideCar.models.WeaponInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Room {
    @SerializedName("roomID")
    private String roomId;
    private String dsc;
    private int gameMode;
    private String region;
    private String sessionType;
    private String map;
    private String roomName;
    private String steamSession;
    private String password;
    private boolean bSaveChatLog;
    private boolean bEnableWordCensorship;
    private boolean bAllowSpectator;
    private int maxScore;
    private int pspTakingTime;
    private int goalTakenTime;
    private List<PlayerInfo> blueTeam;
    private List<PlayerInfo> redTeam;
    private List<WeaponInfo> weaponRules;
    private List<PlayerInfo> blackList;
    private List<String> mapRotation;
    private int maxPlayer;
    private int warmupTime;
    private int injuryTime;
    private int timeBetweenMatches;
    private int spawnProtectionTime;
    private int gameLength;
    private int numOfBots;

    public String getRoomId() {
        return roomId;
    }

    public String getDsc() {
        return dsc;
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

    public String getSteamSession() {
        return steamSession;
    }

    public String getPassword() {
        return password;
    }

    public boolean isbSaveChatLog() {
        return bSaveChatLog;
    }

    public boolean isbEnableWordCensorship() {
        return bEnableWordCensorship;
    }

    public boolean isbAllowSpectator() {
        return bAllowSpectator;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getPspTakingTime() {
        return pspTakingTime;
    }

    public int getGoalTakenTime() {
        return goalTakenTime;
    }

    public List<PlayerInfo> getBlueTeam() {
        return blueTeam;
    }

    public List<PlayerInfo> getRedTeam() {
        return redTeam;
    }

    public List<WeaponInfo> getWeaponRules() {
        return weaponRules;
    }

    public List<PlayerInfo> getBlackList() {
        return blackList;
    }

    public List<String> getMapRotation() {
        return mapRotation;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public int getWarmupTime() {
        return warmupTime;
    }

    public int getInjuryTime() {
        return injuryTime;
    }

    public int getTimeBetweenMatches() {
        return timeBetweenMatches;
    }

    public int getSpawnProtectionTime() {
        return spawnProtectionTime;
    }

    public int getGameLength() {
        return gameLength;
    }

    public int getNumOfBots() {
        return numOfBots;
    }
}
