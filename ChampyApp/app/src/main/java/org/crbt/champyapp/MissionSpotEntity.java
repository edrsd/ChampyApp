package org.crbt.champyapp;

public class MissionSpotEntity {

    private String tipo;
    private float id;
    private float x;
    private float y;
    private float qz;
    private float qw;
    private float periodo;
    private float velBotella;
    private float velStaker;
    private float templadorId;
    private float accionId;
    private float accionBotonId;
    private float tiempoAlerta;



    private float tiempoEspera;
    private float idTagEstacionado;
    private float distanciaEstacionado;
    private float anguloEstacionado;
    private float alturaTemplador;

    public MissionSpotEntity(){

    }

    public MissionSpotEntity(float[] parametrosRecibidos){
        if(parametrosRecibidos[0]==1){
            id=parametrosRecibidos[1];
            x=parametrosRecibidos[2];
            y=parametrosRecibidos[3];
            qz=parametrosRecibidos[4];
            qw=parametrosRecibidos[5];
            distanciaEstacionado=parametrosRecibidos[6];
            anguloEstacionado=parametrosRecibidos[7];
            idTagEstacionado=parametrosRecibidos[8];
            alturaTemplador=parametrosRecibidos[9];

        }
        else if(parametrosRecibidos[0]==2){
            id=parametrosRecibidos[1];
            x=parametrosRecibidos[2];
            y=parametrosRecibidos[3];
            qz=parametrosRecibidos[4];
            qw=parametrosRecibidos[5];
            periodo=parametrosRecibidos[6];
            velBotella=parametrosRecibidos[7];
            velStaker=parametrosRecibidos[8];
            templadorId=parametrosRecibidos[9];
            accionId=parametrosRecibidos[10];
            accionBotonId=parametrosRecibidos[11];
            distanciaEstacionado=parametrosRecibidos[12];
            anguloEstacionado=parametrosRecibidos[13];
            idTagEstacionado=parametrosRecibidos[14];
            alturaTemplador=parametrosRecibidos[15];
            tiempoAlerta=parametrosRecibidos[16];
            tiempoEspera=parametrosRecibidos[17];
        }
    }

    public MissionSpotEntity(String tipoMaquinaOrTemplador){
        this.tipo=tipoMaquinaOrTemplador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public float getId() {
        return id;
    }

    public void setId(float id) {
        this.id = id;
    }

    public float getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Float periodo) {
        this.periodo = periodo;
    }

    public float getVelBotella() {
        return velBotella;
    }

    public void setVelBotella(float velBotella) {
        this.velBotella = velBotella;
    }

    public float getVelStaker() {
        return velStaker;
    }

    public void setVelStaker(float velStaker) {
        this.velStaker = velStaker;
    }

    public float getTempladorId() {
        return templadorId;
    }

    public void setTempladorId(float templadorId) {
        this.templadorId = templadorId;
    }

    public float getAccionId() {
        return accionId;
    }

    public void setAccionId(float accionId) {
        this.accionId = accionId;
    }

    public float getAccionBotonId() {
        return accionBotonId;
    }

    public void setAccionBotonId(float accionBotonId) {
        this.accionBotonId = accionBotonId;
    }

    public float getTiempoAlerta() {
        return tiempoAlerta;
    }

    public void setTiempoAlerta(float tiempoAlerta) {
        this.tiempoAlerta = tiempoAlerta;
    }

    public float getTiempoEspera() {
        return tiempoEspera;
    }

    public void setTiempoEspera(float tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
    }

    public float getPosX() {
        return x;
    }

    public void setPosX(float x) {
        this.x = x;
    }

    public float getPosY() {
        return y;
    }

    public void setPosY(float y) {
        this.y = y;
    }

    public float getQz() {
        return qz;
    }

    public void setQz(float qz) {
        this.qz = qz;
    }

    public float getQw() {
        return qw;
    }

    public void setQw(float qw) {
        this.qw = qw;
    }

    public float getIdTagEstacionado() {
        return idTagEstacionado;
    }

    public void setIdTagEstacionado(float idTagEstacionado) {
        this.idTagEstacionado = idTagEstacionado;
    }

    public float getDistanciaEstacionado() {
        return distanciaEstacionado;
    }

    public void setDistanciaEstacionado(float distanciaEstacionado) {
        this.distanciaEstacionado = distanciaEstacionado;
    }

    public float getAnguloEstacionado() {
        return anguloEstacionado;
    }

    public void setAnguloEstacionado(float anguloEstacionado) {
        this.anguloEstacionado = anguloEstacionado;
    }

    public float getAlturaTemplador() {
        return alturaTemplador;
    }

    public void setAlturaTemplador(float alturaTemplador) {
        this.alturaTemplador = alturaTemplador;
    }

}
