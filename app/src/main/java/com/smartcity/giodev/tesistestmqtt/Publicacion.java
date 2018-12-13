package com.smartcity.giodev.tesistestmqtt;

public class Publicacion {
    String fecha_llegada;
    String hora_llegada;
    String fecha_llegada_ntp;
    String hora_llegada_ntp;
    String hora_envio;
    String fecha_envio;
    double value;


    public Publicacion(String fecha_llegada, String hora_llegada, String fecha_llegada_ntp, String hora_llegada_ntp,
                       String hora_envio, String fecha_envio, double value) {
        this.fecha_llegada = fecha_llegada;
        this.hora_llegada = hora_llegada;
        this.fecha_llegada_ntp = fecha_llegada_ntp;
        this.hora_llegada_ntp = hora_llegada_ntp;
        this.hora_envio = hora_envio;
        this.fecha_envio = fecha_envio;
        this.value = value;
    }

    public Publicacion(String fecha_llegada, String hora_llegada, String hora_envio, String fecha_envio, double value) {
        this.fecha_llegada = fecha_llegada;
        this.hora_llegada = hora_llegada;
        this.hora_envio = hora_envio;
        this.fecha_envio = fecha_envio;
        this.value = value;
    }

    @Override
    public String toString() {
        return  fecha_llegada + "," + hora_llegada + "," + fecha_llegada_ntp + "," + hora_llegada_ntp
                + "," +hora_envio +","+ fecha_envio +","+ value+"\n";
    }

    public String toStringsinNTP() {
        return fecha_llegada +"," + hora_llegada + ","
                + hora_envio +","+ fecha_envio +","+ value+"\n";
    }
}
