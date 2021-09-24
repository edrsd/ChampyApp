package org.crbt.champyapp;

import android.util.Log;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;

import std_msgs.Float32MultiArray;

public class SubListenerInfoMision extends AbstractNodeMain {
    private static final java.lang.String TAG = SubListenerInfoMision.class.getSimpleName();

    private final SubListenerInfoMision.GetMissionListener escuchador;

    public SubListenerInfoMision(GetMissionListener escuchador){
        this.escuchador=escuchador;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("suscriptorGetMission");
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {

        Subscriber<Float32MultiArray> suscriberTag = connectedNode.newSubscriber("load_mission", Float32MultiArray._TYPE);

        suscriberTag.addMessageListener(infoTag -> {
            Log.i(TAG, " Entrando en onNewMessage Posición ");
            float [] arrayInfoMision=infoTag.getData();
            escuchador.onNewInfoMission(arrayInfoMision);
        });
    }


    @Override
    public void onShutdown(Node node) {
        node.removeListeners();
        node.shutdown();
        super.onShutdown(node);
    }

    public interface GetMissionListener{
        void onNewInfoMission(float[] InfoMission);
    }
}
