package com.example.androidproject;

public class TaskItem {

    private int id;
    private static int idCounter = 0;
    private int day;
    private int month;
    private int year;

    public void setDate(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public TaskItem(int day, int month, int year, String title) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.title = title;
        this.id = idCounter++;
    }

    public String GetDate(){
        return String.valueOf(day) + "." + String.valueOf(month) + "." + String.valueOf(year);
    }

    public String GetTitle(){
        return title;
    }

    public int getId() {
        return id;
    }
}
