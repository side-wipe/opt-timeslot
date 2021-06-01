package com.example.timeslot.domain;

/**
 * @program: timeslot
 * @description: ${description}
 * @author: qing.ye
 * @create: 2021-06-01 10:17
 **/
public class Room {

    private String name;

    private Room() {
    }

    public Room(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    // ********************************
    // Getters and setters
    // ********************************

    public String getName() {
        return name;
    }
}
