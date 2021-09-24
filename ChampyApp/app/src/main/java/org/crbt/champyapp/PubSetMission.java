package org.crbt.champyapp;

import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import geometry_msgs.Twist;
import geometry_msgs.Vector3;
import std_msgs.Float32MultiArray;

public class PubSetMission extends AbstractNodeMain {

    private static final java.lang.String TAG = PubSetMission.class.getSimpleName();

    Float32MultiArray comandoHaciaRobot;
    float[] comandoDesdeApp;
    boolean enviado=false;

    @Override
    public GraphName getDefaultNodeName() {
        return  GraphName.of("PublicadorSetMission");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        final Publisher<Float32MultiArray> publicador=connectedNode.newPublisher("set_mission",Float32MultiArray._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void setup() {
                comandoHaciaRobot = publicador.newMessage();
            }

            @Override
            protected void loop() throws InterruptedException {
                if(publicador.hasSubscribers()&&!enviado){
                    comandoHaciaRobot.setData(comandoDesdeApp);
                    publicador.publish(comandoHaciaRobot);
                    enviado=true;
                }
            }
        });
    }

    public void sendInfoToRobot(float[] infoDesdeApp){
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
