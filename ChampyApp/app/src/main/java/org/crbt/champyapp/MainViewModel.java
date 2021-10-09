package org.crbt.champyapp;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.apache.commons.lang.ArrayUtils;
import org.crbt.champyapp.Connection.ConnectionCheckTask;
import org.ros.node.AbstractNodeMain;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    RosRepository rosRepository;
    SshRepositoryImpl sshRepository;
    static String nombreTmpMapa,nombreTmpMision;
    static String ventanaAnterior;

    static MissionEntity missionEntity;
    List<MissionSpotEntity> maquinas,templadores;

    static boolean nodoLocalizacionActivado=false,nodoMapeoActivado=false,nodoAutoActivado=false,controlManualActivado=false;

    static String error;


    //--------------------------CONSTRUCTOR------------------------------------------------
    public MainViewModel(@NonNull Application application){
        super(application);
        rosRepository=RosRepository.getInstance(application);
        sshRepository=SshRepositoryImpl.getInstance(application);

//        maquinas=new ArrayList<>();
//        templadores=new ArrayList<>();
    }
    //--------------------------CONSTRUCTOR------------------------------------------------


    //--------------------------Conectar y conectar al master-------------------------------------------
    public boolean connectRobotToMaster(String masterIp){
        String ipAndroidDevice=ConnectionCheckTask.getAndroidDeviceIp(true);
        if(!masterIp.isEmpty() && !masterIp.equals("")){
            MasterEntity master=new MasterEntity();
            master.ip=masterIp;
            rosRepository.updateMaster(master);
            SSHEntity sshEntity=new SSHEntity();
            sshEntity.ip=masterIp;
            sshRepository.updateSshEntity(sshEntity);
            rosRepository.setMasterDeviceIp(ipAndroidDevice);
            rosRepository.connectToMaster();
            return  true;
        }
        else
            return false;
    }


    public void desconectar(){
        rosRepository.disconnectFromMaster();
    }

    //--------------------------------------------------------------------------------------

    //--------------------------COMANDOS UTILIZANDO SSH---------------------------------------------
    //Solicita el inicio del nodo correspondiente a la creacion del mapa a partir de un  nombre dado
    public void sshIniciarMapeo(String nombreMapa){
        //Se comunica al repositorio ssh que ejecute los comandos necesarios
        nombreTmpMapa=nombreMapa;
        // para la creacion del mapa con nombre contenido en la variable nombreMapa
        sshRepository.iniciarNodoMapeo(nombreMapa);
    }

    public void sshEliminarMision(String nombreMision){
        nombreTmpMision=nombreMision;
        sshRepository.eliminarMision(nombreMision);
    }

    public void sshCancelarMapeo(){
        sshRepository.cancelarCreacionDeMapa(nombreTmpMapa);
    }

    public void sshGuardarMision(String nombreDeMision){
        sshRepository.guardarMision(nombreDeMision);
    }

    public void sshCargarMision(String nombreMision){
        sshRepository.cargarMision(nombreMision);
    }

    public void sshCargarMapa(String nombreMapa){
        sshRepository.cargarMapa(nombreMapa);
    }

    public  void setNombreMapa(String nombreMapa){
        this.nombreTmpMapa=nombreMapa;
    }

    public void sshEliminarMapa(){
        sshRepository.eliminarMapa(this.nombreTmpMapa);

    }

    public void sshApagarRobot(){
        sshRepository.apagarRobot();
    }

    public void sshReiniciarRobot(){
        sshRepository.reiniciarRobot();
    }

    public void reiniciarBanderaComandosRealizados(){
        sshRepository.initCommandsDoneFlag();
    }

    public void reiniciarBanderaRespuestaRobot(){
        rosRepository.initRespuestaRobot();
    }


//-------------------------------------------------------------------------------------------------

