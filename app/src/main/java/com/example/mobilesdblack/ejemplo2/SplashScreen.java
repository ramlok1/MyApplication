package com.example.mobilesdblack.ejemplo2;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;



public class SplashScreen extends Activity {

    variables_publicas variables=new variables_publicas();

    private Integer versionBD = variables.version_local_database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Intent intent = new Intent(SplashScreen.this, Language.class);
        //startActivity(intent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                consulta();
            }
        },2000);

    }

    public void consulta() {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        try {

            Cursor c = bd.rawQuery("select idDetalleOpVehi from cupones ", null);

            if (c != null ) {
                //Toast.makeText(this, "No es null el cursor", Toast.LENGTH_SHORT).show();
                if  (c.moveToFirst()) {
                    c.close();
                    //Toast.makeText(this, "Tiene primer elemento", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashScreen.this, Operacion.class);
                    intent.putExtra("idOpVehi","");
                    //Intent intent = new Intent(SplashScreen.this, EncuestaAgregarFolio.class);
                    startActivity(intent);
                }
                else{
                    c.close();
                    //Toast.makeText(this, "No existen encuestas cargadas localmente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashScreen.this, EncuestaAgregarFolio.class);
                    startActivity(intent);
                }
            }

            c.close();
        }catch (Exception e){
            Toast.makeText(SplashScreen.this, e.toString(), Toast.LENGTH_LONG).show();
        }finally {
            bd.close();
        }

        //cuestionarios(idCuestionario bigint ,pregunta text, tipo int, idIdioma int)

    }
}
