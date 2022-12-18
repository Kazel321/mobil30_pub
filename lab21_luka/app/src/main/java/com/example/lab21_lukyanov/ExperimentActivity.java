package com.example.lab21_lukyanov;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.lab21_lukyanov.models.ChemElement;
import com.example.lab21_lukyanov.models.Exp;
import com.example.lab21_lukyanov.models.ExpAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ExperimentActivity extends AppCompatActivity {

    ListView lstExp;
    ArrayList<Exp> exps = new ArrayList<>();
    ExpAdapter adp;
    EditText txtAPI;
    AlertDialog alertDialog;
    ArrayAdapter<String> tagAdapter;
    Spinner spnTags;

    Activity ctx;
    String tag;

    Intent i;
    ArrayList<Timer> timers = new ArrayList<>();

    Bundle bundle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        ctx = this;
        bundle = savedInstanceState;

        txtAPI = findViewById(R.id.txtAPI);
        g.db = new DB(this, "Settings.db", null, 1);
        String api = g.db.getEndPoint();
        if (api != null) txtAPI.setText(api);

        lstExp = findViewById(R.id.lstExp);
        adp = new ExpAdapter(this, exps);
        lstExp.setAdapter(adp);
        lstExp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                i = new Intent(ctx, MainActivity.class);
                startActivityForResult(i, 0);
            }
        });

        tagAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        LayoutInflater dialogLayout = LayoutInflater.from(this);
        View dialogView = dialogLayout.inflate(R.layout.dialog_tag, null);
        alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        spnTags = dialogView.findViewById(R.id.spnTags);
        Button btnReturnTags = dialogView.findViewById(R.id.btnReturnTags);

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
                        tagAdapter.add(arr.getString(i));
                    }
                    spnTags.setAdapter(tagAdapter);
                    String dbTag = g.db.getTag();
                    if (dbTag != null)
                    {
                        spnTags.setSelection(tagAdapter.getPosition(dbTag));
                        tag = spnTags.getSelectedItem().toString();
                    }
                    update();
                }
                catch (JSONException e) {}
                //super.on_ready(res);
            }
        };
        req.send("/rpc/get_tags", "{}");

        spnTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                tag = spnTags.getSelectedItem().toString();
                g.db.saveTag(tag);
                update();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnReturnTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    void update()
    {
        for (int j = 0; j < timers.size(); j++)
        {
            timers.get(j).cancel();
        }
        exps.clear();

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
                        JSONObject obj = arr.getJSONObject(i);
                        int id = obj.getInt("id");
                        String date = obj.getString("created_at");
                        String name = obj.getString("note");
                        String status = obj.getString("status");

                        Exp exp = new Exp(id, date, name, status);
                        exps.add(exp);

                        if (!status.equals("done"))
                        {
                            updateExps(i);
                        }
                    }
                    adp.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        req.send("/rpc/get_experiments", "{\"tagname\": \"" + tag + "\"}");
    }

    void updateExps(int position)
    {
        Timer timer = new Timer();
        TimerTask tt = new TimerTask()
        {
            @Override
            public void run()
            {
                int expId = exps.get(position).id;
                ApiHelper req = new ApiHelper(ctx)
                {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void on_ready(String res)
                    {
                        if (res.isEmpty()) return;
                        exps.get(position).status = res.substring(1, res.length()-1);
                        if (exps.get(position).status.equals("done"))
                        {
                            adp.updateItem(position);
                            timer.cancel();
                        }
                        else adp.updateItem(position);
                        /*if (res.equals("done"))
                        {
                            adp.updateItem(position);
                            timer.cancel();
                        }
                        else
                        {
                            adp.updateItem(position);
                        }*/
                    }
                };
                req.send("/rpc/get_status", "{\"experiment\": \"" + expId + "\"}");
            }
        };
        timers.add(timer);
        timers.get(timers.indexOf(timer)).schedule(tt, 0, 1000);
    }

    public void open_dialog_tags(View v)
    {
        alertDialog.show();
    }

    public void save_api(View v)
    {
        String api = txtAPI.getText().toString();
        g.db.saveEndPoint(api);
        tagAdapter.clear();
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
                        tagAdapter.add(arr.getString(i));
                    }
                    spnTags.setAdapter(tagAdapter);
                    String dbTag = g.db.getTag();
                    if (dbTag != null)
                    {
                        spnTags.setSelection(tagAdapter.getPosition(dbTag));
                        tag = spnTags.getSelectedItem().toString();
                    }
                    update();
                }
                catch (JSONException e) {}
            }
        };
        req.send("/rpc/get_tags", "{}");
    }
}