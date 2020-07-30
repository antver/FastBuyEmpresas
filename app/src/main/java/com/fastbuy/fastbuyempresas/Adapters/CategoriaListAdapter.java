package com.fastbuy.fastbuyempresas.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fastbuy.fastbuyempresas.Entidades.Categoria;
import com.fastbuy.fastbuyempresas.R;

import java.util.ArrayList;

public class CategoriaListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Categoria> categoriaList;
    private Typeface script;

    public CategoriaListAdapter(Context context, int layout,ArrayList<Categoria> categoriaList){
        this.context = context;
        this.layout = layout;
        this.categoriaList=categoriaList;
    }
    @Override
    public int getCount() {
        return categoriaList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoriaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class ViewHolder{
        TextView txtcategoria;
    }

    public View getView(final int position, View view, ViewGroup parent){
        View row = view;
        ViewHolder holder = new ViewHolder();
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtcategoria=(TextView) row.findViewById(R.id.txtCategorias);
            row.setTag(holder);
        }else{
            holder = (ViewHolder) row.getTag();
        }
        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(context.getAssets(),fuente2);
        holder.txtcategoria.setTypeface(script);

        Categoria categoria= categoriaList.get(position);
        holder.txtcategoria.setText(categoria.getDescripcion());

        return  row;
    }
}
