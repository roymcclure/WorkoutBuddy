package com.example.workoutbuddy;

public class MuscleGroupRow {

    private int ID;
    private String name;

    public MuscleGroupRow(int id, String name_) {
        this.ID = id;
        this.name = name_;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

}
