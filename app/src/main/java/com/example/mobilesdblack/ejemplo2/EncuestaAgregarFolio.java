package com.example.mobilesdblack.ejemplo2;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Date;

public class EncuestaAgregarFolio extends AppCompatActivity {

    TextView lblFecha, txtOtro;
    Date date;
    String fecha;
    TextView txtGuia, txtVehiculo, txtTransporte;
    EditText txtFolio;

    String folioString;
    Long folioLong;

    String Folio_OrdenServicio,chofer,obs;
    Boolean OrdenServicioValida;

    int idGuia;

    variables_publicas variables=new variables_publicas();

    private Integer versionBD = variables.version_local_database;
/*
    @Override
    public void onBackPressed() {
        return ;
    }
*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_encuesta_agregar_folio);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_encuesta_agregar_folio);

        Button btn_BuscarFolio = (Button)findViewById(R.id.btn_buscarFolio);
        Button btn_CrearEncuesta = (Button)findViewById(R.id.btnCancelarCambioCupon);

        Inicializar();
        //SetFecha();



        btn_BuscarFolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                folioString = "" + txtFolio.getText();

                folioLong = Long.valueOf(folioString);

                TareaWSObtenerOrdenServicio tareaWSConsulta = new TareaWSObtenerOrdenServicio();
                tareaWSConsulta.execute();

                    InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                //SQLBuscar_Folio(SQL_String_Folio);

            }
        });

        btn_CrearEncuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OrdenServicioValida){
                    //Intent intent_cupon = new Intent(EncuestaAgregarFolio.this,EncuestaCupones.class);


                    alta(Long.parseLong(folioString),Boolean.TRUE, Boolean.FALSE, idGuia );


                    Intent intent_cupon = new Intent(EncuestaAgregarFolio.this,Operacion.class); //tenia WizardMain'
                    intent_cupon.putExtra("idOpVehi",folioString);
                    intent_cupon.putExtra("guia",txtGuia.getText());
                    intent_cupon.putExtra("trans",txtTransporte.getText());
                    intent_cupon.putExtra("chofer",chofer);
                    intent_cupon.putExtra("obs",obs);
                    startActivity(intent_cupon);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Debe seleccionar una orden válida.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void alta(Long idOpVehi, Boolean Habilitado, Boolean enviado, int idGuia ) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("idopveh", idOpVehi);
        cv.put("guia", txtGuia.getText().toString());
        cv.put("camioneta", txtTransporte.getText().toString());
        cv.put("operador", chofer);
        cv.put("obs", obs);


        bd.insert("vehiculo", null, cv);

        bd.close();

    }

    private void Inicializar(){
        txtGuia = (TextView)findViewById(R.id.txtGuia);
        txtTransporte = (TextView)findViewById(R.id.txtTransporte);
        txtVehiculo = (TextView) findViewById(R.id.txtVehiculo);
        txtFolio = (EditText)findViewById(R.id.txtFolio);
        Folio_OrdenServicio = "";
        OrdenServicioValida = Boolean.FALSE;
        txtOtro = (TextView)findViewById(R.id.txtOtro);
    }

    public void abrirAjustes(View v){
        Intent intent_ajustes = new Intent(EncuestaAgregarFolio.this,Actualizar_Preguntas.class);
        startActivity(intent_ajustes);

    }

    private class TareaWSObtenerOrdenServicio extends AsyncTask<String,Integer,Boolean> {

        private Entity_OrdenServicio[] OrdenServicio;
        ProgressDialog progressDialog = new ProgressDialog(EncuestaAgregarFolio.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Buscando información...");
            progressDialog.show();
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            OrdenServicioValida = Boolean.FALSE;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://sql2mobilesd.cloudapp.net/MyWebService/ServicioClientes.asmx";
            final String METHOD_NAME = "ObtenerOrdenServicio";
            final String SOAP_ACTION = "http://suarpe.com/ObtenerOrdenServicio";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idOpVehi", folioLong);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapObject resSoap =(SoapObject)envelope.getResponse();

                OrdenServicio = new Entity_OrdenServicio[resSoap.getPropertyCount()];

                for (int i = 0; i < OrdenServicio.length; i++)
                {
                    SoapObject ic = (SoapObject)resSoap.getProperty(i);

                    Entity_OrdenServicio cli = new Entity_OrdenServicio();
                    cli.guia = ic.getProperty(0).toString();
                    cli.vehiculo = ic.getProperty(1).toString();
                    cli.transportadora = ic.getProperty(2).toString();
                    cli.chofer=ic.getProperty(4).toString();
                    cli.obs=ic.getProperty(5).toString();
                    idGuia = cli.idGuia = Integer.parseInt((ic.getProperty(3).toString()));

                    OrdenServicio[i] = cli;
                }
                OrdenServicioValida = Boolean.TRUE;
            }
            catch (Exception e)
            {

                resul = false;

            }

            return resul;



                /*SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                String res = resultado_xml.toString();
                msje=res;

                if(res.equals("Gracias por Iniciar Sesion")){

                    resul = true;
                }
                else{
                    resul = false;
                }
            */
        }


        protected void onPostExecute(Boolean result) {

            if (result)
            {
                progressDialog.dismiss();
                //Rellenamos la lista con los nombres de los clientes
                final String[] datos = new String[OrdenServicio.length];

                for(int i=0; i<OrdenServicio.length; i++){
                    txtGuia.setText(OrdenServicio[i].guia);
                    txtVehiculo.setText(OrdenServicio[i].vehiculo);
                    txtTransporte.setText(OrdenServicio[i].transportadora);
                    chofer= OrdenServicio[i].chofer;
                    obs= OrdenServicio[i].obs;
                    Folio_OrdenServicio = "" + txtFolio.getText();
                    txtOtro.setText(Folio_OrdenServicio);
                }
                    //datos[i] = OrdenServicio[i].guia;
            /*
                ArrayAdapter<String> adaptador =
                        new ArrayAdapter<String>(MainActivity.this,
                                android.R.layout.simple_list_item_1, datos);

                lstClientes.setAdapter(adaptador);
             */
            }
            else
            {
                Toast.makeText(getBaseContext(),"Something went wrong.. :(",Toast.LENGTH_LONG).show();
            }

        }
    }




}