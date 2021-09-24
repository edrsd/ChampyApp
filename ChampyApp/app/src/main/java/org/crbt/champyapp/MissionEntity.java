package org.crbt.champyapp;

import java.util.ArrayList;
import java.util.List;

public class MissionEntity {

    private String missionName;
    private List<MissionSpotEntity> listaPuntosDeMision,listaDeMaquinas,listaDeTempladores;
    private ArrayList<String> listaStringMaquinas,listaStringTempladores;

    public MissionEntity(){
        listaPuntosDeMision=new ArrayList<>();
        listaDeMaquinas=new ArrayList<>();
        listaDeTempladores=new ArrayList<>();
    }

    public MissionEntity(String missionName){
        this.missionName=missionName;
        listaPuntosDeMision=new ArrayList<>();
        listaDeMaquinas=new ArrayList<>();
        listaDeTempladores=new ArrayList<>();
    }

    public MissionEntity(List<MissionSpotEntity> listadDeLugares){
        this.listaPuntosDeMision=listadDeLugares;
    }

    public int getSize(){
        return listaPuntosDeMision.size();
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public void agregarMaquinaAMision(MissionSpotEntity missionSpot){
        listaDeMaquinas.add(missionSpot);
    }

    public void agregarTempladorAMision(MissionSpotEntity missionSpot){
        listaDeTempladores.add(missionSpot);
    }

    public void borrarMaquinaDeMision(int positionToRemove){
        listaDeMaquinas.remove(positionToRemove);

    }

    public void borrarTempladorDeMision(int positionToRemove){
        listaDeTempladores.remove(positionToRemove);

    }

    public List<MissionSpotEntity> getMissionSpotList(){
        return listaPuntosDeMision;
    }

    public List<MissionSpotEntity> obtenerMaquinasDeMision(){
        return listaDeMaquinas;
    }

    public List<MissionSpotEntity> obtenerTempladoresDeMision(){
        return listaDeTempladores;
    }

    public void setListaStringMaquinas(ArrayList<String> listaStringMaquinas){
        this.listaStringMaquinas=listaStringMaquinas;
    }

    public void setListaStringTempladores(ArrayList<String> listaStringTempladores){
        this.listaStringTempladores=listaStringTempladores;
    }

    public ArrayList<String> getListaStringMaquinas(){
        listaStringMaquinas=new ArrayList<>();
        listaStringMaquinas.clear();
        for (int c=0;c<listaDeMaquinas.size();c++) {
            listaStringMaquinas.add("Máquina"+(int)listaDeMaquinas.get(c).getId());
        }
        return listaStringMaquinas;
    }

    public ArrayList<String> getListaStringTempladores(){
        listaStringTempladores=new ArrayList<>();
        listaStringTempladores.clear();
        for (int c=0;c<listaDeTempladores.size();c++) {
            listaStringTempladores.add("Templador"+(int)listaDeTempladores.get(c).getId());
        }
        return listaStringTempladores;
    }
}