//--------------------------------LIVEDATA--------------------------------------------------------

    public LiveData seHanCompletadoLosComandos(){
        return sshRepository.isCommandsDone();
    }

    public LiveData<RosRepository.RespuestaRobotStatus> getStatusRespuestaRobot(){
        return rosRepository.getStatusrespuestaRobot();
    }

    public LiveData getRosServiceStatusConection(){
        return rosRepository.getRosConnectionStatus();
    }

    public LiveData<String> getPosition(){
        return rosRepository.getPosition();
    }

    public void resetearPosicion(){
        rosRepository.resetearPosicion();
    }

    public LiveData<String> getTagParams(){
        return rosRepository.getTagParams();
    }

    public void resetearTag(){
        rosRepository.resetearTag();
    }

    public LiveData<Bitmap> getMap(){
        return rosRepository.getMap();
    }

    public void resetearMapa(){
        rosRepository.resetearMapa();
    }

    public LiveData<String> getListaSolicitada(){
        return rosRepository.getListaSolicitada();
    }

    public LiveData<String> getListaMisionesSolicitada(){
        return rosRepository.getListaMisionesSolicitada();
    }

    public LiveData<float[]> getInfoMisionSolicitada(){return rosRepository.getInfoMisionSolicitada();}

    public void resetearSolicitudMision(){rosRepository.resetearSolicitudMision();}

    public LiveData<float[]> getDatosRobot(){return rosRepository.getDatosRobot();}

    public LiveData<Bitmap> getCameraImage(){
        return rosRepository.getCameraImage();
    }


//------------------------------------------------------------------------------------------------


//------------------------------NODOS SUSCRIPTORES------------------------------------------------

    public void iniciarEscuchaPosicion(){
        rosRepository.registrarNodoSuscriptorPosicion();
    }

    public void terminarEscuchaPosicion(){
        rosRepository.borrarNodoSuscriptorPosicion();
    }

    public void iniciarEscuchaTagParams(){
        rosRepository.registrarNodoSuscriptorTagParams();
    }

    public void terminarEscuchaTagParams(){
        rosRepository.borrarNodoSuscriptorTagParams();
    }

    public void iniciarEscuchaMapeo(){
        rosRepository.registrarNodoSuscriptorMapeo();
    }

    public void terminarEscuchaMapeo(){
        rosRepository.borrarNodoSuscriptorMapeo();
    }

    public void iniciarEscuchaDatosRobot(){ rosRepository.registrarNodoListenerDatosRobot();}

    public void terminarEscuchaDatosRobot(){ rosRepository.borrarNodoListenerDatosRobot();}

    public void iniciarEscuchaRespuestaRobot(){ rosRepository.registrarNodoListenerRespuestaRobot(); }

    public void terminarEscuchaRespuestaRobot(){ rosRepository.borrarNodoListenerRespuestaRobot(); }

    public void iniciarEscuchaListaRobot(){ rosRepository.registrarNodoListenerListRequest();}

    public void terminarEscuchaListaRobot(){ rosRepository.borrarNodoListenerListRequest();}

    public void iniciarEscuchaListaMisiones(){
        rosRepository.registrarNodoListenerListMissionsRequest();
    }

    public void terminarEscuchaListaMisiones(){
        rosRepository.borrarNodoListenerListMissionsRequest();
    }

//    public void iniciarNodoListRequest(SubListenerListRequest nodo){
//        rosRepository.iniciarNodoListRequest(nodo);
//    }

//    public void iniciarEscuchaListaMisionesRobot(){ rosRepository.registrarNodoListenerListRequest();}

    public void terminarEscuchaListaMisionesRobot(){ rosRepository.borrarNodoListenerListRequest();}

    public void iniciarEscuchaInfoMision(){ rosRepository.registrarNodoListenerInfoMision();}

    public void terminarEscuchaInfoMision(){ rosRepository.borrarNodoListenerInfoMision();}

    public void iniciarEscuchaCamara(){
        rosRepository.registrarNodoSuscriptorCamara();
    }


//------------------------------------------------------------------------------------------------


