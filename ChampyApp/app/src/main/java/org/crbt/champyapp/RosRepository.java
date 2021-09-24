package org.crbt.champyapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.ros.address.InetAddressFactory;
import org.ros.node.AbstractNodeMain;
import org.ros.node.NodeConfiguration;

import java.lang.ref.WeakReference;
import java.net.URI;

import org.crbt.champyapp.Connection.ConnectionType;
import org.crbt.champyapp.Connection.ConnectionCheckTask;
import org.crbt.champyapp.Connection.ConnectionListener;
import org.crbt.champyapp.Service.NodeMainExecutorService;
import org.crbt.champyapp.Service.NodeMainExecutorServiceListener;

public class RosRepository  implements SubPosition.Escuchador, SubTagParams.TagParamsListener, SubListenerRespuestaRobot.RespuestaRobotListener,
        SubMapping.MapeoListener, SubListenerInfoMision.GetMissionListener, SubRobotData.DatosRobotListener,
SubListenerListMissionsRequest.ListaMisionesListener
    ,SubListenerListRequest.ListaListener,SubCamera.CamaraListener
{

    private static final String TAG = RosRepository.class.getSimpleName();
    private static RosRepository instance;

    private final WeakReference<Context> contextReference;
    private MasterEntity master;
    private final MutableLiveData<ConnectionType> rosConnected;
    private NodeMainExecutorService nodeMainExecutorService;
    private NodeConfiguration nodeConfiguration;


    private PubJoystick nodoPublicador;
    private ConexionRealizada listener;

    private MutableLiveData<String> posicion;
    private MutableLiveData<String> tagParams;
    private MutableLiveData<Bitmap> mapa;
    private MutableLiveData<RespuestaRobotStatus> respuestaRobot;
    private MutableLiveData<String> listaRecibida;
    private MutableLiveData<String> listaMisionesRecibida;
    private MutableLiveData<float[]> infoMisionRecibida;
    private MutableLiveData<float[]> datoRobotRecibido;
    private MutableLiveData<Bitmap> cameraFrame;




    public enum RespuestaRobotStatus {EMPTY, PENDING, DONE, FAILED}

    SubPosition subPosition;
    SubTagParams subTagParams;
    SubMapping subMapping;
    SubListenerRespuestaRobot subListenerRespuestaRobot;
    SubListenerListRequest subListenerListRequest;
    SubListenerListMissionsRequest subListenerListMissionsRequest;
    SubListenerInfoMision subListenerInfoMision;
    SubRobotData subListenerRobotData;
    SubCamera subCamera;

    PubSetMission pubSetMission;
    PubStrings pubListRequest;
    PubAppRequest pubAppRequest;
    PubActuators pubActuators;
    PubStrings pubStrings;
    PubRobotStatus pubRobotStatus;
    PubCameraParams pubCameraParams;

    boolean nodoSetMissionActivado=false, nodoPubLaunchNodosActivado=false,nodoPubListRequestActivado=false;


    String respuestaR;


    //--------------------------------LISTENERS-----------------------------------------------
    @Override
    public void unFrameEscuchado(Bitmap cameraFrame) {
        this.cameraFrame.postValue(cameraFrame);
    }

    @Override
    public void onNewParamsListened(String tagParamsListened) {
        this.tagParams.postValue(tagParamsListened);
    }

    public void resetearTag(){
        this.tagParams.postValue(null);
    }


    @Override
    public void unaNuevaPosicionEscuchada(String posicionRecibida) {
        this.posicion.postValue(posicionRecibida);
    }

    public void resetearPosicion(){
        this.posicion.postValue(null);
    }


    @Override
    public void unMapaNuevoEscuchado(Bitmap bitmap) {
        this.mapa.postValue(bitmap);
    }

    public void resetearMapa(){
        this.mapa.postValue(null);
    }


    @Override
    public void unaNuevaLista(String respuestaRecibida) {
        this.listaRecibida.postValue(respuestaRecibida);
    }

    @Override
    public void unaNuevaListaMisiones(String respuestaRecibida) {
        this.listaMisionesRecibida.postValue(respuestaRecibida);
    }

    @Override
    public void onNewInfoMission(float[] infoMission) {
        this.infoMisionRecibida.postValue(infoMission);
    }

    public void resetearSolicitudMision(){
        this.infoMisionRecibida.postValue(null);
    }

    @Override
    public void unNuevoDatoRecibido(float[] datoRecibido) {
        this.datoRobotRecibido.postValue(datoRecibido);
    }

    //Respuestas del robot
    @Override
    public void unaNuevaRespuestaDelRobot(String respuestaRecibida) {
        String [] respuesta=respuestaRecibida.split(";");
        //representa la operación realizada
        String tipoOperacion=respuesta[0];
        //Representa el exito o fracaso de la ejecución del comando
        String isSuccess=respuesta[1];


//Mapeo
        if(tipoOperacion.equals("0")&&isSuccess.equals("0"))
            respuestaRobot.postValue(RespuestaRobotStatus.FAILED);
        else if(tipoOperacion.equals("0")&&isSuccess.equals("1"))
            respuestaRobot.postValue(RespuestaRobotStatus.DONE);

//Matar mapeo o localización
        if(tipoOperacion.equals("1")&&isSuccess.equals("0"))
            respuestaRobot.postValue(RespuestaRobotStatus.FAILED);
        else if(tipoOperacion.equals("1")&&isSuccess.equals("1"))
            respuestaRobot.postValue(RespuestaRobotStatus.DONE);


//Maquinas y templadores
        if(tipoOperacion.equals("2")&&isSuccess.equals("0"))
            respuestaRobot.postValue(RespuestaRobotStatus.FAILED);
        else if(tipoOperacion.equals("2")&&isSuccess.equals("1"))
            respuestaRobot.postValue(RespuestaRobotStatus.DONE);

//Localizacion
        else if(tipoOperacion.equals("8")&&isSuccess.equals("0"))
            respuestaRobot.postValue(RespuestaRobotStatus.FAILED);
        else if(tipoOperacion.equals("8")&&isSuccess.equals("1"))
            respuestaRobot.postValue(RespuestaRobotStatus.DONE);

//Modo auto
        else if(tipoOperacion.equals("9")&&isSuccess.equals("0"))
            respuestaRobot.postValue(RespuestaRobotStatus.FAILED);
        else if(tipoOperacion.equals("9")&&isSuccess.equals("1"))
            respuestaRobot.postValue(RespuestaRobotStatus.DONE);

//Matar modo auto
        else if(tipoOperacion.equals("10")&&isSuccess.equals("0"))
            respuestaRobot.postValue(RespuestaRobotStatus.FAILED);
        else if(tipoOperacion.equals("10")&&isSuccess.equals("1"))
            respuestaRobot.postValue(RespuestaRobotStatus.DONE);



//Lista de mapas
        else if(tipoOperacion.equals("11")&&isSuccess.equals("0"))
            respuestaRobot.postValue(RespuestaRobotStatus.FAILED);
        else if(tipoOperacion.equals("11")&&isSuccess.equals("1"))
            respuestaRobot.postValue(RespuestaRobotStatus.DONE);


//Lista de misiones
        else if(tipoOperacion.equals("12")&&isSuccess.equals("0"))
            respuestaRobot.postValue(RespuestaRobotStatus.FAILED);
        else if(tipoOperacion.equals("12")&&isSuccess.equals("1"))
            respuestaRobot.postValue(RespuestaRobotStatus.DONE);

//Cambio de modo
        else if(tipoOperacion.equals("13")&&isSuccess.equals("0"))
            respuestaRobot.postValue(RespuestaRobotStatus.FAILED);
        else if(tipoOperacion.equals("13")&&isSuccess.equals("1"))
            respuestaRobot.postValue(RespuestaRobotStatus.DONE);





    }

    //------------------------------------------------------------------------------------------

    interface ConexionRealizada{
        public void onConnectionOk(ConnectionType connectionType);
    }

    //----------------------------------Constructor-------------------------------------------
    /**
     * Default private constructor. Initialize empty lists and maps of intern widgets and nodes.
     */
    private RosRepository(Context context) {
        this.contextReference = new WeakReference<>(context);
        this.rosConnected = new MutableLiveData<>(ConnectionType.DISCONNECTED);
        this.mapa=new MutableLiveData<>();
        this.posicion=new MutableLiveData<>();
        this.tagParams=new MutableLiveData<>();
        this.cameraFrame=new MutableLiveData<>();

        this.respuestaRobot=new MutableLiveData<>(RespuestaRobotStatus.EMPTY);

        this.listaRecibida=new MutableLiveData<>();
        this.infoMisionRecibida=new MutableLiveData<>();
        this.datoRobotRecibido=new MutableLiveData<>();
        this.listaMisionesRecibida=new MutableLiveData<>();

        master=new MasterEntity();
    }
    //--------------------------------------------------------------------------------------------


    //--------------------------------SINGLETON------------------------------------------
    /**
     * Return the singleton instance of the repository.
     * @return Instance of this Repository
     */
    public static RosRepository getInstance(final Context context){
        if(instance == null){
            instance = new RosRepository(context);
        }
        return instance;
    }
    //-----------------------------------------------------------------------------------

    //--------------------------CONEXIÓN AL ROBOT-------------------------------------------
    /**
     * Connect all registered nodes and establish a connection to the ROS master with
     * the connection details given by the already delivered master entity.
     */
    public void connectToMaster() {
        Log.i(TAG, "Connect to Master");

        ConnectionType connectionType = rosConnected.getValue();
        if (connectionType == ConnectionType.CONNECTED || connectionType == ConnectionType.PENDING) {
            return;
        }

        rosConnected.setValue(ConnectionType.PENDING);

        // Check connection
        new ConnectionCheckTask(new ConnectionListener() {

            @Override
            public void onSuccess() {
                bindService();
            }

            @Override
            public void onFailed() {
                rosConnected.postValue(ConnectionType.FAILED);
            }
        }).execute(master);
    }
    //---------------------------------------------------------------------------------------

    /**
     * Disconnect all running nodes and cut the connection to the ROS master.
     */
    public void disconnectFromMaster() {
        Log.i(TAG, "Disconnect from Master");
        if (nodeMainExecutorService == null) {
            return;
        }

        nodeMainExecutorService.shutdown();
    }


    /**
     * Change the connection details to the ROS master like the IP or port.
     * @param master Master data
     */
    public void updateMaster(MasterEntity master) {
        Log.i(TAG, "Update Master");

        if(master == null) {
            Log.i(TAG, "Master is null");
            return;
        }

        this.master = master;

        // nodeConfiguration = NodeConfiguration.newPublic(master.deviceIp, getMasterURI());
    }


    /**
     --------------------* Set the master device IP in the Nodeconfiguration----------------------
     */
    public void setMasterDeviceIp(String deviceIp) {
        nodeConfiguration = NodeConfiguration.newPublic(deviceIp, getMasterURI());
    }
//-----------------------------------------------------------------------------------------------





//---------------------------------------Conexión a ROS--------------------------------------------
    private void bindService() {
        Context context = contextReference.get();
        if (context == null) {
            return;
        }

        RosServiceConnection serviceConnection = new RosServiceConnection(getMasterURI());

        // Create service intent
        Intent serviceIntent = new Intent(context, NodeMainExecutorService.class);
        serviceIntent.setAction(NodeMainExecutorService.ACTION_START);

        // Start service and check state
        context.startService(serviceIntent);
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
//---------------------------------------Conexión a ROS--------------------------------------------
    /**
     * Create a node for a specific widget entity.
     * The node will be created and subsequently registered.
     * @param widget Widget to be added
     */
//    private AbstractNode addNode(BaseEntity widget) {
//        // Create a new node from widget
//        AbstractNode node;
//
//        if (widget instanceof PublisherEntity) {
//            node = new PubNode();
//
//        } else if (widget instanceof SubscriberEntity) {
//            node = new SubNode(this);
//
//        }else {
//            Log.i(TAG, "Widget is either publisher nor subscriber.");
//            return null;
//        }

    // Set node topic, add to node list and register it
//        node.setWidget(widget);
//        currentNodes.put(node.getTopic(), node);

//        return node;
//    }



    /**
     * Connect the node to ROS node graph if a connection to the ROS master is running.
     * @param node Node to connect
     */
    private void registerNode(AbstractNodeMain node) {
        Log.i(TAG, "register Node");

        if (rosConnected.getValue() != ConnectionType.CONNECTED) {
            Log.w(TAG, "Not connected with master");
            return;
        }

        nodeMainExecutorService.execute(node, nodeConfiguration);
    }

    /**
     * Disconnect the node from ROS node graph if a connection to the ROS master is running.
     * @param node Node to disconnect
     */
    private void unregisterNode(AbstractNodeMain node) {
        Log.i(TAG, "unregister Node");

        if (rosConnected.getValue() != ConnectionType.CONNECTED) {
            Log.w(TAG, "Not connected with master");
            return;
        }

        nodeMainExecutorService.shutdownNodeMain(node);
    }


    /**
     * Result of a change in the internal data of a node header. Therefore it has to be
     * unregistered from the service and reregistered due to the implementation of ROS.
     * @param node Node main to be reregistered
     */
    private void reregisterNode(AbstractNodeMain node) {
        Log.i(TAG, "Reregister Node");

        unregisterNode(node);
        registerNode(node);
    }

    private URI getMasterURI() {
        String masterString = String.format("http://%s:%s/", master.ip, master.port);
        return URI.create(masterString);
    }

    private String getDefaultHostAddress() {
        return InetAddressFactory.newNonLoopback().getHostAddress();
    }


//-------------------------------Conexión con el administrador de nodos de Ros------------------------------------------
    private final class RosServiceConnection implements ServiceConnection {

        NodeMainExecutorServiceListener serviceListener;
        URI customMasterUri;


        RosServiceConnection(URI customUri) {
            customMasterUri = customUri;
        }


        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            nodeMainExecutorService = ((NodeMainExecutorService.LocalBinder) binder).getService();
            nodeMainExecutorService.setMasterUri(customMasterUri);
            nodeMainExecutorService.setRosHostname(getDefaultHostAddress());

            serviceListener = nodeMainExecutorService ->
                    rosConnected.postValue(ConnectionType.DISCONNECTED);

            nodeMainExecutorService.addListener(serviceListener);
            rosConnected.setValue(ConnectionType.CONNECTED);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            nodeMainExecutorService.removeListener(serviceListener);
        }
    }
//-------------------------------Conexión con el administrador de nodos de Ros------------------------------------------



    public void addListenerConexionRealizada(ConexionRealizada listener){
        this.listener=listener;
    }

    //--------------------------LIVEDATA------------------------------------------------------
    /**
     * Get the current connection status of the ROS service as a live data.
     * @return Connection status
     */
    public LiveData<ConnectionType> getRosConnectionStatus() {
        return rosConnected;
    }

    public LiveData<String> getPosition() {
        return posicion;
    }

    public LiveData<String> getTagParams() {
        return tagParams;
    }

    public LiveData<Bitmap> getMap() {
        return mapa;
    }

    public LiveData<RespuestaRobotStatus> getStatusrespuestaRobot(){
        return respuestaRobot;
    }

    public void initRespuestaRobot(){
        this.respuestaRobot.postValue(RespuestaRobotStatus.EMPTY);
    }

    public LiveData<String> getListaSolicitada(){
        return listaRecibida;
    }

    public LiveData<String> getListaMisionesSolicitada(){
        return listaMisionesRecibida;
    }

    public LiveData<float[]> getInfoMisionSolicitada(){
        return  infoMisionRecibida;
    }

    public LiveData<float[]> getDatosRobot(){
        return  datoRobotRecibido;
    }

    public LiveData<Bitmap> getCameraImage() {
        return cameraFrame;
    }


    //-----------------------------------------------------------------------------------------


//*********--------------------------NODOS SUSCRIPTORES----------------------------------------

    //-------------------------Nodo de posición----------------------------------------
    public void registrarNodoSuscriptorPosicion(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subPosition==null) {
            subPosition=new SubPosition(this);
            nodeMainExecutorService.execute(subPosition,nodeConfiguration);
        }
    }

    public void borrarNodoSuscriptorPosicion(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subPosition!=null) {
            nodeMainExecutorService.shutdownNodeMain(subPosition);
            subPosition=null;
        }
    }
    //----------------------------------------------------------------------------------

    //-----------------------------Nodo de parametros del Tag--------------------------
    public void registrarNodoSuscriptorTagParams(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subTagParams==null) {
            subTagParams=new SubTagParams(this);
            nodeMainExecutorService.execute(subTagParams,nodeConfiguration);
        }
    }

    public void borrarNodoSuscriptorTagParams(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subTagParams!=null) {
            nodeMainExecutorService.shutdownNodeMain(subTagParams);
            subTagParams=null;
        }
    }
    //---------------------------------------------------------------------------------

    //--------------------------------Nodo suscriptor de mapa--------------------------
    public void registrarNodoSuscriptorMapeo(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subMapping==null) {
            subMapping=new SubMapping(this);
            nodeMainExecutorService.execute(subMapping,nodeConfiguration);
        }
    }

    public void borrarNodoSuscriptorMapeo(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subMapping!=null) {
            nodeMainExecutorService.shutdownNodeMain(subMapping);
            subMapping=null;
        }
    }
    //----------------------------------------------------------------------------------------


    //--------------------------Nodo suscriptor a datos del robot--------------------------------
    public void registrarNodoListenerDatosRobot(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subListenerRobotData==null) {
            subListenerRobotData=new SubRobotData(this);
            nodeMainExecutorService.execute(subListenerRobotData,nodeConfiguration);
        }
    }

    public void borrarNodoListenerDatosRobot(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subListenerRobotData!=null) {
            nodeMainExecutorService.shutdownNodeMain(subListenerRobotData);
            subListenerRobotData=null;
        }

    }

    //-------------------------------------------------------------------------------------------

    //----------------------------Nodo suscriptor a respuesta del robot-----------------------
    public void registrarNodoListenerRespuestaRobot(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subListenerRespuestaRobot==null) {
            subListenerRespuestaRobot=new SubListenerRespuestaRobot(this);
            nodeMainExecutorService.execute(subListenerRespuestaRobot,nodeConfiguration);
        }
    }

    public void borrarNodoListenerRespuestaRobot(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subListenerRespuestaRobot!=null) {
            nodeMainExecutorService.shutdownNodeMain(subListenerRespuestaRobot);
            subListenerRespuestaRobot=null;
        }

    }
    //-------------------------------------------------------------------------------------------

    //--------------------------Nodo suscriptor a lista de mapas del robot---------------------------------
    public void registrarNodoListenerListRequest(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && subListenerListRequest==null){
            subListenerListRequest=new SubListenerListRequest(this);
            nodeMainExecutorService.execute(subListenerListRequest,nodeConfiguration);
        }
    }

    public void borrarNodoListenerListRequest(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && subListenerListRequest!=null){
            nodeMainExecutorService.shutdownNodeMain(subListenerListRequest);
            subListenerListRequest=null;
        }
    }

