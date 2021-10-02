package org.crbt.champyapp;

import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import std_msgs.Int32;

public class PubAppRequest extends AbstractNodeMain {
    private static final java.lang.String TAG = PubAppRequest.class.getSimpleName();

    Int32 comandoHaciaRobot;
    int comandoDesdeApp;
    boolean enviado=false;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("PublicadorSolicitudesDeApp");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        final Publisher<Int32> publicador=connectedNode.newPublisher("publicador_app",Int32._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void setup() {
                comandoHaciaRobot = publicador.newMessage();
//                boolean sub=publicador.hasSubscribers();
            }

            @Override
            protected void loop() throws InterruptedException {
                if(publicador.hasSubscribers() && !enviado){
                    comandoHaciaRobot.setData(comandoDesdeApp);
                    publicador.publish(comandoHaciaRobot);
                    enviado=true;
                }
            }
        });
    }

    public void sendInfoToRobot(int infoDesdeApp){
        this.comandoDesdeApp=infoDesdeApp;
    }

    public void resetearEnvio(){
        enviado=false;
    }

    @Override
    public void onShutdown(Node node) {
        Log.i(TAG, " Nodo publicador cerrado ");
        node.removeListeners();
        node.shutdown();
        super.onShutdown(node);
    }
}
