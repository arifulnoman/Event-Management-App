package com.example.eventmanagement;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnExit,btnCreateNew;
    ListView lvEvents;
    ArrayList<Event> events;
    CustomEventAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvEvents = findViewById(R.id.listEvents);
        btnExit = findViewById(R.id.btnExit);
        btnCreateNew = findViewById(R.id.btnCreateNew);
        loadData();
    }
    public void onStart(){
        super.onStart();
        loadData();
//        String keys[] = {"action", "sid", "semester"};
//        String values[] = {"restore", "2019-1-60-196", "2023-2"};
//        httpRequest(keys, values);
    }

    protected void onResume(){
        super.onResume();
        loadData();
    }
    private void loadData() {
        events = new ArrayList<>();
        EventDB db = new EventDB(this);
        Cursor rows = db.selectEvents("SELECT * FROM events");
        if (rows.getCount() == 0) {
            return;
        }
        while (rows.moveToNext()) {
            String ID = rows.getString(0);
            String name = rows.getString(1);
            String place = rows.getString(2);
            long dateTime = rows.getLong(3);
            int capacity = rows.getInt(4);
            double budget = rows.getDouble(5);
            String email = rows.getString(6);
            String phone = rows.getString(7);
            String description = rows.getString(8);
            String eventType = rows.getString(9);

            Event e = new Event(ID, name, place, dateTime, capacity, budget, email, phone, description, eventType);
            events.add(e);
        }
        db.close();
        adapter = new CustomEventAdapter(this, events);
        lvEvents.setAdapter(adapter);

        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Intent i = new Intent(MainActivity.this,CreateEventActivity.class);
                i.putExtra("EventID", events.get(position).id);
                startActivity(i);
            }
        });

        lvEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String message = "Do you want to delete event - "+events.get(position).name +" ?";
                showDialog(message, "Delete Event", events.get(position).id);
                return true;
            }
        });
        btnCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,CreateEventActivity.class);
                startActivity(i);
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void showDialog(String message, String title, String id)
    {
        final String eventID = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);

        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EventDB eventDB = new EventDB(MainActivity.this);
                        eventDB.deleteEvent(eventID);
                        eventDB.close();
                        dialog.cancel();
                        loadData();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Event successfully Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
                    updateEventListByServerData(data);
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    private void updateEventListByServerData(String data){
        System.out.println("found");
        try{
            JSONObject jo = new JSONObject(data);
            if(jo.has("events")){
                events.clear();
                JSONArray ja = jo.getJSONArray("events");
                for(int i=0; i<ja.length(); i++){
                    JSONObject event = ja.getJSONObject(i);
                    String id = event.getString("id");
                    String title =  event.getString("title");
                    String place = event.getString("place");
                    String type = event.getString("type");
                    long date_time = event.getLong("date_time");
                    int capacity = event.getInt("capacity");
                    double budget = event.getDouble("budget");
                    String email = event.getString("email");
                    String phone = event.getString("phone");
                    String des = event.getString("des");

                    Event e = new Event(id,title,place,date_time,capacity,budget,email,phone,des,type);
                    events.add(e);
                }
            }
        }catch(Exception e){}
    }
}