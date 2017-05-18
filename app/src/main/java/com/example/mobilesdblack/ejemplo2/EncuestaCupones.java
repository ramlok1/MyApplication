package com.example.mobilesdblack.ejemplo2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EncuestaCupones extends Activity {


    String idOpVehi;
    String SQL_Cupones;
    ListView Lista;
    SimpleAdapter simpleAdapter;
    Connection connection;
    TextView lblOrdenServicio, txtEncuestasRestantes;

    private Integer versionBD = 5;

    public void Inicio() {


        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor c = bd.rawQuery("select idCupones from cupones ", null);



        if (c != null ) {
            if  (! c.moveToFirst()) {

                    SQL_Cupones = ("select rD.nombreHuesped [Huesped],  h.nombreHotel [Hotel] , rD.numCupon [numCupon], rD.idIdioma [idioma]" +
                            "from reservacionDetalle rD " +
                            "inner join hotel h on rD.idHotel = h.idHotel " +
                            "inner join detalleOpVehi dOV on dOV.idReservaDetalle = rD.idReservaDetalle " +
                            "where dOV.idOpVehi = 65942623");
                    ObtenerCuponesOnline(SQL_Cupones);
            }
        }

        c.close();
        bd.close();

        //db.execSQL("create table cupones(idCupones bigint, hotel text, nombre text, email text, idIdioma int, habilitado boolean)");

    }

    public void ObtenerCuponesOnline(String comandosql){
        ResultSet rs;
        try{
            connection = conexion();
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(comandosql);

            while(rs.next()) {

                alta(rs.getLong("numCupon"), rs.getString("Hotel").trim(),rs.getString("Huesped"), rs.getInt("idioma"), Boolean.TRUE);
            }

            Toast.makeText(this, "Se han cargado los cupones asignados a esta orden a su dispositivo", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // Damos de alta los usuarios en nuestra aplicación
    public void alta(long idCupones, String hotel, String nombre, int idIdioma, boolean habilitado ) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        registro.put("idCupones", idCupones);
        registro.put("hotel", hotel);
        registro.put("nombre", nombre);
        registro.put("idIdioma", idIdioma);
        registro.put("habilitado", habilitado);

        // los inserto en la base de datos
        bd.insert("cupones",null, registro);

        bd.close();

    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            String cupon = ((TextView) view.findViewById(R.id.lblnumCupon)).getText().toString();
            String Hotel = ((TextView) view.findViewById(R.id.lbl_Hotel)).getText().toString();
            String Huesped = ((TextView) view.findViewById(R.id.lblHuesped)).getText().toString();
            Toast.makeText(getApplicationContext(),"Cupon: "+ cupon + ", Hotel: " + Hotel + "Huesped: " + Huesped, Toast.LENGTH_LONG).show();
            Intent intent_cupon = new Intent(EncuestaCupones.this,MainActivity.class);
            //intent_cupon.putExtra("idCupones",cupon);
            //intent_cupon.putExtra("hotel",Hotel);
            //intent_cupon.putExtra("nombre",Huesped);
            startActivity(intent_cupon);


        }
    };

    private void Inicializar(){

        //idOpVehi = (getIntent().getExtras().getString("idOpVehi"));
        Lista = (ListView) findViewById(R.id.lstCupones);
/*
        SQL_DatosCupon = ("select rD.nombreHuesped [Huesped],  h.nombreHotel [Hotel] , rD.numCupon [numCupon]" +
                "from reservacionDetalle rD " +
                "inner join hotel h on rD.idHotel = h.idHotel " +
                "inner join detalleOpVehi dOV on dOV.idReservaDetalle = rD.idReservaDetalle " +
                "where dOV.idOpVehi = 65942623" + idOpVehi);
        //" + idOpVehi);
        */
        lblOrdenServicio = (TextView)findViewById(R.id.lblOrdenServicio);
        lblOrdenServicio.setText(idOpVehi);
        txtEncuestasRestantes = (TextView)findViewById(R.id.txtEncuestasRestantes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //connection = conexion();
        setContentView(R.layout.activity_encuesta_cupones);
        Inicializar();
        Inicio();
        CargaCuponesLocales();
        Lista.setOnItemClickListener(onListClick);

    }

    @SuppressLint("NewApi")
    private Connection conexion() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;

        String url = "jdbc:jtds:sqlserver://mobilesdSQLIT.cloudapp.net;instance=SQLEXPRESS;DatabaseName=GoNaturalV2";
        String driver = "net.sourceforge.jtds.jdbc.Driver";
        String userName = "usercs";
        String password = "";

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            //ConnURL = "jdbc:jtds:sqlserver://" + _server + ";databaseName=" + _DB + ";user= " + _user + ";password=" + _pass;
            //conn = DriverManager.getConnection(ConnURL);
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);

        } catch (SQLException sqlException) {
            Toast.makeText(getApplicationContext(), sqlException.toString(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        return conn;
    }

    public void CargaCuponesLocales(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            Cursor c = bd.rawQuery("select idCupones, hotel, nombre  from cupones ", null);
            //Cursor c = bd.rawQuery("select pregunta, tipo from cuestionarios where idIdioma = 2 and tipo = 1 ", null);

            List<Map<String,String>> data = null;
            data = new ArrayList<Map<String, String>>();

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        Map<String,String> datanum = new HashMap<String,String>();
                        datanum.put("A", c.getString(c.getColumnIndex("idCupones")));
                        datanum.put("B", c.getString(c.getColumnIndex("hotel")).trim());
                        datanum.put("C", c.getString(c.getColumnIndex("nombre")));

                        data.add(datanum);

                    }while (c.moveToNext());
                    Toast.makeText(this,"Cupones cargados localmente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "No existen más cupones por encuestar", Toast.LENGTH_SHORT).show();
                }
            }

            c.close();

            bd.close();

            String[] from = {"A","B","C"};
            int[] views = {R.id.lblnumCupon, R.id.lbl_Hotel,R.id.lblHuesped}; //modelo para las lineas del adaptador - Adapter
            simpleAdapter = new SimpleAdapter(this, data, R.layout.cupones, from, views);
            Lista.setAdapter(simpleAdapter);
            Lista.setOnItemClickListener(onListClick);
            txtEncuestasRestantes.setText("Encuestas restantes: " + Lista.getAdapter().getCount());

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }



}
