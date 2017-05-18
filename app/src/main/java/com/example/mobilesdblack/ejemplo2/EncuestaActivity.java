package com.example.mobilesdblack.ejemplo2;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class EncuestaActivity extends AppCompatActivity {

    ListView Lista_Preguntas_Abiertas;
    Connection connection;
    SimpleAdapter simpleAdapter;
    private ArrayList<Valoracion> arrayList;

    Button btn_Finish;
/*
    String SQL_PreguntasAbiertas = ("select C.pregunta,cP.idIdioma, C.idTipo " +
            "from Cuestionario C " +
            "inner join categoriaPregunta cP on cP.idCategoriaPregunta = C.idCategoriaPregunta " +
            "where CP.idIdioma = " + getIntent().getExtras().getInt("idIdioma") +" and idTipo= 0 and CP.idTourPadre = 1");
*/

       private void declarar() {
        btn_Finish = (Button)findViewById(R.id.btn_button);
        Lista_Preguntas_Abiertas = (ListView) findViewById(R.id.lista_abierta);
    }

    private void inicializar() {
        declarar();
        //connection = conexion("usercs","","GoNatural","mobilesdSQLIT.cloudapp.net");
    }

    @SuppressLint("NewApi")
    private Connection conexion(String _user, String _pass, String _DB, String _server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;

        String url = "jdbc:jtds:sqlserver://mobilesdSQLIT.cloudapp.net;instance=SQLEXPRESS;DatabaseName=GoNatural";
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

    public void CargaPreguntasLocales() {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, 1);

        SQLiteDatabase bd = admin.getWritableDatabase();

        arrayList = new ArrayList<>();

        Cursor c = bd.rawQuery("select pregunta, tipo from cuestionarios where idIdioma = 2 and tipo = 0 ", null);

        //
        List<Map<String,String>> data = null;
        data = new ArrayList<Map<String, String>>();


        //

        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    Map<String,String> datanum = new HashMap<String,String>();
                    datanum.put("A", c.getString(c.getColumnIndex("pregunta")));
                    datanum.put("B", c.getString(c.getColumnIndex("pregunta")));

                    data.add(datanum);

                    //arrayList.add(new Valoracion(0, c.getString(c.getColumnIndex("pregunta")), c.getString(c.getColumnIndex("pregunta"))));
                }while (c.moveToNext());

                String[] from = {"A","B"};
                int[] views = {R.id.txt_titulo,R.id.txt_contenido}; //modelo para las lineas del adaptador - Adapter
                simpleAdapter = new SimpleAdapter(this, data, R.layout.preguntas_abiertas, from, views);
                Lista_Preguntas_Abiertas.setAdapter(simpleAdapter);

            }
        }
        else{
            Toast.makeText(this, "No existen encuestas cargadas localmente", Toast.LENGTH_SHORT).show();
        }
        c.close();

        bd.close();

        //cuestionarios(idCuestionario bigint ,pregunta text, tipo int, idIdioma int)
    }

    public void QueryPreguntasAbiertas(String comandosql){
        ResultSet rs;
        try{
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(comandosql);

            List<Map<String,String>> data = null;
            data = new ArrayList<Map<String, String>>();

            while(rs.next()){
                Map<String,String> datanum = new HashMap<String,String>();
                datanum.put("A", rs.getString("pregunta"));
                datanum.put("B", rs.getString("idTipo"));

                data.add(datanum);

            }

            String[] from = {"A","B"};
            int[] views = {R.id.txt_titulo,R.id.txt_contenido}; //modelo para las lineas del adaptador - Adapter
            simpleAdapter = new SimpleAdapter(this, data, R.layout.preguntas_abiertas, from, views);
            Lista_Preguntas_Abiertas.setAdapter(simpleAdapter);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta);

        inicializar();
        CargaPreguntasLocales();
        //QueryPreguntasAbiertas(SQL_PreguntasAbiertas);
    }
}
