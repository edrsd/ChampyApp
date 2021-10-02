package org.crbt.champyapp;

import android.util.Log;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;

import geometry_msgs.Pose;
import geometry_msgs.PoseStamped;
import geometry_msgs.Quaternion;
import std_msgs.Float32MultiArray;

public class SubRobotData extends AbstractNodeMain {
    private static final java.lang.String TAG = SubRobotData.class.getSimpleName();
    private final DatosRobotListener listener;

    public SubRobotData(DatosRobotListener listener){
        this.listener=listener;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("SuscriptorDatoRobot");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        Subscriber<Float32MultiArray> suscriber = connectedNode.newSubscriber("info_robot", Float32MultiArray._TYPE);

        suscriber.addMessageListener(infoDato -> {
            Log.i(TAG, " Entrando en onNewMessage DatoRobot ");
            float [] arrayInfoDato=infoDato.getData();
            listener.unNuevoDatoRecibido(arrayInfoDato);
        });
    }

    @Override
    public void onShutdown(Node node) {
        node.removeListeners();
        node.shutdown();
        super.onShutdown(node);
    }

    public interface DatosRobotListener{
        void unNuevoDatoRecibido(float[] datoRecibido);
    }
}
