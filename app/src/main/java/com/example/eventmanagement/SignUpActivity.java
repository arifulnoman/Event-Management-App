package com.example.eventmanagement;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private TextView btnToggle,tvToggleLabel,tvTittle,tvExit,tvGo;
    public EditText etName,etEmail,etPhone,etUserId,etPassword,etRePassword;
    private TableRow rowName,rowEmail,rowPhone,rowRepass;
    private boolean isLoginPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tvToggleLabel = findViewById(R.id.tvToggleLabel);
        tvTittle = findViewById(R.id.tvTittle);
        btnToggle = findViewById(R.id.btnToggle);
        rowName = findViewById(R.id.rowName);
        rowEmail = findViewById(R.id.rowEmail);
        rowPhone = findViewById(R.id.rowPhone);
        rowRepass = findViewById(R.id.rowRePassword);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etUserId = findViewById(R.id.etUserId);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);
        tvExit = findViewById(R.id.tvExit);
        tvGo = findViewById(R.id.tvGo);
        changeView();
        loadUserInfo();

        btnToggle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isLoginPage = !isLoginPage;
                changeView();
            }
        });
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String userID = etUserId.getText().toString();
                String password = etPassword.getText().toString();
                String rePassword = etRePassword.getText().toString();
                StringBuilder errMsgBuilder = new StringBuilder();
                if(!isValidNameFormat(name)){
                    errMsgBuilder.append("Invalid Name\n");
                }
                if (!isValidEmail(email)) {
                    errMsgBuilder.append("Input a valid e-mail\n");
                }
                if(!isValidPhoneNumber(phone)) {
                    errMsgBuilder.append("Input a valid phone number\n");
                }
                if(!password.equals(rePassword))
                {
                    errMsgBuilder.append("Password is not matched\n");
                }
                if(errMsgBuilder.length() > 0) {
                    showErrorDialog(errMsgBuilder.toString());
                }
                else
                {
                    saveUserInfo(name,email,phone,userID,password,rePassword);
                    Intent i = new Intent(SignUpActivity.this,CreateEventActivity.class);
                    startActivity(i);
                }
            }
        });
    }
    private void changeView()
    {
        if(isLoginPage)
        {
            rowName.setVisibility(View.GONE);
            rowEmail.setVisibility(View.GONE);
            rowPhone.setVisibility(View.GONE);
            rowRepass.setVisibility(View.GONE);
            tvTittle.setText("Login");
            tvToggleLabel.setText("Don't have an account");
            btnToggle.setText("Sign up");
        }
        else
        {
            rowName.setVisibility(View.VISIBLE);
            rowEmail.setVisibility(View.VISIBLE);
            rowPhone.setVisibility(View.VISIBLE);
            rowRepass.setVisibility(View.VISIBLE);
            tvTittle.setText("Signup");
            tvToggleLabel.setText("Already have an account");
            btnToggle.setText("Login");
        }
    }
    public boolean isValidNameFormat(String name) {
        Pattern pattern = Pattern.compile("^(?:[A-Z][a-z]*\\s)*[A-Z][a-z]*$");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^(\\+?880|0)1[1-9][0-9]{8}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }
    public void saveUserInfo(String name, String email, String phone, String userID,String password,String rePassword) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("userID", userID);
        editor.putString("password", password);
        editor.putString("rePassword", rePassword);
        editor.apply();
    }

    public void loadUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        String savedName = sharedPreferences.getString("name", "");
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPhone = sharedPreferences.getString("phone", "");
        String savedUserID = sharedPreferences.getString("userID", "");
        String savedPassword = sharedPreferences.getString("password", "");
        String savedRePassword = sharedPreferences.getString("rePassword", "");

        etName.setText(savedName);
        etEmail.setText(savedEmail);
        etPhone.setText(savedPhone);
        etUserId.setText(savedUserID);
        etPassword.setText(savedPassword);
        etRePassword.setText(savedRePassword);
    }
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errorMessage);
        builder.setTitle("Error");
        builder.setCancelable(true);
        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}