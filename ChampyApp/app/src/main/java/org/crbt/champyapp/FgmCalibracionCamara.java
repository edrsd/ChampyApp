package org.crbt.champyapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import org.crbt.champyapp.databinding.FgmCalibracionCamaraBinding;
import org.crbt.champyapp.databinding.FgmCrearMapaOnLiveBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmCalibracionCamara#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmCalibracionCamara extends Fragment implements SeekBar.OnSeekBarChangeListener {
    FgmCalibracionCamaraBinding fgmBinding;
    MainViewModel mainViewModel;
    int [] arrayParametrosCamara=new int[8];

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmCalibracionCamara() {
        // Required empty public constructor
    }

    public static FgmCalibracionCamara newInstance(){
        return new FgmCalibracionCamara();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmCalibracionCamara.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmCalibracionCamara newInstance(String param1, String param2) {
        FgmCalibracionCamara fragment = new FgmCalibracionCamara();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fgmBinding= FgmCalibracionCamaraBinding.inflate(inflater,container,false);

        fgmBinding.sbBrightness.setOnSeekBarChangeListener(this);
        fgmBinding.sbContrast.setOnSeekBarChangeListener(this);
        fgmBinding.sbHue.setOnSeekBarChangeListener(this);
        fgmBinding.sbSaturation.setOnSeekBarChangeListener(this);
        fgmBinding.sbSharpness.setOnSeekBarChangeListener(this);
        fgmBinding.sbGamma.setOnSeekBarChangeListener(this);
        fgmBinding.sbGain.setOnSeekBarChangeListener(this);
        fgmBinding.sbExposure.setOnSeekBarChangeListener(this);


        return  fgmBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Se inicializa el viewModel
        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        activarListenerCamara();


        fgmBinding.btnVolverMenuPrincipal.setOnClickListener(view -> {
            irMenuPrincipal();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int valor, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_brightness:
                fgmBinding.tvBrightness.setText("Brightness: "+Integer.toString(valor));
                arrayParametrosCamara[0]=valor;
                mainViewModel.cambiarParametrosDeCamara(arrayParametrosCamara);
                break;
            case R.id.sb_contrast:
                fgmBinding.tvContrast.setText("Contrast: "+Integer.toString(valor));
                arrayParametrosCamara[1]=valor;
                mainViewModel.cambiarParametrosDeCamara(arrayParametrosCamara);
                break;
            case R.id.sb_hue:
                fgmBinding.tvHue.setText("Hue: "+Integer.toString(valor));
                arrayParametrosCamara[2]=valor;
                mainViewModel.cambiarParametrosDeCamara(arrayParametrosCamara);
                break;
            case R.id.sb_saturation:
                fgmBinding.tvSaturation.setText("Saturation: "+Integer.toString(valor));
                arrayParametrosCamara[3]=valor;
                mainViewModel.cambiarParametrosDeCamara(arrayParametrosCamara);
                break;
            case R.id.sb_sharpness:
                fgmBinding.tvSharpness.setText("Sharpness: "+Integer.toString(valor));
                arrayParametrosCamara[4]=valor;
                mainViewModel.cambiarParametrosDeCamara(arrayParametrosCamara);
                break;
            case R.id.sb_gamma:
                fgmBinding.tvGamma.setText("Gamma: "+Integer.toString(valor));
                arrayParametrosCamara[5]=valor;
                mainViewModel.cambiarParametrosDeCamara(arrayParametrosCamara);
                break;
            case R.id.sb_gain:
                fgmBinding.tvGain.setText("Gain: "+Integer.toString(valor));
                arrayParametrosCamara[6]=valor;
                mainViewModel.cambiarParametrosDeCamara(arrayParametrosCamara);
                break;
            case R.id.sb_exposure:
                fgmBinding.tvExposure.setText("Exposure: "+Integer.toString(valor));
                arrayParametrosCamara[7]=valor;
                mainViewModel.cambiarParametrosDeCamara(arrayParametrosCamara);
                break;
            default:
                return;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void activarListenerCamara(){
        //Se escucha la respuesta del nodo de mapeo, el cual entrega una imagen del mapa
        mainViewModel.getCameraImage().observe(getViewLifecycleOwner(),bitmapCamara -> {
            fgmBinding.ivCamara.setImageBitmap(bitmapCamara);
        });
        mainViewModel.iniciarEscuchaCamara();
    }

    public void irMenuPrincipal(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMainMenu.newInstance(),"fgm_menu_principal")
                .commit();
    }
}