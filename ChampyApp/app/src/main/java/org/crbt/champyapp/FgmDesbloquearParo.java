package org.crbt.champyapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.crbt.champyapp.databinding.FgmDesbloquearParoBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmDesbloquearParo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmDesbloquearParo extends Fragment {
    FgmDesbloquearParoBinding fgmBinding;
    MainViewModel mainViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmDesbloquearParo() {
        // Required empty public constructor
    }

    public static FgmDesbloquearParo newInstance(){
        return new FgmDesbloquearParo();
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmDesbloquearParo.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmDesbloquearParo newInstance(String param1, String param2) {
        FgmDesbloquearParo fragment = new FgmDesbloquearParo();
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
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fgmBinding= FgmDesbloquearParoBinding.inflate(inflater,container,false);
        return  fgmBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Se inicializa el viewModel
        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);



        fgmBinding.btnDesbloquearParo.setOnClickListener(view -> {
            activarListenerRespuestaRobot();
            String password=fgmBinding.etPasswordParo.getText().toString();
            if(password.equals("champy")){
                mainViewModel.actualizarEstadoAParoDesactivado();
                Toast.makeText(getContext(),"Enviando estado de paro desactivado",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(),"Contraseña invalida",Toast.LENGTH_SHORT).show();
        });
    }



    public void activarListenerRespuestaRobot(){
        mainViewModel.getStatusRespuestaRobot().observe(getViewLifecycleOwner(),statusRespuestaRobot->{
            if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.DONE){

                if(mainViewModel.isControlManualActivado())
                    irControlManual();
                else{
                    mainViewModel.setVentanaAnterior("fgm_mission_ol");
                    irMisionEnDirecto();
                }



                Toast.makeText(getContext(),"Paro desacivado",Toast.LENGTH_SHORT).show();
                mainViewModel.reiniciarBanderaRespuestaRobot();
//                mainViewModel.terminarEnvioEstadoARobot();
            }
            else if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.FAILED){
                Toast.makeText(getContext(),"Ha llegado la petición, pero no se pudo inicia mapeo",Toast.LENGTH_LONG).show();
            }

        });

        //Se registra el nodo suscriptor que recibe la respuesta del robot
        mainViewModel.iniciarEscuchaRespuestaRobot();
    }

    public void regresarFgmAnterior(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void irMisionEnDirectoInvisible(){
        getActivity().getSupportFragmentManager().popBackStack("fgm_mission_ol", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void irMisionEnDirecto(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMisionOnLive.newInstance(),"fgm_mission_ol")
                .commit();

//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.main_container, getActivity().getSupportFragmentManager().findFragmentByTag("fgm_mission_ol"))
//                .show(getActivity().getSupportFragmentManager().findFragmentByTag("fgm_mission_ol"))
//                .commit();
    }

    public void irControlManual(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, getActivity().getSupportFragmentManager().findFragmentByTag("fgm_control_manual"))
                .show(getActivity().getSupportFragmentManager().findFragmentByTag("fgm_control_manual"))
                .commit();    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }
}