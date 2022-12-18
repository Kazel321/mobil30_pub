package com.example.lab21_lukyanov.models;

import android.view.View;

public class Exp
{
    public int id;
    String dateTime;
    String name;
    public String status;

    View v;

    public Exp(int id, String dateTime, String name, String status)
    {
        this.id = id;
        this.dateTime = dateTime.substring(0, dateTime.lastIndexOf('.')).replace('T', ' ').replace('-','.');
        this.name = name;
        this.status = status;
    }
}
