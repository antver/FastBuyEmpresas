package com.fastbuy.fastbuyempresas.ui.dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Adapters.PedidoListAdapter;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.Pedido;
import com.fastbuy.fastbuyempresas.MenuActivity;
import com.fastbuy.fastbuyempresas.R;
import com.fastbuy.fastbuyempresas.ReporteActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {
    ImageView ivimagen, ivimagen2;
    ProgressDialog progDailog = null;
    TextView txtNombre, txtDecripcion;
    ImageView ivPortada, ivproductos;
    TextView tvCantidadAtendidos, tvTotalVentas, tvCrecimiento, tvProductosRegistrados, tvProductosVenta, txtCantProductoVendido, tvNombreProducto;
    CardView cardproducto;
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        /*final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        String codempresa= mypreferences.getString("CODIGO_EMPRESA","unknown");
        String codubicacion= mypreferences.getString("UBICACION","unknown");

        String nombreempresa= mypreferences.getString("NOMBRE_EMPRESA","unknown");
        String razonsocial= mypreferences.getString("RAZON_SOCIAL","unknown");
        String fotoempresa= mypreferences.getString("FOTO_EMPRESA","unknown");
        txtNombre= (TextView) root.findViewById(R.id.txtNombreEmpresa);
        txtDecripcion= (TextView) root.findViewById(R.id.txtDecripcion);
        cardproducto = (CardView) root.findViewById(R.id.cardproducto);
        txtNombre.setText(nombreempresa);
        txtDecripcion.setText(razonsocial);

        ivimagen= (ImageView) root.findViewById(R.id.ivempresa);
        ivimagen2= (ImageView) root.findViewById(R.id.ivempresa2);
       //tablero
        ivPortada = (ImageView) root.findViewById(R.id.ivPortada);
        tvCantidadAtendidos = (TextView) root.findViewById(R.id.tvCantidadAtendidos);
        tvTotalVentas = (TextView) root.findViewById(R.id.tvTotalVentas);
        tvCrecimiento = (TextView) root.findViewById(R.id.tvCrecimiento);
        tvProductosRegistrados = (TextView) root.findViewById(R.id.tvProductosRegistrados);
        tvProductosVenta = (TextView) root.findViewById(R.id.tvProductosVenta);
        txtCantProductoVendido = (TextView) root.findViewById(R.id.txtCantProductoVendido);
        tvNombreProducto = (TextView) root.findViewById(R.id.tvNombreProducto);
        ivproductos = (ImageView) root.findViewById(R.id.ivproductos);
        Button btnReporteVentas = (Button) root.findViewById(R.id.btnReporteVentas);
        btnReporteVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentreportes = new Intent(getContext(), ReporteActivity.class);
                startActivity(intentreportes);
            }
        });
        progDailog = new ProgressDialog(getContext());
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        String url= Globales.servidorfotos+"/logos/"+fotoempresa;
        CargarImagen(url, ivimagen);
        CargarImagen(url, ivimagen2);
        String consulta = "https://www.apifbempresas.fastbuych.com/Empresas/DashboardEmpresa?empresa="+codempresa+"&ubicacion=" + codubicacion;
        Log.v("CONSULTA", consulta);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        //list = new ArrayList<>();
                        JSONArray lista = new JSONArray(response);
                        String nombreportada = "", imagenproducto = "";
                        for (int i = 0; i < lista.length(); i++){
                            nombreportada = lista.getJSONObject(i).getString("EMP_Portada");
                            double actual = lista.getJSONObject(i).getDouble("TotalVentas");
                            double mesantes = lista.getJSONObject(i).getDouble("MesAnterior");
                            double x = ((actual / mesantes) - 1) * 100;
                            tvCantidadAtendidos.setText(lista.getJSONObject(i).getString("PedidosAtendidos"));
                            tvTotalVentas.setText("S/ " + String.valueOf(actual));
                            tvCrecimiento.setText(String.format("%.2f", x).toString().replace(",",".") + " %");
                            tvProductosRegistrados.setText(lista.getJSONObject(i).getString("TotalProductos"));
                            tvProductosVenta.setText(lista.getJSONObject(i).getString("ProductosVenta"));
                            if(!lista.getJSONObject(i).getString("ProductoMasVendido").equals("null")) {
                                String producto = lista.getJSONObject(i).getString("ProductoMasVendido");
                                txtCantProductoVendido.setText(producto.split("#FF_BB#")[1] + " Unid.");
                                tvNombreProducto.setText(producto.split("#FF_BB#")[0]);
                                imagenproducto = producto.split("#FF_BB#")[2];

                            }else{
                                tvNombreProducto.setText("No tiene productos vendidos en este mes.");
                                txtCantProductoVendido.setVisibility(View.GONE);
                                ivproductos.setVisibility(View.GONE);
                                cardproducto.setVisibility(View.GONE);
                            }
                        }
                        Picasso.with(getContext())
                                .load("https://fastbuych.com/empresas/portadas/" + nombreportada)
                                .error(R.mipmap.ic_launcher)
                                .fit()
                                .centerInside()
                                .into(ivPortada);
                        Picasso.with(getContext())
                                .load( "https://fastbuych.com/productos/fotos/" +imagenproducto)
                                .error(R.mipmap.ic_launcher)
                                .fit()
                                .centerInside()
                                .into(ivproductos);

                        progDailog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
            }
        });

        queue.add(stringRequest);

        return root;
    }

    void CargarImagen(String url, final ImageView imageview){
        Picasso.with(getContext())
                .load(url)
                .error(R.mipmap.ic_launcher)
                .fit()
                .centerInside()
                .into(imageview, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imagebitmap= ((BitmapDrawable) imageview.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable= RoundedBitmapDrawableFactory.create(getResources(),imagebitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imagebitmap.getWidth(),imagebitmap.getHeight())/ 2.0f);
                        imageview.setImageDrawable(imageDrawable);
                    }
                    @Override
                    public void onError() {
                        imageview.setImageResource(R.mipmap.ic_launcher);
                    }
                });
    }
}
