package org.crbt.champyapp;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.ros.internal.node.topic.PublisherIdentifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

public class SshRepositoryImpl implements SshRepository {

    public static final String TAG = SshRepositoryImpl.class.getSimpleName();
    private static SshRepositoryImpl mInstance;

    private SSHEntity sshEntity;

    public static final String[] COMANDO_CREAR_MAPA={"rosparam set /rtabmap/database_path \"~/mapas/nombre.db\"","roslaunch champy_2-0 slam_mapeo.launch"};
    public static final String[] COMANDO_REINICIAR_ROBOT={"shutdown -r now"};
    public static final String[] COMANDO_APAGAR_ROBOT={"sudo poweroff"};
    public static final String[] COMANDO_GUARDAR_MAPA={"rosnode kill /rtabmap"};
    public static final String[] COMANDO_CANCELAR_MAPA={"rosnode kill /rtabmap && rm ~/mapas/nombre.db"};
    public static final String[] COMANDO_GUARDAR_MISION={"rosparam dump ~/misiones/nombre.yaml /mision"};
    public static final String[] COMANDO_COMENZAR_MISION={"rosrun champy_2.0 mod_auto.py"};
    public static final String[] COMANDO_DETENER_MISION ={"rosnode kill /modo_auto"};
    public static final String[] COMANDO_ABRIR_MISION={"rosparam load ~/misiones/nombre.yaml /mision"};
    public static final String[] COMANDO_ELIMINAR_MISION={"rm ~/misiones/nombre.yaml"};
    public static final String[] COMANDO_ABRIR_MAPA_EXISTENTE={"rosparam set database_path \"~/mapas/nombre.db\""};
    public static final String[] COMANDO_ELIMINAR_MAPA_EXISTENTE={"rm ~/mapas/nombre.db"};

    private String error;

    JSch jsch;
    Session session;
    ChannelExec channelExec;
    ChannelShell channelssh;
    OutputStream input_for_the_channel;
    InputStream output_from_the_channel;
    PrintStream commander;
    BufferedReader br;

    MutableLiveData<String> outputData;
    MutableLiveData<Boolean> connected;
    MutableLiveData<SshCommandsStatus> sshCommandsStatus;

    boolean channelStatus=false,sessionStatus=false;

    Handler mainHandler;

    public enum SshCommandsStatus {DISCONNECTED, PENDING, DONE, FAILED,ERROR}

    private SshRepositoryImpl(@NonNull Application application) {
        connected = new MutableLiveData<>();
        outputData = new MutableLiveData<>();

        sshCommandsStatus = new MutableLiveData<>(SshCommandsStatus.DISCONNECTED);

//        this.configRepository = ConfigRepositoryImpl.getInstance(application);

        // React on Config Changes
//        currentSSH = Transformations.switchMap(configRepository.getCurrentConfigId(), configId -> configRepository.getSSH(configId));
//        currentSSH.observeForever(this::updateSSH);

        mainHandler = new Handler(Looper.getMainLooper());

    }


    public static SshRepositoryImpl getInstance(Application application) {
        if (mInstance == null) {
            mInstance = new SshRepositoryImpl(application);
        }
        return mInstance;
    }

