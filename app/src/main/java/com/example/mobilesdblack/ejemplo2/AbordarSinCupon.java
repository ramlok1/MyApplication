package com.example.mobilesdblack.ejemplo2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.ConnectException;

public class AbordarSinCupon extends AppCompatActivity {

    int NumAdultos = 0, NumNinos = 0, NumInfantes = 0, idDetalleOpVehi = 0;
    String cupon = "", sinCuponAutoriza = "", obs = "";
    Boolean pasajero_abordo = false;

    variables_publicas variables = new variables_publicas();
    private Integer versionBD = variables.version_local_database;

    TextView ETNumAdultos;
    TextView ETNumNinos;
    TextView ETNumInfantes;
    TextView TVCupon;

    EditText AutorizaSinCupon;
    EditText Obs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abordar_sin_cupon);

        ETNumAdultos = (TextView)findViewById(R.id.txtNumAdultos);
        ETNumNinos = (TextView)findViewById(R.id.txtNumNinos);
        ETNumInfantes = (TextView)findViewById(R.id.txtNumInfantes);
        TVCupon = (TextView)findViewById(R.id.txtCupon);

        Bundle b = getIntent().getExtras();
        NumAdultos = b.getInt("numAdultos");
        NumNinos = b.getInt("numNinos");
        NumInfantes = b.getInt("numInfantes");
        cupon = b.getString("numCupon");
        idDetalleOpVehi = b.getInt("idDetalleOpVehi");


        AutorizaSinCupon = (EditText)findViewById(R.id.txtAutorizaSinCupon);
        Obs = (EditText)findViewById(R.id.txtObservaciones);

        ETNumAdultos.setText(NumAdultos+"");
        ETNumNinos.setText(NumNinos+"");
        ETNumInfantes.setText(NumInfantes+"");
        TVCupon.setText(cupon);
    }

    public void AbordarSinCupon(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENCIÓN");
        builder.setMessage("¿Está completamente seguro de que los datos son correctos?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                sinCuponAutoriza = AutorizaSinCupon.getText() + "";
                obs = Obs.getText() + "";

                TareaWSAbordarSinCupon tareaWSAbordarSinCupon = new TareaWSAbordarSinCupon();
                tareaWSAbordarSinCupon.execute();
                //Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://www..com"));
                //Bundle b = new Bundle();
                //intent.putExtras(b);
                //startActivity(intent);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    int TipoErrorWS = 0;

    private class TareaWSAbordarSinCupon extends AsyncTask<String,Integer,Boolean> {

        String error = "";

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            TipoErrorWS = 0;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://sql2mobilesd.cloudapp.net/MyWebService/ServicioClientes.asmx";
            final String METHOD_NAME = "AbordarSinCupon";
            final String SOAP_ACTION = "http://suarpe.com/AbordarSinCupon";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idDetalleOpVehi", idDetalleOpVehi);
            request.addProperty("status", 12);
            request.addProperty("numAdulto", NumAdultos);
            request.addProperty("numNino", NumNinos);
            request.addProperty("numInfante", NumInfantes);
            request.addProperty("sinCuponAutoriza", sinCuponAutoriza);
            request.addProperty("obs", obs);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                pasajero_abordo = Boolean.parseBoolean(resultado_xml.toString());


            }
            catch (java.net.UnknownHostException UHE){
                error = "ERROR RELACIONADO CON LA RED, POR FAVOR VERIFIQUE QUE TENGA CONECCIÓN A INTERNET";
                resul = true;
                TipoErrorWS = 1;
            }
            catch (org.xmlpull.v1.XmlPullParserException XMLException){
                error = "EL WS NO SE ENCUENTRA DISPONIBLE, COMUNIQUESE AL ÁREA DE SISTEMAS";
                resul = true;
                TipoErrorWS = 1;

            }
            catch(ConnectException CE){
                error = "NO SE PUDO CONECTAR AL SERVIDOR, COMUNIQUESE AL ÁREA DE SISTEMAS";
                resul = true;
                TipoErrorWS = 1;
            }
            catch (Exception e)
            {
                error = e.toString();
                resul = false;
                TipoErrorWS = 2;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {
                AfterAbordado();
            }
            else
            {
                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    public void AfterAbordado(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (pasajero_abordo){
            builder.setTitle("OK");
            builder.setMessage("¡Pasajero sin cupón a bordo!");
            CambiarStatus(12, idDetalleOpVehi, NumAdultos, NumNinos, NumInfantes);
        }
        else{
            if (TipoErrorWS != 2){
                builder.setTitle("OK - Offline");
                builder.setMessage("¡Pasajero sin cupón a bordo! - Modo Offline");
                CambiarStatus(12, idDetalleOpVehi, NumAdultos, NumNinos, NumInfantes);
                InsertarOffline(idDetalleOpVehi, 4, 12, "", "", "", "", 0, 0, 0, "", true );

            }
            else{
                builder.setTitle("Atención");
                builder.setMessage("Ocurrió un error");
            }

        }
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SalirActividad();
            }
        });
        builder.show();

    }

    public void SalirActividad(){
        //Intent intent_cupon = new Intent(CambioCupon.this,SeleccionarCupon.class); //tenia WizardMain'

        Intent intent_cupon = new Intent(AbordarSinCupon.this, Operacion.class);
        intent_cupon.putExtra("idOpVehi", "");
        startActivity(intent_cupon);
        this.finish();
    }

    public void Salir(View view){
        //Intent intent_cupon = new Intent(CambioCupon.this,SeleccionarCupon.class); //tenia WizardMain'

        Intent intent_cupon = new Intent(AbordarSinCupon.this, Operacion.class);
        intent_cupon.putExtra("idOpVehi", "");
        startActivity(intent_cupon);
        this.finish();
    }

    private int CambiarStatus(int status, int _idDetalleOpVehi, int A, int N, int I) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        // actualizamos con los nuevos datos, la información cambiada
        registro.put("status", status);
        registro.put("numAdultos",A);
        registro.put("numNinos",N);
        registro.put("numInfantes",I);

        int cant = 0;

        try {

            cant = bd.update("cupones", registro, "idDetalleOpVehi=" + _idDetalleOpVehi, null);
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        bd.close();

        return cant;

    }

    public void InsertarOffline(int idDetalleOpVehi, int tipoSolicitud, int status, String folioNoShow, String sincuponAutoriza, String recibeNoShow, String observacion, int a, int n, int i, String cupon, Boolean habilitado ) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        registro.put("idDetalleOpVehi", idDetalleOpVehi);
        registro.put("tipoSolicitud", tipoSolicitud);
        registro.put("status", status);
        registro.put("folioNoShow", folioNoShow);
        registro.put("sincuponAutoriza", sincuponAutoriza);
        registro.put("recibeNoShow",recibeNoShow);
        registro.put("observacion", observacion);
        registro.put("a", a);
        registro.put("n", n);
        registro.put("i", i);
        registro.put("cupon",cupon);
        registro.put("habilitado", habilitado);

        // los inserto en la base de datos
        try {
            bd.insert("offline", null, registro);
        }
        catch (Exception e){
            Toast.makeText(this, e.toString() + "", Toast.LENGTH_SHORT).show();
        }
        bd.close();

    }

    public void CancelarNoShow (View view){
        //Intent intent_cupon = new Intent(NoShow.this,SeleccionarCupon.class);
        Intent intent_cupon = new Intent(AbordarSinCupon.this,Operacion.class);
        intent_cupon.putExtra("idOpVehi","");
        startActivity(intent_cupon);
        this.finish();
    }

    public void plusAdultos(View view){
        NumAdultos ++;
        ETNumAdultos.setText(NumAdultos+"");
    }

    public void minusAdultos(View view){
        if(NumAdultos==0)
            return;
        NumAdultos --;
        ETNumAdultos.setText(NumAdultos+"");
    }

    public void plusNinos(View view){
        NumNinos ++;
        ETNumNinos.setText(NumNinos+"");
    }

    public void minusNinos(View view){
        if(NumNinos==0)
            return;
        NumNinos --;
        ETNumNinos.setText(NumNinos+"");
    }

    public void plusInfantes(View view){
        NumInfantes ++;
        ETNumInfantes.setText(NumInfantes+"");
    }

    public void minusInfantes(View view){
        if(NumInfantes==0)
            return;
        NumInfantes --;
        ETNumInfantes.setText(NumInfantes+"");
    }
}