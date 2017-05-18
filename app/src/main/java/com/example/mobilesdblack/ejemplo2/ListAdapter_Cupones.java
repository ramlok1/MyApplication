package com.example.mobilesdblack.ejemplo2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * Created by MobileSD Black on 13/09/2016.
 */
public class ListAdapter_Cupones extends ArrayAdapter<Entity_CuponesHoja> {

    private List<Entity_CuponesHoja> lista;

    public ListAdapter_Cupones(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter_Cupones(Context context, int resource, List<Entity_CuponesHoja> items) {
        super(context, resource, items);
        this.lista = items;
    }

    @Override
    public Entity_CuponesHoja getItem(int position) {
        return lista.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.cupones, null);
        }

        Entity_CuponesHoja p = getItem(position);

        if (p != null) {
/*
            String color_background ;
            String color_text ;
            switch (p.getProperty(14).toString()){
                case "10": //GoShow
                    color_background = "#58D3F7";
                    color_text = "#ffffff";
                    break;
                case "11": //No Show
                    color_background = "#FFBF00";
                    color_text = "#ffffff";
                    break;
                case "14"://Abordado
                    color_background = "#1e9267";
                    color_text = "#ffffff";
                    break;
                //15 se crea para discrepancia en la información pero sólo en la nube
                default:
                    color_background = "#ffffff";
                    color_text = "#000000";
                    break;
            }
*/

            TextView textdetov = (TextView)v.findViewById(R.id.lbl_id_detalleov);
            textdetov.setText(p.getProperty(0).toString());
            TextView textView1 = (TextView)v.findViewById(R.id.lblnumCupon);
            //textView1.setBackgroundColor(Color.parseColor(color_background));
            //textView1.setTextColor(Color.parseColor(color_text));
            textView1.setText(p.getProperty(1).toString());

            TextView textView6 = (TextView)v.findViewById(R.id.lblHuesped);
            textView6.setText(p.getProperty(2).toString());

            TextView textView2 = (TextView)v.findViewById(R.id.lblAdultos);
            textView2.setText(p.getProperty(3).toString());

            TextView textView3 = (TextView)v.findViewById(R.id.lblNinos);
            textView3.setText(p.getProperty(4).toString());

            TextView textView4 = (TextView)v.findViewById(R.id.lblI);
            textView4.setText(p.getProperty(5).toString());

            TextView textView5 = (TextView)v.findViewById(R.id.lblIncentivos);
            textView5.setText(p.getProperty(6).toString());

            TextView textView7 = (TextView)v.findViewById(R.id.lbl_Hotel);
            textView7.setText(p.getProperty(7).toString());

            TextView textView8 = (TextView)v.findViewById(R.id.lbl_Habitacion);
            textView8.setText(p.getProperty(8).toString());

            TextView txt_ididioma = (TextView)v.findViewById(R.id.lbl_ididioma);
            txt_ididioma.setText(p.getProperty(16).toString());

            TextView txt_tour_padre = (TextView)v.findViewById(R.id.lbl_tour_padre);
            txt_tour_padre.setText(p.getProperty(15).toString());

            ImageView textView9 = (ImageView)v.findViewById(R.id.lbl_Idioma);
            switch (p.getProperty(9).toString().trim()) {
                case "INGLES":
                    textView9.setImageResource(R.drawable.english);
                    break;
                case "FRANCES":
                    textView9.setImageResource(R.drawable.french);
                    break;
                case "ESPAÑOL":
                    textView9.setImageResource(R.drawable.spanish);
                    break;
                default:
                    textView9.setImageResource(R.drawable.help);
                    break;
            }

            TextView textView10 = (TextView)v.findViewById(R.id.lbl_Pickuplobby);
            textView10.setText(p.getProperty(10).toString());

            TextView textView11 = (TextView)v.findViewById(R.id.lbl_Agencia);
            textView11.setText(p.getProperty(11).toString());

            TextView textView12 = (TextView)v.findViewById(R.id.lbl_Representante);
            textView12.setText(p.getProperty(12).toString());

            //TextView  textView13 = (TextView)v.findViewById(R.id.lbl_Observaciones);
            //textView13.setBackgroundColor(Color.parseColor(color_background));

            //textView13.setTextColor(Color.parseColor(color_text));
            //textView13.setText(p.getProperty(13).toString());

            ImageView textView14 = (ImageView)v.findViewById(R.id.lbl_Observaciones);
            switch (p.getProperty(14).toString().trim()) {
                case "10": //GoShow
                    textView14.setImageResource(R.drawable.cambiarpasajero);
                    break;
                case "11": //No Show
                    textView14.setImageResource(R.drawable.noshow);
                    break;
                case "12": //SinCupon
                    textView14.setImageResource(R.drawable.abordosincupon);
                    break;
                case "14"://Abordado
                    textView14.setImageResource(R.drawable.abordar);
                    break;
                //15 se crea para discrepancia en la información pero sólo en la nube
                default:
                    textView14.setImageResource(R.drawable.regresarstatus);
                    break;
            }
        }

        return v;
    }
}
