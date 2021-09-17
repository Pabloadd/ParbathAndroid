package com.parbathprojectmaps.util;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parbathprojectmaps.Models.ServiceLocation;
import com.parbathprojectmaps.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomAdpaterMapa extends ArrayAdapter<ServiceLocation> implements View.OnClickListener{

    ArrayList<ServiceLocation> serviceLocations;
    Context context;
    Location location_user;
    DecimalFormat format_float = new DecimalFormat("#.##");
    private static class ViewHolder{
        TextView txvUbicacion;
        TextView txvNombreLugar;
        TextView distancia;
    }

    public CustomAdpaterMapa(ArrayList<ServiceLocation> data, Context context, Location ls) {
        super(context, R.layout.row_item,data);
        this.serviceLocations = data;
        this.location_user = ls;
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        Object object = getItem(position);
        ServiceLocation services = (ServiceLocation)object;
    }

    public int lastPosition;

    public View getView(int position, View converView, ViewGroup parent){
        ServiceLocation serviceLocation = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (converView == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            converView = layoutInflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txvNombreLugar = (TextView) converView.findViewById(R.id.name);
            viewHolder.txvUbicacion = (TextView) converView.findViewById(R.id.type);
            viewHolder.distancia = (TextView) converView.findViewById(R.id.version_heading);

            result = converView;
            converView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) converView.getTag();
            result = converView;
        }

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.txvNombreLugar.setText(serviceLocation.getPlace_description().getName_place());
        viewHolder.txvUbicacion.setText(serviceLocation.getLocation_path());
        float distance_to = getDistanceFromUser(serviceLocation);
        Log.i("Info distance > ","distancia obtenida " + String.valueOf(distance_to));
        String message_distance = getmessageDistanceto(distance_to);
        viewHolder.distancia.setText(message_distance);

        return converView;
    }

    private String getmessageDistanceto(float distance_to){
        String message = "?";
        String distance_str = "";
        float auxdistance_to = 0;
        if (distance_to >= 1000.0){
            auxdistance_to = distance_to / 1000;
            distance_str = format_float.format(auxdistance_to);
            message = String.valueOf(distance_str) + " km";
        }else if (distance_to < 1000.0){
            distance_str = format_float.format(auxdistance_to);
            message = String.valueOf(distance_str) + " metros";
        }
        Log.i("Info distance > ","distancia obtenida " + message);
        return message;
    }

    private int getDistanceFromUser(ServiceLocation sl){
        float distance_to_marker = 0;
        Location marker_position = new Location("");
        marker_position.setLongitude(sl.getPosition_lat_long().getLongitude());
        marker_position.setLatitude(sl.getPosition_lat_long().getLatitude());
        distance_to_marker = location_user.distanceTo(marker_position);
        return (int) distance_to_marker;
    }

}