//--------------------------------NODOS PUBLICADORES----------------------------------------------

    //--------------------------------Nodo publicador de parametros de cámara---------------------------
    public void cambiarParametrosDeCamara(int[] cameraParams){
        rosRepository.enviarParametrosDeCamara(cameraParams);
    }
    //------------------------------------------------------------------------------------

    //--------------------------------Nodo publicador de Strings---------------------------

    public void pedirMisionARobot(String mision){
        rosRepository.enviarStringARobot(mision);
    }

    public void pedirCargarMapaEnRobot(String nombreMapa){
        rosRepository.enviarStringARobot(nombreMapa);
    }
    //------------------------------------------------------------------------------------

    //--------------------Nodo publicador estado del robot--------------------------------------
    public void terminarEnvioEstadosARobot(){
        rosRepository.terminarEnvioDeEstadoARobot();
    }

    public void actualizarEstadoARobotDetenido(){
        int[] comandoAEnviar={0,0};
        rosRepository.enviarEstadoARobot(comandoAEnviar);
    }

    public void actualizarEstadoARobotEnManual(){
        int[] comandoAEnviar={0,0};
        rosRepository.enviarEstadoARobot(comandoAEnviar);
    }

    public void actualizarEstadoARobotEnMapeo(){
        int[] comandoAEnviar={2,0};
        rosRepository.enviarEstadoARobot(comandoAEnviar);
    }

    public void actualizarEstadoARobotEnAuto(){
        int[] comandoAEnviar={0,1};
        rosRepository.enviarEstadoARobot(comandoAEnviar);
    }

    public void actualizarEstadoARobotEnParo(){
        int[] comandoAEnviar={2,1};
        rosRepository.enviarEstadoARobot(comandoAEnviar);
    }

    public void actualizarEstadoAParoDesactivado(){
        int[] comandoAEnviar={2,0};
        rosRepository.enviarEstadoARobot(comandoAEnviar);
    }

    public void terminarEnvioEstadoARobot(){
        rosRepository.terminarEnvioDeEstadoARobot();
    }

    //------------------------------------------------------------------------------------------


    //-------------------------Nodo publicador de actuadores--------------------------------------

    public void abrirPinzas(int velocidad){
        int[] comandoAEnviar={1,0,velocidad*2};
        rosRepository.enviarComandoActuadorARobot(comandoAEnviar);
    }

    public void cerrarPinza(int velocidad){
        int[] comandoAEnviar={1,1,velocidad*2};
        rosRepository.enviarComandoActuadorARobot(comandoAEnviar);
    }

    public void sacarStaker(int velocidad){
        int[] comandoAEnviar={0,1,velocidad*2};
        rosRepository.enviarComandoActuadorARobot(comandoAEnviar);
    }

    public void meterStaker(int velocidad){
        int[] comandoAEnviar={0,0,velocidad*2};
        rosRepository.enviarComandoActuadorARobot(comandoAEnviar);
    }
    //----------------------------------------------------------------------------------------------


    //--------------------------nodo publicador de misión set_mision------------------------------

    public void sendMissionSpotToRobot(MissionSpotEntity missionSpotEntity){
        float[] parametrosMisionAEnviar = missionSpotParamsToFloatArray(missionSpotEntity);
        rosRepository.enviarInfoDeMisionARobot(parametrosMisionAEnviar);
    }

    public void deleteMissionSpotFromRobot(MissionSpotEntity missionSpotEntity){
        float[] comandoAEnviar=new float[2];
        if(missionSpotEntity.getTipo().equals("templador")){
            comandoAEnviar[0]=3;
        }
        else if(missionSpotEntity.getTipo().equals("maquina")){
            comandoAEnviar[0]=4;
        }
        float[] parametrosMision = missionSpotParamsToFloatArray(missionSpotEntity);
        System.arraycopy(parametrosMision,1,comandoAEnviar,1,1);

        rosRepository.enviarInfoDeMisionARobot(comandoAEnviar);
    }

    public void sendMisionToRobot(MissionEntity missionEntity){
        float templadoresDeMision=missionEntity.obtenerTempladoresDeMision().size();
        float maquinasDeMision=missionEntity.obtenerMaquinasDeMision().size();

        float[] infoAEnviar=new float[]{0,templadoresDeMision,maquinasDeMision};

        rosRepository.enviarInfoDeMisionARobot(infoAEnviar);
    }

    public void sendFullMissiontoRobot(MissionEntity missionEntity){
        float numeroDeTempladores=missionEntity.obtenerTempladoresDeMision().size();
        float numeroDeMaquinas=missionEntity.obtenerMaquinasDeMision().size();
        float[] totalTempladores=new float[(int) (10*numeroDeTempladores)];
        for(int c=0;c<numeroDeTempladores;c++){
            MissionSpotEntity templadorTemp=missionEntity.obtenerTempladoresDeMision().get(c);
            System.arraycopy(missionSpotParamsToFloatArray(templadorTemp),0,totalTempladores,c*10,10);
//            totalTempladores=ArrayUtils.addAll(missionSpotParamsToFloatArray(templadorTemp),null);
        }
        float[] totalMaquinas=new float[(int) (18*numeroDeMaquinas)];
        for(int c=0;c<numeroDeMaquinas;c++){
            MissionSpotEntity maquinaTemp=missionEntity.obtenerMaquinasDeMision().get(c);
            System.arraycopy(missionSpotParamsToFloatArray(maquinaTemp),0,totalMaquinas,c*18,18);
        }
        float[] misionAEnviar=new float[]{numeroDeTempladores,numeroDeMaquinas};
        misionAEnviar=ArrayUtils.addAll(misionAEnviar,totalTempladores);
        misionAEnviar=ArrayUtils.addAll(misionAEnviar,totalMaquinas);

        rosRepository.enviarInfoDeMisionARobot(misionAEnviar);

    }

    public void teminarEnvioInfoMision(){
        rosRepository.terminarEnvioDeInfoDeMisionARobot();
    }
    //----------------------------------------------------------------------------------------

    //-------------------------nodo publicador de solicitudes de la app hacia el robot-------------

    public void solicitarApagarRobot(){
        rosRepository.enviarSolicitudDeAppARobot(100);
    }

    public void solicitarReiniciarRobot(){
        rosRepository.enviarSolicitudDeAppARobot(101);
    }

    public void solicitarMapas(){
        rosRepository.enviarSolicitudDeAppARobot(1);
    }

    public void solicitarMisiones(){
        rosRepository.enviarSolicitudDeAppARobot(2);
    }

    public void solicitarPuntosDeMision(){
        rosRepository.enviarSolicitudDeAppARobot(3);
    }

    public void solicitarIniciarMapeoEnRobot(){
        rosRepository.enviarSolicitudDeAppARobot(4);
    }

    public void solicitarTerminarMapeoEnRobot(){
        rosRepository.enviarSolicitudDeAppARobot(5);
    }

    public void solicitarIniciarModoAuto(){ rosRepository.enviarSolicitudDeAppARobot(6);}

    public void solicitarTerminarMisionEnRobot(){ rosRepository.enviarSolicitudDeAppARobot(7);}

    public  void solicitarIniciarLocalizacion(){
        rosRepository.enviarSolicitudDeAppARobot(8);
    }

    public void solicitarTerminarLocalizacion(){
        rosRepository.enviarSolicitudDeAppARobot(5);
    }

    public void terminarSolicitudDeAppEnRobot(){
        rosRepository.terminarEnvioDeSolicitudDeAppARobot();
    }

