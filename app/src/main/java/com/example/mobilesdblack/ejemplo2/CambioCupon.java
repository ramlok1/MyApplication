package com.example.mobilesdblack.ejemplo2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.ConnectException;

public class CambioCupon extends AppCompatActivity {

    EditText FolioACambiar;

    TextView lblNumeroCuponNuevo, lblCupon;
    EditText txtNuevoCupon;
    ImageView imageView;

    Button btnVerificar;
    Button btnCambiarCupon;
    Button btnBuscaFolio;

    String cupon_numero; //Cupón que tiene sólo los últimos 4 dígitos
    String numCupon = ""; //Número de cupón completo

    Boolean VieneDeOperacion = false;

    int idReservaDetalle = 0;

    Boolean cupon_cambiado = false;


    private Integer versionBD = variables_publicas.version_local_database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_cupon);

        Bundle b = getIntent().getExtras();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        cupon_numero = b.getString("cupon");
        idReservaDetalle = b.getInt("idReservaDetalle");
        VieneDeOperacion = true;


        FolioACambiar = (EditText)findViewById(R.id.txtNuevoCupon);
        lblCupon = (TextView)findViewById(R.id.txtCupon);

        btnVerificar = (Button)findViewById(R.id.btnVerificar);
        lblNumeroCuponNuevo = (TextView) findViewById(R.id.lblNuevoCupon);
        txtNuevoCupon = (EditText)findViewById(R.id.txtCorroborarNuevoCupon);
        btnCambiarCupon = (Button)findViewById(R.id.btnCambiarCupon);

        btnCambiarCupon.setEnabled(false);

        btnBuscaFolio = (Button)findViewById(R.id.btn_buscarFolio);

        lblCupon.setText(cupon_numero);

        lblNumeroCuponNuevo.setVisibility(View.INVISIBLE);
        txtNuevoCupon.setVisibility(View.INVISIBLE);
        btnVerificar.setVisibility(View.INVISIBLE);

    }

    public void CambiarCupon (final View view){
        numCupon = txtNuevoCupon.getText() + "";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENCIÓN");
        builder.setMessage("¿Está completamente seguro de que el nuevo número de cupón es '" + txtNuevoCupon.getText() + "'?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                TareaWSCambiarCupon tareaWSCambiarCupon = new TareaWSCambiarCupon();
                tareaWSCambiarCupon.execute();

            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    public void AfterCambioCupon(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (cupon_cambiado){
            builder.setTitle("OK");
            builder.setMessage("¡Cupón actualizado exitosamente!");
            modificar_Cupon(idReservaDetalle, numCupon);
        }
        else{
            if (TipoErrorWS != 2){
                builder.setTitle("OK - Offline");
                builder.setMessage("¡Cupón actualizado exitosamente! - Modo Offline");
                modificar_Cupon(idReservaDetalle, numCupon);
                InsertarOffline(idReservaDetalle, 2, 0, "", "", "", "", 0, 0, 0, numCupon, true );

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
        if (VieneDeOperacion) {
            Intent intent_cupon = new Intent(CambioCupon.this, Operacion.class);
            intent_cupon.putExtra("idOpVehi", "");
            startActivity(intent_cupon);
        }
        this.finish();
    }

    public void CancelarCupon (View view){
        //Intent intent_cupon = new Intent(CambioCupon.this,SeleccionarCupon.class); //tenia WizardMain'
        if (VieneDeOperacion) {
            Intent intent_cupon = new Intent(CambioCupon.this, Operacion.class);
            intent_cupon.putExtra("idOpVehi", "");
            startActivity(intent_cupon);
        }
        this.finish();
    }

    public void ValidarCupon (View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        String folio;
        folio = "" + FolioACambiar.getText();

        if (folio.equals("")){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ATENCIÓN");
            builder.setMessage("Ingrese el nuevo cupón.");
            builder.setPositiveButton("Aceptar", null);
            builder.show();

            lblNumeroCuponNuevo.setVisibility(View.INVISIBLE);
            txtNuevoCupon.setVisibility(View.INVISIBLE);
            btnVerificar.setVisibility(View.INVISIBLE);
        }
        else{

            lblNumeroCuponNuevo.setVisibility(View.VISIBLE);
            txtNuevoCupon.setVisibility(View.VISIBLE);
            btnVerificar.setVisibility(View.VISIBLE);

            txtNuevoCupon.setFocusableInTouchMode(true);
            txtNuevoCupon.requestFocus();

            txtNuevoCupon.setText("");
        }

    }

    public void VerificarNumeroCupon(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        String folio, folio2;
        folio = "" + FolioACambiar.getText();
        folio2 = "" + txtNuevoCupon.getText();

        if (folio.equals(folio2)){
            btnCambiarCupon.setEnabled(true);
            btnCambiarCupon.setBackgroundColor(Color.parseColor("#1e9267"));
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ATENCIÓN");
            builder.setMessage("No corresponde el nuevo número de cupón con el que usted ha ingresado, por favor verifique.");
            builder.setPositiveButton("Aceptar", null);
            builder.show();

            btnCambiarCupon.setEnabled(false);
            btnCambiarCupon.setBackgroundColor(Color.parseColor("#D8D8D8"));

            txtNuevoCupon.setFocusableInTouchMode(true);
            txtNuevoCupon.requestFocus();
            txtNuevoCupon.setText("");
        }
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


    public  void IniciarTutorial(View view){
        /*
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);

        sequence.setConfig(config);

        sequence.addSequenceItem(FolioACambiar, "This is button one", "GOT IT");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(btnBuscaFolio)
                        .setDismissText("GOT IT")
                        .setContentText("This is button two")
                        .withRectangleShape(true)
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(btnCambiarCupon)
                        .setDismissText("GOT IT")
                        .setContentText("This is button three")
                        .withRectangleShape()
                        .build()
        );

        sequence.start();
        */
    }

    int TipoErrorWS = 0;

    private class TareaWSCambiarCupon extends AsyncTask<String,Integer,Boolean> {

        String error = "";


        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            TipoErrorWS = 0;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonatural/WS.asmx";
            final String METHOD_NAME = "CambiarCupon";
            final String SOAP_ACTION = "http://suarpe.com/CambiarCupon";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("numCupon", cupon_numero); //INUTIL
            request.addProperty("idOpVehi", variables_publicas.id_op_vehi);
            request.addProperty("idReservaDetalle", idReservaDetalle);
            request.addProperty("nuevoNumCupon", numCupon);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                cupon_cambiado = Boolean.parseBoolean(resultado_xml.toString());


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
                AfterCambioCupon();
            }
            else
            {
                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    public int modificar_Cupon(int _idReservaDetalle, String nuevoCupon) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        // actualizamos con los nuevos datos, la información cambiada
        registro.put("numCupon", nuevoCupon);

        int cant = 0;

        try {

            cant = bd.update("cupones", registro, "idReservaDetalle=" + _idReservaDetalle, null);
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)

                    .show();
        }
        bd.close();

        return cant;

    }

}
