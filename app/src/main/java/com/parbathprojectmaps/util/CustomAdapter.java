package com.parbathprojectmaps.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parbathprojectmaps.Models.ToiletPlaces;
import com.parbathprojectmaps.R;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<ToiletPlaces>
        implements View.OnClickListener {

    private ArrayList<ToiletPlaces> toiletPlacesArraList;
    Context context;


    private static class ViewHolder{
        TextView txtlugar;
        TextView txtubicacion;
        TextView tipoService;
        ImageView icono;
    }

    public CustomAdapter(ArrayList<ToiletPlaces> data, Context context){
        super(context, R.layout.row_item,data);
        this.context = context;
        this.toiletPlacesArraList = data;
    }

    private int lastPosition;

    public View getView(int position, View converView, ViewGroup parent){
        //Obtien la data item por su posicion
        ToiletPlaces toiletPlaces = getItem(position);
        //verifica si la vista existente esta siendo reusada, de otra forma se llena la vista
        ViewHolder viewHolder; //view sube la cache almacenada en tag

        final View result;

        if(converView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            converView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtlugar = (TextView) converView.findViewById(R.id.name);
            viewHolder.txtubicacion = (TextView) converView.findViewById(R.id.version_heading);
            viewHolder.tipoService = (TextView) converView.findViewById(R.id.type);
            viewHolder.icono = (ImageView) converView.findViewById(R.id.item_info);

            result = converView;

            converView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) converView.getTag();
            result = converView;
        }

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtlugar.setText(toiletPlaces.getName_place());
        viewHolder.txtubicacion.setText(toiletPlaces.getUbicationReference());
        viewHolder.tipoService.setText(toiletPlaces.getServiceType());
//        viewHolder.icono.setImageResource(Integer.decode(toiletPlaces.getIcono()));

        return converView;

    }

    @Override
    public void onClick(View view) {
        int positon = (Integer) view.getTag();
        Object object = getItem(positon);
        ToiletPlaces toilets = (ToiletPlaces)object;

//        switch (view.getId()){
//            case R.drawable
//        }
    }
}
