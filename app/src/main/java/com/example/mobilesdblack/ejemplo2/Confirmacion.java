package com.example.mobilesdblack.ejemplo2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Confirmacion extends AppCompatActivity {

    variables_publicas variables=new variables_publicas();
    private Integer versionBD = variables.version_local_database;
    TextView txt_enc,txt_detalle;
    String numCupon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        Bundle b = getIntent().getExtras();
        numCupon = b.getString("numCupon");

        txt_detalle = (TextView) findViewById(R.id.lblMensajeFinal);
        txt_enc = (TextView) findViewById(R.id.lblGracias);


        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getApplicationContext(), "cuestionarios", null, variables_publicas.version_local_database);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor d = bd.rawQuery("select titulo,mensaje from encuestaMensaje where idioma = "+variables_publicas.idioma,null);
        if (d != null) {
            if (d.moveToFirst()) {
                do {
                   txt_enc.setText(d.getString(d.getColumnIndex("titulo")));
                    txt_detalle.setText(d.getString(d.getColumnIndex("mensaje")));

                } while (d.moveToNext());
            }
        }
        d.close();
    }

    public void FinalizarEncuesta(View view){

        if (modificar_Contestado(0, numCupon) != 0){
            Intent intent = new Intent(Confirmacion.this, SeleccionarCupon.class);
            intent.putExtra("idOpVehi","");
            startActivity(intent);
            this.finish();
        }

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
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
