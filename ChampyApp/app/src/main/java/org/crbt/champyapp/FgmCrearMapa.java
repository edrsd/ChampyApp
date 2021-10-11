package org.crbt.champyapp;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.crbt.champyapp.databinding.FgmCrearMapaBinding;
import org.crbt.champyapp.databinding.FgmMainMenuBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmCrearMapa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmCrearMapa extends Fragment {

    // vinculacion con el Layout
    FgmCrearMapaBinding fgmBinding;
    //manejo de datos de la app
    MainViewModel mainViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmCrearMapa() {
        // Required empty public constructor
    }

    //Crea una nueva instancia del fragment
    public static FgmCrearMapa newInstance(){
        return new FgmCrearMapa();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmCrearMapa.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmCrearMapa newInstance(String param1, String param2) {
        FgmCrearMapa fragment = new FgmCrearMapa();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Forza al fragment a vizualizarse en posición horizontal
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Se inicializa fgmBinding
        fgmBinding=FgmCrearMapaBinding.inflate(inflater,container,false);
        //Se obtiene la vista raiz
        return  fgmBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Se inicializa el viewModel
        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        activarListenerRespuestaRobot();

        activarListenerSsh();

        fgmBinding.btnCrearMapaCm.setOnClickListener(view -> {

            String nombreMapa=fgmBinding.etNombreDelMapa.getText().toString();

            if(fgmBinding.etNombreDelMapa.getText().toString().isEmpty() || fgmBinding.etNombreDelMapa.getText().toString().equals("")){
                Toast.makeText(getContext(),"Debe colocar un nombre para el mapa",Toast.LENGTH_LONG).show();
            }else{
                mainViewModel.sshIniciarMapeo(nombreMapa);

                fgmBinding.btnVolverMenuPrincipal.setEnabled(false);
                fgmBinding.btnCrearMapaCm.setEnabled(false);
                fgmBinding.tilCrearMapa.setEnabled(false);
            }

        });

        fgmBinding.btnVolverMenuPrincipal.setOnClickListener(view -> {
            irFgmMenuPrincipal();
        });
    }


    public void activarListenerRespuestaRobot(){

        mainViewModel.getStatusRespuestaRobot().observe(getViewLifecycleOwner(),statusRespuestaRobot->{
            if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.DONE){

                fgmBinding.btnCrearMapaCm.setBackgroundColor(Color.GREEN);
                fgmBinding.btnCrearMapaCm.setText("¡Guardado!");

                irFgmRealizarMapeo();

                mainViewModel.setNodoMapeoActivado();
                Toast.makeText(getContext(),"Nodo de mapeo iniciado correctamente",Toast.LENGTH_SHORT).show();

                mainViewModel.reiniciarBanderaRespuestaRobot();
//                mainViewModel.terminarEscuchaRespuestaRobot();
//                mainViewModel.terminarSolicitudDeAppEnRobot();

            }
            else if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.FAILED){
                Toast.makeText(getContext(),"Ha llegado la petición, pero no se pudo inicia mapeo",Toast.LENGTH_LONG).show();
            }
        });

        //Se registra el nodo suscriptor que recibe la respuesta del robot
//        mainViewModel.iniciarEscuchaRespuestaRobot();

    }


    public void activarListenerSsh(){
        //Se espera la respuesta del estatus de los comandos mandados enviados por ssh
        mainViewModel.seHanCompletadoLosComandos().observe(getViewLifecycleOwner(),sshCommandsStatus->{

            if(sshCommandsStatus== SshRepositoryImpl.SshCommandsStatus.DONE){
                mainViewModel.solicitarIniciarMapeoEnRobot();

                Toast.makeText(getContext(),"SSH: mapa creado; solicitando inicio de mapeo publicador_app=4",Toast.LENGTH_SHORT).show();

                mainViewModel.reiniciarBanderaComandosRealizados();
            } else if (sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.PENDING) {
                fgmBinding.btnCrearMapaCm.setTextColor(Color.BLACK);
                fgmBinding.btnCrearMapaCm.setBackgroundColor(Color.YELLOW);
                fgmBinding.btnCrearMapaCm.setText("Guardando...");

            } else if (sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.FAILED) {
                Toast.makeText(getContext(), "SSH: No se ha podido realizar el comando", Toast.LENGTH_LONG).show();
                mainViewModel.reiniciarBanderaComandosRealizados();
                //Se muestra una ventana para conectar de nuevo cuando el tiempo de conexión expira
//                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//                dialog.setMessage("No se ha podido establecer conexión")
//                        .setPositiveButton("Ok",(dialogInterface,i)->{ dialogInterface.dismiss(); });
//                dialog.show();
            }
            else if(sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.ERROR){
                String mensajeError=mainViewModel.solicitarError();

                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Error al establecer la conexión SSH.");
                dialog.setMessage("\n"+mensajeError)
                        .setPositiveButton("Ok",(a,b)->{
                            a.dismiss();
                        });
                dialog.show();
                mainViewModel.reiniciarBanderaComandosRealizados();

                irFgmMenuPrincipal();

            }
        });
    }

    public void irFgmRealizarMapeo(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmCrearMapaOnLive.newInstance(),"fgm_crear_mapa_on_live")
                .addToBackStack(null).commit();
    }

    public void irFgmMenuPrincipal(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMainMenu.newInstance(),"fgm_menu_principal")
                .commit();
//        getActivity().getSupportFragmentManager().popBackStack("main_menu", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }
}