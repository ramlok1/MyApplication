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

public class NoShow extends AppCompatActivity {

    String Cupon = "";
    int idReservaDetalle = 0;

    int TipoErrorWS = 0;

    String StringNoFolio = "", StringAQuienSeEntrega = "", StringMotivo = "";

    Boolean GoShowMarcado = false;


    private Integer versionBD = variables_publicas.version_local_database;

    TextView txtNumCupon;
    EditText NoFolio;
    EditText AQuienSeEntrega;
    EditText Motivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_show);

        txtNumCupon = (TextView) findViewById(R.id.txtCupon);
        NoFolio = (EditText) findViewById(R.id.txtAutorizaSinCupon);
        AQuienSeEntrega = (EditText) findViewById(R.id.txtEntregoNoShow);
        Motivo = (EditText)findViewById(R.id.txtMotivoNoShow);

        Bundle b = getIntent().getExtras();
        Cupon = b.getString("cupon");
        idReservaDetalle = b.getInt("idReservaDetalle");

        txtNumCupon.setText(Cupon);

    }


    public void FinalizarNoShow (View view){

        try {

            StringNoFolio = NoFolio.getText() + "";
            StringAQuienSeEntrega = AQuienSeEntrega.getText() + "";
            StringMotivo = Motivo.getText() + "";

            TareaWSMarcarNoSwhow tareaWSMarcarNoSwhow = new TareaWSMarcarNoSwhow();
            tareaWSMarcarNoSwhow.execute();

        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public void SalirActividad(){
        Intent intent_cupon = new Intent(NoShow.this,Operacion.class);
        intent_cupon.putExtra("idOpVehi","");
        startActivity(intent_cupon);
        this.finish();
    }



    public void CancelarNoShow (View view){
        //Intent intent_cupon = new Intent(NoShow.this,SeleccionarCupon.class);
        Intent intent_cupon = new Intent(NoShow.this,Operacion.class);
        intent_cupon.putExtra("idOpVehi","");
        startActivity(intent_cupon);
        this.finish();
    }

    private class TareaWSMarcarNoSwhow extends AsyncTask<String,Integer,Boolean> {

        String error = "";

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            TipoErrorWS = 0;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/MyWebService/WS.asmx";
            final String METHOD_NAME = "MarcarNoShow";
            final String SOAP_ACTION = "http://suarpe.com/MarcarNoShow";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("status", 11);
            request.addProperty("idOpVehi", variables_publicas.id_op_vehi);
            request.addProperty("idReservaDetalle", idReservaDetalle);
            request.addProperty("NoFolio",StringNoFolio);
            request.addProperty("AQuienSeEntrega",StringAQuienSeEntrega);
            request.addProperty("Motivo",StringMotivo);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                GoShowMarcado = Boolean.parseBoolean(resultado_xml.toString());

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
                AfterNoShow();
            }
            else
            {
                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    public void AfterNoShow(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (GoShowMarcado){
            builder.setTitle("OK");
            builder.setMessage("¡No show marcado!");
            CambiarStatus(11, Cupon);
        }
        else{
            if (TipoErrorWS != 2){
                builder.setTitle("OK - Offline");
                builder.setMessage("¡No show marcado! - Modo Offline");
                CambiarStatus(11, Cupon);
                InsertarOffline(idReservaDetalle, 1, 11, StringNoFolio, "", StringAQuienSeEntrega, StringMotivo, 0, 0, 0, "", true );

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

    public void InsertarOffline(int _idReservaDetalle, int tipoSolicitud, int status, String folioNoShow, String sincuponAutoriza, String recibeNoShow, String observacion, int a, int n, int i, String cupon, Boolean habilitado ) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        registro.put("idOpVehi", variables_publicas.id_op_vehi);
        registro.put("idReservaDetalle", _idReservaDetalle);
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

    private int CambiarStatus(int status, String cupon) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        // actualizamos con los nuevos datos, la información cambiada
        registro.put("status", status);

        int cant = 0;

        try {

            cant = bd.update("cupones", registro, "numCupon=" + cupon, null);
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        bd.close();

        return cant;

    }


}