//    public void iniciarNodoListRequest(SubListenerListRequest nodo){
//        if(rosConnected.getValue() == ConnectionType.CONNECTED && nodo!=null){
//            nodeMainExecutorService.execute(nodo,nodeConfiguration);
//        }
//    }


    //--------------------------Nodo suscriptor a lista de misiones del robot---------------------------------
    public void registrarNodoListenerListMissionsRequest(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && subListenerListMissionsRequest==null){
            subListenerListMissionsRequest=new SubListenerListMissionsRequest(this);
            nodeMainExecutorService.execute(subListenerListMissionsRequest,nodeConfiguration);
        }
    }

    public void borrarNodoListenerListMissionsRequest(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && subListenerListMissionsRequest!=null){
            nodeMainExecutorService.shutdownNodeMain(subListenerListMissionsRequest);
            subListenerListMissionsRequest=null;
        }
    }



    //------------------------Nodo suscriptor de información de misión----------------------------

    public void registrarNodoListenerInfoMision(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && subListenerInfoMision==null){
            subListenerInfoMision=new SubListenerInfoMision(this);
            nodeMainExecutorService.execute(subListenerInfoMision,nodeConfiguration);
        }
    }

    public void borrarNodoListenerInfoMision(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && subListenerInfoMision!=null){
            nodeMainExecutorService.shutdownNodeMain(subListenerInfoMision);
            subListenerInfoMision=null;
        }
    }

    //--------------------------------------------------------------------------------------------

    //--------------------------------Nodo suscriptor de camara--------------------------
    public void registrarNodoSuscriptorCamara(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subCamera==null) {
            subCamera=new SubCamera(this);
            nodeMainExecutorService.execute(subCamera,nodeConfiguration);
        }
    }

    public void borrarNodoSuscriptorCamara(){
        if (rosConnected.getValue() == ConnectionType.CONNECTED && subCamera!=null) {
            nodeMainExecutorService.shutdownNodeMain(subCamera);
            subCamera=null;
        }
    }
    //----------------------------------------------------------------------------------------


    //-------------------------------------------------------------------------------------------


