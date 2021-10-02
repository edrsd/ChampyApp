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

import org.crbt.champyapp.databinding.FgmMainMenuBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmMainMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmMainMenu extends Fragment  {

    private FgmMainMenuBinding fgmBinding;
    private MainViewModel mainViewModel;

    private boolean btnApagarActivado=false,btnReiniciarActivado=false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmMainMenu() {
        // Required empty public constructor
    }

    //Se crea una instancia del Fragment
    public static FgmMainMenu newInstance(){
        return new FgmMainMenu();
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmMainMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmMainMenu newInstance(String param1, String param2) {
        FgmMainMenu fragment = new FgmMainMenu();
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
        fgmBinding= FgmMainMenuBinding.inflate(inflater,container,false);
        //Se obtiene la vista raiz
        return  fgmBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Se inicializa el viewModel
        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        activarListenerSsh();

        //Se muestra la ventana para crear un mapa nuevo
        fgmBinding.btnCrearMapa.setOnClickListener(view -> {
            irFgmCrearMapa();
        });

        //Se muestra la ventana para ver las lista de mapas creados
        fgmBinding.btnListaMapas.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, FgmListaMapas.newInstance(),"fgm_lista_mapas")
                    .addToBackStack("main_menu")
                    .commit();
        });


        //Se muestra la ventana para calibrar la cámara
        fgmBinding.btnCalibrarCamara.setOnClickListener(view -> {
            Toast.makeText(getContext(),"Calibrando cámara",Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container,FgmCalibracionCamara.newInstance(),"fgm_calibracion_camara")
                    .addToBackStack("main_menu")
                    .commit();

        });

        //Se muestra la ventana para mover los actuadores del robot
        fgmBinding.btnControlManual.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container,FgmMoverRobot.newInstance(),"fgm_control_manual")
                    .addToBackStack("main_menu")
                    .commit();
        });

        fgmBinding.btnDesconectar.setOnClickListener(view -> {
            mainViewModel.desconectar();
            Toast.makeText(getContext(),"Desconectado",Toast.LENGTH_SHORT).show();
            irInicio();

        });


        fgmBinding.btnApagarRobot.setOnClickListener(view -> {
            //Se muestra una ventana para conectar de nuevo cuando el tiempo de conexión expira
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage("¿Desea apagar el robot?")
                    .setPositiveButton("Aceptar",(dialogInterface,i)->{ apagarRobot(); })
                    .setNegativeButton("Cancelar",(dialogInterface,i)->{ dialogInterface.dismiss(); });
            dialog.show();
            Toast.makeText(getContext(),"Apagando robot",Toast.LENGTH_SHORT).show();

        });

        fgmBinding.btnReiniciarRobot.setOnClickListener(view -> {
            //Se muestra una ventana para conectar de nuevo cuando el tiempo de conexión expira
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage("¿Desea reiniciar el robot?")
                    .setPositiveButton("Aceptar",(dialogInterface,i)->{ reiniciarRobot(); })
                    .setNegativeButton("Cancelar",(dialogInterface,i)->{ dialogInterface.dismiss(); });
            dialog.show();
            Toast.makeText(getContext(),"Reiniciando robot",Toast.LENGTH_SHORT).show();

        });
    }



    public void irFgmCrearMapa(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmCrearMapa.newInstance(),"fgm_crear_mapa")
                .addToBackStack("main_menu")
                .commit();
    }

    public void apagarRobot(){
        btnApagarActivado=true;
        fgmBinding.btnApagarRobot.setText("Apagando...");
        fgmBinding.btnApagarRobot.setBackgroundColor(Color.RED);
        mainViewModel.desconectar();
        mainViewModel.solicitarApagarRobot();
        irInicio();
    }


    public void reiniciarRobot(){
        btnReiniciarActivado=true;
        fgmBinding.btnReiniciarRobot.setText("Reiniciando...");
        fgmBinding.btnReiniciarRobot.setBackgroundColor(Color.RED);
        mainViewModel.desconectar();
        mainViewModel.solicitarReiniciarRobot();
        irInicio();
    }

    public void irFgmConexion(){
        getActivity().getSupportFragmentManager().popBackStack("inicio", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    public void activarListenerSsh(){

        mainViewModel.seHanCompletadoLosComandos().observe(getViewLifecycleOwner(),sshCommandsStatus->{
            if(sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.DONE){

                mainViewModel.reiniciarBanderaComandosRealizados();
                mainViewModel.seHanCompletadoLosComandos().removeObservers(getViewLifecycleOwner());

                if(btnApagarActivado || btnReiniciarActivado){
                    btnReiniciarActivado=false;
                    btnApagarActivado=false;
                    irInicio();
                }
            }
            else if(sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.FAILED){
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

                dialog.setTitle("Fallo en la conexión")
                        .setMessage("El comando SSH no ha podido enviarse")
                        .setPositiveButton("Ok",(dialogInterface,i)->{ dialogInterface.dismiss(); });
                dialog.show();
            }
        });
    }


    public void irInicio(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmConnectToChampy.newInstance(),"fgm_conexion_champy")
                .commit();    }

//    public void irFgmMenuPrincipal(){
//        getActivity().getSupportFragmentManager().popBackStack("main_menu", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }


}