package com.example.mobilesdblack.ejemplo2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
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

public class Actualizar_Preguntas extends Activity {

    private EditText et1, et2, et3, et4;
    Connection connection;
    String SQL_Preguntas;
    variables_publicas variables=new variables_publicas();

    private Integer versionBD = variables.version_local_database;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_actualizar__preguntas);

        // proviene del layout, son los campos de texto
        et1 = (EditText) findViewById(R.id.editText1); et2 = (EditText) findViewById(R.id.editText2);
        et3 = (EditText) findViewById(R.id.editText3); et4 = (EditText) findViewById(R.id.editText4);

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

    public void actualizar_preguntas(View v){
        // To dismiss the dialog
        connection = conexion();
        SQL_Preguntas = ("select C.idCuestionario, C.pregunta, C.idTipo, cP.idIdioma " +
                "from Cuestionario C " +
                "inner join categoriaPregunta cP on cP.idCategoriaPregunta = C.idCategoriaPregunta " +
                "where CP.idTourPadre = 1 and cP.idIdioma = 2 and C.idTipo = 1");
        ObtenerPreguntasOnline(SQL_Preguntas);

    }

    public void ObtenerPreguntasOnline(String comandosql){
        ResultSet rs;
        try{
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(comandosql);

            //alta(1,"hola",1,2);

            while(rs.next()) {

                alta(rs.getLong("idCuestionario"), rs.getString("pregunta"),rs.getInt("idTipo"), rs.getInt("idIdioma"));
            }

            Toast.makeText(this, "Encuesta cargada exitosamente", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // Damos de alta los usuarios en nuestra aplicación
    public void alta(long id, String pregunta, int tipo, int idioma ) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        registro.put("idCuestionarios", id);
        registro.put("pregunta", pregunta);
        registro.put("tipo", tipo);
        registro.put("idIdioma", idioma);

        // los inserto en la base de datos
        bd.insert("cuestionarios",null, registro);

        bd.close();

    }



    // Hacemos búsqueda de usuario por DNI
    public void consulta(View v) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        String dni = et1.getText().toString();


        try {
            Cursor fila = bd.rawQuery(

                    "select pregunta, tipo, idIdioma from cuestionarios where idCuestionarios = 1", null);


            if (fila.moveToFirst()) {

                et2.setText(fila.getString(0));
                et3.setText(fila.getString(1));
                et4.setText(fila.getString(2));

            } else

                Toast.makeText(this, "No existe ningún usuario con ese dni", Toast.LENGTH_SHORT).show();

            }
        catch (Exception e){
            Toast.makeText(this, e.toString(),

                    Toast.LENGTH_SHORT).show();
        }
        finally {
            bd.close();
        }


        //cuestionarios(idCuestionario bigint ,pregunta text, tipo int, idIdioma int)

    }


    /* Método para dar de baja al usuario insertado*/
    public void baja(View v) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        String dni = et1.getText().toString();

        // aquí borro la base de datos del usuario por el dni
        int cant = bd.delete("cuestionarios", null , null);

        bd.close();
/*
        et1.setText(""); et2.setText(""); et3.setText(""); et4.setText("");

        if (cant == 1)

            Toast.makeText(this, "Usuario eliminado",

                    Toast.LENGTH_SHORT).show();

        else

            Toast.makeText(this, "No existe usuario",

                    Toast.LENGTH_SHORT).show();
        */
    }


    // Método para modificar la información del usuario
    public void modificacion(View v) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "administracion", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        String dni = et1.getText().toString();
        String nombre = et2.getText().toString();
        String ciudad = et3.getText().toString();
        String numero = et4.getText().toString();

        ContentValues registro = new ContentValues();

        // actualizamos con los nuevos datos, la información cambiada
        registro.put("nombre", nombre);
        registro.put("ciudad", ciudad);
        registro.put("numero", numero);

        int cant = bd.update("usuario", registro, "dni=" + dni, null);

        bd.close();

        if (cant == 1)

            Toast.makeText(this, "Datos modificados con éxito", Toast.LENGTH_SHORT)

                    .show();

        else

            Toast.makeText(this, "No existe usuario",

                    Toast.LENGTH_SHORT).show();

    }

/* fin del programa */
}
