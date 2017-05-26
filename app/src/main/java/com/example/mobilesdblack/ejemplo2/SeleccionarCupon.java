package com.example.mobilesdblack.ejemplo2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeleccionarCupon extends AppCompatActivity  {

    ListView Lista;
    SimpleAdapter simpleAdapter;
    TextView txtEncuestasRestantes;
    Button btnSincronizar;
    LinearLayout barraTitulo;


    String idOpVehi;

    variables_publicas variables=new variables_publicas();

    private Integer versionBD = variables.version_local_database;

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            String cupon = ((TextView) view.findViewById(R.id.lblnumCupon)).getText().toString();
            String idetopv = ((TextView) view.findViewById(R.id.lbl_id_detalleov)).getText().toString();
            String nombre = ((TextView) view.findViewById(R.id.lblHuesped)).getText().toString();
            String hotel = ((TextView) view.findViewById(R.id.lbl_Hotel)).getText().toString();
            variables_publicas.idioma = Integer.parseInt(((TextView) view.findViewById(R.id.lbl_ididioma)).getText().toString());
            variables_publicas.tour_padre = Integer.parseInt(((TextView) view.findViewById(R.id.lbl_tour_padre)).getText().toString());
            variables_publicas.idcupon=Integer.parseInt(cupon);
            variables_publicas.id_op_vehi=Integer.parseInt(idetopv);
            variables_publicas.nombre=nombre;
            variables_publicas.hotel=hotel;
            variables_publicas.email="";
            Intent intent_cupon = new Intent(SeleccionarCupon.this,ContestarEncuesta.class);
            intent_cupon.putExtra("numCupon",cupon);
            startActivity(intent_cupon);

        }
    };

    private void Inicializar(){

        txtEncuestasRestantes = (TextView)findViewById(R.id.txtEncuestasRestantes);
        btnSincronizar = (Button)findViewById(R.id.btnSincronizar);

        barraTitulo = (LinearLayout)findViewById(R.id.BarraTitulo);
        Lista = (ListView) findViewById(R.id.lstCupones);
        this.registerForContextMenu(Lista);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_cupon);
        //connection = conexion();

        Bundle b = getIntent().getExtras();
        idOpVehi = b.getString("idOpVehi");

        Inicializar();

        CargaCuponesLocales();
        Lista.setOnItemClickListener(onListClick);

    }

    public void AbrirConfiguraciones(View view){
        Intent intent_cupon = new Intent(SeleccionarCupon.this, Actualizar_Preguntas.class);

        startActivity(intent_cupon);
    }

    List<Entity_CuponesHoja> data;

    public void CargaCuponesLocales(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            Cursor c = bd.rawQuery("select idDetalleOpVehi,numCupon, Huesped, numAdultos, numNinos , numInfantes, Incentivos, Hotel, Habitacion, Idioma, PickUpLobby, nombreAgencia, nombreRepresentante, Observaciones, status, tour_padre,ididioma from cupones where Habilitado = 1 and status in (12,14) ", null);
            //Cursor c = bd.rawQuery("select pregunta, tipo from cuestionarios where idIdioma = 2 and tipo = 1 ", null);

            data = null;
            data = new ArrayList<Entity_CuponesHoja>();

            if (c != null ) {
                if  (c.moveToFirst()) {

                    do {
                        Entity_CuponesHoja datanum = new Entity_CuponesHoja();
                        datanum.idDetalleOpVehi = c.getInt(c.getColumnIndex("idDetalleOpVehi"));
                        datanum.numCupon = c.getString(c.getColumnIndex("numCupon"));
                        datanum.Huesped = c.getString(c.getColumnIndex("Huesped"));
                        datanum.numAdultos = c.getInt(c.getColumnIndex("numAdultos"));
                        datanum.numNinos = c.getInt(c.getColumnIndex("numNinos"));
                        datanum.numInfantes = c.getInt(c.getColumnIndex("numInfantes"));
                        datanum.Incentivos = c.getInt(c.getColumnIndex("Incentivos"));
                        datanum.Hotel = c.getString(c.getColumnIndex("Hotel")).trim();
                        datanum.Habitacion = c.getString(c.getColumnIndex("Habitacion"));
                        datanum.Idioma = c.getString(c.getColumnIndex("Idioma"));
                        datanum.PickUpLobby = c.getString(c.getColumnIndex("PickUpLobby"));
                        datanum.nombreAgencia = c.getString(c.getColumnIndex("nombreAgencia"));
                        datanum.nombreRepresentante = c.getString(c.getColumnIndex("nombreRepresentante"));
                        datanum.Observaciones = c.getString(c.getColumnIndex("Observaciones"));
                        datanum.status = c.getInt(c.getColumnIndex("status"));
                        datanum.tour_padre = c.getInt(c.getColumnIndex("tour_padre"));
                        datanum.idIdioma = c.getInt(c.getColumnIndex("ididioma"));

                        data.add(datanum);

                    }while (c.moveToNext());
                    //Toast.makeText(this,"Cupones cargados localmente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "No existen más cupones por encuestar", Toast.LENGTH_SHORT).show();
                }
            }

            c.close();

            bd.close();

            //String[] from = {"A","B","C","D","E","F","G","H","I","J","K","L","M"};
            //int[] views = {R.id.lblnumCupon,R.id.lblHuesped,R.id.lblAdultos, R.id.lblNinos,R.id.lblI,R.id.lblIncentivos,R.id.lbl_Hotel,R.id.lbl_Habitacion,R.id.lbl_Idioma,R.id.lbl_Pickuplobby,R.id.lbl_Agencia,R.id.lbl_Representante,R.id.lbl_Observaciones}; //modelo para las lineas del adaptador - Adapter
            //simpleAdapter = new SimpleAdapter(this, data, R.layout.cupones, from, views);

            ListAdapter_Cupones_encuesta customAdapter = new ListAdapter_Cupones_encuesta(this, R.layout.cupones_encuesta, data);
            Lista.setAdapter(customAdapter);
            Lista.setOnItemClickListener(onListClick);

            int encuestasRestantes = Lista.getAdapter().getCount();

            if(encuestasRestantes == 0){
                Toast.makeText(getApplicationContext(),"Favor de sincronizar.",Toast.LENGTH_LONG).show();
                Intent intent_cupon = new Intent(SeleccionarCupon.this,Operacion.class);
                intent_cupon.putExtra("idOpVehi",Integer.toString(variables_publicas.id_op_vehi));
                startActivity(intent_cupon);


                this.finish();
            }

            txtEncuestasRestantes.setText("" + encuestasRestantes);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}


