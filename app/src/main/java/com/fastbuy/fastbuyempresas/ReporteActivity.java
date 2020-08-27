package com.fastbuy.fastbuyempresas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.widget.ImageViewCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Adapters.PedidoListAdapter;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.Pedido;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.core.Tag;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

public class ReporteActivity extends AppCompatActivity {
    private static final String TAG = "BackgroundSoundService";
    Spinner spinner,spinner2,spinner3,spinner4,tiporeporte_spinner;
    TextView tvtiporeporte;
    Typeface script;
    float barWidth = 9f;
    String mes1, mes2;
    String year1,year2;
    Button bntreportar;
    EditText etPlannedDate,etPlannedDate2;
    LinearLayout lycombo1, lycombo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        bntreportar= (Button) findViewById(R.id.bntreportar);
        spinner=(Spinner) findViewById(R.id.mes1_spinner);
        spinner2=(Spinner) findViewById(R.id.year_spinner);
        spinner3=(Spinner) findViewById(R.id.mes2_spinner);
        spinner4=(Spinner) findViewById(R.id.year1_spinner);
        tiporeporte_spinner=(Spinner) findViewById(R.id.tiporeporte_spinner);
        tvtiporeporte=(TextView) findViewById(R.id.tvtiporeporte);
        etPlannedDate=(EditText) findViewById(R.id.etPlannedDate);
        etPlannedDate2=(EditText) findViewById(R.id.etPlannedDate2);
        lycombo1= (LinearLayout) findViewById(R.id.lyCombo1);
        lycombo2= (LinearLayout) findViewById(R.id.lycombo2);

        if(isNetDisponible()){
            //Toast.makeText(CategoriasActivity.this,"Conexión a internet", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent=new Intent(ReporteActivity.this, DesconectadoActivity.class);
            startActivity(intent);
        }

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.reporte,
                                            android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter1=ArrayAdapter.createFromResource(this,R.array.year,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter3=ArrayAdapter.createFromResource(this,R.array.year,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2=ArrayAdapter.createFromResource(this,R.array.reporte,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter4=ArrayAdapter.createFromResource(this,R.array.tiporeporte,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter1);
        spinner3.setAdapter(adapter2);
        spinner4.setAdapter(adapter3);
        tiporeporte_spinner.setAdapter(adapter4);
        tiporeporte_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    lycombo1.setVisibility(View.GONE);
                    lycombo2.setVisibility(View.GONE);
                    etPlannedDate.setVisibility(View.VISIBLE);
                    etPlannedDate2.setVisibility(View.VISIBLE);
                    etPlannedDate.setText("");
                    etPlannedDate2.setText("");
                }
                if(position==1){
                    lycombo1.setVisibility(View.GONE);
                    lycombo2.setVisibility(View.GONE);
                    etPlannedDate.setVisibility(View.VISIBLE);
                    etPlannedDate2.setVisibility(View.VISIBLE);
                    etPlannedDate.setText("");
                    etPlannedDate2.setText("");
                }
                if(position==2){
                    lycombo1.setVisibility(View.VISIBLE);
                    lycombo2.setVisibility(View.VISIBLE);
                    etPlannedDate.setVisibility(View.GONE);
                    etPlannedDate2.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        int color= ContextCompat.getColor(getApplicationContext(), R.color.blanco);

        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(ReporteActivity.this);
        String nombreempresa= mypreferences.getString("NOMBRE_EMPRESA","unknown");
        String razonsocial= mypreferences.getString("RAZON_SOCIAL","unknown");
        String fotoempresa= mypreferences.getString("FOTO_EMPRESA","unknown");
        final String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        final String codigou= mypreferences.getString("UBICACION","unknown");

        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(getAssets(),fuente2);
        tvtiporeporte.setTypeface(script);
        etPlannedDate.setTypeface(script);
        etPlannedDate2.setTypeface(script);
        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(0);
            }
        });
        etPlannedDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(1);
            }
        });

        String url= Globales.servidorfotos+"/logos/"+fotoempresa;

        bntreportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tiporeporte_spinner.getSelectedItem().toString().equals("MENSUAL") &&(etPlannedDate.getText().toString().isEmpty() || etPlannedDate2.getText().toString().isEmpty())){
                    Toast.makeText(ReporteActivity.this,"Ingrese Rango de Fechas",Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(ReporteActivity.this,"I "+etPlannedDate.getText(),Toast.LENGTH_SHORT).show();
                Globales.codigoe=codigoe;
                Globales.codigou= codigou;
                if(tiporeporte_spinner.getSelectedItem().toString().equals("MENSUAL")){
                    mes1 = spinner.getSelectedItem().toString();
                    year1=  spinner2.getSelectedItem().toString();
                    mes2=spinner3.getSelectedItem().toString();
                    year2= spinner4.getSelectedItem().toString();
                    Date primerDia= new Date(Integer.parseInt(year1),fecha(mes1),1);
                    Date ultimoDia= new Date(Integer.parseInt(year2),fecha(mes2),0);
                    String a= "00"+ primerDia.getDate();
                    String aa= "00"+ ultimoDia.getDate();
                    String b= "00"+ fecha(mes1);
                    String bb= "00"+ fecha(mes2);
                    Globales.tipoReporte="mes";
                    Globales.fechainicio= year1 + '-'+ b.substring(b.length()-2) + '-'+a.substring(a.length()-2);
                    Globales.fechFinal = year2 + '-'+ bb.substring(bb.length()-2) + '-'+ aa.substring(aa.length()-2);
                }
                if(tiporeporte_spinner.getSelectedItem().toString().equals("DIARIO"))
                {
                    Globales.tipoReporte="dia";
                    Globales.fechainicio=etPlannedDate.getText().toString();
                    Globales.fechFinal=etPlannedDate2.getText().toString();
                }
                if(tiporeporte_spinner.getSelectedItem().toString().equals("SEMANAL"))
                {
                    Globales.tipoReporte="sem";
                    Globales.fechainicio=etPlannedDate.getText().toString();
                    Globales.fechFinal=etPlannedDate2.getText().toString();
                }
                //Toast.makeText(ReporteActivity.this,"D. "+Globales.fechainicio+"/"+Globales.fechFinal,Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(ReporteActivity.this, GraficoActivity.class);
                startActivity(intent);
            }
            }
        });


    }
    public int fecha(String mes){
        int mesentero=0;
        switch(mes) {
            case "ENERO":
                mesentero=1;
                break;
            case "FEBRERO":
                mesentero=2;
                break;
            case "MARZO":
                mesentero=3;
                break;
            case "ABRIL":
                mesentero=4;
                break;
            case "MAYO":
                mesentero=5;
                break;
            case "JUNIO":
                mesentero=6;
                break;
            case "JULIO":
                mesentero=7;
                break;
            case "AGOSTO":
                mesentero=8;
                break;
            case "SETIEMBRE":
                mesentero=9;
                break;
            case "OCTUBRE":
                mesentero=10;
                break;
            case "NOVIEMBRE":
                mesentero=11;
                break;
            case "DICIEMBRE":
                mesentero=12;
                break;
            default:
                mesentero=0;
                break;
        }
        return mesentero;
    }
    private void showDatePickerDialog(final int numero) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = year+ "-" + twoDigits(month+1)+ "-"+twoDigits(day)    ;
                if(numero==0){
                    etPlannedDate.setText(selectedDate);
                }else{
                    etPlannedDate2.setText(selectedDate);
                }

            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }
    private boolean isNetDisponible() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }
}
