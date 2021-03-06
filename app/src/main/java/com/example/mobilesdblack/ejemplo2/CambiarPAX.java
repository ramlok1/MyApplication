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
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.ConnectException;

public class CambiarPAX extends AppCompatActivity {

    int NumAdultos = 0, NumNinos = 0, NumInfantes = 0, totalPasajeros = 0, idReservaDetalle=0;
    String cupon = "";
    Boolean PAXcambiado = false;

    TextView ETNumAdultos;
    TextView ETNumNinos;
    TextView ETNumInfantes;
    TextView TVCupon;


    private Integer versionBD = variables_publicas.version_local_database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_pax);

        ETNumAdultos = (TextView)findViewById(R.id.txtNumAdultos);
        ETNumNinos = (TextView)findViewById(R.id.txtNumNinos);
        ETNumInfantes = (TextView)findViewById(R.id.txtNumInfantes);
        TVCupon = (TextView)findViewById(R.id.txtCupon);

        Bundle b = getIntent().getExtras();
        NumAdultos = b.getInt("numAdultos");
        NumNinos = b.getInt("numNinos");
        NumInfantes = b.getInt("numInfantes");
        cupon = b.getString("numCupon");
        idReservaDetalle = b.getInt("idReservaDetalle");

        ETNumAdultos.setText(NumAdultos+"");
        ETNumNinos.setText(NumNinos+"");
        ETNumInfantes.setText(NumInfantes+"");
        TVCupon.setText(cupon);

    }

    public void CancelarNoShow (View view){
        //Intent intent_cupon = new Intent(NoShow.this,SeleccionarCupon.class);
        Intent intent_cupon = new Intent(CambiarPAX.this,Operacion.class);
        intent_cupon.putExtra("idOpVehi","");
        startActivity(intent_cupon);
        this.finish();
    }

    public void CambiarPAX(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENCIÓN");
        builder.setMessage("¿Es la cantidad correcta de pasajeros?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                TareaWSCambiarPAX tareaWSCambiarPAX = new TareaWSCambiarPAX();
                tareaWSCambiarPAX.execute();
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

    private class TareaWSCambiarPAX extends AsyncTask<String,Integer,Boolean> {

        String error = "";

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            TipoErrorWS = 0;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonatural/WS.asmx";
            final String METHOD_NAME = "CambiarPAX";
            final String SOAP_ACTION = "http://suarpe.com/CambiarPAX";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idOpVehi", variables_publicas.id_op_vehi);
            request.addProperty("idReservaDetalle", idReservaDetalle);
            request.addProperty("numAdulto", NumAdultos);
            request.addProperty("numNino", NumNinos);
            request.addProperty("numInfante", NumInfantes);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                PAXcambiado = Boolean.parseBoolean(resultado_xml.toString());


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
                PAXCambiado();
            }
            else
            {
                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    public void PAXCambiado(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (PAXcambiado){
            builder.setTitle("OK");
            builder.setMessage("¡Número de pasajeros cambiados exitosamente!");
            CambiarStatus(idReservaDetalle, NumAdultos, NumNinos, NumInfantes);
        }
        else{
            if (TipoErrorWS != 2){
                builder.setTitle("OK - Offline");
                builder.setMessage("¡Número de pasajeros cambiados exitosamente! - Modo Offline");
                CambiarStatus(idReservaDetalle, NumAdultos, NumNinos, NumInfantes);
                InsertarOffline(idReservaDetalle, 3, 0, "", "", "", "", NumAdultos, NumNinos, NumInfantes, "", true );

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

    private int CambiarStatus(int _idReservaDetalle, int A, int N, int I) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        // actualizamos con los nuevos datos, la información cambiada
        registro.put("numAdultos",A);
        registro.put("numNinos",N);
        registro.put("numInfantes",I);

        int cant = 0;

        try {

            cant = bd.update("cupones", registro, "idReservaDetalle=" + _idReservaDetalle, null);
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        bd.close();

        return cant;

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

    public boolean validar(){
        totalPasajeros = NumAdultos + NumNinos + NumInfantes;
        if (totalPasajeros == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CambiarPAX.this);
            builder.setTitle("ATENCIÓN");
            builder.setMessage("Es imposible abordar a 0 personas. Si el cliente no se presentó, vaya al menú de opciones de la orden de servicio y seleccione No Show.");
            builder.setPositiveButton("Aceptar", null);
            builder.show();
            return true;
        }
        else
            return false;
    }

    public void SalirActividad(){
        //Intent intent_cupon = new Intent(CambioCupon.this,SeleccionarCupon.class); //tenia WizardMain'

        Intent intent_cupon = new Intent(CambiarPAX.this, Operacion.class);
        intent_cupon.putExtra("idOpVehi", "");
        startActivity(intent_cupon);
        this.finish();
    }

    public void plusAdultos(View view){

        NumAdultos ++;
        ETNumAdultos.setText(NumAdultos+"");
    }

    public void minusAdultos(View view){
        if(validar())
            return;
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
        if(validar())
            return;
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
        if(validar())
            return;
        if(NumInfantes==0)
            return;
        NumInfantes --;
        ETNumInfantes.setText(NumInfantes+"");
    }
}
