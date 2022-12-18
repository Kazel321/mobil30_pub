package com.example.lab21_lukyanov;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.example.lab21_lukyanov.models.SpecLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SpectraView extends SurfaceView {

    ArrayList<SpecLine> lines = new ArrayList<>();

    float bg_lum = 0.25f;

    boolean have_background = false;

    float wlen_min = 380.0f;
    float wlen_max = 780.0f;

    Activity ctx;

    JSONArray arr;

    public int pos = 0;

    Paint p;

    public boolean isDivisions;
    public float intensity;

    public SpectraView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        p = new Paint();
        //setWillNotDraw(false);
    }

    void download_background(final SpectraView me, int steps)
    {
        ApiHelper req = new ApiHelper(ctx)
        {
            @Override
            public void on_ready(String res) {
                try
                {
                    arr = new JSONArray(res);
                }
                catch (JSONException ex)
                {

                }
                have_background = true;
                me.invalidate();
                //super.on_ready(res);
            }
        };

        JSONObject obj = new JSONObject();
        try {
            obj.put("nm_from", wlen_min);
            obj.put("nm_to", wlen_max);
            obj.put("steps", steps);
        }
        catch (JSONException ex)
        {

        }

        req.send("/rpc/nm_to_rgb_range", obj.toString());
    }

    float lerp(float a, float b, float t)
    {
        return a + (b - a) * t;
    }

    float unlerp(float x, float x0, float x1)
    {
        return (x - x0) / (x1 - x0);
    }

    float map(float x, float x0, float x1, float a, float b)
    {
        float t = unlerp(x, x0, x1);
        return lerp(a, b, t);
    }

    float last_x = 0.0f;
    int img_w;
    boolean moving = false;

    int[] divisions = new int[] {380, 440, 490, 510, 580, 645, 780};

    void savePos(int position)
    {
        g.db.saveLastPosition(wlen_min, wlen_max, position);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                last_x = event.getX();
                moving = true;
                return true;

            case MotionEvent.ACTION_UP:
                moving = false;
                have_background = false;
                invalidate();
                savePos(pos);
                return true;

            case MotionEvent.ACTION_MOVE:
                float new_x = event.getX();
                float delta_x = new_x - last_x;
                float delta_nm = wlen_max - wlen_min;
                float nm_per_pixel = delta_nm / img_w;

                wlen_min -= delta_x * nm_per_pixel;
                wlen_max -= delta_x * nm_per_pixel;

                last_x = event.getX();
                invalidate();
                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        img_w = w;

        p.setStrokeWidth(1);
        canvas.drawColor(Color.BLACK);

        if (have_background == false)
            download_background(this, w);
        else
        {
            if (moving == false)
                for (int i = 0; i < arr.length(); i+=(int)(10.0f/intensity))
                {
                    try
                    {
                        //Log.println(Log.ASSERT, "arr length", String.valueOf(arr.length()));
                        JSONObject obj = arr.getJSONObject(i);
                        int r = (int) (obj.getDouble("red") * bg_lum * 255.0);
                        int g = (int) (obj.getDouble("green") * bg_lum * 255.0);
                        int b = (int) (obj.getDouble("blue") * bg_lum * 255.0);
                        p.setARGB(255, r, g, b);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    canvas.drawLine(i, 0, i, h, p);
                }
        }

        for (int i = 0; i < lines.size(); i++)
        {
            /*Log.println(Log.ASSERT, "arr length", String.valueOf(lines.size()));
            Log.println(Log.ASSERT, "wmin", String.valueOf(wlen_min));
            Log.println(Log.ASSERT, "wmax", String.valueOf(wlen_max));*/

            SpecLine sl = lines.get(i);
            float x = map(sl.wavelength, wlen_min, wlen_max, 0, w - 1);
            sl.setPaintColor(p);
            canvas.drawLine(x, 0, x, h, p);
        }

        if (isDivisions)
        for (int i = 0; i < divisions.length; i++)
        {
            float x = map(divisions[i], wlen_min, wlen_max, 0, w - 1);
            p.setColor(Color.WHITE);
            p.setStrokeWidth(5);
            canvas.drawLine(x, 0, x, h, p);
        }

        //super.onDraw(canvas);
    }
}
