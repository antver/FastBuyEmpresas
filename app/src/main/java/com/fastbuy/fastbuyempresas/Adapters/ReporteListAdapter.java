package com.fastbuy.fastbuyempresas.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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

import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.DetallePedido;
import com.fastbuy.fastbuyempresas.Entidades.Reporte;
import com.fastbuy.fastbuyempresas.R;

import java.util.ArrayList;

public class ReporteListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Reporte> reporteList;
    Typeface script;
    public ReporteListAdapter(Context context, int layout, ArrayList<Reporte> reporteList) {
        this.context = context;
        this.layout = layout;
        this.reporteList = reporteList;
    }

    @Override
    public int getCount() {
        return reporteList.size();
    }

    @Override
    public Object getItem(int position) {
        return reporteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder{
        TextView txtfecha;
        TextView txtProductos,txthoras,txthoraDetalle;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View row = view;
        ReporteListAdapter.ViewHolder holder = new ReporteListAdapter.ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtfecha = (TextView) row.findViewById(R.id.txtfecha);
            holder.txtProductos = (TextView) row.findViewById(R.id.txtProductos);
            holder.txthoras = (TextView) row.findViewById(R.id.txthoras);
            holder.txthoraDetalle = (TextView) row.findViewById(R.id.txthoraDetalle);
            row.setTag(holder);

        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(context.getAssets(),fuente2);
        holder.txtfecha.setTypeface(script);
        holder.txtProductos.setTypeface(script);
        holder.txthoras.setTypeface(script);
        holder.txthoraDetalle.setTypeface(script);

        Reporte reporte = reporteList.get(position);
        holder.txtfecha.setText(reporte.getFecha());
        holder.txtProductos.setText(reporte.getNproducto());
        holder.txthoras.setText(reporte.getNprecio());
        holder.txthoraDetalle.setText(reporte.getHora());

        //ExceptionTargeta(holder);
        return  row;
    }

}
