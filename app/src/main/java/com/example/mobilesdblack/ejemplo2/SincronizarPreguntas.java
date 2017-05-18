package com.example.mobilesdblack.ejemplo2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SincronizarPreguntas extends AppCompatActivity {

    variables_publicas variables=new variables_publicas();
    private Integer versionBD = variables.version_local_database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizar_preguntas);
    }

    public void RomperOperacion(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(SincronizarPreguntas.this);
        builder.setTitle("¡¡ATENCIÓN!!");
        builder.setMessage("A PUNTO DE TERMINAR");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (BorrarBDLocal()){
                    Intent intent_restart = new Intent(SincronizarPreguntas.this,SplashScreen.class);
                    startActivity(intent_restart);
                    SalirActividad();
                }
                else {
                    Toast.makeText(SincronizarPreguntas.this, "HUBO UN ERROR AL ELIMINAR LA OPERACIÓN DE SU DISPOSITIVO", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();

    }

    public void SalirActividad(){
        this.finish();
    }

    public boolean BorrarBDLocal(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        boolean deleted = true;
        try{
            bd.delete("cuestionarios", null , null);
            bd.delete("encuesta", null , null);
            bd.delete("cupones", null , null);
            bd.delete("encuestaDetalle", null , null);
            bd.delete("offline", null , null);

        }
        catch (Exception e){
            deleted = false;
        }

        bd.close();

        return deleted;
    }
}
