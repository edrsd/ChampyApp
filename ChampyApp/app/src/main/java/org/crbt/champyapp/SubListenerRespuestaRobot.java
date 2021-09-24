package org.crbt.champyapp;

import android.util.Log;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;

import std_msgs.Int32MultiArray;

public class SubListenerRespuestaRobot extends AbstractNodeMain {
    private static final java.lang.String TAG = SubListenerRespuestaRobot.class.getSimpleName();


    private final  RespuestaRobotListener listener;

    public SubListenerRespuestaRobot(RespuestaRobotListener escuchador){
        this.listener=escuchador;
    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("suscriptorRespuestaRobot");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<Int32MultiArray> suscriptor=connectedNode.newSubscriber("respuesta_robot", Int32MultiArray._TYPE);

        suscriptor.addMessageListener( respuesta->{
            Int32MultiArray comandoRecibido=respuesta;
            Log.i(TAG, " Entrando en respuesta del estatus de la operación en el Robot");
            int [] arrayRespuestaRobot=respuesta.getData();
            listener.unaNuevaRespuestaDelRobot(arrayRespuestaRobot[0]+";"+arrayRespuestaRobot[1]);
        });
    }


    @Override
    public void onShutdown(Node node) {
        node.removeListeners();
        node.shutdown();
        super.onShutdown(node);
    }


    public interface RespuestaRobotListener{
        void unaNuevaRespuestaDelRobot(String respuestaRecibida);
    }

}
