package com.holamundo.pabloxd.practicemaps.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.holamundo.pabloxd.practicemaps.Models.ServiceLocation;
import com.holamundo.pabloxd.practicemaps.R;

import java.util.ArrayList;

public class CustomAdpaterMapa extends ArrayAdapter<ServiceLocation> implements View.OnClickListener{

    ArrayList<ServiceLocation> serviceLocations;
    Context context;

    private static class ViewHolder{
        TextView txvUbicacion;
        TextView txvNombreLugar;
        TextView distancia;
    }

    public CustomAdpaterMapa(ArrayList<ServiceLocation> data, Context context) {
        super(context, R.layout.row_item,data);
        this.serviceLocations = data;
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
        viewHolder.txvNombreLugar.setText(serviceLocation.getLugares().getNombrelugar());
        viewHolder.txvUbicacion.setText(serviceLocation.getUbicacion());
        viewHolder.distancia.setText("0");

        return converView;
    }


}
