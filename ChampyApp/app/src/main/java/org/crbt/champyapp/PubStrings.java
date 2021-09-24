package org.crbt.champyapp;

import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;



public class PubStrings extends AbstractNodeMain {
    private static final java.lang.String TAG = PubStrings.class.getSimpleName();

    std_msgs.String comandoHaciaRobot;
    String comandoDesdeApp;
    boolean enviado=false;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("PublicadorMisionSeleccionada");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        final Publisher<std_msgs.String> publicador=connectedNode.newPublisher("publicador_mision",std_msgs.String._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void setup() {
                comandoHaciaRobot = publicador.newMessage();
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

    public void sendInfoToRobot(String infoDesdeApp){
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
