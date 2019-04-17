package com.conor.aughergaa.Classes;

public class MyMatchClass {

    String name_team_1;
    String name_team_2;
    String ref;
    long times_stamp;

    public MyMatchClass(String name_team_1, String name_team_2, long times_stamp,String ref) {

        this.ref = ref;
        this.name_team_1 = name_team_1;
        this.name_team_2 = name_team_2;
        this.times_stamp = times_stamp;
    }


    public String getRef() {
        return ref;
    }

    public String getName_team_1() {
        return name_team_1;
    }

    public String getName_team_2() {
        return name_team_2;
    }

    public long getTimes_stamp() {
        return times_stamp;
    }

}
