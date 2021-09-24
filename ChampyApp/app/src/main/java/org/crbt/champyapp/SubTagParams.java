package org.crbt.champyapp;

import android.util.Log;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import std_msgs.Float32MultiArray;

public class SubTagParams extends AbstractNodeMain {
    private static final java.lang.String TAG = SubTagParams.class.getSimpleName();
    private final SubTagParams.TagParamsListener escuchador;

    public SubTagParams(TagParamsListener escuchador){
        this.escuchador=escuchador;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("nodo_suscriptor_parametros_tag");
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {

        Subscriber<Float32MultiArray> suscriberTag = connectedNode.newSubscriber("marker_data", Float32MultiArray._TYPE);

        suscriberTag.addMessageListener(infoTag -> {
            Log.i(TAG, " Entrando en onNewMessage Posición ");
            float [] arrayInfoTag=infoTag.getData();
            escuchador.onNewParamsListened(arrayInfoTag[0]+";"+arrayInfoTag[1]+";"+arrayInfoTag[2]+";"+arrayInfoTag[3]);
        });
    }


    @Override
    public void onShutdown(Node node) {
        node.removeListeners();
        node.shutdown();
        super.onShutdown(node);
    }

    public interface TagParamsListener{
        void onNewParamsListened(String tagParams);
    }
}
