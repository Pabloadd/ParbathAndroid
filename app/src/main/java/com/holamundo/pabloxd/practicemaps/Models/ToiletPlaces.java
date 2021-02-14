package com.holamundo.pabloxd.practicemaps.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class ToiletPlaces implements Parcelable {

    private String nombrelugar;
    private String descripcion;
    private String estado;
    private String tipo;
    private String ubicacion;
    private String icono;
    private String horario;
    private String idServicio;
    private ArrayList<String> foto;

    public ToiletPlaces(String descripcion, String estado, ArrayList<String> foto, String horario, String idServicio, String icono, String nombrelugar,
                        String tipo, String ubicacion) {
        this.nombrelugar = nombrelugar;
        this.estado = estado;
        this.descripcion = descripcion;
        this.icono = icono;
        this.tipo = tipo;
        this.ubicacion = ubicacion;
        this.horario = horario;
        this.idServicio = idServicio;
        this.foto = foto;
    }

    public ToiletPlaces(){    }

    protected ToiletPlaces(Parcel in){
        nombrelugar = in.readString();
        descripcion = in.readString();
        estado = in.readString();
        icono = in.readString();
        tipo = in.readString();
        ubicacion = in.readString();
        horario = in.readString();
        idServicio = in.readString();
        in.readStringList(foto);
    }

    public static final Creator<ToiletPlaces> CREATOR = new Creator<ToiletPlaces>() {
        @Override
        public ToiletPlaces createFromParcel(Parcel parcel) {
            return new ToiletPlaces(parcel);
        }

        @Override
        public ToiletPlaces[] newArray(int i) {
            return new ToiletPlaces[i];
        }
    };

    public String getEstado() { return estado; }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombrelugar() {
        return nombrelugar;
    }

    public void setNombrelugar(String nombrelugar) {
        this.nombrelugar = nombrelugar;
    }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getIcono() { return icono; }

    public void setIcono(String icono) { this.icono = icono; }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
    }

    public ArrayList<String> getFoto() {
        return foto;
    }

    public void setFoto(ArrayList<String> foto) {
        this.foto = foto;
    }

    public static Creator<ToiletPlaces> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(estado);
        parcel.writeString(nombrelugar);
        parcel.writeString(descripcion);
        parcel.writeString(icono);
        parcel.writeString(tipo);
        parcel.writeString(ubicacion);
        parcel.writeString(horario);
        parcel.writeString(idServicio);
        parcel.writeStringList(foto);
    }
}
