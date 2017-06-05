package com.example.mobilesdblack.ejemplo2;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChild extends Fragment {


    String childname;
    int tipo, posicion;
    TextView textViewChildName;
    ViewPager viewPager;
    Boolean pager=false;
    View view_parent;
    int c1=0;
    int c2=0;
    SQLiteDatabase bd;
    AdminSQLiteOpenHelper admin;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        childname = bundle.getString("data");
        tipo = bundle.getInt("tipo");
        posicion = bundle.getInt("posicion");

        View view ;
        view_parent = inflater.inflate(R.layout.fragment_parent, container, false);
        View view_preguntas = inflater.inflate(R.layout.fragment_child, container, false);
        viewPager = (ViewPager) getActivity().findViewById(R.id.my_viewpager);
         admin = new AdminSQLiteOpenHelper(getActivity(), "cuestionarios", null, variables_publicas.version_local_database);
         bd = admin.getWritableDatabase();


        if (tipo == 0){
            pager=false;
            view = inflater.inflate(R.layout.fragment_inicio, container, false);
            TextView name = (TextView) view.findViewById(R.id.txtNombreE);
            TextView hotel = (TextView) view.findViewById(R.id.txtHotelE);

            name.setText(variables_publicas.nombre);
            hotel.setText(variables_publicas.hotel);


            final EditText txt_email = (EditText) view.findViewById(R.id.txt_email);
            final Button btn_next = (Button) view.findViewById(R.id.btn_next);

            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ValidaEmail(txt_email.getText())) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        pager = true;
                    }else{
                        Toast.makeText(getActivity(),"Invalid Email...",Toast.LENGTH_SHORT).show();
                    }

                }
            });
            txt_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                        if(ValidaEmail(txt_email.getText())) {
                            variables_publicas.email = txt_email.getText().toString();
                        }else
                        {
                            variables_publicas.email="";
                        }
                    }
                }
            });
            view.setTag("inicio" + posicion);

            if(pager) {
                return view_parent;
            }else{
                return view;
            }
        }
        else if (tipo == -1){
            view = inflater.inflate(R.layout.fragment_finalizar, container, false);
            view.setTag("fin" + posicion);
            return view;
        }
        else {
            pager=false;

            getIDs(view_preguntas);
            view_preguntas.setTag("medio" + posicion);
            LinearLayout layout_principal = (LinearLayout) view_preguntas.findViewById(R.id.lay_v_child);
            layout_principal.removeAllViews();
            LinearLayout contenedor = null;



            try {

                Cursor c = bd.rawQuery("select idCuestionarios,pregunta,tipo from cuestionarios where idCategoriaPregunta="+tipo+" order by orden_pregunta", null);
                if (c != null) {
                    if (c.moveToFirst()) {

                        do {
                            c1++;
                            contenedor = new LinearLayout(getActivity());
                            contenedor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            contenedor.setOrientation((LinearLayout.HORIZONTAL));


                            TextView txt_pregunta = new TextView(getActivity());
                            final String pregunta_desc = c.getString(c.getColumnIndex("pregunta"));
                            txt_pregunta.setText(pregunta_desc);
                            txt_pregunta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
                            txt_pregunta.setTextSize(36);
                            txt_pregunta.setPadding(30,0,0,0);
                            final int id_pregunta =c.getInt(c.getColumnIndex("idCuestionarios"));;

                            final int idtipo = c.getInt(c.getColumnIndex("tipo"));


                            final EditText txt_resp = new EditText(getActivity());
                            txt_resp.setTextSize(30);
                            txt_resp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
                            txt_resp.setBackgroundColor(Color.parseColor("#f2f2f2"));
                            txt_resp.setPadding(20,0,0,0);
                            txt_resp.setHint("Type Here");


                            RatingBar rbar = new RatingBar(getActivity());
                            rbar.setNumStars(5);
                            rbar.setStepSize(1);
                            Drawable progressDrawable = rbar.getProgressDrawable();
                            if (progressDrawable != null) {
                                DrawableCompat.setTint(progressDrawable, ContextCompat.getColor(getContext(), R.color.star));
                            }



                            Cursor d = bd.rawQuery("select valor_respuesta from encuestaDetalle where idDetalleOpVehi = "+variables_publicas.id_op_vehi+" and idCupon="+variables_publicas.idcupon+" and idCuestionario="+id_pregunta, null);
                            if (d != null) {
                                if (d.moveToFirst()) {
                                    do {
                                        if(idtipo==1) {
                                            rbar.setRating(Integer.parseInt(d.getString(d.getColumnIndex("valor_respuesta"))));
                                        }else {
                                        txt_resp.setText(d.getString(d.getColumnIndex("valor_respuesta")));
                                        }
                                        c2++;

                                    } while (d.moveToNext());
                                }
                            }
                                // Triger pregunta abierta
                            txt_resp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean b) {
                                    if(!b){
                                        String resp= txt_resp.getText().toString();
                                        if(resp.length()<2) {
                                            Toast.makeText(getActivity(),"Please be more descriptive",Toast.LENGTH_SHORT).show();
                                        }else
                                        {
                                            insert_upd(pregunta_desc,resp,0,id_pregunta);
                                        }
                                    }
                                }
                            });

                            // Trigger estrellas
                            rbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                                  insert_upd(pregunta_desc,"",Math.round(ratingBar.getRating()), id_pregunta);

                                }
                            });

                            contenedor.addView(txt_pregunta);
                            if(idtipo==1) {
                                contenedor.addView(rbar);
                            }else{
                                contenedor.addView(txt_resp);
                            }
                            layout_principal.addView(contenedor);
                        } while (c.moveToNext());

                    }

                }


            }catch (Exception e){
                String error = e.getMessage().toString();

            }
            final Button btn_nextc = (Button) view_preguntas.findViewById(R.id.btn_nextc);
            btn_nextc.setFocusable(true);
            btn_nextc.setFocusableInTouchMode(true);///add this line
            btn_nextc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn_nextc.requestFocus();
                    if(c1!=c2) {
                    Toast.makeText(getActivity(),"Please answer all questions",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        pager = true;
                    }

                }
            });
        }


        if(pager) {
            return view_parent;
        }else{
            return view_preguntas;
        }
    }

    private void getIDs(View view) {
        textViewChildName = (TextView) view.findViewById(R.id.textViewChild);
        textViewChildName.setText(childname);
    }
    public final static boolean ValidaEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final  void insert_upd (String pregunta, String resp_abierta, int valor_star, int id_pregunta){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();


        Cursor d = bd.rawQuery("select idEncuestaDetalle from encuestaDetalle where idDetalleOpVehi = "+variables_publicas.id_op_vehi+" and idCupon="+variables_publicas.idcupon+" and idCuestionario="+id_pregunta, null);
        if (d != null) {
            if (d.moveToFirst()) {
                do {

                    ContentValues vu = new ContentValues();
                    if (valor_star==0) {
                        vu.put("valor_respuesta", resp_abierta);
                    }else{
                        vu.put("valor_respuesta", Integer.toString(valor_star));
                    }
                    bd.update("encuestaDetalle", vu, "idEncuestaDetalle=" + d.getInt(d.getColumnIndex("idEncuestaDetalle")), null);

                } while (d.moveToNext());
            } else {

                c2++;
                ContentValues cv = new ContentValues();
                cv.put("idDetalleOpVehi", variables_publicas.id_op_vehi);
                cv.put("idCupon", variables_publicas.idcupon);
                cv.put("idCuestionario", id_pregunta);
                cv.put("pregunta", pregunta);
                if (valor_star==0) {
                    cv.put("valor_respuesta", resp_abierta);
                }else{
                    cv.put("valor_respuesta", Integer.toString(valor_star));
                }
                cv.put("fechaDetalle", dateFormat.format(date));
                cv.put("enviado", 0);

                bd.insert("encuestaDetalle", null, cv);
            }
        }
    }

}
