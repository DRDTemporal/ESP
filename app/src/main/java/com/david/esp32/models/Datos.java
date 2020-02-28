package com.david.esp32.models;

public class Datos {
    String ac, dc, va, vd, humedad, temperatura, fecha, hora;

    public Datos() {
    }

    public Datos(String ac, String dc, String va, String vd, String humedad, String temperatura, String fecha, String hora) {
        this.ac = ac;
        this.dc = dc;
        this.va = va;
        this.vd = vd;
        this.humedad = humedad;
        this.temperatura = temperatura;
        this.fecha = fecha;
        this.hora = hora;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public String getVa() {
        return va;
    }

    public void setVa(String va) {
        this.va = va;
    }

    public String getVd() {
        return vd;
    }

    public void setVd(String vd) {
        this.vd = vd;
    }

    public String getHumedad() {
        return humedad;
    }

    public void setHumedad(String humedad) {
        this.humedad = humedad;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}