package org.crbt.champyapp;

import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import std_msgs.Int32;

public class PubLaunchNodos extends AbstractNodeMain {
    private static final java.lang.String TAG = PubLaunchNodos.class.getSimpleName();

    Int32 comandoHaciaRobot;
    int comandoDesdeApp;
    boolean publicado=false;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("PublicadorLaunchNodos");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        final Publisher<Int32> publicador=connectedNode.newPublisher("launch_nodos",Int32._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void setup() {
                comandoHaciaRobot = publicador.newMessage();
            }

            @Override
            protected void loop() throws InterruptedException {

                if(publicador.hasSubscribers()&&!publicado){
                    comandoHaciaRobot.setData(comandoDesdeApp);
                    publicador.publish(comandoHaciaRobot);
                    publicado=true;
                }
            }
        });
    }

    public void sendInfoToRobot(int infoDesdeApp){
        this.comandoDesdeApp=infoDesdeApp;
    }

    //restablece la variable "pulicado" para poder enviar un nuevo comando
    public void reiniciarEnvio(){
        publicado=false;
    }

    @Override
    public void onShutdown(Node node) {
        Log.i(TAG, " Nodo publicador cerrado ");
        node.removeListeners();
        node.shutdown();
        super.onShutdown(node);
    }
}
