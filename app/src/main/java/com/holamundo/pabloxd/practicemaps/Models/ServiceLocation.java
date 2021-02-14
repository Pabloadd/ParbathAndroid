package com.holamundo.pabloxd.practicemaps.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class ServiceLocation implements Parcelable {

    private ToiletPlaces lugares;
    private GeoPoint posicion;
    private String ubicacion;

    public ServiceLocation(ToiletPlaces lugares, GeoPoint posicion, String ubicacion) {
        this.lugares = lugares;
        this.posicion = posicion;
        this.ubicacion = ubicacion;
    }

    public ServiceLocation(){ }


    protected ServiceLocation(Parcel in) {
        lugares = in.readParcelable(ToiletPlaces.class.getClassLoader());
    }

    public static final Creator<ServiceLocation> CREATOR = new Creator<ServiceLocation>() {
        @Override
        public ServiceLocation createFromParcel(Parcel in) {
            return new ServiceLocation(in);
        }

        @Override
        public ServiceLocation[] newArray(int size) {
            return new ServiceLocation[size];
        }
    };


    public ToiletPlaces getLugares() {
        return lugares;
    }

    public void setLugares(ToiletPlaces lugares) {
        this.lugares = lugares;
    }

    public GeoPoint getPosicion() {
        return posicion;
    }

    public void setPosicion(GeoPoint posicion) {
        this.posicion = posicion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(lugares, i);
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
