package org.crbt.champyapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorDeListas extends RecyclerView.Adapter<AdaptadorDeListas.ListViewHolder> implements View.OnClickListener{

    ArrayList<String> elementosLista;
    private View.OnClickListener listener;

    public AdaptadorDeListas(ArrayList<String> elementos){
        this.elementosLista=elementos;
    }

    @NonNull
    @Override
    public AdaptadorDeListas.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.formato_view_listas, null, false);
        AdaptadorDeListas.ListViewHolder viewHolder = new AdaptadorDeListas.ListViewHolder(vista);
        vista.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorDeListas.ListViewHolder holder, int position) {
        holder.tvNombreElemento.setText(elementosLista.get(position));
    }

    @Override
    public int getItemCount() {
        return elementosLista.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }

    static public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreElemento;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreElemento=itemView.findViewById(R.id.tv_elemento);

        }
    }
}
