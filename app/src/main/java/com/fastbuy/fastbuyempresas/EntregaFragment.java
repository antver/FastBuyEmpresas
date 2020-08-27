package com.fastbuy.fastbuyempresas;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Adapters.PedidoListAdapter;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.Pedido;
import com.fastbuy.fastbuyempresas.Entidades.RDPedidos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EntregaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match

    ProgressDialog progDailog = null;
    ArrayList<Pedido> list;
    PedidoListAdapter adapter = null;
    GridView gridView2;
    DatabaseReference nDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference cambioPedidos = nDatabaseReference.child("pedidos/");
    public static EntregaFragment instance;
    public static EntregaFragment newInstance() {
        if(instance == null)
            instance = new EntregaFragment();
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_entrega, container, false);

        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        String codigou= mypreferences.getString("UBICACION","unknown");
        gridView2 = (GridView) root.findViewById(R.id.gvPedidos2);
        progDailog = new ProgressDialog(getContext());
        progDailog.setMessage("Cargando pendientes...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        cargarPedidos(codigoe,codigou);

        return root;
    }


    public void cargarPedidos(String codigoe,String codigou){

        String consulta = Globales.servidor +"/Empresas/PedidosxEmpresaUbicacion?auth="+Globales.token+ "&codigoe=" + codigoe + "&codigou=" + codigou;;
        //Log.v("URL", consulta);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        list = new ArrayList<>();
                        JSONArray lista = new JSONArray(response);
                        Log.i("Consultas",lista.toString());
                        int cont = 0;
                        for (int i = 0; i < lista.length(); i++){
                            JSONObject pendiente = lista.getJSONObject(i);
//981293432
                            Pedido pedido = new Pedido();
                            pedido.setCodigo(Integer.parseInt(pendiente.getString("codigop")));
                            pedido.setVendido(Float.parseFloat(pendiente.getString("vendido")));
                            pedido.setTiempopreparacion(pendiente.getString("tiempo"));
                            pedido.setHoraPedido(pendiente.getString("horaped").toString());
                            pedido.setFechaPedido(pendiente.getString("fecha"));
                            pedido.setItem(Integer.parseInt(pendiente.getString("items")));
                            if(Integer.parseInt(pendiente.getString("atendido"))==0){
                                cont++;
                            }
                            pedido.setAtendido(cargarPedido(Integer.parseInt(pendiente.getString("atendido"))));
                            pedido.setFpago(pendiente.getString("fpago"));
                            if(pendiente.getInt("atendido") == 3 || pendiente.getInt("atendido") == 5 || pendiente.getInt("atendido") == 1)
                                list.add(pedido);
                        }
                        //txtPedidos.setText(String.valueOf(cont) + " Pedidos Pendientes");

                        adapter = new PedidoListAdapter(getContext(), R.layout.item_pedidos, list);
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

    public String cargarPedido(int atendido){

        if(atendido==0){
            return " PENDIENTE";
        }if(atendido==1){
            return " FINALIZADO";
        }if(atendido==2){
            return " ANULADO";
        }if(atendido==3){
            return " PREPARANDO";
        }if(atendido==4){
            return " PREPARANDO";
        }if(atendido==5){
            return " RECOGER";
        }if(atendido==7){
            return " ESPERA";
        }
        return "";
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        String codigou= mypreferences.getString("UBICACION","unknown");
        cargarPedidos(codigoe,codigou);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        final String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        final String codigou= mypreferences.getString("UBICACION","unknown");
        cargarPedidos(codigoe,codigou);
        cambioPedidos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RDPedidos data = dataSnapshot.getValue(RDPedidos.class);
                if(codigoe.equals(data.getEmpresa()) && codigou.equals(data.getUbicacion())){
                    cargarPedidos(codigoe,codigou);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
