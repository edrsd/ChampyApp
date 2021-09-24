package org.crbt.champyapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.crbt.champyapp.databinding.FgmConnectToChampyBinding;
import org.crbt.champyapp.Connection.ConnectionType;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmConnectToChampy#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmConnectToChampy extends Fragment {

    private FgmConnectToChampyBinding fgmBinding;
    private MainViewModel mainViewModel;

    SharedPreferences.Editor editor;
    public String masterIp;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmConnectToChampy() {
        // Required empty public constructor
    }

    //Se crea una instancia del Fragment
    public static FgmConnectToChampy newInstance(){
        return new FgmConnectToChampy();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmConnectToChampy.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmConnectToChampy newInstance(String param1, String param2) {
        FgmConnectToChampy fragment = new FgmConnectToChampy();
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

        SharedPreferences pref=getActivity().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor= pref.edit();
        masterIp= pref.getString("ipMaster","192.168.43.154");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //Se inicializa fgmBinding
        fgmBinding=FgmConnectToChampyBinding.inflate(inflater,container,false);
        //Se obtiene la vista raiz
        return  fgmBinding.getRoot();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Se inicializa el viewModel
        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        fgmBinding.etRobotIp.setText(masterIp);

        //Si se oprime el botón de conectar con robot, entonces...
        fgmBinding.btnConnectToRobot.setOnClickListener(view -> {
            activarListenerDeEstatusDeConexion();

            //Se pide la conexión al master a través del viewModel utilizando la ip escrita en el editText del fragment
            mainViewModel.connectRobotToMaster(fgmBinding.etRobotIp.getText().toString());
        });
    }




    public void activarListenerDeEstatusDeConexion(){
        //Se observa el estatus de la conexión con el servicio de ROS para manipular las vistas conforme al estado de la conexión
        mainViewModel.getRosServiceStatusConection().observe(getViewLifecycleOwner(),statusConnection-> {
            //Si se ha realizado la conexión correctamente, entonces...
            if (statusConnection == ConnectionType.CONNECTED) {
                mainViewModel.iniciarEscuchaRespuestaRobot();
                mainViewModel.iniciarEscuchaListaMisiones();
                mainViewModel.iniciarEscuchaListaRobot();
                mainViewModel.iniciarEscuchaInfoMision();
                mainViewModel.iniciarEscuchaDatosRobot();

                editor.putString("ipMaster",fgmBinding.etRobotIp.getText().toString());
                editor.commit();

                //Se muestra el botón en la app
                fgmBinding.btnConnectToRobot.setVisibility(View.VISIBLE);
                //Se cambia a verde el color del botón
                fgmBinding.btnConnectToRobot.setBackgroundColor(Color.GREEN);
                //Se cambia el texto del botón a "Conectado"
                fgmBinding.btnConnectToRobot.setText("¡Conectado!");
                //Se muestra un pequeño mensaje
                Toast.makeText(getContext(),"Dispositivo conectado satisfactoriamente",Toast.LENGTH_SHORT).show();
//                //Se ejecuta el método para cambiar al fragment correspondiente
                irMenuPrincipal();

//                irListaMision();

            } //Si la conexión se esta realizando aún, entonces...
            else if (statusConnection == ConnectionType.PENDING) {
                fgmBinding.btnConnectToRobot.setTextColor(Color.BLACK);
                fgmBinding.btnConnectToRobot.setBackgroundColor(Color.YELLOW);
                fgmBinding.btnConnectToRobot.setText("Conectando...");

            } else if(statusConnection == ConnectionType.DISCONNECTED){
                //Muestra el botón
//                fgmBinding.btnConnectToRobot.setVisibility(View.VISIBLE);
                //Oculta el progressBar
//                fgmBinding.pbConectando.setVisibility(View.GONE);
                //Pinta de rojo el botón
                fgmBinding.btnConnectToRobot.setBackgroundColor(Color.RED);
                //Cambia el texto del botón a "Conectar"
                fgmBinding.btnConnectToRobot.setText("Conectar");
            }
            else if(statusConnection == ConnectionType.FAILED){

                fgmBinding.btnConnectToRobot.setText("Conectar");
                fgmBinding.btnConnectToRobot.setBackgroundColor(getResources().getColor(R.color.pavisa_blue));
                fgmBinding.btnConnectToRobot.setTextColor(Color.WHITE);

                Toast.makeText(getContext(),"Tiempo de conexión espirado",Toast.LENGTH_SHORT).show();

//                //Se muestra una ventana para conectar de nuevo cuando el tiempo de conexión expira
//                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//                dialog.setMessage("Tiempo de conexión expirado")
//                        .setPositiveButton("Ok",(dialogInterface,i)->{
//                            //Muestra el botón
//                            fgmBinding.btnConnectToRobot.setVisibility(View.VISIBLE);
//                            //Oculta el progressBar
//                            fgmBinding.pbConectando.setVisibility(View.GONE);
//                            //Pinta de rojo el botón
//                            fgmBinding.btnConnectToRobot.setBackgroundColor(Color.RED);
//                            //Cambia el texto del botón a "Conectar"
//                            fgmBinding.btnConnectToRobot.setText("Conectar");
//                            dialogInterface.dismiss();
//                        });
//                dialog.show();
            }
        });
    }

    //Se cambia a la ventada de menú principal
    public void irMenuPrincipal(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMainMenu.newInstance(),"fgm_main_menu")
                .addToBackStack("inicio")
                .commit();
    }

//    //Método que ejecuta las instrucciones para cambiar al fragment que muestra la ventana donde se crea un mapa
//    public void irCrearMapaNuevo(){
//        //Se remplaza el fragment actual por el fragment en donde se crea un mapa nuevo, guardando en la pila de
//        // retroceso el fragment actual con el nombre de inicio por si es necesario regresar
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.main_container,FgmCrearMapa.newInstance(),"fgm_crear_mapa")
//                .addToBackStack("inicio")
//                .commit();
//    }

    public void irListaMision(){
        //Se remplaza el fragment actual por el fragment en donde se crea un mapa nuevo, guardando en la pila de
        // retroceso el fragment actual con el nombre de inicio por si es necesario a uturo regresar a este fragment actual
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmListaMisiones.newInstance(),"fgm_misiones_creadas")
                .addToBackStack("inicio")
                .commit();
    }

    //Se ejecuta este método cuando el fragment es destruido
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Se quita la referencia al fragment para no gastar memoria
        fgmBinding = null;
    }



    
}