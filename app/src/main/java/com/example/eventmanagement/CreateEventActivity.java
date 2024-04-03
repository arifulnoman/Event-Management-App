package com.example.eventmanagement;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etName, etPlace,etDate,etCapacity,etBudget,etEmail,etDescription,etPhone;
    private RadioButton rdIndoor,rdOutdoor,rdOnline;
    private RadioGroup radioGroup;
    private Button btnSave,btnCancel;
    ArrayList<Event> events;
    String eventID = "";
    private String value = "";
    EventDB eventDB = new EventDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        etName = findViewById(R.id.etName);
        etPlace = findViewById(R.id.etPlace);
        radioGroup = findViewById(R.id.radioGroup);
        etDate = findViewById(R.id.etDate);
        etCapacity = findViewById(R.id.etCapacity);
        etBudget = findViewById(R.id.etBudget);
        etEmail = findViewById(R.id.etEmail);
        etDescription = findViewById(R.id.etDescription);
        etPhone = findViewById(R.id.etPhone);
        rdIndoor = findViewById(R.id.rdIndoor);
        rdOutdoor = findViewById(R.id.rdOutdoor);
        rdOnline = findViewById(R.id.rdOnline);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        Intent i = getIntent();
        if(i.hasExtra("EventID"))
        {
            String ID = i.getStringExtra("EventID");
            eventID=ID;
            String query = "SELECT * FROM events WHERE ID = '" + ID + "'";
            Cursor rows = eventDB.selectEvents(query);
            if (rows.getCount() == 0) {
                return;
            }
            rows.moveToNext();
            String id = rows.getString(0);
            System.out.println(id);
            if(ID.equals(id))
            {
                String name = rows.getString(1);
                String place = rows.getString(2);
                long dateTime = rows.getLong(3);
                int capacity = rows.getInt(4);
                double budget = rows.getDouble(5);
                String email = rows.getString(6);
                String phone = rows.getString(7);
                String description = rows.getString(8);
                String eventType = rows.getString(9);
                etName.setText(name);
                etPlace.setText(place);
                Date d = new Date(dateTime);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm");
                String formattedDate = dateFormat.format(d);
                String formattedTime = timeFormat.format(d);

                String formattedDateTime = formattedDate + " " + formattedTime;
                etDate.setText(formattedDateTime);
                etCapacity.setText(String.valueOf(capacity));
                etBudget.setText(String.valueOf(budget));
                etEmail.setText(email);
                etPhone.setText(phone);
                etDescription.setText(description);
                if(eventType.equals("Indoor"))
                {
                    rdIndoor.setChecked(true);
                }
                else if(eventType.equals("Outdoor"))
                {
                    rdOutdoor.setChecked(true);
                }
                else
                {
                    rdOnline.setChecked(true);
                }
            }
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder errMsgBuilder = new StringBuilder();
                String name = etName.getText().toString();
                String place = etPlace.getText().toString();
                String date = etDate.getText().toString();
                String email = etEmail.getText().toString();
                String description = etDescription.getText().toString();
                String phone = etPhone.getText().toString();
                String budgetText = etBudget.getText().toString();
                String capacityText = etCapacity.getText().toString();
                double budget = 0;
                int cap = 0;

                if(name.length()<4 || name.length()>12 || !isValidNameFormat(name)){
                    errMsgBuilder.append("Invalid Name\n");
                }
                if(!place.matches("^[a-zA-Z0-9]+$") || place.length() < 6 || place.length() > 64)
                {
                    errMsgBuilder.append("Invalid Place\n");
                }
                if (radioGroup.getCheckedRadioButtonId() == -1)
                {
                    errMsgBuilder.append("Must select a type of event\n");
                }
                if (!isValidDateTime(date)) {
                    errMsgBuilder.append("Input a valid date and time\n");
                }
                if (TextUtils.isEmpty(budgetText))
                {
                    errMsgBuilder.append("Budget is required\n");
                } else {
                    try {
                        budget = Double.parseDouble(budgetText);
                        if (budget < 1000) {
                            errMsgBuilder.append("Budget must be greater than 1000\n");
                        }
                    } catch (NumberFormatException e) {
                        errMsgBuilder.append("Invalid budget format\n");
                    }
                }
                if(TextUtils.isEmpty(capacityText))
                {
                    errMsgBuilder.append("Capacity is required\n");
                }
                else
                {
                    try {
                        cap = Integer.parseInt(etCapacity.getText().toString());
                        if(cap < 1)
                        {
                            errMsgBuilder.append("Capacity must be greater than 0\n");
                        }

                    }catch (NumberFormatException e) {
                        errMsgBuilder.append("Invalid Capacity format\n");
                    }
                }
                if (!isValidEmail(email)) {
                    errMsgBuilder.append("Input a valid e-mail\n");
                }
                if(phone.length() < 8 || phone.length() > 17)
                {
                    errMsgBuilder.append("Input a valid phone number\n");
                }
                else
                {
                    if (!isValidPhoneNumber(phone))
                    {
                        errMsgBuilder.append("Input a valid phone number\n");
                    }
                }
                if(description.length() < 10 || description.length() > 1000)
                {
                    errMsgBuilder.append("Description must be between 10-1000 letters\n");
                }
                if(errMsgBuilder.length() > 0)
                {
                    showErrorDialog(errMsgBuilder.toString());
                }
                else
                {
                    int radioNo = radioGroup.getCheckedRadioButtonId();
                    String eventType = "";
                    if (radioNo == R.id.rdIndoor) {
                        eventType = "Indoor";
                    }
                    else if(radioNo == R.id.rdOutdoor)
                    {
                        eventType = "Outdoor";
                    }
                    else
                    {
                        eventType = "Online";
                    }
                    long _date = convertDateStringToMillis(date);
                    if(_date != -1) {
                        value = name + "__" + place + "__" + phone + "__" + capacityText + "__" + date + "__" + email + "__" + eventType+"__"+description+"__"+budgetText;
                        if(eventID.equals(""))
                        {
                            try {
                                eventID = name+System.currentTimeMillis();
                                eventDB.insertEvent(eventID, name, place, _date, cap, budget, email, phone, description,eventType);
                                Toast.makeText(CreateEventActivity.this, "Event successfully saved", Toast.LENGTH_SHORT).show();

                            } catch (Exception ex) {
                                Toast.makeText(CreateEventActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        {
                            eventDB.updateEvent(eventID, name, place, _date, cap, budget, email, phone, description,eventType);
                            Toast.makeText(CreateEventActivity.this, "Event successfully updated", Toast.LENGTH_SHORT).show();
                        }

                        long reminderTimeMillis = _date - (15 * 60 * 1000);

                        Intent reminderIntent = new Intent(CreateEventActivity.this, ReminderReceiver.class);
                        PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(CreateEventActivity.this, 0, reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTimeMillis, reminderPendingIntent);

                        Toast.makeText(CreateEventActivity.this, "Reminder set for the event", Toast.LENGTH_SHORT).show();
                    }
//                    String keys[] = {"action", "sid", "semester","id","title","place","type","date_time","capacity","budget","email","phone","des"};
//                    String values[] = {"backup", "2019-1-60-196", "2023-2",eventID,name,place,eventType,""+date,""+capacityText,""+budgetText,email,phone,description};
//                    httpRequest(keys, values);
                    Intent i = new Intent(CreateEventActivity.this,MainActivity.class);
                    startActivity(i);
                }
                eventDB.close();
            }
        });
    }
    public boolean isValidNameFormat(String name) {
        Pattern pattern = Pattern.compile("^(?:[A-Z][a-z]*\\s)*[A-Z][a-z]*$");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
    public static boolean isValidDateTime(String dateTimeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm");
        try {
            Date inputDate = sdf.parse(dateTimeString);
            Date currentDate = new Date();

            if (inputDate.after(currentDate)) {
                return true;
            }
        } catch (ParseException e) {
            return false;
        }
        return false;
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
    public static long convertDateStringToMillis(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm");

        try {
            Date date = dateFormat.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://www.muthosoft.com/univ/cse489/index.php";
                String data="";
                try {
                    data=JSONParser.getInstance().makeHttpRequest(url,"POST",params);
                    System.out.println(data);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    System.out.println("Ok2");
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}