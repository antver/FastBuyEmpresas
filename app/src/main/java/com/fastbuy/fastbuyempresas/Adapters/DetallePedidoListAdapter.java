package com.fastbuy.fastbuyempresas.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.CategoriasActivity;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.DetallesActivity;
import com.fastbuy.fastbuyempresas.Entidades.DetallePedido;
import com.fastbuy.fastbuyempresas.MenuActivity;
import com.fastbuy.fastbuyempresas.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class DetallePedidoListAdapter extends BaseAdapter {
    private Context context;
    private Activity activity;
    private int layout;
    private ArrayList<DetallePedido> pedidoList;
    public int contador;
    Typeface script;

    public DetallePedidoListAdapter(Context context, int layout, ArrayList<DetallePedido> pedidoList, Activity activity) {
        this.context = context;
        this.activity = activity;
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
        TextView txtCantidad;
        TextView txtProducto;
        TextView txtPrecio;
        TextView txtCambioestado;
        ImageButton btnCancelarD, btnlistoped;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View row = view;
        DetallePedidoListAdapter.ViewHolder holder = new DetallePedidoListAdapter.ViewHolder();
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtCantidad = (TextView) row.findViewById(R.id.txtCantidad);
            holder.txtProducto = (TextView) row.findViewById(R.id.txtProducto);
            holder.txtPrecio = (TextView) row.findViewById(R.id.txtPrecio);
            holder.txtCambioestado= (TextView) row.findViewById(R.id.txtCambioestado);
            holder.btnCancelarD= (ImageButton) row.findViewById(R.id.ivcancelard);
            holder.btnlistoped=(ImageButton) row.findViewById(R.id.ivPreparado);
            SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(context);
            final String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
            final View finalRow = row;
            holder.btnCancelarD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String url=Globales.servidor + "/Empresas/cancelarPD?auth=" + Globales.token+"&codigo="+Globales.pedidoSeleccionado+"&item="+pedidoList.get(position).getNumero()+"&empresa="+codigoe;
                    String mensaje="Cancelado";
                    int numero=pedidoList.get(position).getNumero();
                    String tipo="Desea cancelar este pedido?";
                    createCustomDialog(position,url,mensaje,tipo,numero, finalRow).show();
                }
            });
            final View finalRow1 = row;
            holder.btnlistoped.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    String url=Globales.servidor + "/Empresas/preparadoPD?auth=" + Globales.token+"&codigo="+Globales.pedidoSeleccionado+"&item="+pedidoList.get(position).getNumero();
                    String mensaje="Preparado";
                    int numero=pedidoList.get(position).getNumero();
                    String tipo="Pedido Preparado?";
                    createCustomDialog(position,url,mensaje,tipo,numero, finalRow1).show();
                }
            });
            row.setTag(holder);
        }
        else{
            holder = (DetallePedidoListAdapter.ViewHolder) row.getTag();
        }
        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(context.getAssets(),fuente2);
        holder.txtProducto.setTypeface(script);
        holder.txtCantidad.setTypeface(script);
        holder.txtPrecio.setTypeface(script);
        holder.txtCambioestado.setTypeface(script);

        DetallePedido detallePedido = pedidoList.get(position);
        holder.txtCantidad.setText(String.valueOf(detallePedido.getCantidad()));
        holder.txtProducto.setText(detallePedido.getProducto().getDescripcion() + "\n"+detallePedido.getPresentacion()+"\n" + detallePedido.getPersonalizacion());
        holder.txtPrecio.setText(String.valueOf(detallePedido.getTotal()) + "0");
        //ExceptionTargeta(holder);
        botonesDetalle(detallePedido.getEstado(),detallePedido.getAtendido(),holder, Globales.fpago);
        return  row;
    }
    public void botonesDetalle(int entregado, int atendido,ViewHolder holder,String fpago){
        if(atendido!=7 && atendido!=2 && atendido!=1) {
                if (entregado == 0) {
                    if(fpago.equals("Efectivo")){
                        holder.txtCambioestado.setVisibility(View.GONE);
                        holder.btnCancelarD.setBackgroundResource(R.drawable.efecto_btncancelarpd);
                        holder.btnlistoped.setVisibility(View.INVISIBLE);
                    }
                    else{
                        //TextView txtOpciones= (TextView) activity.findViewById(R.id.txtOpciones);
                        //txtOpciones.setVisibility(View.GONE);
                        //holder.txtCambioestado.setText("Tarjeta");
                        holder.txtCambioestado.setBackgroundResource(R.color.rojo);
                        holder.txtCambioestado.setTextColor(holder.txtCambioestado.getContext().getResources().getColor(R.color.blanco));
                        holder.txtCambioestado.setVisibility(View.INVISIBLE);
                        holder.btnlistoped.setVisibility(View.INVISIBLE);
                        holder.btnCancelarD.setVisibility(View.INVISIBLE);
                    }
                }
                if (entregado == 4) {
                    holder.txtCambioestado.setText("Preparando");
                    holder.txtCambioestado.setBackgroundResource(R.color.preparando);
                    holder.txtCambioestado.setTextColor(holder.txtCambioestado.getContext().getResources().getColor(R.color.blanco));
                    holder.txtCambioestado.setVisibility(View.VISIBLE);
                    holder.btnlistoped.setVisibility(View.INVISIBLE);
                    holder.btnCancelarD.setVisibility(View.INVISIBLE);
                }
                if (entregado == 3) {
                    holder.txtCambioestado.setText("Preparado");
                    holder.txtCambioestado.setBackgroundResource(R.color.recoger);
                    holder.txtCambioestado.setTextColor(holder.txtCambioestado.getContext().getResources().getColor(R.color.blanco));
                    holder.txtCambioestado.setVisibility(View.VISIBLE);
                    holder.btnlistoped.setVisibility(View.INVISIBLE);
                    holder.btnCancelarD.setVisibility(View.INVISIBLE);
                }
                if (entregado == 2) {
                    holder.txtCambioestado.setText("Cancelado");
                    holder.txtCambioestado.setBackgroundResource(R.color.rojo);
                    holder.txtCambioestado.setTextColor(holder.txtCambioestado.getContext().getResources().getColor(R.color.blanco));
                    holder.txtCambioestado.setVisibility(View.VISIBLE);
                    holder.btnCancelarD.setVisibility(View.INVISIBLE);
                    holder.btnlistoped.setVisibility(View.INVISIBLE);
                }
            if (entregado == 5) {
                holder.txtCambioestado.setText("Recoger");
                holder.txtCambioestado.setBackgroundResource(R.color.proceso);
                holder.txtCambioestado.setTextColor(holder.txtCambioestado.getContext().getResources().getColor(R.color.blanco));
                holder.txtCambioestado.setVisibility(View.VISIBLE);
                holder.btnCancelarD.setVisibility(View.INVISIBLE);
                holder.btnlistoped.setVisibility(View.INVISIBLE);
            }
            }
        if(atendido==7){
            if (entregado == 2) {
                holder.txtCambioestado.setText("Cancelado");
                holder.txtCambioestado.setBackgroundResource(R.color.rojo);
                holder.txtCambioestado.setTextColor(holder.txtCambioestado.getContext().getResources().getColor(R.color.blanco));
                holder.txtCambioestado.setVisibility(View.VISIBLE);
                holder.btnCancelarD.setVisibility(View.INVISIBLE);
                holder.btnlistoped.setVisibility(View.INVISIBLE);
            }if (entregado == 0) {
                holder.txtCambioestado.setVisibility(View.GONE);
                holder.btnCancelarD.setBackgroundResource(R.color.rojo);
                holder.btnCancelarD.setEnabled(false);
                holder.btnlistoped.setVisibility(View.INVISIBLE);
            }
        }
        if(atendido==2){
                holder.txtCambioestado.setText("Cancelado");
                holder.txtCambioestado.setBackgroundResource(R.color.rojo);
                holder.txtCambioestado.setTextColor(holder.txtCambioestado.getContext().getResources().getColor(R.color.blanco));
                holder.txtCambioestado.setVisibility(View.VISIBLE);
                holder.btnCancelarD.setVisibility(View.INVISIBLE);
                holder.btnlistoped.setVisibility(View.INVISIBLE);
        }
        if(atendido==1){
            holder.txtCambioestado.setText("Finalizado");
            holder.txtCambioestado.setBackgroundResource(R.color.atendido);
            holder.txtCambioestado.setTextColor(holder.txtCambioestado.getContext().getResources().getColor(R.color.blanco));
            holder.txtCambioestado.setVisibility(View.VISIBLE);
            holder.btnCancelarD.setVisibility(View.INVISIBLE);
            holder.btnlistoped.setVisibility(View.INVISIBLE);
        }
    }

    public AlertDialog createCustomDialog(final int position, final String urls, final String mensaje, String tipo, final int numero, final View row) {
        final AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        final int longitud= pedidoList.size();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflar y establecer el layout para el dialogo
        // Pasar nulo como vista principal porque va en el diseño del diálogo
        View v = inflater.inflate(R.layout.dialog_aceptar, null);
        //builder.setView(inflater.inflate(R.layout.dialog_signin, null))
        TextView txtNombreRepartidor = (TextView) v.findViewById(R.id.txtEmpresa);
        TextView txtAlerta= (TextView) v.findViewById(R.id.txtDialog);
        txtAlerta.setText(tipo);
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String nombreempresa = (myPreferences.getString("NOMBRE_EMPRESA", "unknown"));
        txtNombreRepartidor.setText(nombreempresa);
        Button btnSi = (Button) v.findViewById(R.id.btnSi);
        Button btnNo = (Button) v.findViewById(R.id.btnNo);

        btnSi.setTypeface(script);
        btnNo.setTypeface(script);
        txtNombreRepartidor.setTypeface(script);
        txtAlerta.setTypeface(script);

        builder.setView(v);
        alertDialog = builder.create();
        // Add action buttons
        btnSi.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Aceptar
                        if(mensaje.equals("Cancelado")){
                            pedidoList.get(position).setEstado(2);
                            Globales.Pedidositem.add(String.valueOf(numero));
                            //EditText cajaCambio = (EditText) activity.findViewById(R.id.cajaCambio);
                           // cajaCambio.setText("1");
                            Globales.cancelarDetalle = 1;
                            notifyDataSetChanged();
                            alertDialog.dismiss();
                        }

                    }
                }
        );
        btnNo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                }
        );
        return alertDialog;
    }
    /*public void ExceptionTargeta(ViewHolder holder){
        if(Globales.fpago.equals("Efectivo")){
            holder.btnCancelarD.setVisibility(View.VISIBLE);
        }else{
            holder.btnCancelarD.setVisibility(View.GONE);
        }
    }*/

}

