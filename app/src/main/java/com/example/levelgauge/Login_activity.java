package com.example.levelgauge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.CharacterPickerDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class Login_activity extends AppCompatActivity {
    public EditText enterlogin;
    public TextView tv_login;
    public TextView tv_forgotpass;
    public EditText enterpassword;
    private Button btn_enter;
    public Button btn_changepass;
    Integer status;
    public String loginpassword;
    public String login_place;
    public String pass_place;
    public int login_flag = 0;
    private TextView tv_aboutloginform;
    RadioGroup radioGroup;
    RadioButton radioButton;
    boolean passwordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        //enterlogin = findViewById(R.id.enterlogin);
        enterpassword = findViewById(R.id.enterpassword);
        status = 0;

        btn_enter = (Button) findViewById(R.id.btn_enter);
        tv_forgotpass = findViewById(R.id.tv_forgotpass);
        radioGroup = findViewById(R.id.radioGroup);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(Login_activity.this, "Восстановите", Toast.LENGTH_SHORT).show();
            }
        };
        /*
        String text = "Забыли пароль?";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(clickableSpan1, 0,13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) findViewById(R.id.tv_forgotpass)).setText(spannableString);
        ((TextView) findViewById(R.id.tv_forgotpass)).setMovementMethod(LinkMovementMethod.getInstance());

         */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(enterpassword, InputMethodManager.SHOW_IMPLICIT);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass_place = enterpassword.getText().toString();
                String user="USER";
                String admin="ADMIN";
                if(login_place.length()< 1 || pass_place.length() < 1){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Логин/Пароль Поле не должно быть пустым.",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                else{
                    if(login_place.equals(user)){
                        loginpassword = "U" + pass_place;
                        if(pass_place.length() != 6){
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "ПАРОЛЬ СОСТОИТ ИЗ 6 ЦИФР",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        Intent intent = new Intent(Login_activity.this, MainActivity.class);
                        intent.putExtra("Loginpass",loginpassword);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else if(login_place.equals(admin)){
                        loginpassword = "A" + pass_place;
                        if(pass_place.length() != 8){
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "ПАРОЛЬ СОСТОИТ ИЗ 8 ЦИФР",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        Intent intent = new Intent(Login_activity.this, MainActivity.class);
                        intent.putExtra("Loginpass",loginpassword);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Логин и пароль введены не верно.",
                                Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                /*
                if(status == 1){
                    if(login_flag == 1){
                        loginpassword = "u" + pass_place;
                        putvariable(loginpassword);
                    }
                    else if(login_flag == 2){
                        loginpassword = "a" + pass_place;
                        Intent intent = new Intent(Login_activity.this, MainActivity.class);
                        intent.putExtra("Loginpass",loginpassword);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                else if(status == 2){



                }
                else if(status == 3){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Логин/Пароль указаны неверно! Повторите попытку.",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    loginpass();
                }

                 */

            }
        });
    }

    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        //tv_login.setText(radioButton.getText());
        login_place = radioButton.getText().toString();
    }


    @Override
    public void onBackPressed(){
        //Intent a = new Intent(Intent.ACTION_MAIN);
        //a.addCategory(Intent.CATEGORY_HOME);
        //a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(a);
        Toast toast = Toast.makeText(getApplicationContext(),
                "ПОЖАЛУЙСТА ВВЕДИТЕ ЛОГИН И ПАРОЛЬ",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return;
    }
    /*
    private void loginpass(){
        ((TextView) findViewById(R.id.tv_aboutloginform)).setText("ВВЕДИТЕ ЛОГИН И ПАРОЛЬ");



        if (pass_place.length() > 1) {
            if(login_place.equals(user)){
                login_flag = 1;
                status = 1;
            }
            else if(login_place.equals(admin)){
                login_flag = 2;
                status = 1;
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Что то пошло не так",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else if (login_place.length() == 0 && pass_place.length() == 0) {
            status = 2;
        }else if(login_place.length() < 7 && pass_place.length() < 7){
            status = 3;
        }

    }

     */

/*
    private void loggin(){
        Toast toast = Toast.makeText(getApplicationContext(),
                "Введите ЛОГИН И ПАРОЛЬ",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();


        if(login_place.length() < 1 || pass_place.length() < 1){

        }
        else {
            login_place = enterlogin.getText().toString();
            pass_place = enterpassword.getText().toString();
        }
    }
    private void putvariable(String loginpas){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Loginpass",loginpas);
        setResult(RESULT_OK, intent);
        finish();
    }

 */
}