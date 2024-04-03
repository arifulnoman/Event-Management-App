package com.example.eventmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomEventAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final ArrayList<Event> events;

    public CustomEventAdapter(@NonNull Context context, @NonNull ArrayList<Event> events) {
        super(context, -1, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.event_row, parent, false);
        TextView eventName = rowView.findViewById(R.id.tvEventName);
        TextView eventDateTime = rowView.findViewById(R.id.tvEventDateTime);
        TextView eventPlaceName = rowView.findViewById(R.id.tvEventPlace);

        Event e = events.get(position);

        eventName.setText(e.name);
        Date d = new Date(e.datetime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String formattedDate = dateFormat.format(d);
        eventDateTime.setText(formattedDate);
        eventPlaceName.setText(e.place);
        return rowView;
    }
}
