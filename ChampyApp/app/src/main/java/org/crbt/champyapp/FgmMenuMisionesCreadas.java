package org.crbt.champyapp;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.crbt.champyapp.databinding.FgmMenuMisionesCreadasBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmMenuMisionesCreadas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmMenuMisionesCreadas extends Fragment {

    FgmMenuMisionesCreadasBinding fgmBinding;
    MainViewModel mainViewModel;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmMenuMisionesCreadas() {
        // Required empty public constructor
    }

    public static FgmMenuMisionesCreadas newInstance(){
        return new FgmMenuMisionesCreadas();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmMenuMisionesCreadas.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmMenuMisionesCreadas newInstance(String param1, String param2) {
        FgmMenuMisionesCreadas fragment = new FgmMenuMisionesCreadas();
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
        fgmBinding= FgmMenuMisionesCreadasBinding.inflate(inflater,container,false);
        return  fgmBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Se inicializa el viewModel
        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        fgmBinding.btnMenuComenzarMision.setOnClickListener(view -> {

//            activarListenerRespuestaRobot();
//            mainViewModel.solicitarIniciarMisionEnRobot();

        });

        fgmBinding.btnMenuEditarMision.setOnClickListener(view -> {


        });

        fgmBinding.btnMenuPrincipal.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().popBackStack("main_menu", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
    }


    private void activarListenerRespuestaRobot(){
        //*
//        mainViewModel.getRespuestaRobot().observe(getViewLifecycleOwner(),respuestaRecibida->{
//            if(isCancelBtnClicked || isSaveBtnClicked){
//                String [] respuesta=respuestaRecibida.split(";");
//                String tipoOperacion=respuesta[0];
//                String isSuccess=respuesta[1];
//
//                if(tipoOperacion.equals("1")&&isSuccess.equals("0")){
//                    mainViewModel.terminarMapeoEnRobot();
//                }
//                else if(tipoOperacion.equals("1")&&isSuccess.equals("1")){
//
//                    mainViewModel.getRespuestaRobot().removeObservers(getViewLifecycleOwner());
//
//                    mainViewModel.terminarEscuchaRespuestaRobot();
//
//                    mainViewModel.terminarSolicitudDeAppEnRobot();
//
//                    Toast.makeText(getContext(),"El nodo de mapeo ha sido detenido",Toast.LENGTH_LONG).show();
//
//                    if(isCancelBtnClicked){
//                        activarListenerSsh();
//                        mainViewModel.sshCancelarMapeo();
//                        isCancelBtnClicked=false;
//                    }else if(isSaveBtnClicked){
//                        isSaveBtnClicked=false;
//                        irFgmListaMapasMisiones();
//                    }
//                }
//            }
//        });
//
//        mainViewModel.iniciarEscuchaRespuestaRobot();
    }


    public void irFgmMainComenzarMision(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMisionOnLive.newInstance(),"fgm_main_crear_mision")
                .addToBackStack(null)
                .commit();
    }

    public void irFgmListaMisiones(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, FgmListaMisiones.newInstance(),"fgm_lista_misiones")
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }
}