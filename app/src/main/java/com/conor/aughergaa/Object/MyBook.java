package com.conor.aughergaa.Object;

public class MyBook {

    String room;
    String path;
    String uid;
    String name;

    private long start;
    private long end;

    public MyBook(String room, String path, long start, long end,String uid,String name) {
        this.room = room;
        this.path = path;
        this.start = start;
        this.end = end;
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }

    public String getPath() {
        return path;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
}