//------------------------------------NODOS SUSCRIPTORES------------------------------------------



//*********---------------------------NODOS PUBLICADORES-----------------------------------------

    //-------------------Nodo publicador de parametros de camara-----------------------------------
    public void registrarNodoPublicadorParametrosDeCamara(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubCameraParams==null){
            pubCameraParams=new PubCameraParams();
            nodeMainExecutorService.execute(pubCameraParams,nodeConfiguration);
        }
    }

    public void borrarNodoPublicadorParametrosDeCamara(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubCameraParams!=null){
            nodeMainExecutorService.shutdownNodeMain(pubCameraParams);
            pubCameraParams=null;
        }
    }

    public void enviarParametrosDeCamara(int[] cameraParams){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubCameraParams==null){
            pubCameraParams =new PubCameraParams();
            pubCameraParams.sendInfoToRobot(cameraParams);
            pubCameraParams.resetearEnvio();
            nodeMainExecutorService.execute(pubCameraParams, nodeConfiguration);
        }
        else if(pubCameraParams!=null){
            pubCameraParams.sendInfoToRobot(cameraParams);
            pubCameraParams.resetearEnvio();
        }
    }

    public void terminarEnvioDeParametrosDeCamara(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubCameraParams!=null){
            nodeMainExecutorService.shutdownNodeMain(pubCameraParams);
            pubCameraParams=null;
        }
    }

    //--------------------------------------------------------------------------------------

    //-------------------Nodo publicador de estado del robot-----------------------------------

    public void registrarNodoPublicadorEstadoRobot(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubRobotStatus==null){
            pubRobotStatus=new PubRobotStatus();
            nodeMainExecutorService.execute(pubRobotStatus,nodeConfiguration);
        }
    }

    public void borrarNodoPublicadorEstadoRobot(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubRobotStatus!=null){
            nodeMainExecutorService.shutdownNodeMain(pubRobotStatus);
            pubRobotStatus=null;
        }
    }

    public void enviarEstadoARobot(int[] infoDeMision){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubRobotStatus==null){
            pubRobotStatus =new PubRobotStatus();
            pubRobotStatus.sendInfoToRobot(infoDeMision);
            pubRobotStatus.resetearEnvio();
            nodeMainExecutorService.execute(pubRobotStatus, nodeConfiguration);
        }
        else if(pubRobotStatus!=null){
            pubRobotStatus.sendInfoToRobot(infoDeMision);
            pubRobotStatus.resetearEnvio();
        }
    }

    public void terminarEnvioDeEstadoARobot(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubRobotStatus!=null){
            nodeMainExecutorService.shutdownNodeMain(pubRobotStatus);
            pubRobotStatus=null;
        }
    }
    //--------------------------------------------------------------------------------------


    //----------------Nodo publicador del comando de envio de misiones-----------------------

    public void registrarNodoPublicadorInfoMision(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubSetMission==null){
            pubSetMission=new PubSetMission();
            nodeMainExecutorService.execute(pubSetMission,nodeConfiguration);
        }
    }

    public void borrarNodoPublicadorInfoMision(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubSetMission!=null){
            nodeMainExecutorService.shutdownNodeMain(pubSetMission);
            pubSetMission=null;
        }
    }

    public void enviarInfoDeMisionARobot(float [] infoDeMision){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubSetMission==null){
            pubSetMission =new PubSetMission();
            pubSetMission.sendInfoToRobot(infoDeMision);
            pubSetMission.resetearEnvio();
            nodeMainExecutorService.execute(pubSetMission, nodeConfiguration);
        }
        else if(pubSetMission!=null){
            pubSetMission.sendInfoToRobot(infoDeMision);
            pubSetMission.resetearEnvio();
        }
    }

    public void terminarEnvioDeInfoDeMisionARobot(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubSetMission!=null){
            nodeMainExecutorService.shutdownNodeMain(pubSetMission);
            pubSetMission=null;
        }
    }

    //-----------------------------------------------------------------------------------------

    //------------------Nodo publicador de solicitudes de la aplicación-----------------------

    private void registrarNodoPublicadorAppRequest(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubAppRequest==null){
            pubAppRequest=new PubAppRequest();
            nodeMainExecutorService.execute(pubAppRequest,nodeConfiguration);
        }
    }

    private void borrarNodoPublicadorAppRequest(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubAppRequest!=null){
            nodeMainExecutorService.shutdownNodeMain(pubAppRequest);
            pubAppRequest=null;
        }
    }

    public void enviarSolicitudDeAppARobot(int codigoDeSolicitud){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubAppRequest==null){
            pubAppRequest =new PubAppRequest();
            pubAppRequest.sendInfoToRobot(codigoDeSolicitud);
            pubAppRequest.resetearEnvio();
            nodeMainExecutorService.execute(pubAppRequest, nodeConfiguration);
        }
        else if(pubAppRequest!=null){
            pubAppRequest.sendInfoToRobot(codigoDeSolicitud);
            pubAppRequest.resetearEnvio();
        }
    }

    public void terminarEnvioDeSolicitudDeAppARobot(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubAppRequest!=null){
            nodeMainExecutorService.shutdownNodeMain(pubAppRequest);
            pubAppRequest=null;
        }
    }

    //-------------------------------------------------------------------------------------------

    //-------------------Nodo publicador de actuadores----------------------------------------------

    private void registrarNodoPublicadorActuators(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubActuators==null){
            pubActuators=new PubActuators();
            nodeMainExecutorService.execute(pubActuators,nodeConfiguration);
        }
    }

    private void borrarNodoPublicadorActuators(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubActuators!=null){
            nodeMainExecutorService.shutdownNodeMain(pubActuators);
            pubActuators=null;
        }
    }

    public void enviarComandoActuadorARobot(int[] codigoDeSolicitud){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubActuators==null){
            pubActuators =new PubActuators();
            pubActuators.sendInfoToRobot(codigoDeSolicitud);
            pubActuators.resetearEnvio();
            nodeMainExecutorService.execute(pubActuators, nodeConfiguration);
        }
        else if(pubActuators!=null){
            pubActuators.sendInfoToRobot(codigoDeSolicitud);
            pubActuators.resetearEnvio();
        }
    }

    public void terminarEnvioComandoActuadorARobot(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubActuators!=null){
            nodeMainExecutorService.shutdownNodeMain(pubActuators);
            pubActuators=null;
        }
    }


    //------------------------------------------------------------------------------------------

    //--------------------------------Nodo publicador de Strings---------------------------

    private void registrarNodoPublicadorStrings(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubStrings==null){
            pubStrings=new PubStrings();
            nodeMainExecutorService.execute(pubStrings,nodeConfiguration);
        }
    }

    private void borrarNodoPublicadorStrings(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubStrings!=null){
            nodeMainExecutorService.shutdownNodeMain(pubStrings);
            pubStrings=null;
        }
    }

    public void enviarStringARobot(String codigoDeSolicitud){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubStrings==null){
            pubStrings =new PubStrings();
            pubStrings.sendInfoToRobot(codigoDeSolicitud);
            pubStrings.resetearEnvio();
            nodeMainExecutorService.execute(pubStrings, nodeConfiguration);
        }
        else if(pubStrings!=null){
            pubStrings.sendInfoToRobot(codigoDeSolicitud);
            pubStrings.resetearEnvio();
        }
    }

    public void terminarEnvioStringARobot(){
        if(rosConnected.getValue() == ConnectionType.CONNECTED && pubStrings!=null){
            nodeMainExecutorService.shutdownNodeMain(pubStrings);
            pubStrings=null;
        }
    }


    //------------------------------------------------------------------------------------


    //---------------------------Nodo publicador del Joystick--------------------------------------
    public void registrarNodoJoystick(AbstractNodeMain nodo){
        nodeMainExecutorService.execute(nodo, nodeConfiguration);
    }

    public void borrarNodoJoystick(AbstractNodeMain nodo){
        nodeMainExecutorService.shutdownNodeMain(nodo);
    }
    //-----------------------------------------------------------------------------------------



//------------------------------NODOS PUBLICADORES-----------------------------------------------

}
