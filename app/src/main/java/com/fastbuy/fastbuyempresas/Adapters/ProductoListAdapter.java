package com.fastbuy.fastbuyempresas.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.fastbuy.fastbuyempresas.CategoriasActivity;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.Categoria;
import com.fastbuy.fastbuyempresas.Entidades.Producto;
import com.fastbuy.fastbuyempresas.LoginActivity;
import com.fastbuy.fastbuyempresas.MenuActivity;
import com.fastbuy.fastbuyempresas.ProductosActivity;
import com.fastbuy.fastbuyempresas.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

public class ProductoListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Producto> productoList;
    private Typeface script;

    public ProductoListAdapter(Context context, int layout,ArrayList<Producto> productoList){
        this.context = context;
        this.layout = layout;
        this.productoList=productoList;
    }
    @Override
    public int getCount() {
        return productoList.size();
    }

    @Override
    public Object getItem(int position) {
        return productoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class ViewHolder{
        TextView txtdescripcion,txtprecio;
        ImageView ivproducto;
        Switch swactivar;
    }

    public View getView(final int position, View view, ViewGroup parent){
        View row = view;
        ProductoListAdapter.ViewHolder holder = new ProductoListAdapter.ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtdescripcion=(TextView) row.findViewById(R.id.txtproductos);
            holder.txtprecio=(TextView) row.findViewById(R.id.txtPrecio);
            holder.ivproducto= (ImageView) row.findViewById(R.id.ivproductos);
            holder.swactivar= (Switch) row.findViewById(R.id.swproductos);
            row.setTag(holder);



        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(context.getAssets(),fuente2);
        holder.txtdescripcion.setTypeface(script);

        Producto producto= productoList.get(position);
        holder.txtdescripcion.setText(producto.getDescripcion());
        holder.txtprecio.setText("S/. "+producto.getPrecio());
        String url= Globales.servidorfotosproductos+"/fotos/"+producto.getImagen();
        activarswift(producto.getEstado(),holder);

        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(context);
        final String codigou= mypreferences.getString("UBICACION","unknown");
        final ViewHolder finalHolder1 = holder;
        final ViewHolder finalHolder2 = holder;
        holder.swactivar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                        finalHolder2.swactivar.setChecked(false);
                        String url = Globales.servidor + "/Empresas/sp_empresas_activarPro?auth=" +
                                Globales.token + "&codigop=" + productoList.get(position).getCodigo() +
                                "&codigou=" + codigou+"&pres="+productoList.get(position).getPresentacion();
                        String mensaje = "Activado";
                        String tipo = "Desea Activar este Producto?";
                        createCustomDialog(position, url, mensaje, tipo, finalHolder1).show();
                        //Toast.makeText(context, "Pedido Entrante: " + productoList.get(position).getCodigo(), Toast.LENGTH_SHORT).show();

                }else{
                        finalHolder2.swactivar.setChecked(true);
                        String url = Globales.servidor + "/Empresas/sp_empresas_desactivarpro?auth=" +
                                Globales.token + "&codigop=" + productoList.get(position).getCodigo() +
                                "&codigou=" + codigou+"&pres="+productoList.get(position).getPresentacion().getCodigo();
                        String mensaje = "Desactivado";
                        String tipo = "Desea Desactivar este Producto?";
                        createCustomDialog(position, url, mensaje, tipo, finalHolder1).show();
                        //Toast.makeText(context, "Pedido Entrante: " + productoList.get(position).getCodigo(), Toast.LENGTH_SHORT).show();

                }
            }
        });
        //Log.v("RUTA_IMAGEN",url);
        final ViewHolder finalHolder = holder;
        Glide.with(context)
                .load(url)
                .placeholder ( R.mipmap.ic_launcher)
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .into(holder.ivproducto);
        return  row;
    }
    public AlertDialog createCustomDialog(final int position, final String urls, final String mensaje, final String tipo, final ViewHolder holder) {
        final AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);


        // Get the layout inflater
        final int longitud= productoList.size();
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
                        final String consulta= urls;
                        RequestQueue queue = Volley.newRequestQueue(context);
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
                            @Override

                            public void onResponse(String response) {
                                if (response.length()>0){
                                    try {
                                        JSONObject object = new JSONObject(response);
                                        String respuesta = object.getString("mensaje");
                                        if(respuesta.equals("LISTO")){
                                            Toast toast = Toast.makeText(context, "Producto "+mensaje, Toast.LENGTH_SHORT);
                                            View vistaToast = toast.getView();
                                            vistaToast.setBackgroundResource(R.drawable.toast_exito);
                                            toast.show();
                                            ((Activity)context).finish();
                                            ((Activity)context).overridePendingTransition(0,0);
                                            Intent intent=new Intent(context, CategoriasActivity.class);
                                            context.startActivity(intent);
                                            ((Activity)context).overridePendingTransition(0,0);
                                        }
                                        alertDialog.dismiss();
                                        notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        alertDialog.dismiss();
                                    }
                                }
                            }
                        }, new Response.ErrorListener(){
                            @Override

                            public void onErrorResponse(VolleyError error) {

                                alertDialog.dismiss();
                            }
                        });
                        queue.add(stringRequest);
                    }
                }
        );
        btnNo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if(tipo.equals("Activado")){
                            holder.swactivar.setChecked(false);
                        }else{
                            holder.swactivar.setChecked(true);
                        }*/
                        alertDialog.dismiss();
                    }
                }
        );
        return alertDialog;
    }
    public void activarswift(int estado, ViewHolder holder){
        if(estado==0){
            holder.swactivar.setChecked(true);
        }else{
            holder.swactivar.setChecked(false);
        }
    }
}