    @Override
    public void startSession() {
//        final SSHEntity ssh = currentSSH.getValue();
        final SSHEntity sshEntity=new SSHEntity();
        new Thread(() -> {
            try {
//                assert ssh != null;
//                startSessionTask2(ssh.username, ssh.password, ssh.ip, ssh.port);
                startSessionTask2(sshEntity.username, sshEntity.password, sshEntity.ip, sshEntity.port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startSessionTask(String username, String password, String ip, int port) throws JSchException, IOException {
        // Check if session already running
        if(session != null && session.isConnected()){
            Log.i(TAG, "Session is running already");
            return;
        }

        Log.i(TAG, "Start session");

        // Create new session
        jsch = new JSch();
        session = jsch.getSession(username, ip, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        java.util.Properties prop = new java.util.Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        // Start connection
        session.connect(30000);

        // SSH Channel

        channelssh = (ChannelShell)session.openChannel("shell");
        input_for_the_channel = channelssh.getOutputStream();
        output_from_the_channel = channelssh.getInputStream();

//        channelExec=(ChannelExec) session.openChannel("exec");
//        input_for_the_channel = channelExec.getOutputStream();
//        output_from_the_channel = channelExec.getInputStream();


        commander = new PrintStream(input_for_the_channel, true);
        br = new BufferedReader(new InputStreamReader(output_from_the_channel));

        // Connect to channel
        channelssh.connect();

//        channelExec.connect();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check for connection
        if (channelssh.isConnected()) {
            connected.postValue(true);
        }

//        if (channelExec.isConnected()) {
//            connected.postValue(true);
//        }

        String line;
        while ((line = br.readLine()) != null && channelssh.isConnected()) {
//        while ((line = br.readLine()) != null && channelExec.isConnected()) {
            // TODO: Check if every line will be displayed
            Log.i(TAG, "looper session");

            // Remove ANSI control chars (Terminal VT 100)
            line = line.replaceAll("\u001B\\[[\\d;]*[^\\d;]","");
            final String finalLine = line;

            // Publish lineData to LiveData
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    outputData.setValue(finalLine);
                }
            });
            // outputData.setValue(finalLine);
            // outputData.postValue(finalLine);
        }
    }


    private void startSessionTask2(String username, String password, String ip, int port) throws JSchException, IOException {
        // Check if session already running
        if(session != null && session.isConnected()){
            Log.i(TAG, "Session is running already");
            return;
        }

        Log.i(TAG, "Start session");

        // Create new session
        jsch = new JSch();
        session = jsch.getSession(username, ip, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        java.util.Properties prop = new java.util.Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        // Start connection
        session.connect(30000);

        if (session.isConnected()) {
            connected.postValue(true);
        }

        // SSH Channel
//        String [] commands={"export TURTLEBOT3_MODEL=waffle_pi","roslaunch turtlebot3_teleop turtlebot3_teleop_key.launch","rosrun turtlesim turtle_teleop_key"};

        String [] commands={"cd ~/catkin_ws && source devel/setup.bash && rosrun champy_2-0 enviodato_app.py","cd ~/catkin_ws && source devel/setup.bash && rosrun champy_2-0 set_params.py"};

        for (String command : commands) {
            ChannelShell channel=(ChannelShell) session.openChannel("shell");
            input_for_the_channel = channel.getOutputStream();
            output_from_the_channel = channel.getInputStream();

            commander = new PrintStream(input_for_the_channel, true);
            br = new BufferedReader(new InputStreamReader(output_from_the_channel));


            channel.connect(5000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean channelStatus = channel.isConnected();

            commander.println(command);

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            printOutput2(output_from_the_channel,channel);
            channel.disconnect();
        }

        session.disconnect();


//        if (channelssh.isConnected()) {
//            connected.postValue(true);
//        }

    }



    private void printOutput(Channel channel) throws IOException {
        String line;
        while ((line = br.readLine()) != null && channel.isConnected()) {
//        while ((line = br.readLine()) != null && channelExec.isConnected()) {
            // TODO: Check if every line will be displayed
            Log.i(TAG, "looper session");

            // Remove ANSI control chars (Terminal VT 100)
            line = line.replaceAll("\u001B\\[[\\d;]*[^\\d;]","");
            final String finalLine = line;

            // Publish lineData to LiveData
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    outputData.setValue(finalLine);
                }
            });
            // outputData.setValue(finalLine);
            // outputData.postValue(finalLine);
        }
    }

    private void printOutput2(InputStream in,Channel channel) throws IOException {
        byte[] tmp=new byte[1024];
        while(true){
            while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String finalLine=new String(tmp, 0, i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        outputData.setValue(finalLine);
                    }
                });
            }
            channel.disconnect();
            if(channel.isClosed()){
                if(in.available()>0) continue;
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
            }
            try{Thread.sleep(1000);}catch(Exception e){}
        }
    }




    @Override
    public void stopSession() {
        if (channelssh.isConnected()) {
            channelssh.disconnect();
            session.disconnect();
            connected.postValue(false);
        }

//        if (channelExec.isConnected()) {
//            channelExec.disconnect();
//            session.disconnect();
//            connected.postValue(false);
//        }
    }

    @Override
    public LiveData<Boolean> isConnected() {
        return connected;
    }

    @Override
    public void sendMessage(String message) {
        new Thread((() -> {
            try {
                commander.println(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        })).start();
    }

    @Override
    public LiveData<String> getOutputData() {
        return outputData;
    }


//    public void updateSSH(SSHEntity ssh) {
//        Log.i(TAG, "Update SSH");
//        if(ssh == null) {
//            Log.i(TAG, "SSH is null");
//        }
//    }

//    public void updateSSHConfig(SSHEntity ssh) {
//        configRepository.updateSSH(ssh);
//    }

//    public LiveData<SSHEntity> getCurrentSSH() {
//        return this.currentSSH;
//    }


    public void updateSshEntity(SSHEntity sshEntity){
            Log.i(TAG, "Update SShEntity");

            if(sshEntity == null) {
                Log.i(TAG, "SShEntity is null");
                return;
            }

            this.sshEntity = sshEntity;
    }

    //Método que se encarga de la ejecución de los comandos mediante SSH
    public void sshEjecutarComandos(String [] comandos) {
        //Entidad que contiene la información necesaria para establecer la conexión
//        final SSHEntity sshEntity=new SSHEntity();
        //Se guarda el estado de la conexión como PENDING representando que la peticion de conexión
        // esta en proceso
        sshCommandsStatus.postValue(SshCommandsStatus.PENDING);
        //Se crea un nuevo hilo
        new Thread(() -> {
            try {
                //Se ejecuta el método que inicia la sesion y se le proporcionan los paramentros necesarios
                iniciarSesionSSH(sshEntity.username, sshEntity.password, sshEntity.ip, sshEntity.port,comandos);
            } catch (Exception e) {
                e.printStackTrace();
                establecerError(e.toString());
                sshCommandsStatus.postValue(SshCommandsStatus.ERROR);
            }
        }).start();
    }

    private void iniciarSesionSSH(String username, String password, String ip, int port,String [] comandos ) throws JSchException, IOException {
        // Check if session already running
        if(session != null && session.isConnected()){
            Log.i(TAG, "Session is running already");
            return;
        }

        Log.i(TAG, "Start session");

        // Create new session
        jsch = new JSch();
        session = jsch.getSession(username, ip, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        java.util.Properties prop = new java.util.Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        // Start connection
        session.connect(5000);

        if (sessionStatus=session.isConnected()) {
            connected.postValue(true);
        }

        int nc=0;
        for (String comando : comandos) {
            ChannelShell channel=(ChannelShell) session.openChannel("shell");
            input_for_the_channel = channel.getOutputStream();
            output_from_the_channel = channel.getInputStream();

            commander = new PrintStream(input_for_the_channel, true);
            br = new BufferedReader(new InputStreamReader(output_from_the_channel));

            channel.connect(5000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(nc>0) channelStatus = channelStatus&&channel.isConnected();
            else    channelStatus=channel.isConnected();
            nc++;

            commander.println(comando);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.disconnect();
        }
        session.disconnect();
        if(sessionStatus && channelStatus){
            sshCommandsStatus.postValue(SshCommandsStatus.DONE);
        }else{
            sshCommandsStatus.postValue(SshCommandsStatus.FAILED);
        }
    }

    //Método que envia comandos de consola por ssh que el Master debe realizar para iniciar el mapeo
    public boolean iniciarNodoMapeo(String nombreMapa){
        //Comando de consola que se ejecutara en el Master
        String[] comandoIniciarMapeo={"rosparam set /rtabmap/database_path \"~/mapas/"+nombreMapa+".db\""};
        // Se envian los comandos hacia el master a través de comunición SSH
        sshEjecutarComandos(comandoIniciarMapeo);
        return true;
    }

    public void eliminarMision(String nombreMision){
        //Comando de consola que se ejecutara en el Master
        String[] comandoIniciarMapeo={"rm ~/misiones/"+nombreMision};
        // Se envian los comandos hacia el master a través de comunición SSH
        sshEjecutarComandos(comandoIniciarMapeo);
    }

    public boolean cancelarCreacionDeMapa(String nombreMapa){
        String[] comandoCancelarMapeo={"rm ~/mapas/"+nombreMapa+".db"};
        sshEjecutarComandos(comandoCancelarMapeo);
        return true;
    }



    public void cargarMapa(String nombreMapa){
        //Comando de consola que se ejecutara en el Master
        String[] comandoIniciarMapeo={"rosparam set /rtabmap/database_path \"~/mapas/"+nombreMapa+"\""};
        // Se envian los comandos hacia el master a través de comunición SSH
        sshEjecutarComandos(comandoIniciarMapeo);
    }

    public void eliminarMapa(String nombreMapa){
        String[] comandoCancelarMapeo={"rm ~/mapas/"+nombreMapa};
        sshEjecutarComandos(comandoCancelarMapeo);

    }

    public void guardarMision(String nombreMision){
        String[] comandoCancelarMapeo={"rosparam dump ~/misiones/"+nombreMision+".yaml /mision"};
        sshEjecutarComandos(comandoCancelarMapeo);
    }

    public void cargarMision(String nombreMision){
        String[] comandoCargarMision={"rosparam load ~/misiones/"+nombreMision+" /mision"};
        sshEjecutarComandos(comandoCargarMision);
    }


    public void apagarRobot(){
        sshEjecutarComandos(COMANDO_APAGAR_ROBOT);
    }

    public void reiniciarRobot(){
        sshEjecutarComandos(COMANDO_REINICIAR_ROBOT);
    }

    public LiveData<SshCommandsStatus> isCommandsDone(){
        return this.sshCommandsStatus;
    }

    public void initCommandsDoneFlag(){
        this.sessionStatus=false;
        this.channelStatus=false;
        this.sshCommandsStatus.postValue(SshCommandsStatus.DISCONNECTED);
    }

    public String solicitarError(){
        return error;
    }

    public void establecerError(String errorCapturado){
        this.error=errorCapturado;
    }

}