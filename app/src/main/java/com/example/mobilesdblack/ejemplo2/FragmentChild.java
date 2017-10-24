package com.example.mobilesdblack.ejemplo2;


import android.content.ContentValues;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
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


    String childname, desc_spin;
    int tipo, posicion;
    TextView textViewChildName,lblpais,lblestado,lblnumber;
    ViewPager viewPager;
    Boolean pager=false;
    View view_parent;
    int c1=0;
    int c2=0;
    int idtipo, id_spin_p;
    SQLiteDatabase bd;
    AdminSQLiteOpenHelper admin;
    Spinner spin;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        childname = bundle.getString("data");
        tipo = bundle.getInt("tipo");
        posicion = bundle.getInt("posicion");
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        View view ;
        view_parent = inflater.inflate(R.layout.fragment_parent, container, false);
        View view_preguntas = inflater.inflate(R.layout.fragment_child, container, false);
        viewPager = (ViewPager) getActivity().findViewById(R.id.my_viewpager);
         admin = new AdminSQLiteOpenHelper(getActivity(), "cuestionarios", null, variables_publicas.version_local_database);
         bd = admin.getWritableDatabase();
        final Button btn_nextc = (Button) view_preguntas.findViewById(R.id.btn_nextc);
        final Button btn_backc = (Button) view_preguntas.findViewById(R.id.btn_backc);


        if (tipo == 0){
            pager=false;
            view = inflater.inflate(R.layout.fragment_inicio, container, false);
            TextView name = (TextView) view.findViewById(R.id.txtNombreE);
            TextView hotel = (TextView) view.findViewById(R.id.txtHotelE);
            TextView bmensaje = (TextView) view.findViewById(R.id.txtmensaje);

             lblpais = (TextView) view.findViewById(R.id.lblpais);
             lblestado = (TextView) view.findViewById(R.id.lblest);
             lblnumber = (TextView) view.findViewById(R.id.lbltel);

            name.setText(variables_publicas.nombre);
            hotel.setText(variables_publicas.hotel);


            final EditText txt_email = (EditText) view.findViewById(R.id.txt_email);
            final EditText txt_pais = (EditText) view.findViewById(R.id.txtpais);
            final EditText txt_estado = (EditText) view.findViewById(R.id.txtestado);
            final EditText txt_tel = (EditText) view.findViewById(R.id.txttelefono);
            final Button btn_next = (Button) view.findViewById(R.id.btn_next);
            final Button btn_back = (Button) view.findViewById(R.id.btn_back);


            // Obtener mensaje

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "cuestionarios", null, variables_publicas.version_local_database);
            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor d = bd.rawQuery("select titulo,mensaje from encuestaMensaje where idioma = "+variables_publicas.idioma+" and titulo='bmensaje'",null);
            if (d != null) {
                if (d.moveToFirst()) {
                    do {


                        bmensaje.setText(d.getString(d.getColumnIndex("mensaje")));

                    } while (d.moveToNext());
                }
            }
            d.close();

            /// Seccion de cambio idioma campos
            if(variables_publicas.idioma==3){
                lblpais.setText("Country:");
                lblestado.setText("State:");
                lblnumber.setText("Phone:");
            }

            if(variables_publicas.idioma==4){
                lblpais.setText("Pays:");
                lblestado.setText("État:");
                lblnumber.setText("Téléphone:");
            }


            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        pager = true;
                }
            });


            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_cupon = new Intent(getActivity(),SeleccionarCupon.class);
                    intent_cupon.putExtra("idOpVehi",Integer.toString(variables_publicas.id_op_vehi));
                    startActivity(intent_cupon);
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
                            variables_publicas.email="na";
                        }
                    }
                }
            });

            txt_pais.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                            variables_publicas.pais=txt_pais.getText().toString();
                    }
                }
            });

            txt_estado.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                            variables_publicas.estado=txt_estado.getText().toString();
                    }
                }
            });

            txt_tel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                            variables_publicas.tel=txt_tel.getText().toString();
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


            view_preguntas.setTag("medio" + posicion);
            LinearLayout layout_principal = (LinearLayout) view_preguntas.findViewById(R.id.lay_v_child);
            layout_principal.removeAllViews();
            LinearLayout contenedor;






            try {

                Cursor c = bd.rawQuery("select idCuestionarios,pregunta,tipo from cuestionarios where idCategoriaPregunta="+tipo+" order by orden_pregunta", null);
                if (c != null) {
                    if (c.moveToFirst()) {
                    c1=0;c2=0;
                        do {

                            contenedor = new LinearLayout(getActivity());
                            contenedor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            contenedor.setOrientation((LinearLayout.HORIZONTAL));


                            TextView txt_pregunta = new TextView(getActivity());
                           final String pregunta_desc = c.getString(c.getColumnIndex("pregunta"));
                            txt_pregunta.setText(pregunta_desc);
                            txt_pregunta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
                            txt_pregunta.setTextSize(30);
                            txt_pregunta.setPadding(30,0,0,0);
                            final int id_pregunta =c.getInt(c.getColumnIndex("idCuestionarios"));

                            idtipo = c.getInt(c.getColumnIndex("tipo"));


                            final EditText txt_resp = new EditText(getActivity());
                            txt_resp.setTextSize(30);
                            txt_resp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
                            txt_resp.setBackgroundColor(Color.parseColor("#f2f2f2"));
                            txt_resp.setPadding(20,0,0,0);
                            txt_resp.setHint("Type Here");





                            RatingBar rbar = new RatingBar(getActivity());
                            rbar.setNumStars(5);
                            rbar.setStepSize(.5f);
                            Drawable progressDrawable = rbar.getProgressDrawable();
                            if (progressDrawable != null) {
                                DrawableCompat.setTint(progressDrawable, ContextCompat.getColor(getContext(), R.color.star));
                            }

                             spin = new Spinner(getActivity(),Spinner.MODE_DIALOG);


                            ArrayAdapter<String> spinnerArrayAdapter=null;
                            if (idtipo==3){

                                id_spin_p=id_pregunta;
                                desc_spin=pregunta_desc;
                                ArrayList<String> spinnerArray = new ArrayList<String>();

                                Cursor d = bd.rawQuery("select respuesta from categoria_respuesta where idCuestionario = "+id_pregunta+" order by orden",null);
                                if (d != null) {
                                    if (d.moveToFirst()) {
                                        do {
                                            spinnerArray.add(d.getString(d.getColumnIndex("respuesta")));
                                        } while (d.moveToNext());
                                    }
                                }
                                d.close();
                                spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item_style, spinnerArray);
                                spin.setAdapter(spinnerArrayAdapter);
                            }




                            Cursor d = bd.rawQuery("select valor_respuesta from encuestaDetalle where idReservaDetalle = "+variables_publicas.idReservaDetalle+" and idCupon='"+variables_publicas.numcupon+"' and idCuestionario="+id_pregunta, null);
                            if (d != null) {
                                if (d.moveToFirst()) {
                                    do {
                                        if(idtipo==1) {
                                            c2++;
                                            rbar.setRating(Integer.parseInt(d.getString(d.getColumnIndex("valor_respuesta"))));
                                        }else if (idtipo==2) {
                                                                                    txt_resp.setText(d.getString(d.getColumnIndex("valor_respuesta")));
                                        }else if (idtipo==3){

                                            String resp = d.getString(d.getColumnIndex("valor_respuesta"));
                                            int spinnerPosition = spinnerArrayAdapter.getPosition(resp);
                                            spin.setSelection(spinnerPosition);

                                        }



                                    } while (d.moveToNext());
                                }
                            }
                                // Trigger pregunta abierta
                            txt_resp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean b) {
                                    if(!b){
                                        String resp= txt_resp.getText().toString();
                                        insert_upd(pregunta_desc,resp,0,id_pregunta);

                                    }
                                }
                            });


                            // Trigger estrellas
                            rbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                                    double rating =ratingBar.getRating()*2;
                                  insert_upd(pregunta_desc,"",(int)rating, id_pregunta);

                                }
                            });

                            contenedor.addView(txt_pregunta);
                            if(idtipo==1) {
                                c1++;
                                contenedor.addView(rbar);
                            }else if (idtipo==2){
                                contenedor.addView(txt_resp);
                            }else if (idtipo==3){
                                contenedor.addView(spin);
                            }
                            layout_principal.addView(contenedor);
                        } while (c.moveToNext());

                    }

                }


            }catch (Exception e){
                String error = e.getMessage().toString();

            }


            btn_nextc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn_nextc.requestFocus();
                    if(c1!=c2) {
                    Toast.makeText(getActivity(),"Please answer all questions",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (idtipo==3){
                            String resp = spin.getSelectedItem().toString();
                            insert_upd(desc_spin,resp,0,id_spin_p);
                        }
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        pager = true;
                    }

                }
            });


            btn_backc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn_backc.requestFocus();

                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                        pager = true;


                }
            });

        }

        btn_nextc.requestFocus();
        if(pager) {
            return view_parent;
        }else{
            return view_preguntas;
        }
    }



    public final static boolean ValidaEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final  void insert_upd (String pregunta, String resp_abierta, int valor_star, int id_pregunta){
   if (Integer.toString(variables_publicas.id_op_vehi).length()<2) {
       Toast.makeText(getActivity(),"Numero de operacion invalido, seleccionar nuevamente el cupon",Toast.LENGTH_SHORT).show();
       Intent intent = new Intent(getContext(), SeleccionarCupon.class);
       intent.putExtra("numCupon", variables_publicas.numcupon);
       startActivity(intent);
   }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();


        Cursor d = bd.rawQuery("select idEncuestaDetalle from encuestaDetalle where idReservaDetalle = "+variables_publicas.idReservaDetalle+" and idCupon='"+variables_publicas.numcupon+"' and idCuestionario="+id_pregunta, null);
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


                ContentValues cv = new ContentValues();
                cv.put("idReservaDetalle", variables_publicas.idReservaDetalle);
                cv.put("idCupon", variables_publicas.numcupon);
                cv.put("idCuestionario", id_pregunta);
                cv.put("pregunta", pregunta);
                if (valor_star==0) {
                    cv.put("valor_respuesta", resp_abierta);

                }else{
                    c2++;
                    cv.put("valor_respuesta", Integer.toString(valor_star));
                }
                cv.put("fechaDetalle", dateFormat.format(date));
                cv.put("enviado", 0);

                bd.insert("encuestaDetalle", null, cv);
            }
        }
    }

}