//-----------------------------------------------------------------------------------------

    //Conversión de el pundo de misión en un array flotante
    private float[] missionSpotParamsToFloatArray(MissionSpotEntity missionSpot){
        float[] floatArrayParamsMaquina=new float[18];
        float[] floatArrayParamsTemplador=new float[10];
        float[] array;
        if(missionSpot.getTipo().equals("maquina")){
            floatArrayParamsMaquina[0]=2;
            floatArrayParamsMaquina[1]=missionSpot.getId();
            floatArrayParamsMaquina[2]=missionSpot.getPosX();
            floatArrayParamsMaquina[3]=missionSpot.getPosY();
            floatArrayParamsMaquina[4]=missionSpot.getQz();
            floatArrayParamsMaquina[5]=missionSpot.getQw();
            floatArrayParamsMaquina[6]=missionSpot.getPeriodo();
            floatArrayParamsMaquina[7]=missionSpot.getVelBotella();
            floatArrayParamsMaquina[8]=missionSpot.getVelStaker();
            floatArrayParamsMaquina[9]=missionSpot.getTempladorId();
            floatArrayParamsMaquina[10]=missionSpot.getAccionId();
            floatArrayParamsMaquina[11]=missionSpot.getAccionBotonId();
            floatArrayParamsMaquina[12]=missionSpot.getIdTagEstacionado();
            floatArrayParamsMaquina[13]=missionSpot.getAnguloEstacionado();
            floatArrayParamsMaquina[14]=missionSpot.getDistanciaEstacionado();
            floatArrayParamsMaquina[15]=missionSpot.getAlturaTemplador();
            floatArrayParamsMaquina[16]=missionSpot.getTiempoAlerta();
            floatArrayParamsMaquina[17]=missionSpot.getTiempoEspera();
        }
        else if(missionSpot.getTipo().equals("templador")){
            floatArrayParamsTemplador[0]=1;
            floatArrayParamsTemplador[1]=missionSpot.getId();
            floatArrayParamsTemplador[2]=missionSpot.getPosX();
            floatArrayParamsTemplador[3]=missionSpot.getPosY();
            floatArrayParamsTemplador[4]=missionSpot.getQz();
            floatArrayParamsTemplador[5]=missionSpot.getQw();
            floatArrayParamsTemplador[6]=missionSpot.getIdTagEstacionado();
            floatArrayParamsTemplador[7]=missionSpot.getAnguloEstacionado();
            floatArrayParamsTemplador[8]=missionSpot.getDistanciaEstacionado();
            floatArrayParamsTemplador[9]=missionSpot.getAlturaTemplador();
        }
        return array=missionSpot.getTipo().equals("maquina")?floatArrayParamsMaquina:floatArrayParamsTemplador;
    }


    //--------------------------Nodos activos en el robot---------------------------------

    public void setNodoLocalizacionActivado(){
        this.nodoLocalizacionActivado=true;
    }

    public void setNodoLocalizacionDesactivado(){
        this.nodoLocalizacionActivado=true;
    }

    public void setNodoMapeoActivado(){
        this.nodoMapeoActivado=true;
    }

    public void setNodoMapeoDesactivado(){
        this.nodoMapeoActivado=true;
    }

    public void setNodoAutoActivado(){
        this.nodoAutoActivado=true;
    }

    public void setNodoAutoDesactivado(){
        this.nodoAutoActivado=true;
    }

    public boolean isNodoLocalizacionActivado() {
        return nodoLocalizacionActivado;
    }

    public boolean isNodoMapeoActivado() {
        return nodoMapeoActivado;
    }

    public boolean isNodoAutoActivado() {
        return nodoAutoActivado;
    }
    //-------------------------------------------------------------------------------------

    //-----------------------Cambio a paro desde manual o mision en vivo-------------------------
    public void setControlManualActivado(){
        this.controlManualActivado=true;
    }

    public void setControlManualDesactivado(){
        this.controlManualActivado=false;
    }

    public boolean isControlManualActivado() {
        return controlManualActivado;
    }


    //-----------------------------------------------------------------------------------

    //-------------------------

    public MissionEntity getMissionEntity(){
        return missionEntity;
    }

    public void setMissionEntity(MissionEntity misionAAgregar){
        this.missionEntity=misionAAgregar;
    }

    public void agregarTemplador(MissionSpotEntity templador){
        if(missionEntity!=null){
            missionEntity.agregarTempladorAMision(templador);
        }
    }

    public void agregarMaquina(MissionSpotEntity maquina){
        if(missionEntity!=null){
            missionEntity.agregarMaquinaAMision(maquina);
        }
    }

    public void obtenerMaquinasDeMision(){
        if(missionEntity!=null){
            maquinas=missionEntity.obtenerMaquinasDeMision();
        }
    }

    public void obtenerTempladoresDeMision(){
        if(missionEntity!=null){
            templadores=missionEntity.obtenerTempladoresDeMision();
        }
    }

    public void setVentanaAnterior(String ventanaAnterior){
        this.ventanaAnterior=ventanaAnterior;
    }

    public String getVentanaAnterior(){
        return ventanaAnterior;
    }

    public String solicitarError(){
        return sshRepository.solicitarError();
    }

}
