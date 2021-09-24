package org.crbt.champyapp;

import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import std_msgs.Int32MultiArray;

public class PubRobotStatus extends AbstractNodeMain {
    private static final java.lang.String TAG = PubRobotStatus.class.getSimpleName();

    Int32MultiArray comandoHaciaRobot;
    int[] comandoDesdeApp;
    boolean enviado=false;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("PublicadorRobotStatus");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        final Publisher<Int32MultiArray> publicador=connectedNode.newPublisher("set_estado",Int32MultiArray._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void setup() {
                comandoHaciaRobot = publicador.newMessage();
            }

            @Override
            protected void loop() throws InterruptedException {
                if(publicador.hasSubscribers() && !enviado) {
                    comandoHaciaRobot.setData(comandoDesdeApp);
                    publicador.publish(comandoHaciaRobot);
                    enviado=true;
                }
            }
        });
    }

    public void sendInfoToRobot(int[] infoDesdeApp){
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
