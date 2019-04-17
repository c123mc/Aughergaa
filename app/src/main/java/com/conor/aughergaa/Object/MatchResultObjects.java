package com.conor.aughergaa.Object;

public class MatchResultObjects {

    String team1first;
    String team1second;
    String team2first;
    String team2second;

    public MatchResultObjects(String team1first, String team1second, String team2first, String team2second) {
        this.team1first = team1first;
        this.team1second = team1second;
        this.team2first = team2first;
        this.team2second = team2second;
    }

    public String getTeam1first() {
        return team1first;
    }

    public String getTeam1second() {
        return team1second;
    }

    public String getTeam2first() {
        return team2first;
    }

    public String getTeam2second() {
        return team2second;
    }

}
