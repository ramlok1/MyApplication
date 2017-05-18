package com.example.mobilesdblack.ejemplo2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Confirmacion extends AppCompatActivity {

    variables_publicas variables=new variables_publicas();
    private Integer versionBD = variables.version_local_database;

    String numCupon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        Bundle b = getIntent().getExtras();
        numCupon = b.getString("numCupon");
    }

    public void FinalizarEncuesta(View view){

        if (modificar_Contestado(0, numCupon) != 0){
            Intent intent = new Intent(Confirmacion.this, SeleccionarCupon.class);
            intent.putExtra("idOpVehi","");
            startActivity(intent);
            this.finish();
        }

    }

    public int modificar_Contestado(int Habilitado, String cupon) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        // actualizamos con los nuevos datos, la información cambiada
        registro.put("Habilitado", Habilitado);

        int cant = 0;

        try {

            cant = bd.update("cupones", registro, "numCupon=" + cupon, null);
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)

                    .show();
        }
        bd.close();

        return cant;

    }
}
