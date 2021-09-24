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

import org.crbt.champyapp.databinding.FgmCrearMisionBinding;
import org.crbt.champyapp.databinding.FgmMisionesBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmCrearMision#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmCrearMision extends Fragment {

    // vinculacion con el Layout
    FgmCrearMisionBinding fgmBinding;
    //manejo de datos de la app
    MainViewModel mainViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmCrearMision() {
        // Required empty public constructor
    }

    public static FgmCrearMision newInstance(){
        return new FgmCrearMision();
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmCrearMision.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmCrearMision newInstance(String param1, String param2) {
        FgmCrearMision fragment = new FgmCrearMision();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Se inicializa fgmBinding
        fgmBinding= FgmCrearMisionBinding.inflate(inflater,container,false);
        //Se obtiene la vista raiz
        return  fgmBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        activarListenerSsh();

        fgmBinding.btnGuardarMision.setOnClickListener(view -> {
            fgmBinding.btnGuardarMision.setTextColor(Color.BLACK);
            fgmBinding.btnGuardarMision.setText("Guardando...");
            fgmBinding.btnGuardarMision.setBackgroundColor(Color.YELLOW);

            mainViewModel.sshGuardarMision(fgmBinding.etNombreMision.getText().toString());
        });

        fgmBinding.btnCancelarCrearMision.setOnClickListener(view -> {
            irMainCrearMision();
        });
    }

    public void activarListenerSsh(){
        mainViewModel.seHanCompletadoLosComandos().observe(getViewLifecycleOwner(),sshCommandsStatus->{
            if(sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.DONE){

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                fgmBinding.btnGuardarMision.setText("Guardado");
                fgmBinding.btnGuardarMision.setBackgroundColor(Color.GREEN);

//                mainViewModel.seHanCompletadoLosComandos().removeObservers(getViewLifecycleOwner());
                irListaMisiones();
                Toast.makeText(getContext(),"SSH:Se ha guardado la misión correctamente",Toast.LENGTH_SHORT).show();

                mainViewModel.reiniciarBanderaComandosRealizados();

            }
            else if(sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.FAILED){
                fgmBinding.btnGuardarMision.setText("Guardar");
                fgmBinding.btnGuardarMision.setBackgroundColor(Color.RED);
                Toast.makeText(getContext(),"No se ha podido establecer conexión ssh con el robot",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void irListaMisiones(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmListaMisiones.newInstance(),"fgm_lista_misiones")
                .addToBackStack(null)
                .commit();
    }

    public void irMainCrearMision(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, getActivity().getSupportFragmentManager().findFragmentByTag("fgm_main_crear_mision"))
                .show(getActivity().getSupportFragmentManager().findFragmentByTag("fgm_main_crear_mision"))
                .commit();    }

    public void regresaACrearMision(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }
}