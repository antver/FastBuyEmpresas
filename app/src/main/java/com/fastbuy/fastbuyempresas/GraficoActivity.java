package com.fastbuy.fastbuyempresas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Adapters.ProductoListAdapter;
import com.fastbuy.fastbuyempresas.Adapters.ReporteListAdapter;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.Producto;
import com.fastbuy.fastbuyempresas.Entidades.Reporte;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GraficoActivity extends AppCompatActivity {
    RequestQueue rq;
    ExpandableHeightGridView gridView3;
    JsonRequest jsr;
    ProgressDialog progDailog = null;
    BarChart barChart;
    StringRequest stringRequest;
    Typeface script;
    TextView tv1;
    public static final int MY_DEFAULT_TIMEOUT = 15000;
    LinearLayout lyGrafico;
    String url= "";
    ReporteListAdapter adapter=null;
    ArrayList<Reporte> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_grafico);
        lyGrafico=(LinearLayout) findViewById(R.id.lygrafico);
        gridView3= (ExpandableHeightGridView) findViewById(R.id.gvDetallesReporte);

        /*DisplayMetrics medida= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medida);
        int ancho= medida.widthPixels;
        int alto= medida.heightPixels;*/
        //lyGrafico.getWidth(ancho);
        if(isNetDisponible()){
            //Toast.makeText(CategoriasActivity.this,"Conexión a internet", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent=new Intent(GraficoActivity.this, DesconectadoActivity.class);
            startActivity(intent);
        }

        tv1= (TextView) findViewById(R.id.tv1);

        progDailog = new ProgressDialog(GraficoActivity.this);
        progDailog.setMessage("Cargando Reporte...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();

        tv1.setText("REPORTE "+titulo(Globales.tipoReporte)+" S/.");
        if(Globales.tipoReporte.equals("mes")){
            url=Globales.servidor +"/Empresas/Empresa_reportexmes?auth="+Globales.token+ "&codigoe=" +
                    Globales.codigoe + "&codigou=" + Globales.codigou+"&fecha1="+Globales.fechainicio+"&fecha2="+Globales.fechFinal;
        }
        if(Globales.tipoReporte.equals("sem")){
            url=Globales.servidor +"/Empresas/Empresa_reportexSemanal?auth="+Globales.token+ "&codigoe=" +
                    Globales.codigoe + "&codigou=" + Globales.codigou+"&fecha1="+Globales.fechainicio+"&fecha2="+Globales.fechFinal;
        }
        if(Globales.tipoReporte.equals("dia")){
            url=Globales.servidor +"/Empresas/Empresa_reportexdia?auth="+Globales.token+ "&codigoe=" +
                    Globales.codigoe + "&codigou=" + Globales.codigou+"&fecha1="+Globales.fechainicio+"&fecha2="+Globales.fechFinal;
        }
        rq= Volley.newRequestQueue(getApplicationContext());
        cargarReporteDetallado();
        cargarReporte();
    }
    public void cargarReporteDetallado(){
        String urls=Globales.servidor+"/Empresas/Empresa_reporteDetallado?auth="+Globales.token+ "&codigoe=" +
                Globales.codigoe + "&codigou=" + Globales.codigou+"&fecha1="+Globales.fechainicio+"&fecha2="+Globales.fechFinal;
        stringRequest = new StringRequest(Request.Method.GET, urls, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONArray lista = new JSONArray(response);
                        int cont=0;

                        list= new ArrayList<>();
                        //Log.v("datos", String.valueOf(lista.length()));
                        //yVals1.add(new BarEntry(0,12));
                        for (int i = 0; i < lista.length(); i++){
                            Reporte reporte= new Reporte();
                            JSONObject pendiente = lista.getJSONObject(i);
                            cont++;
                            String des="";

                            des=pendiente.getString("dia")+"-"+fecha(pendiente.getInt("mes"))+"-"+pendiente.getString("year");

                            reporte.setNproducto("("+pendiente.getString("cantidad")+") "+pendiente.getString("descripcion"));
                            reporte.setHora(pendiente.getString("hora"));
                            reporte.setFecha(des);
                            if(pendiente.getString("tipo").equals("PEDIDO")){
                                reporte.setNprecio(pendiente.getString("monto"));
                            }else{
                                float y=Float.parseFloat(pendiente.getString("costo"));
                                float x=Float.parseFloat(pendiente.getString("cantidad"));
                                reporte.setNprecio(String.valueOf(y*x));
                            }
                            list.add(reporte);
                            //Toast.makeText(GraficoActivity.this,"D. "+list.size(),Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(GraficoActivity.this,"D. "+yVals1.size(),Toast.LENGTH_SHORT).show();
                        adapter = new ReporteListAdapter(GraficoActivity.this, R.layout.reportedetalle, list);
                        gridView3.setNumColumns(1);
                        gridView3.setAdapter(adapter);
                        gridView3.setExpanded(true);

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
                error.printStackTrace();
                progDailog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.add(stringRequest);
    }
    public void cargarReporte(){
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        barChart=(BarChart) findViewById(R.id.barChart);
                        JSONArray lista = new JSONArray(response);
                        int cont=0;
                        String[] labels=new String[lista.length()];
                        ArrayList<BarEntry> yVals1 = new ArrayList<>();
                        list= new ArrayList<>();
                        //Log.v("datos", String.valueOf(lista.length()));
                        //yVals1.add(new BarEntry(0,12));
                        for (int i = 0; i < lista.length(); i++){

                            JSONObject pendiente = lista.getJSONObject(i);
                            cont++;
                            String des="";
                            if(Globales.tipoReporte.equals("mes")){
                                des=fecha(pendiente.getInt("mes"))+"-"+pendiente.getString("year");
                            }if(Globales.tipoReporte.equals("dia")){
                                des=pendiente.getString("dia")+"-"+fecha(pendiente.getInt("mes"))+"-"+pendiente.getString("year");
                            }
                            if(Globales.tipoReporte.equals("sem")){
                                des="sem. "+cont+"-"+fecha(pendiente.getInt("mes"))+"-"+pendiente.getString("year");
                            }

                            String cantidad;
                            if(pendiente.getString("tipo").equals("PEDIDO") || pendiente.getString("tipo").equals("MEXCLA") ){
                                 cantidad=pendiente.getString("monto");
                            }
                            else{
                                float x= Float.parseFloat(pendiente.getString("costo"));
                                float y= Float.parseFloat(pendiente.getString("cantidad"));
                                cantidad=String.valueOf(x*y);
                            }
                            labels[i]=des;
                            //Toast.makeText(GraficoActivity.this,"D. "+list.size(),Toast.LENGTH_SHORT).show();
                            yVals1.add(new BarEntry(i,Float.parseFloat(cantidad)));

                        }
                        //Toast.makeText(GraficoActivity.this,"D. "+yVals1.size(),Toast.LENGTH_SHORT).show();
                        barChart.setDrawBarShadow(false);
                        barChart.setDrawValueAboveBar(true);
                        barChart.getDescription().setEnabled(false);
                        barChart.setDrawGridBackground(false);

                        XAxis xl = barChart.getXAxis();
                        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xl.setTypeface(script);
                        xl.setDrawAxisLine(false);
                        xl.setDrawGridLines(false);
                        xl.setGranularity(10f);

                        YAxis yl = barChart.getAxisLeft();
                        yl.setTypeface(script);
                        yl.setDrawAxisLine(true);
                        yl.setDrawGridLines(true);
                        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
                        yl.setInverted(false);

                        YAxis yr = barChart.getAxisRight();
                        yr.setTypeface(script);
                        yr.setDrawAxisLine(true);
                        yr.setDrawGridLines(true);
                        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)
                        yr.setInverted(false);

                        String fuente2="fonts/GOTHIC.ttf";
                        script= Typeface.createFromAsset(getAssets(),fuente2);
                        tv1.setTypeface(script);

                        BarDataSet dataset = new BarDataSet(yVals1, "Total");
                        barChart.animateXY(2000, 2000);                        //dataset.setValueTextSize(10f);
                        dataset.setValueTextColor(Color.BLACK);
                        BarData data = new BarData(dataset);
                        data.setValueFormatter(new MyValueFormatter());
                        barChart.setData(data);
                        barChart.setFitBars(false);
                        barChart.invalidate();

                        XAxis xAxis=barChart.getXAxis();
                        xAxis.setValueFormatter(new GraficoActivity.MyAxisValueFormat(labels));
                        xAxis.setGranularity(1f);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
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
                error.printStackTrace();
                progDailog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.add(stringRequest);
    }
    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.00"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return "S/ " +mFormat.format(value); // e.g. append a dollar-sign
        }
    }
    public class MyAxisValueFormat implements IAxisValueFormatter
    {
        private String[] mValues;

        public MyAxisValueFormat(String[] mValues) {
            this.mValues = mValues;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            return mValues[(int) value] ;
        }

    }
    public String fecha(int mes){
        String mesentero="";
        switch(mes) {
            case 1:
                mesentero="ENE.";
                break;
            case 2:
                mesentero="FEB.";
                break;
            case 3:
                mesentero="MAR.";
                break;
            case 4:
                mesentero="ABR.";
                break;
            case 5:
                mesentero="MAY.";
                break;
            case 6:
                mesentero="JUN.";
                break;
            case 7:
                mesentero="JUL.";
                break;
            case 8:
                mesentero="AGO.";
                break;
            case 9:
                mesentero="SET.";
                break;
            case 10:
                mesentero="OCT.";
                break;
            case 11:
                mesentero="NOV.";
                break;
            case 12:
                mesentero="DIC.";
                break;
            default:
                mesentero="";
                break;
        }
        return mesentero;
    }
    public String titulo(String tipo){
        String Titu="";
        switch (tipo) {
            case "mes":
                Titu = "X MES";
                break;
            case "dia":
                Titu = "DIARIO";
                break;
            case "sem":
                Titu = "SEMANAL";
                break;
        }
        return Titu;
    }
    private boolean isNetDisponible() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }
    /*public void setGridViewHeightBasedOnChildren(GridView gridview, int columns) {
        ListAdapter listAdapter = gridview.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return; }
        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;
        View listItem = listAdapter.getView(0, null, gridview);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();
        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }
        ViewGroup.LayoutParams params = gridview.getLayoutParams();
        params.height = totalHeight;
        gridview.setLayoutParams(params);
    }*/

}
