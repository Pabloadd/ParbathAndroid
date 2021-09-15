package com.parbathprojectmaps.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parbathprojectmaps.Models.Reporte;
import com.parbathprojectmaps.R;

import java.util.ArrayList;

public class CustomAdapterComentarios extends ArrayAdapter<Reporte>
        implements View.OnClickListener {

    private static final String TAG = "";
    private ArrayList<Reporte> reporteArrayList;
    Context context;



    private static class ViewHolder{
        TextView txtNombre;
        TextView txtComentario;
        TextView txtHoraComent;
        RatingBar ratingBarCalif;
    }

    public CustomAdapterComentarios(ArrayList<Reporte> data, Context context){
        super(context, R.layout.row_item_comentarios,data);
        this.context = context;
        this.reporteArrayList = data;
    }

    private int lastPosicion;

    public View getView(int posicion, View converView, ViewGroup parent){
        Reporte reporte = getItem(posicion);
        ViewHolder viewHolder;

        final View resultado;

        if(converView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            converView = inflater.inflate(R.layout.row_item_comentarios, parent, false);
            viewHolder.txtComentario = (TextView) converView.findViewById(R.id.textComent);
            viewHolder.txtHoraComent = (TextView) converView.findViewById(R.id.textHoraComent);
            viewHolder.txtNombre = (TextView) converView.findViewById(R.id.textNombre);
//            viewHolder.ratingBarCalif = (RatingBar) converView.findViewById(R.id.ratingBarComent);

            resultado = converView;

            converView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) converView.getTag();
            resultado = converView;
        }

        Animation animation = AnimationUtils.loadAnimation(context,
                (posicion > lastPosicion) ? R.anim.up_from_bottom : R.anim.down_from_top);
        resultado.startAnimation(animation);
        lastPosicion = posicion;

        viewHolder.txtNombre.setText(reporte.getNombre() + " " + reporte.getApellido());
        try {

            viewHolder.txtHoraComent.setText((String.valueOf( reporte.getTimestamp().getDate() + " / "+
                    reporte.getTimestamp().getMonth() + " / " +
                    reporte.getTimestamp().getYear() )));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String opinionformat = reporte.getOpinion();
        opinionformat.replace("&"," \n ");
        viewHolder.txtComentario.setText(opinionformat);
        try {
            viewHolder.ratingBarCalif.setRating(reporte.getRatestars());
        } catch (Exception e) {
            Log.d(TAG,"Error de ratingBar Comentarios " + e.getMessage());
        }
        return  converView;
    }

    @Override
    public void onClick(View view) {
        int posicion = (Integer) view.getTag();
        Object object = getItem(posicion);
        Reporte reportes = (Reporte)object;


    }

}
