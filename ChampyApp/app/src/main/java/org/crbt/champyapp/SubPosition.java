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

public class SubPosition extends AbstractNodeMain {
    private static final java.lang.String TAG = SubPosition.class.getSimpleName();
    private final Escuchador escuchador;

    public SubPosition(Escuchador escuchador){
        this.escuchador=escuchador;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("suscriptor_posicion");
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<PoseStamped> subscriberPosicion=connectedNode.newSubscriber("slam_out_pose", PoseStamped._TYPE);

        subscriberPosicion.addMessageListener( posicion-> {
            Log.i(TAG, " Entrando en onNewMessage Posición ");

            PoseStamped poseStamped = (PoseStamped) posicion;
            Pose pose = poseStamped.getPose();
            Quaternion quaternion = pose.getOrientation();

            escuchador.unaNuevaPosicionEscuchada(pose.getPosition().getX()+";"+
                    pose.getPosition().getY()+";"+
                    quaternion.getZ()+";"+
                    quaternion.getW()+";"+
                    quaternionToEuler(quaternion));
        });

    }


    public static int  quaternionToEuler(Quaternion q){
        int angulosEuler;

        double qX=q.getX();
        double qY=q.getY();
        double qZ=q.getZ();
        double qW=q.getW();

//        double qw2 = q.getW() * q.getW();
//        double qx2 = q.getX() * q.getX();
        double qy2 = q.getY() * q.getY();
        double qz2 = q.getZ() * q.getZ();

//        angulosEuler[0] = (float)Math.atan2(2f * qX *qZ + 2f * qY * q.Z, 1 - 2f * (sqz  + sqw));     // Yaw
//        angulosEuler[1] = (float)Math.asin(2f * ( q.getX() * q.getZ() - q.getW() * q.getY() ) );                             // Pitch
        angulosEuler = (int) Math.toDegrees(Math.atan2(2f * qX * qY + 2f * qZ * qW, 1 - 2f * (qy2 + qz2)));      // Roll

        return angulosEuler;
    }


    @Override
    public void onShutdown(Node node) {
        node.removeListeners();
        node.shutdown();
    }

    public interface Escuchador{
        void unaNuevaPosicionEscuchada(String posicion);
    }
}
