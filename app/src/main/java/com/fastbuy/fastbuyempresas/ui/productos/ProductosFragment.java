package com.fastbuy.fastbuyempresas.ui.productos;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.ViewModelProviders;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Adapters.CategoriaListAdapter;
import com.fastbuy.fastbuyempresas.Adapters.ProductoListAdapter;
import com.fastbuy.fastbuyempresas.CategoriasActivity;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.DesconectadoActivity;
import com.fastbuy.fastbuyempresas.Entidades.Categoria;
import com.fastbuy.fastbuyempresas.Entidades.Presentacion;
import com.fastbuy.fastbuyempresas.Entidades.Producto;
import com.fastbuy.fastbuyempresas.MenuActivity;
import com.fastbuy.fastbuyempresas.PerfilActivity;
import com.fastbuy.fastbuyempresas.ProductosActivity;
import com.fastbuy.fastbuyempresas.R;
import com.fastbuy.fastbuyempresas.ReporteActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductosFragment extends Fragment {
    ImageView btnmenu3, btnproductos3,ivimagen3,btnperfil3,btnreportes;
    TextView txtNombre3,txtrazonsocial3;
    GridView gridView3;
    ArrayList<Categoria> list;
    ProgressDialog progDailog=null;
    CategoriaListAdapter adapter=null;
    Typeface script;

    private ProductosViewModel mViewModel;

    public static ProductosFragment newInstance() {
        return new ProductosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.productos_fragment, container, false);

        //btnmenu3=(ImageView) view.findViewById(R.id.btnmenu3);
        //btnproductos3=(ImageView) view.findViewById(R.id.btnproductos3);
        //txtrazonsocial3= (TextView) view.findViewById(R.id.txtrazonsocial3);
        //txtNombre3= (TextView) view.findViewById(R.id.txtNombreEmpresa3);
        gridView3= (GridView) view.findViewById(R.id.gvCategoriasSub);
        //ivimagen3= (ImageView) view.findViewById(R.id.ivempresa3);
        //btnperfil3= (ImageView) view.findViewById(R.id.btnperfil3);
        //btnreportes=(ImageView) view.findViewById(R.id.btnreportes);

        //String fuente2="fonts/GOTHIC.ttf";
        //this.script= Typeface.createFromAsset(getAssets(),fuente2);
        //txtNombre3.setTypeface(script);
        //txtrazonsocial3.setTypeface(script);

        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        //String nombreempresa= mypreferences.getString("NOMBRE_EMPRESA","unknown");
        //String razonsocial= mypreferences.getString("RAZON_SOCIAL","unknown");
        //String fotoempresa= mypreferences.getString("FOTO_EMPRESA","unknown");
        String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        String codigou= mypreferences.getString("UBICACION","unknown");
        //txtNombre3.setText(nombreempresa);
        //txtrazonsocial3.setText(razonsocial);
        //ivimagen3= (ImageView) view.findViewById(R.id.ivempresa3);
        //String url= Globales.servidorfotos+"/logos/"+fotoempresa;
        //Log.v("RUTA_IMAGEN",url);
        /*Picasso.with(getContext())
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
                });*/
        if(isNetDisponible()){
            //Toast.makeText(CategoriasActivity.this,"Conexión a internet", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent=new Intent(getContext(), DesconectadoActivity.class);
            startActivity(intent);
        }
        progDailog = new ProgressDialog(getContext());
        progDailog.setMessage("Cargando Categorias...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        cargarPedidos(codigoe,codigou);
        gridView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Globales.categoriac = String.valueOf(list.get(position).getCodigo());
                Intent intent = new Intent(getContext(), CategoriasActivity.class);
                startActivity(intent);
            }
        });

        //btnproductos3.setBackgroundResource(R.color.fastbuy);
        //MyDrawableCompat.setColorFilter(btnmenu.getBackground(), R.color.blanco);
        ///int color= ContextCompat.getColor(getContext(), R.color.blanco);
        //ImageViewCompat.setImageTintList(btnproductos3, ColorStateList.valueOf(color));
        /*btnmenu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MenuActivity.class);
                startActivity(intent);
            }
        });
        btnreportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ReporteActivity.class);
                startActivity(intent);
            }
        });
        btnperfil3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PerfilActivity.class);
                startActivity(intent);
            }
        });*/



        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProductosViewModel.class);
        // TODO: Use the ViewModel


    }

    public void cargarPedidos(String codigoe,String codigou){
        String consulta = Globales.servidor +"/Empresas/Empresas_categoriasxapp?auth="+Globales.token+ "&codigoe=" + codigoe + "&codigou=" + codigou;
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
                        adapter = new CategoriaListAdapter(getContext(), R.layout.item_categorias, list);
                        gridView3.setAdapter(adapter);
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

    private boolean isNetDisponible() {
        ConnectivityManager connectivityManager = (ConnectivityManager)  getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();
        return (actNetInfo != null && actNetInfo.isConnected());
    }

}
