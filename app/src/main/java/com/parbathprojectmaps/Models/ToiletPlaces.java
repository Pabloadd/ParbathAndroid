package com.parbathprojectmaps.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ToiletPlaces implements Parcelable {

    private String name_place;
    private String description;
    private String estado;
    private String serviceType;
    private String ubicationReference;
    private String icon;
    private String schedules;
    private String idService;
    private ArrayList<String> images;

    public ToiletPlaces(String name_place, String description, String estado, String serviceType, String ubicationReference, String icon, String schedules, String idService, ArrayList<String> images) {
        this.name_place = name_place;
        this.description = description;
        this.estado = estado;
        this.serviceType = serviceType;
        this.ubicationReference = ubicationReference;
        this.icon = icon;
        this.schedules = schedules;
        this.idService = idService;
        this.images = images;
    }

    public ToiletPlaces(){    }

    protected ToiletPlaces(Parcel in){
        name_place = in.readString();
        description = in.readString();
        estado = in.readString();
        icon = in.readString();
        serviceType = in.readString();
        ubicationReference = in.readString();
        schedules = in.readString();
        idService = in.readString();
        in.readStringList(images);
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

    public String getName_place() {
        return name_place;
    }

    public void setName_place(String name_place) {
        this.name_place = name_place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getUbicationReference() {
        return ubicationReference;
    }

    public void setUbicationReference(String ubicationReference) {
        this.ubicationReference = ubicationReference;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSchedules() {
        return schedules;
    }

    public void setSchedules(String schedules) {
        this.schedules = schedules;
    }

    public String getIdService() {
        return idService;
    }

    public void setIdService(String idService) {
        this.idService = idService;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
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
        parcel.writeString(name_place);
        parcel.writeString(description);
        parcel.writeString(icon);
        parcel.writeString(serviceType);
        parcel.writeString(ubicationReference);
        parcel.writeString(schedules);
        parcel.writeString(idService);
        parcel.writeStringList(images);
    }
}
