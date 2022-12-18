package com.example.lab21_lukyanov;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.lab21_lukyanov.models.ExpAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {
    public String base = "http://spectra.spbcoit.ru/lab/spectra/api";
    Activity ctx;
    public String sessionKey = "null";
    public boolean isEnd;
    public Canvas canvas;

    @SuppressLint("SuspiciousIndentation")
    public ApiHelper(Activity ctx)
    {
        this.ctx = ctx;
        if (g.db.getEndPoint() != null)
            base = g.db.getEndPoint();
    }

    public ApiHelper(Activity ctx, Canvas canvas)
    {
        this.ctx = ctx;
        this.canvas = canvas;
        /*if (g.db.getEndPoint() != null)
            base = g.db.getEndPoint();*/
    }

    public void on_ready(String res)
    {

    }

    String http_get(String req, String body) throws IOException
    {
        URL url = new URL(req);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("accept", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream());
        out.write(body.getBytes());
        out.flush();

        BufferedInputStream inp = new BufferedInputStream(con.getInputStream());

        byte[] buf = new byte[512];
        String res = "";

        while (true)
        {
            int num = inp.read(buf);
            if(num < 0) break;

            res += new String(buf, 0, num);
        }

        con.disconnect();

        return res;
    }

    public class NetOp implements Runnable
    {
        public String req;
        public String body;

        public void run()
        {
            try
            {
                final String res = http_get(base + req, body);

                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        on_ready(res);
                    }
                });
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void send(String req, String body)
    {
        NetOp nop = new NetOp();
        nop.body = body;
        nop.req = req;

        Thread th = new Thread(nop);
        th.start();
    }
}
