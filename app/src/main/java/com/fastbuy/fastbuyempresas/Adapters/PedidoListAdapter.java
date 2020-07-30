package com.fastbuy.fastbuyempresas.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.DetallesActivity;
import com.fastbuy.fastbuyempresas.Entidades.Pedido;
import com.fastbuy.fastbuyempresas.MenuActivity;
import com.fastbuy.fastbuyempresas.R;

import java.util.ArrayList;

public class PedidoListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Pedido> pedidoList;
    private Typeface script2;
    //ArrayList<Pedido> list;

    public PedidoListAdapter(Context context, int layout, ArrayList<Pedido> pedidoList) {
        this.context = context;
        this.layout = layout;
        this.pedidoList = pedidoList;
    }
    @Override
    public int getCount() {
        return pedidoList.size();
    }

    @Override
    public Object getItem(int position) {
        return pedidoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder{
        LinearLayout lypedidos;
        TextView txtpedido;
        TextView txthora;
        TextView txtitems;
        ImageView btnVer;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtitems = (TextView) row.findViewById(R.id.txtItems);
            holder.txtpedido = (TextView) row.findViewById(R.id.txtPendiente);
            holder.txthora = (TextView) row.findViewById(R.id.txtHora);
            holder.lypedidos= (LinearLayout) row.findViewById(R.id.lyPedidos);
            holder.btnVer= (ImageView) row.findViewById(R.id.ivdetalles);
            holder.btnVer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Globales.pedidoSeleccionado= String.valueOf(pedidoList.get(position).getCodigo());
                    Globales.pedidoestado= String.valueOf(pedidoList.get(position).getAtendido());
                    Globales.pedidohora= String.valueOf(separarHora(pedidoList.get(position).getHoraPedido()));
                    Globales.pedidoitems= String.valueOf(pedidoList.get(position).getItem()+" items");
                    Globales.fpago= pedidoList.get(position).getFpago();

                    Intent intent = new Intent(context, DetallesActivity.class);
                    context.startActivity(intent);
                }
            });
            holder.lypedidos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Globales.pedidoSeleccionado= String.valueOf(pedidoList.get(position).getCodigo());
                    Globales.pedidoestado= String.valueOf(pedidoList.get(position).getAtendido());
                    Globales.pedidohora= String.valueOf(separarHora(pedidoList.get(position).getHoraPedido()));
                    Globales.pedidoitems= String.valueOf(pedidoList.get(position).getItem()+" items");
                    Globales.fpago= pedidoList.get(position).getFpago();

                    Intent intent = new Intent(context, DetallesActivity.class);
                    context.startActivity(intent);
                }
            });
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }
        String fuente2="fonts/GOTHIC.ttf";
        this.script2= Typeface.createFromAsset(context.getAssets(),fuente2);
        holder.txtpedido.setTypeface(script2);
        holder.txtitems.setTypeface(script2);
        holder.txthora.setTypeface(script2);

        Pedido pedido = pedidoList.get(position);
        holder.txtpedido.setText(pedido.getAtendido());
        holder.txthora.setText(separarHora(pedido.getHoraPedido()));
        holder.txtitems.setText(String.valueOf(" "+pedido.getItem()+" Items"));
        colorPedido(pedido.getAtendido(),holder);
        return  row;
    }
    public String separarHora(String hora){
        String[] partes= hora.split(":");
        String h= partes[0];
        String m= partes[1];
        if(Integer.parseInt(h)>12){
            int hp=Integer.parseInt(h)-12;
            return hp+":"+m+" p.m";
        }else{
            return h+":"+m+" a.m";
        }
    }
    public void colorPedido(String pedido, ViewHolder holder){
        if(pedido==" PENDIENTE"){
            holder.lypedidos.setBackgroundResource(R.drawable.efecto_lypendientes);
        }if(pedido==" FINALIZADO"){
            holder.lypedidos.setBackgroundResource(R.drawable.efecto_lyatendido);
        }if(pedido==" ANULADO"){
            holder.lypedidos.setBackgroundResource(R.drawable.efecto_lyanulado);
        }if(pedido==" EN PROCESO"){
            holder.lypedidos.setBackgroundResource(R.drawable.efecto_lyproceso);
        }if(pedido==" PREPARANDO"){
            holder.lypedidos.setBackgroundResource(R.drawable.efecto_lypreparando);
        }if(pedido==" PREPARADO"){
            holder.lypedidos.setBackgroundResource(R.drawable.efecto_lypreparado);
        }if(pedido==" ESPERA"){
            holder.lypedidos.setBackgroundResource(R.drawable.efecto_lyespera);

        }
        if(pedido==" RECOGER"){
            holder.lypedidos.setBackgroundResource(R.drawable.efecto_lyproceso);
        }
    }
}
