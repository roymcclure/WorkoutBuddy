package com.example.workoutbuddy;

public class ExerciseRow {

    private String name;
    private String comments;
    private String path_pic;
    private int id;
    private int muscleGroup_id;

    public ExerciseRow(int id_,String name_, int mg_id, String path_pic_, String comments_) {
        this.name = name_;
        this.comments = comments_;
        this.path_pic = path_pic_;
        this.id = id_;
        this.muscleGroup_id = mg_id;
    }

    public String getName() {
        return name;
    }

    public String getComments() {
        return comments;
    }

    public String getPath_pic() {
        return path_pic;
    }

    public int getId() {
        return id;
    }

    public int getMuscleGroup_id() {
        return muscleGroup_id;
    }

    public String toString() {
        return this.name;
    }

}
