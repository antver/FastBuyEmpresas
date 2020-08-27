package com.fastbuy.fastbuyempresas.Config;

import android.graphics.Color;
import android.os.Handler;
import android.widget.TextView;

import com.fastbuy.fastbuyempresas.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Operaciones {
    int segundosdehora(String tiempo){
        int hor = Integer.parseInt(tiempo.split(":")[0]);
        int min = Integer.parseInt(tiempo.split(":")[1]);
        int seg = Integer.parseInt(tiempo.split(":")[2]);
        int segundosh = 0;
        int segundosm = 0;
        int segundoss = 0;
        if(hor > 0){
            segundosh = hor * 3600;
        }
        if(min > 0){
            segundosm = min * 60;
        }
        if(seg > 0) {
            segundoss = seg;
        }
        return  (segundosh + segundosm + segundoss);
    }
    //Tiempo preparacion = 00:25:00
    String TiempoRestante(String tiempoPreparacion, String fechaPedido, String HoraPedido){
        int tiempo = segundosdehora(tiempoPreparacion);
        if(tiempo > 600){
            tiempo = tiempo - 600;
        }
        Date date = new Date();
        String horapedido = fechaPedido+ " " + HoraPedido;
        DateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String horaactual = dformat.format(date);
        Date d1 = null;
        Date d2 = null;
        Date fechaLimite = null;
        //Date fechaLimite = null;
        try {
            d1 = dformat.parse(horapedido);
            d2 = dformat.parse(horaactual);
            fechaLimite = AgregarSegundos(d1, tiempo);

        } catch (ParseException e) {
            e.printStackTrace();
        }

// Get msec from each, and subtract.
        long diff = fechaLimite.getTime() - d2.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        //System.out.println("Time in seconds: " + diffSeconds + " seconds.");
        //System.out.println("Time in minutes: " + diffMinutes + " minutes.");
        //System.out.println("Time in hours: " + diffHours + " hours.");
        if(diffHours <= 0 && diffMinutes <=0 && diffMinutes <= 0){
            return ("-" +  Math.abs(diffHours) + ":" + Math.abs(diffMinutes) + ":" + Math.abs(diffSeconds));
        }
        else{
            return (diffHours + ":" + Math.abs(diffMinutes) + ":" + Math.abs(diffSeconds));
        }
    }

    private Date AgregarSegundos(Date date, int segundos){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, segundos);
        return calendar.getTime();
    }

    public void ejecutar(final TextView textView, final String preparacion, final String fechapedido, final String horapdido){
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //metodoEjecutar();//llamamos nuestro metodo
                String fecha = TiempoRestante(preparacion, fechapedido,horapdido);
                if(fecha.substring(0,1).equals("-")){
                    textView.setTextColor(Color.rgb(255,0,0));
                }
                textView.setText(fecha);
                handler.postDelayed(this,1000);//se ejecutara cada 1 segundos
            }
        },1000);//empezara a ejecutarse después de 1 milisegundos
    }
}
