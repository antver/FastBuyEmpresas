package com.fastbuy.fastbuyempresas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.widget.ImageViewCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Adapters.DetallePedidoListAdapter;
import com.fastbuy.fastbuyempresas.Adapters.ProductoListAdapter;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.Categoria;
import com.fastbuy.fastbuyempresas.Entidades.DetallePedido;
import com.fastbuy.fastbuyempresas.Entidades.Empresa;
import com.fastbuy.fastbuyempresas.Entidades.Producto;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoriasActivity extends AppCompatActivity {
    ImageView btnmenu3, btnproductos3,ivimagen3,btnperfil3,btnreportes;
    TextView txtNombre3,txtrazonsocial3;
    GridView gridView3;
    ArrayList<Producto> list;
    ProgressDialog progDailog=null;
    ProductoListAdapter adapter=null;
    Typeface script;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        btnmenu3=(ImageView) findViewById(R.id.btnmenu3);
        btnproductos3=(ImageView) findViewById(R.id.btnproductos3);
        txtrazonsocial3= (TextView) findViewById(R.id.txtrazonsocial3);
        txtNombre3= (TextView) findViewById(R.id.txtNombreEmpresa3);
        gridView3= (GridView) findViewById(R.id.gvCategoriasSub);
        ivimagen3= (ImageView) findViewById(R.id.ivempresa3);
        btnperfil3= (ImageView) findViewById(R.id.btnperfil3);
        btnreportes=(ImageView) findViewById(R.id.btnreportes);

        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(getAssets(),fuente2);
        txtNombre3.setTypeface(script);
        txtrazonsocial3.setTypeface(script);

        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(CategoriasActivity.this);
        String nombreempresa= mypreferences.getString("NOMBRE_EMPRESA","unknown");
        String razonsocial= mypreferences.getString("RAZON_SOCIAL","unknown");
        String fotoempresa= mypreferences.getString("FOTO_EMPRESA","unknown");
        String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        String codigou= mypreferences.getString("UBICACION","unknown");
        txtNombre3.setText(nombreempresa);
        txtrazonsocial3.setText(razonsocial);
        ivimagen3= (ImageView) findViewById(R.id.ivempresa3);
        String url= Globales.servidorfotos+"/logos/"+fotoempresa;
        Log.v("RUTA_IMAGEN",url);
        Picasso.with(CategoriasActivity.this)
                .load(url)
                .error(R.mipmap.ic_launcher)
                .fit()
                .centerInside()
                .into(ivimagen3, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imagebitmap= ((BitmapDrawable) ivimagen3.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable= RoundedBitmapDrawableFactory.create(getResources(),imagebitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imagebitmap.getWidth(),imagebitmap.getHeight())/ 2.0f);
                        ivimagen3.setImageDrawable(imageDrawable);
                    }
                    @Override
                    public void onError() {
                        ivimagen3.setImageResource(R.mipmap.ic_launcher);
                    }
                });
        if(isNetDisponible()){
            //Toast.makeText(CategoriasActivity.this,"Conexión a internet", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent=new Intent(CategoriasActivity.this, DesconectadoActivity.class);
            startActivity(intent);
        }
        progDailog = new ProgressDialog(CategoriasActivity.this);
        progDailog.setMessage("Cargando Productos...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        btnproductos3.setBackgroundResource(R.color.fastbuy);
        //MyDrawableCompat.setColorFilter(btnmenu.getBackground(), R.color.blanco);
        int color= ContextCompat.getColor(getApplicationContext(), R.color.blanco);
        ImageViewCompat.setImageTintList(btnproductos3, ColorStateList.valueOf(color));
        btnmenu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriasActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        btnreportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriasActivity.this, ReporteActivity.class);
                startActivity(intent);
            }
        });
        btnperfil3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriasActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });
        listaDetalles(codigoe,codigou,Globales.categoriac);

    }
    public void listaDetalles(String codigoe,String codigou, String categoria){
        String consulta = Globales.servidor +"/Empresas/sp_empresas_productoxcat?auth="+Globales.token+ "&codigoe=" + codigoe + "&codigou=" + codigou+"&categoria="+categoria;
        RequestQueue queue= Volley.newRequestQueue(CategoriasActivity.this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.length()>0){
                    try{
                        list= new ArrayList<>();
                        JSONArray lista = new JSONArray(response);

                        for (int i = 0; i < lista.length(); i++){
                            JSONObject pendiente = lista.getJSONObject(i);

                            Producto producto = new Producto();
                            producto.setImagen(pendiente.getString("imagen"));
                            producto.setEstado(pendiente.getInt("estado"));
                            producto.setDescripcion(pendiente.getString("descripcion"));
                            producto.setCodigo(pendiente.getInt("codigop"));
                            producto.setPresentacion(pendiente.getInt("pres"));
                            producto.setPrecio(pendiente.getString("precio"));

                            list.add(producto);
                        }
                        adapter = new ProductoListAdapter(CategoriasActivity.this, R.layout.item_categoriasub, list);
                        gridView3.setAdapter(adapter);

                        //adapter.notifyDataSetChanged();

                        progDailog.dismiss();
                    }catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
            }
        }
        );
        queue.add(stringRequest);
    }
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    private boolean isNetDisponible() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }
}
