package com.parbathprojectmaps.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Reporte implements Parcelable {

    private String nombre, apellido, opinion, report_id, ubicacionReporte;
    private Float ratestars;
//    Timestamp timesserver =
    private Date timestamp;

    public Reporte(String nombre, String apellido, String opinion, Float ratestars, Date timestamp,
                   String report_id, String ubicacionReporte) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.opinion = opinion;
        this.ratestars = ratestars;
        this.timestamp = timestamp;
        this.report_id = report_id;
        this.ubicacionReporte = ubicacionReporte;
    }

    public Reporte() {
    }

    protected Reporte(Parcel in) {
        nombre = in.readString();
        apellido = in.readString();
        opinion = in.readString();
        report_id = in.readString();
        ubicacionReporte = in.readString();
        if (in.readByte() == 0) {
            ratestars = null;
        } else {
            ratestars = in.readFloat();
        }
    }

    public static final Creator<Reporte> CREATOR = new Creator<Reporte>() {
        @Override
        public Reporte createFromParcel(Parcel in) {
            return new Reporte(in);
        }

        @Override
        public Reporte[] newArray(int size) {
            return new Reporte[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public Float getRatestars() {
        return ratestars;
    }

    public void setRatestars(Float ratestars) {
        this.ratestars = ratestars;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getReport_id() {
        return report_id;
    }

    public void setReport_id(String report_id) {
        report_id = report_id;
    }

    public String getUbicacionReporte() {
        return ubicacionReporte;
    }

    public void setUbicacionReporte(String ubicacionReporte) {
        this.ubicacionReporte = ubicacionReporte;
    }

    @Override
    public String toString() {
        return "Reporte{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", opinion='" + opinion + '\'' +
                ", Report_id='" + report_id + '\'' +
                ", ratestars=" + ratestars +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(apellido);
        parcel.writeString(opinion);
        parcel.writeString(report_id);
        parcel.writeFloat(ratestars);
        parcel.writeString(String.valueOf(timestamp));
    }
}
