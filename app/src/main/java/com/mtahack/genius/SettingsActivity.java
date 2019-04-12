package com.mtahack.genius;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    private Spinner sendSMSTime;
    private Switch sendLocation;
    private TextView manageContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getSupportActionBar().hide();
        sendSMSTime = (Spinner) findViewById(R.id.sendSMSSpinner);
        sendLocation = (Switch) findViewById(R.id.switchLocation);
        manageContacts = (TextView) findViewById(R.id.manageContacts);
        Settings.init(SettingsActivity.this);
        sendSMSTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    int value = sendSMSTime.getSelectedItemPosition();
                    int[] parseValues = new int[]{30, 60, 120, 180, 240};
                    Settings.getInstance().setAlaramTimeInSeconds(parseValues[value]);
                } catch (Exception e) {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sendLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try{
                    Settings.getInstance().setSendLocation(isChecked);
                } catch (Exception e) {e.printStackTrace();}
            }
        });

        manageContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SettingsActivity.this,ContactsSettingsActivity.class);
                startActivity(intent);
            }
        });
        loadConfiguration();
    }

    public void loadConfiguration(){
        Settings settings = null;
        try{
            settings = Settings.getInstance();
        } catch (Exception e) {}
        if (settings == null) {
            return;
        }
        sendSMSTime.setSelection((int)Math.floor(settings.getAlarmTimeInSeconds() / 60));
        sendLocation.setChecked(settings.getSendLoaction());
    }
}
