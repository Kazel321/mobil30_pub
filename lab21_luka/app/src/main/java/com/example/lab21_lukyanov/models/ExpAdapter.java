package com.example.lab21_lukyanov.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.lab21_lukyanov.ApiHelper;
import com.example.lab21_lukyanov.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ExpAdapter extends BaseAdapter
{
    //Context ctx;
    Activity ctx;
    ArrayList<Exp> exps;
    DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public ExpAdapter(Activity ctx, ArrayList<Exp> exps)
    {
        this.ctx = ctx;
        this.exps = exps;
    }

    @Override
    public int getCount()
    {
        return exps.size();
    }

    @Override
    public Object getItem(int position)
    {
        return exps.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Exp exp = exps.get(position);
        convertView = LayoutInflater.from(ctx.getApplicationContext()).inflate(R.layout.item_experiment, parent, false);
        TextView expName = convertView.findViewById(R.id.tvExpName);

        Date date = null;
        try
        {
            date = dateFormat.parse(exp.dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, 3);


        TextView expDate = convertView.findViewById(R.id.tvExpDate);
        TextView expTimeProcess = convertView.findViewById(R.id.txExpTimeProcess);
        TextView expStatus = convertView.findViewById(R.id.tvExpStatus);
        ImageView img = convertView.findViewById(R.id.imgStatus);
        exp.v = convertView;

        DateFormat dateFormat1 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+6"));

        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        expName.setText(exp.name);
        expDate.setText("Дата: " + dateFormat1.format(date));
        String status = exp.status;

        Date dateNow;
        Date dateStart;
        long time;
        int days;
        int hours;
        int minutes;
        int seconds;
        int ostatok;

        switch (status)
        {
            case "created":
                dateNow = Date.from(Instant.now());
                dateStart = null;
                try
                {
                    dateStart = dateFormat.parse(exp.dateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                time = (long)(dateNow.getTime()- dateStart.getTime()) / 1000 - 10800;

                days = (int)time / 86400;
                ostatok = (int)time % 86400;
                hours = ostatok / 3600;
                ostatok = ostatok % 3600;
                minutes = ostatok / 60;
                seconds = ostatok % 60;
                //expTimeProcess.setText(dateFormat.format(Date.from(Instant.now())) + " - " + exp.dateTime);
                expTimeProcess.setText("Time spent: " + days + "d " + hours + "h " + minutes + "m " + seconds + "s");
                expStatus.setTextColor(Color.YELLOW);
                img.setImageResource(R.drawable.icon_created);
                break;
            case "running":
                dateNow = Date.from(Instant.now());
                dateStart = null;
                try
                {
                    dateStart = dateFormat.parse(exp.dateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                time = (long)(dateNow.getTime() - dateStart.getTime()) / 1000 - 10800;

                days = (int)time / 86400;
                ostatok = (int)time % 86400;
                hours = ostatok / 3600;
                ostatok = ostatok % 3600;
                minutes = ostatok / 60;
                seconds = ostatok % 60;
                //expTimeProcess.setText(dateFormat.format(Date.from(Instant.now())) + " - " + exp.dateTime);
                expTimeProcess.setText("Time spent: " + days + "d " + hours + "h " + minutes + "m " + seconds + "s");
                expStatus.setTextColor(Color.BLACK);
                img.setImageResource(R.drawable.icon_running);
                break;
            case "done":
                expTimeProcess.setText("");
                expStatus.setTextColor(Color.GREEN);
                img.setImageResource(R.drawable.icon_done);
                break;
        }
        expStatus.setText(status);
        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateItem(int position)
    {
        Exp exp = exps.get(position);
        if (exp == null) return;
        TextView expTimeProcess = exp.v.findViewById(R.id.txExpTimeProcess);
        TextView expStatus = exp.v.findViewById(R.id.tvExpStatus);
        ImageView img = exp.v.findViewById(R.id.imgStatus);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        Date dateNow;
        Date dateStart;
        long time;
        int days;
        int hours;
        int minutes;
        int seconds;
        int ostatok;

        String res = exp.status;

        switch (res)
        {
            case "created":
                dateNow = Date.from(Instant.now());
                dateStart = null;
                try
                {
                    dateStart = dateFormat.parse(exp.dateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                time = (dateNow.getTime() - dateStart.getTime()) / 1000;
                days = (int)time / 86400;
                ostatok = (int)time % 86400;
                hours = ostatok / 3600;
                ostatok = ostatok % 3600;
                minutes = ostatok / 60;
                seconds = ostatok % 60;
                //expTimeProcess.setText(dateFormat.format(Date.from(Instant.now())) + " - " + exp.dateTime);
                expTimeProcess.setText("Time spent: " + days + "d " + hours + "h " + minutes + "m " + seconds + "s");
                expStatus.setTextColor(Color.YELLOW);
                img.setImageResource(R.drawable.icon_created);
                break;
            case "running":
                dateNow = Date.from(Instant.now());
                dateStart = null;
                try
                {
                    dateStart = dateFormat.parse(exp.dateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                time = (long)(dateNow.getTime() - dateStart.getTime()) / 1000;
                days = (int)time / 86400;
                ostatok = (int)time % 86400;
                hours = ostatok / 3600;
                ostatok = ostatok % 3600;
                minutes = ostatok / 60;
                seconds = ostatok % 60;
                //expTimeProcess.setText(dateFormat.format(Date.from(Instant.now())) + " - " + exp.dateTime);
                expTimeProcess.setText("Time spent: " + days + "d " + hours + "h " + minutes + "m " + seconds + "s");
                expStatus.setTextColor(Color.BLACK);
                img.setImageResource(R.drawable.icon_running);
                break;
            case "done":
                expTimeProcess.setText("");
                expStatus.setTextColor(Color.GREEN);
                img.setImageResource(R.drawable.icon_done);
                break;
        }
        notifyDataSetChanged();

        /*ApiHelper req = new ApiHelper(ctx)
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void on_ready(String res)
            {
                Date dateNow;
                Date dateStart;
                long time;

                switch (res)
                {
                    case "created":
                        expTimeProcess.setText(dateFormat.format(Date.from(Instant.now())) + " - " + exp.dateTime);
                        expStatus.setTextColor(Color.YELLOW);
                        img.setImageResource(R.drawable.icon_created);
                        break;
                    case "running":
                        dateNow = Date.from(Instant.now());
                        dateStart = null;
                        try
                        {
                            dateStart = dateFormat.parse(exp.dateTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        time = (dateNow.getTime() - dateStart.getTime()) / 1000;
                        //expTimeProcess.setText(dateFormat.format(Date.from(Instant.now())) + " - " + exp.dateTime);
                        expTimeProcess.setText("Time remaining: " + time + " s");
                        expStatus.setTextColor(Color.BLACK);
                        img.setImageResource(R.drawable.icon_running);
                        break;
                    case "done":
                        expTimeProcess.setText("");
                        expStatus.setTextColor(Color.GREEN);
                        img.setImageResource(R.drawable.icon_done);
                        break;
                }
                notifyDataSetChanged();
            }
        };
        req.send("/rpc/get_status", "{\"experiment\": " + exp.id + "}");*/
    }
}
