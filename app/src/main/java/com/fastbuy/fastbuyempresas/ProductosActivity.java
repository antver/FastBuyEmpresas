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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Adapters.CategoriaListAdapter;
import com.fastbuy.fastbuyempresas.Adapters.PedidoListAdapter;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.Categoria;
import com.fastbuy.fastbuyempresas.Entidades.Pedido;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductosActivity extends AppCompatActivity {
  ImageView btnmenu2, btnproductos2,ivimagen2,btnperfil2,btnreportes;
  TextView txtNombre2,txtrazonsocial2;
  ArrayList<Categoria> list;
  GridView gridView2;
  ProgressDialog progDailog=null;
  CategoriaListAdapter adapter=null;
    Typeface script;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        btnmenu2=(ImageView) findViewById(R.id.btnmenu2);
        btnproductos2=(ImageView) findViewById(R.id.btnproductos2);
        txtrazonsocial2= (TextView) findViewById(R.id.txtrazonsocial2);
        txtNombre2= (TextView) findViewById(R.id.txtNombreEmpresa2);
        gridView2= (GridView) findViewById(R.id.gvCategorias);
        ivimagen2= (ImageView) findViewById(R.id.ivempresa2);
        btnperfil2= (ImageView) findViewById(R.id.btnperfil2);
        btnreportes= (ImageButton) findViewById(R.id.btnreportes);
        if(isNetDisponible()){
            //Toast.makeText(ProductosActivity.this,"Conexión a internet", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent=new Intent(ProductosActivity.this, DesconectadoActivity.class);
            startActivity(intent);
        }
        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(getAssets(),fuente2);
        txtNombre2.setTypeface(script);
        txtrazonsocial2.setTypeface(script);

        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(ProductosActivity.this);
        String nombreempresa= mypreferences.getString("NOMBRE_EMPRESA","unknown");
        String razonsocial= mypreferences.getString("RAZON_SOCIAL","unknown");
        String fotoempresa= mypreferences.getString("FOTO_EMPRESA","unknown");
        String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        String codigou= mypreferences.getString("UBICACION","unknown");
        txtNombre2.setText(nombreempresa);
        txtrazonsocial2.setText(razonsocial);
        ivimagen2= (ImageView) findViewById(R.id.ivempresa2);
        String url= Globales.servidorfotos+"/logos/"+fotoempresa;
        //Log.v("RUTA_IMAGEN",url);
        Picasso.with(ProductosActivity.this)
                .load(url)
                .error(R.mipmap.ic_launcher)
                .fit()
                .centerInside()
                .into(ivimagen2, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imagebitmap= ((BitmapDrawable) ivimagen2.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable= RoundedBitmapDrawableFactory.create(getResources(),imagebitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imagebitmap.getWidth(),imagebitmap.getHeight())/ 2.0f);
                        ivimagen2.setImageDrawable(imageDrawable);
                    }
                    @Override
                    public void onError() {
                        ivimagen2.setImageResource(R.mipmap.ic_launcher);
                    }
                });
        btnproductos2.setBackgroundResource(R.color.fastbuy);
        //MyDrawableCompat.setColorFilter(btnmenu.getBackground(), R.color.blanco);
        int color= ContextCompat.getColor(getApplicationContext(), R.color.blanco);
        ImageViewCompat.setImageTintList(btnproductos2, ColorStateList.valueOf(color));
        btnmenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductosActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        btnreportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductosActivity.this, ReporteActivity.class);
                startActivity(intent);
            }
        });
        btnperfil2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductosActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });
        progDailog = new ProgressDialog(ProductosActivity.this);
        progDailog.setMessage("Cargando Categorias...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        cargarPedidos(codigoe,codigou);
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Globales.categoriac = String.valueOf(list.get(position).getCodigo());
                Intent intent = new Intent(ProductosActivity.this, CategoriasActivity.class);
                startActivity(intent);
            }
        });
    }
    public void cargarPedidos(String codigoe,String codigou){

        String consulta = Globales.servidor +"/Empresas/Empresas_categoriasxapp?auth="+Globales.token+ "&codigoe=" + codigoe + "&codigou=" + codigou;
        RequestQueue queue = Volley.newRequestQueue(ProductosActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        list = new ArrayList<>();
                        JSONArray lista = new JSONArray(response);
                        for (int i = 0; i < lista.length(); i++){
                            JSONObject pendiente = lista.getJSONObject(i);
//981293432
                            Categoria categoria = new Categoria();
                            categoria.setCodigo(Integer.parseInt(pendiente.getString("codigoc")));
                            categoria.setDescripcion(pendiente.getString("descripcion"));
                            list.add(categoria);
                        }
                        adapter = new CategoriaListAdapter(ProductosActivity.this, R.layout.item_categorias, list);
                        gridView2.setAdapter(adapter);
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
