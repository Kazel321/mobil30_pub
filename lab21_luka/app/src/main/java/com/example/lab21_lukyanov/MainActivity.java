package com.example.lab21_lukyanov;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.lab21_lukyanov.models.ChemElement;
import com.example.lab21_lukyanov.models.SpecLine;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    Spinner sp;
    SpectraView sv;
    Activity context;
    ArrayAdapter<ChemElement> adp;

    AlertDialog alertDialog;
    CheckBox chkDivisions;
    SeekBar seekBarIntensity;
    TextView labIntensity;
    Button btnRet;

    ChemElement el;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        g.db = new DB(this, "Settings.db", null, 1);

        sv = findViewById(R.id.spectraView);
        context = this;

        sp = findViewById(R.id.spn);
        adp = new ArrayAdapter<ChemElement>(this, android.R.layout.simple_list_item_1);

        LayoutInflater dialogLayout = LayoutInflater.from(this);
        View dialogView = dialogLayout.inflate(R.layout.dialog_settings, null);
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        chkDivisions = dialogView.findViewById(R.id.chkDivisions);
        seekBarIntensity = dialogView.findViewById(R.id.seekBarIntensity);
        labIntensity = dialogView.findViewById(R.id.tvIntensity);
        btnRet = dialogView.findViewById(R.id.btnCancelDialogSettings);

        Integer[] DisplaySettings = g.db.getDisplaySettings();
        if (DisplaySettings != null)
        {
            if (DisplaySettings[0] == 0) chkDivisions.setChecked(false);
            else chkDivisions.setChecked(true);
            seekBarIntensity.setProgress(DisplaySettings[1]);
        }

        sv.isDivisions = chkDivisions.isChecked();
        sv.intensity = (float)seekBarIntensity.getProgress();
        labIntensity.setText("Gradient Intensity: (" + seekBarIntensity.getProgress() + ")");

        chkDivisions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (chkDivisions.isChecked()) g.db.saveDivisions(1);
                else g.db.saveDivisions(0);
                sv.isDivisions = chkDivisions.isChecked();
                sv.invalidate();
            }
        });

        seekBarIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                labIntensity.setText("Gradient Intensity: (" + seekBarIntensity.getProgress() + ")");
                g.db.saveIntensity(seekBarIntensity.getProgress());
                sv.intensity = (float)seekBarIntensity.getProgress();
                sv.have_background = false;
                sv.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                alertDialog.cancel();
            }
        });

        ApiHelper req = new ApiHelper(this)
        {
            @Override
            public void on_ready(String res)
            {
                try
                {
                    JSONArray arr = new JSONArray(res);
                    for (int i = 0; i < arr.length(); i++)
                    {
                        adp.add(new ChemElement(arr.getJSONObject(i)));
                    }
                    sp.setAdapter(adp);
                    Float[] lastPos = g.db.getLastPosition();
                    if (lastPos != null)
                    {
                        sv.wlen_min = lastPos[0];
                        sv.wlen_max = lastPos[1];
                        sp.setSelection((int)(lastPos[2]/1.0f));
                        sv.pos = (int)(lastPos[2]/1.0f);
                        sv.invalidate();
                    }
                    adp.notifyDataSetChanged();
                }
                catch (JSONException e) {}
                //super.on_ready(res);
            }
        };

        req.send("/rpc/get_elements", "{}");

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                el = (ChemElement) sp.getSelectedItem();
                pos = position;
                sv.pos = pos;
                sv.savePos(pos);

                ApiHelper req = new ApiHelper(context)
                {
                    @Override
                    public void on_ready(String res)
                    {
                        try
                        {
                            JSONArray arr = new JSONArray(res);
                            for (int i = 0; i < arr.length(); i++)
                            {
                                sv.lines.add(new SpecLine(arr.getJSONObject(i)));
                                sv.invalidate();
                            }
                        }
                        catch (JSONException ex)
                        {

                        }
                    }
                };
                sv.lines.clear();
                req.send("/rpc/get_lines", "{\"atomic_num\": " + el.atomic_num + "}");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sv.ctx = this;
        sv.setWillNotDraw(false);
        sv.invalidate();
    }

    public void zoom_in(View v)
    {
        float wlen_center = (sv.wlen_max + sv.wlen_min) / 2.0f;
        float wlen_dist = wlen_center - sv.wlen_min;
        float zoom_percent = 0.1f;
        sv.wlen_min += wlen_dist * zoom_percent;
        sv.wlen_max -= wlen_dist * zoom_percent;
        sv.have_background = false;
        sv.invalidate();
        sv.savePos(pos);
    }

    public void zoom_out(View v)
    {
        float wlen_center = (sv.wlen_max + sv.wlen_min) / 2.0f;
        float wlen_dist = wlen_center - sv.wlen_min;
        float zoom_percent = 0.1f;
        sv.wlen_min -= wlen_dist * zoom_percent;
        sv.wlen_max += wlen_dist * zoom_percent;
        sv.have_background = false;
        sv.invalidate();
        sv.savePos(pos);
    }

    public void show_settings(View v)
    {
        alertDialog.show();
    }

    public void back_to_exp(View v)
    {
        finish();
    }
}