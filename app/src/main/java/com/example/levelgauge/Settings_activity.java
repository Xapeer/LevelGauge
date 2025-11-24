package com.example.levelgauge;

import static com.example.levelgauge.MainActivity.UUID_LED0_STATE;
import static com.example.levelgauge.MainActivity.UUID_LED_SERVICE;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Settings_activity extends AppCompatActivity {

    private TextView tv_currentprogstatus;
    private Button btn_changena;
    private Button btn_changepassword;
    private Button btn_changenapssserver;
    private Button btn_currentpass;
    public EditText edit_settings;
    private ImageButton btn_confirmsettings;
    public String userSetting;
    public String currentServerPass;
    public String edit_place;
    public String rightsUser;
    public String getUserRight;
    public String device_settings;
    private final static int REQUEST_CODE_CURRENTSERVERPASS = 9;
    public final static String EXTRA_DATA = "EXTRA_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btn_currentpass = findViewById(R.id.btn_currentpass);
        btn_changena = findViewById(R.id.btn_changena);
        btn_changepassword = findViewById(R.id.btn_changepassword);
        btn_changenapssserver = findViewById(R.id.btn_changenapssserver);
        btn_confirmsettings = findViewById(R.id.btn_confirmsettings);
        edit_settings = findViewById(R.id.edit_settings);
        btn_changena.setVisibility(View.INVISIBLE);
        Intent i = getIntent();
        rightsUser = i.getStringExtra("Userrights");
        getUserRight = rightsUser;
        invisibleinputplace();





        ////////////////////////////////////////////////////////////////
        //СМЕНИТЬ НАЗВАНИЕ УСТРОЙСТВА
        btn_changena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invisibleallbutton();
                ((TextView) findViewById(R.id.tv_currentprogstatus)).setText("СМЕНИТЬ НАЗВАНИЕ УСТРОЙСТВА");
                tv_currentprogstatus.setTextSize(30);
                visibleinputplace();
                btn_confirmsettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit_place = edit_settings.getText().toString();
                        if(edit_place.length()< 1){
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Логин/Пароль Поле не должно быть пустым.",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        else{
                            userSetting = device_settings;
                            Intent intent = new Intent(Settings_activity.this, MainActivity.class);
                            intent.putExtra("user_settings",userSetting);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
            }
        });

        /////////////////////////////////////////////////////////////////
        //СМЕНИТЬ ПАРОЛЬ ДЛЯ УСТРОЙСТВА
        btn_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invisibleallbutton();
                ((TextView) findViewById(R.id.tv_currentprogstatus)).setText("СМЕНИТЬ ПАРОЛЬ ДЛЯ ПОЛЬЗОВАТЕЛЯ");
                visibleinputplace();
                btn_confirmsettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit_place = edit_settings.getText().toString();
                        if(edit_place.length()< 1){
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Логин/Пароль Поле не должно быть пустым.",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        else{
                            if(edit_place.length() != 6){
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "ПАРОЛЬ СОСТОИТ ИЗ 6 ЦИФР",
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }
                            device_settings = "l"+ "c" + edit_place + "\n";
                            userSetting = device_settings;
                            Intent intent = new Intent(Settings_activity.this, MainActivity.class);
                            intent.putExtra("user_settings",userSetting);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });

                //edit_settings.setVisibility(View.VISIBLE);
                //btn_confirmsettings.setVisibility(View.VISIBLE);
            }
        });


        ////////////////////////////////////////////////////////////////
        //СМЕНИТЬ ПАРОЛЬ СЕРВЕРА
        btn_changenapssserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invisibleallbutton();
                ((TextView) findViewById(R.id.tv_currentprogstatus)).setText("СМЕНИТЬ ПАРОЛЬ ДЛЯ СЕРВЕРА");
                visibleinputplace();
                btn_currentpass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String device_settings = "P" + "\n";
                        userSetting = device_settings;
                        Intent intent = new Intent(Settings_activity.this, MainActivity.class);
                        intent.putExtra("user_settings",userSetting);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                btn_confirmsettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit_place = edit_settings.getText().toString();
                        if(edit_place.length()< 1){
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Логин/Пароль Поле не должно быть пустым.",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        else{
                            String device_settings = "C" + edit_place + "\n";
                            userSetting = device_settings;
                            Intent intent = new Intent(Settings_activity.this, MainActivity.class);
                            intent.putExtra("user_settings",userSetting);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
            }
        });

        //UserSettings
    }



    private void invisibleallbutton(){
        btn_changena.setVisibility(View.INVISIBLE);
        btn_changepassword.setVisibility(View.INVISIBLE);
        btn_changenapssserver.setVisibility(View.INVISIBLE);
    }

    private void invisibleinputplace(){
        edit_settings.setVisibility(View.INVISIBLE);
        btn_confirmsettings.setVisibility(View.INVISIBLE);
    }

    private void visibleinputplace(){
        edit_settings.setVisibility(View.VISIBLE);
        btn_confirmsettings.setVisibility(View.VISIBLE);
    }

    private void test(){


    }

}