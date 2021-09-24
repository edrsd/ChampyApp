package org.crbt.champyapp;

import android.util.Log;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;


public class SubListenerListMissionsRequest extends AbstractNodeMain {
    private static final java.lang.String TAG = SubListenerListMissionsRequest.class.getSimpleName();


    private final ListaMisionesListener listener;

    public SubListenerListMissionsRequest(ListaMisionesListener escuchador) {
        this.listener = escuchador;
    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("suscriptorListMissionsRequest");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<std_msgs.String> suscriptor = connectedNode.newSubscriber("list_request_misiones", std_msgs.String._TYPE);

        suscriptor.addMessageListener(respuesta -> {
            std_msgs.String comandoRecibido = respuesta;
            Log.i(TAG, " Entrando en respuesta de la solicitud de lista");
            String arrayRespuestaRobot = respuesta.getData();
            listener.unaNuevaListaMisiones(arrayRespuestaRobot);
        });
    }


    @Override
    public void onShutdown(Node node) {
        node.removeListeners();
        node.shutdown();
        super.onShutdown(node);
    }


    public interface ListaMisionesListener {
        void unaNuevaListaMisiones(String respuestaRecibida);
    }
}
