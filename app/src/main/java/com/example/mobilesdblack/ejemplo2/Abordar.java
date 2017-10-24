package com.example.mobilesdblack.ejemplo2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.ConnectException;

public class Abordar extends AppCompatActivity {
    ProgressDialog progressDialog ;

    variables_publicas variables = new variables_publicas();
    private Integer versionBD = variables.version_local_database;
    AlertDialog.Builder builder ;

    int NumAdultos = 0, NumNinos = 0, NumInfantes = 0, totalPasajeros = 0;

    TextView ETNumAdultos;
    TextView ETNumNinos;
    TextView ETNumInfantes;

    EditText ET4numCupon;

    Button btnCambiarPAX;

    int idReservaDetalle = 0;
    int TipoErrorWS = 0;

    Boolean pasajero_abordo = false;

    String cupon_numero4; //Cupón que tiene sólo los últimos 4 dígitos
    String cupon_numero; //Cupón que tiene sólo los últimos 4 dígitos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abordar);
        progressDialog = new ProgressDialog(Abordar.this);
        builder = new AlertDialog.Builder(this);
        ETNumAdultos = (TextView)findViewById(R.id.txtNumAdultos);
        ETNumNinos = (TextView)findViewById(R.id.txtNumNinos);
        ETNumInfantes = (TextView)findViewById(R.id.txtNumInfantes);
        ET4numCupon = (EditText)findViewById(R.id.txt4digitosCupon);

        btnCambiarPAX = (Button)findViewById(R.id.btnCambiarPAX);

        Bundle b = getIntent().getExtras();
        NumAdultos = b.getInt("numAdultos");
        NumNinos = b.getInt("numNinos");
        NumInfantes = b.getInt("numInfantes");
        cupon_numero = b.getString("cupon");
        cupon_numero4 = b.getString("cupon4");
        idReservaDetalle = b.getInt("idReservaDetalle");


        ETNumAdultos.setText(NumAdultos+"");
        ETNumNinos.setText(NumNinos+"");
        ETNumInfantes.setText(NumInfantes+"");

        btnCambiarPAX.setEnabled(false);
        btnCambiarPAX.setBackgroundColor(Color.parseColor("#D8D8D8"));
    }

    public void CancelarNoShow (View view){
        //Intent intent_cupon = new Intent(NoShow.this,SeleccionarCupon.class);
        Intent intent_cupon = new Intent(Abordar.this,Operacion.class);
        intent_cupon.putExtra("idOpVehi","");
        startActivity(intent_cupon);
        this.finish();
    }

    public void Abordar(View view){

        builder.setTitle("ATENCIÓN");
        builder.setMessage("¿Está completamente seguro de que el cupón que va a abordar es '" + cupon_numero + "'?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (!variables_publicas.offline) {
                    final TareaWSAbordar tareaWSAbordar = new TareaWSAbordar();
                    tareaWSAbordar.execute();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (tareaWSAbordar.getStatus() == AsyncTask.Status.RUNNING)
                                tareaWSAbordar.cancel(true);
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Modo Offline, pasajero abordo", Toast.LENGTH_LONG).show();
                            CambiarStatus(14, idReservaDetalle, NumAdultos, NumNinos, NumInfantes);
                            InsertarOffline(idReservaDetalle, 5, 14, "", "", "", "", NumAdultos, NumNinos, NumInfantes, "", true);
                            variables_publicas.offline = true;
                            SalirActividad();

                        }
                    }, 5000);
                    //Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://www..com"));
                    //Bundle b = new Bundle();
                    //intent.putExtras(b);
                    //startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Modo Offline, pasajero abordo", Toast.LENGTH_LONG).show();
                    CambiarStatus(14, idReservaDetalle, NumAdultos, NumNinos, NumInfantes);
                    InsertarOffline(idReservaDetalle, 5, 14, "", "", "", "", NumAdultos, NumNinos, NumInfantes, "", true);

                    SalirActividad();

                }
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private class TareaWSAbordar extends AsyncTask<String,Integer,Boolean> {

        String error = "";

        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando datos, por favor espere...");
            progressDialog.show();
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            TipoErrorWS = 0;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonatural/WS.asmx";
            final String METHOD_NAME = "Abordar";
            final String SOAP_ACTION = "http://suarpe.com/Abordar";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idOpVehi", variables_publicas.id_op_vehi);
            request.addProperty("idReservaDetalle", idReservaDetalle);
            request.addProperty("status", 14);
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
                pasajero_abordo = Boolean.parseBoolean(resultado_xml.toString());


            }catch (java.net.UnknownHostException UHE){
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
            progressDialog.dismiss();

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
            builder.setMessage("¡Pasajero(s) a bordo!");
            CambiarStatus(14, idReservaDetalle, NumAdultos, NumNinos, NumInfantes);
        }
        else{
            if (TipoErrorWS != 2){
                builder.setTitle("OK - Offline");
                builder.setMessage("¡Pasajero(s) a bordo! - Modo Offline");
                        CambiarStatus(14, idReservaDetalle, NumAdultos, NumNinos, NumInfantes);
                InsertarOffline(idReservaDetalle, 5, 14, "", "", "", "", NumAdultos, NumNinos, NumInfantes, "", true );
                variables_publicas.offline=true;
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

    private int CambiarStatus(int status, int _idReservaDetalle, int A, int N, int I) {

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

        registro.put("idReservaDetalle", _idReservaDetalle);
        registro.put("idOpVehi", variables_publicas.id_op_vehi);
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

    public void SalirActividad(){
        //Intent intent_cupon = new Intent(CambioCupon.this,SeleccionarCupon.class); //tenia WizardMain'

        Intent intent_cupon = new Intent(Abordar.this, Operacion.class);
        intent_cupon.putExtra("idOpVehi", "");
        startActivity(intent_cupon);
        this.finish();
    }

    public boolean validar(){
        totalPasajeros = NumAdultos + NumNinos + NumInfantes;
        if (totalPasajeros == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Abordar.this);
            builder.setTitle("ATENCIÓN");
            builder.setMessage("Es imposible abordar a 0 personas. Si el cliente no se presentó, vaya al menú de opciones de la orden de servicio y seleccione No Show.");
            builder.setPositiveButton("Aceptar", null);
            builder.show();
            return true;
        }
        else
            return false;
    }

    public void InstanciarCambioCupon(View view){
        Intent intent_cupon = new Intent(Abordar.this,CambioCupon.class);
        intent_cupon.putExtra("cupon",cupon_numero);
        intent_cupon.putExtra("idReservaDetalle",idReservaDetalle);
        intent_cupon.putExtra("vieneDeOperacion", false);
        startActivity(intent_cupon);
    }

    public void ValidarCupon (View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        String folio;
        folio = "" + ET4numCupon.getText();

        if (folio.equals(cupon_numero4)){

            btnCambiarPAX.setEnabled(true);
            btnCambiarPAX.setBackgroundColor(Color.parseColor("#1e9267"));
            Toast.makeText(getApplicationContext(),"Cupon Validado, puede proceder...",Toast.LENGTH_SHORT).show();
        }
        else{

            btnCambiarPAX.setEnabled(false);
            btnCambiarPAX.setBackgroundColor(Color.parseColor("#D8D8D8"));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ATENCIÓN");
            builder.setMessage("Los últimos 4 dígitos del cupón no coinciden con los que acaba de ingresar.");
            builder.setPositiveButton("Aceptar", null);
            builder.show();
        }

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
