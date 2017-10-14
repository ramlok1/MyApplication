package com.example.mobilesdblack.ejemplo2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kyanogen.signatureview.SignatureView;

import net.sourceforge.jtds.jdbc.DateTime;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Operacion extends AppCompatActivity {

    EditText txtSearch;
    List<Entity_CuponesHoja> data;
    ListView Lista;
    TextView txtEncuestasRestantes,lblguia,lblorden,lbltrans,lbloperador,lblobs,lblencuesta,lblcupones;
    View layout_popup;
    ImageView imgEncuesta;
    AlertDialog.Builder builder;
    String numero_cupon = "";

    String idOpVehi,guia,trans,chofer,obs;

    variables_publicas variables = new variables_publicas();
    private Integer versionBD = variables.version_local_database;

    Connection connection;
    String SQL_Preguntas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operacion);

        Bundle b = getIntent().getExtras();
        idOpVehi = b.getString("idOpVehi");

        guia = b.getString("guia");
        trans = b.getString("trans");
        chofer = b.getString("chofer");
        obs = b.getString("obs");

        Inicializar();
        lblguia.setText(lblguia.getText()+" "+guia);
        lblorden.setText(lblorden.getText()+" "+idOpVehi);
        lbltrans.setText(trans);
        if(!idOpVehi.equals("")){
            Inicio();
        }
        else{
            CargaCuponesLocales();
            visualiza_cupones();
        }
        cabecera();
        verifica_sincro();
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Toast.makeText(getBaseContext(),txtSearch.getText(),Toast.LENGTH_LONG).show();
                if (txtSearch.getText().equals("")){
                    CargaCuponesLocales();
                    visualiza_cupones();
                }
                else{
                    CargaCuponesLocalesSearch(txtSearch.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //Toast.makeText(Operacion.this, ":v", Toast.LENGTH_SHORT).show();
                Keyboard.hideKeyboard(Operacion.this);
                return false;
            }
        });

        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);

        // Canceka boton encuesta en caso de ser apoyo
        if (variables_publicas.apoyo)
        {
            imgEncuesta.setEnabled(false);
        }

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

    public void Inicio() {


        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor c = bd.rawQuery("select idDetalleOpVehi from cupones ", null);


        if (c != null ) {
            if  (! c.moveToFirst()) {

                TareaWSObtenerCupones tareaWSObtenerCupones = new TareaWSObtenerCupones();
                tareaWSObtenerCupones.execute();

            }else{
                CargaCuponesLocales();
                visualiza_cupones();
            }
        }

        c.close();
        bd.close();

    }

    // Damos de alta los usuarios en nuestra aplicación
    public void alta_cupones(long idReservaDetalle,long idOpVehi,long idDetalleOpVehi, String numCupon, String Huesped, int numAdultos, int numNinos, int numInfantes, int Incentivos, String Hotel,String Habitacion , String Idioma ,String PickUpLobby ,String nombreAgencia , String nombreRepresentante ,String Observaciones,  boolean habilitado, int status, int tour_padre, int ididioma,String color ) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        registro.put("idReservaDetalle", idReservaDetalle);
        registro.put("idOpVehi", idOpVehi);
        registro.put("idDetalleOpVehi", idDetalleOpVehi);
        registro.put("numCupon", numCupon);
        registro.put("Huesped", Huesped);
        registro.put("numAdultos", numAdultos);
        registro.put("numNinos", numNinos);
        registro.put("numInfantes", numInfantes);
        registro.put("Incentivos", Incentivos);
        registro.put("Hotel", Hotel);
        registro.put("Habitacion", Habitacion);
        registro.put("Idioma", Idioma);
        registro.put("PickUpLobby", PickUpLobby);
        registro.put("nombreAgencia", nombreAgencia);
        registro.put("nombreRepresentante", nombreRepresentante);
        registro.put("Observaciones", Observaciones);
        registro.put("Habilitado", habilitado);
        registro.put("status", status);
        registro.put("tour_padre", tour_padre);
        registro.put("ididioma", ididioma);
        registro.put("color", color);

        // los inserto en la base de datos
        bd.insert("cupones",null, registro);

        bd.close();

    }

    private void Inicializar(){

        txtEncuestasRestantes = (TextView)findViewById(R.id.txtEncuestasRestantes);
        lblguia = (TextView)findViewById(R.id.lblguia);
        lblorden = (TextView)findViewById(R.id.lblorden);
        lbltrans = (TextView)findViewById(R.id.lblcamioneta);
        lbloperador = (TextView)findViewById(R.id.lbloperador);
        lblcupones = (TextView)findViewById(R.id.labelcup);
        lblencuesta = (TextView)findViewById(R.id.labelenc);
        lblobs = (TextView)findViewById(R.id.txt_obs_veh);
        imgEncuesta = (ImageView)findViewById(R.id.imgEncuesta);
        Lista = (ListView) findViewById(R.id.lstCupones);
        this.registerForContextMenu(Lista);
        txtSearch = (EditText)findViewById(R.id.txtsearch);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){

        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        if(v.getId() == R.id.lstCupones){

            String st = data.get(info.position).apoyo.toString();
           // if(st.equals("")) {
                this.getMenuInflater().inflate(R.menu.menu_options_cupones, menu);
           // }
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item){

        int selectedItemId = item.getItemId();

        int idReservaDetalle = 0;

        int NumAdultos = 0, NumNinos = 0, NumInfantes = 0;




        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();


        try{
            switch (selectedItemId){

                case R.id.mniABordo:

                    //Intent intent1 = new Intent(Operacion.this,SeleccionarCupon.class);
                    //intent1.putExtra("idOpVehi","");
                    //startActivity(intent1);
                    numero_cupon = data.get(info.position).numCupon.toString();
                    NumAdultos = data.get(info.position).numAdultos;
                    NumNinos = data.get(info.position).numNinos;
                    NumInfantes = data.get(info.position).numInfantes;
                    idReservaDetalle = data.get(info.position).idReservaDetalle;


                    Intent intent_cupon = new Intent(Operacion.this,Abordar.class);
                    //intent_cupon.putExtra("cupon4",numero_cupon.substring(numero_cupon.length() - 4,numero_cupon.length()));
                    //intent_cupon.putExtra("cupon",numero_cupon);

                    intent_cupon.putExtra("numAdultos",NumAdultos);
                    intent_cupon.putExtra("numNinos",NumNinos);
                    intent_cupon.putExtra("numInfantes",NumInfantes);
                    intent_cupon.putExtra("cupon4",numero_cupon.substring(numero_cupon.length() - 4,numero_cupon.length()));
                    intent_cupon.putExtra("cupon",numero_cupon);
                    intent_cupon.putExtra("idReservaDetalle",idReservaDetalle);



                    startActivity(intent_cupon);
                    this.finish();
                    break;

                case R.id.mniCambiarCupon:

                    numero_cupon = data.get(info.position).numCupon.toString();
                    idReservaDetalle = data.get(info.position).idReservaDetalle;

                    Intent intent = new Intent(Operacion.this,CambioCupon.class);
                    intent.putExtra("cupon",numero_cupon);
                    intent.putExtra("idReservaDetalle",idReservaDetalle);

                    startActivity(intent);
                    this.finish();
                    break;

                case R.id.mniNoShow:

                    numero_cupon = data.get(info.position).numCupon.toString();
                    idReservaDetalle = data.get(info.position).idReservaDetalle;

                    Intent intentv = new Intent(Operacion.this,NoShow.class);

                    intentv.putExtra("cupon",numero_cupon);
                    intentv.putExtra("idReservaDetalle",idReservaDetalle);
                    startActivity(intentv);
                    this.finish();

                    break;


                case R.id.mniCambioPasajeros:
                    //numero_cupon = data.get(info.position).numCupon.toString();

                    //CambiarStatus(1, numero_cupon);
                    //CargaCuponesLocales();

                    numero_cupon = data.get(info.position).numCupon.toString();
                    NumAdultos = data.get(info.position).numAdultos;
                    NumNinos = data.get(info.position).numNinos;
                    NumInfantes = data.get(info.position).numInfantes;
                    idReservaDetalle = data.get(info.position).idReservaDetalle;

                    Intent intentPAX = new Intent(Operacion.this,CambiarPAX.class);
                    //intent_cupon.putExtra("cupon4",numero_cupon.substring(numero_cupon.length() - 4,numero_cupon.length()));
                    //intent_cupon.putExtra("cupon",numero_cupon);

                    intentPAX.putExtra("numAdultos",NumAdultos);
                    intentPAX.putExtra("numNinos",NumNinos);
                    intentPAX.putExtra("numInfantes",NumInfantes);
                    intentPAX.putExtra("numCupon",numero_cupon);
                    intentPAX.putExtra("idReservaDetalle",idReservaDetalle);

                    startActivity(intentPAX);
                    this.finish();

                    break;

                case R.id.mniAbordarSinCupon:
                    //numero_cupon = data.get(info.position).numCupon.toString();

                    //CambiarStatus(1, numero_cupon);
                    //CargaCuponesLocales();
                    numero_cupon = data.get(info.position).numCupon.toString();
                    NumAdultos = data.get(info.position).numAdultos;
                    NumNinos = data.get(info.position).numNinos;
                    NumInfantes = data.get(info.position).numInfantes;
                    idReservaDetalle = data.get(info.position).idReservaDetalle;

                    Intent intent4 = new Intent(Operacion.this,AbordarSinCupon.class);
                    //intent4.putExtra("cupon4",numero_cupon.substring(numero_cupon.length() - 4,numero_cupon.length()));
                    //intent4.putExtra("cupon",numero_cupon);
                    intent4.putExtra("numAdultos",NumAdultos);
                    intent4.putExtra("numNinos",NumNinos);
                    intent4.putExtra("numInfantes",NumInfantes);
                    intent4.putExtra("numCupon",numero_cupon);
                    intent4.putExtra("idReservaDetalle",idReservaDetalle);

                    startActivity(intent4);
                    this.finish();
                    break;

                case R.id.mniRegresarStatus:
                    AlertDialog.Builder builder = new AlertDialog.Builder(Operacion.this);

                    numero_cupon = data.get(info.position).numCupon.toString();

                    builder.setTitle("ATENCIÓN");
                    builder.setMessage("El estatus actual del cupón se cambiará a Pendiente, ¿está de acuerdo?");
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //TareaWSCambiarCupon tareaWSCambiarCupon = new TareaWSCambiarCupon();
                            //tareaWSCambiarCupon.execute();

                            CambiarStatus(1, numero_cupon);
                            CargaCuponesLocales();
                            visualiza_cupones();
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                    break;

              /*  case R.id.mniVerObservaciones:
                    builder = new AlertDialog.Builder(Operacion.this);
                    builder.setTitle("OBSERVACIONES");
                    builder.setMessage(data.get(info.position).Observaciones);
                    builder.setPositiveButton("Aceptar", null);
                    builder.show();
                    break;*/

            }
        }
        catch (Exception e){
            return false;
        }

        return super.onContextItemSelected(item);
    }

    public void AbrirEncuesta(View view){
        Intent intent_cupon = new Intent(Operacion.this,SeleccionarCupon.class);
        intent_cupon.putExtra("idOpVehi","");
        startActivity(intent_cupon);
    }

    public void RomperOperacion(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(Operacion.this);
        builder.setTitle("¡¡ATENCIÓN!!");
        builder.setMessage("Está a punto de borrar toda la información almacenada localmente, esta acción no se puede deshacer, ¿está seguro de continuar?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (BorrarBDLocal()){
                    Intent intent_restart = new Intent(Operacion.this,SplashScreen.class);
                    startActivity(intent_restart);
                    SalirActividad();
                }
                else {
                    Toast.makeText(Operacion.this, "HUBO UN ERROR AL ELIMINAR LA OPERACIÓN DE SU DISPOSITIVO", Toast.LENGTH_SHORT).show();
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
            bd.delete("categoria_respuesta", null , null);
            bd.delete("encuesta", null , null);
            bd.delete("cupones", null , null);
            bd.delete("encuestaDetalle", null , null);
            bd.delete("offline", null , null);
            bd.delete("vehiculo", null , null);

        }
        catch (Exception e){
            deleted = false;
        }

        bd.close();

        return deleted;
    }

    List<Entity_offline> Lista_Offline;

    int WSidReservaDetalle,WScuestionario,WScalifica;
    String WSfolioNoShow,WSpregunta,WSrespuesta,WSfecha,WScomentario,Wsemail,WSentrada,WSsalida,WSpais,WSestado,WStel;
    String WSrecibeNoShow;
    String WSsincuponAutoriza;
    String WSobservacion;
    byte[] WSfirma;
    int WSa;
    int WSn;
    int WSi;
    String WScupon;
    int WOfflineID;



    public void preMaster(View view){


        builder = new AlertDialog.Builder(Operacion.this);
        builder.setTitle("Sincronización");
        builder.setMessage("Esta seguro que desea sincronizar? (La aplicacion se congeleara en el proceso de sicronizacion. Favor de esperar)");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MasterActualizar();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();

    }


    public void MasterActualizar(){


        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            Cursor c = bd.rawQuery("select offlineID, idReservaDetalle, tipoSolicitud, status, folioNoShow, recibeNoShow, sincuponAutoriza, observacion, a, n, i, cupon from offline where habilitado = 1", null); //where Habilitado = 1
            Lista_Offline = null;
            Lista_Offline = new ArrayList<Entity_offline>();

            if (c != null ) {
                if  (c.moveToFirst()) {

                    do {
                        Entity_offline data_off = new Entity_offline();
                        data_off.idReservaDetalle = c.getInt(c.getColumnIndex("idReservaDetalle"));
                        data_off.tipoSolicitud = c.getInt(c.getColumnIndex("tipoSolicitud"));
                        data_off.status = c.getInt(c.getColumnIndex("status"));
                        data_off.folioNoShow = c.getString(c.getColumnIndex("folioNoShow"));
                        data_off.recibeNoShow = c.getString(c.getColumnIndex("recibeNoShow"));
                        data_off.sincuponAutoriza = c.getString(c.getColumnIndex("sincuponAutoriza"));
                        data_off.observacion = c.getString(c.getColumnIndex("observacion"));
                        data_off.a = c.getInt(c.getColumnIndex("a"));
                        data_off.n = c.getInt(c.getColumnIndex("n"));
                        data_off.i = c.getInt(c.getColumnIndex("i"));
                        data_off.cupon = c.getString(c.getColumnIndex("cupon"));
                        data_off.offlineID = c.getInt(c.getColumnIndex("offlineID"));

                        Lista_Offline.add(data_off);

                    }while (c.moveToNext());
                }
                else{
                    Toast.makeText(this, "No existen más procesos por subir a la base de datos :)", Toast.LENGTH_SHORT).show();
                }
            }

            c.close();

            bd.close();

            //Toast.makeText(this, Lista_Offline.size() + "", Toast.LENGTH_SHORT).show();

            for(int i = 0; i < Lista_Offline.size(); i++){
                boolean str_result;
                //Toast.makeText(this, Lista_Offline.get(i).idDetalleOpVehi + "" + Lista_Offline.get(i).tipoSolicitud, Toast.LENGTH_SHORT).show();
                switch (Lista_Offline.get(i).tipoSolicitud){
                    case 1:
                        WSidReservaDetalle = Lista_Offline.get(i).idReservaDetalle;
                        WSfolioNoShow = Lista_Offline.get(i).folioNoShow;
                        WSrecibeNoShow = Lista_Offline.get(i).recibeNoShow;
                        WSobservacion = Lista_Offline.get(i).observacion;
                        WOfflineID = Lista_Offline.get(i).offlineID;

                        TareaWSMarcarNoSwhow tareaWSMarcarNoSwhow = new TareaWSMarcarNoSwhow();
                        str_result= tareaWSMarcarNoSwhow.execute().get();
                        break;
                    case 2:
                        WSidReservaDetalle = Lista_Offline.get(i).idReservaDetalle;
                        WScupon = Lista_Offline.get(i).cupon;

                        TareaWSCambiarCupon tareaWSCambiarCupon = new TareaWSCambiarCupon();
                        str_result= tareaWSCambiarCupon.execute().get();
                        break;
                    case 3:

                        WSidReservaDetalle = Lista_Offline.get(i).idReservaDetalle;
                        WSa = Lista_Offline.get(i).a;
                        WSn = Lista_Offline.get(i).n;
                        WSi = Lista_Offline.get(i).i;

                        TareaWSCambiarPAX tareaWSCambiarPAX = new TareaWSCambiarPAX();
                        str_result= tareaWSCambiarPAX.execute().get();
                        break;
                    case 4:
                        WSidReservaDetalle = Lista_Offline.get(i).idReservaDetalle;
                        WSa = Lista_Offline.get(i).a;
                        WSn = Lista_Offline.get(i).n;
                        WSi = Lista_Offline.get(i).i;
                        WSsincuponAutoriza = Lista_Offline.get(i).sincuponAutoriza;
                        WSobservacion = Lista_Offline.get(i).observacion;

                        TareaWSAbordarSinCupon tareaWSAbordarSinCupon = new TareaWSAbordarSinCupon();
                        str_result= tareaWSAbordarSinCupon.execute().get();
                        break;
                    case 5:

                        WSidReservaDetalle = Lista_Offline.get(i).idReservaDetalle;
                        WSa = Lista_Offline.get(i).a;
                        WSn = Lista_Offline.get(i).n;
                        WSi = Lista_Offline.get(i).i;
                        WOfflineID = Lista_Offline.get(i).offlineID;

                        TareaWSAbordar tareaWSAbordar = new TareaWSAbordar();
                        str_result= tareaWSAbordar.execute().get();
                        break;
                    default: break;
                }

            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        sincroniza_encuesta();
        verifica_sincro();
        //progressDialog.dismiss();

    }



    public void CargaCuponesLocalesSearch(String cadena){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            Cursor c = bd.rawQuery("select idDetalleOpVehi, numCupon, Huesped, numAdultos, numNinos , numInfantes, Incentivos, Hotel, Habitacion, Idioma, PickUpLobby, nombreAgencia, nombreRepresentante, Observaciones, status,color from cupones where Habilitado = 1 and Hotel like '%"+ cadena+"%' order by (status = 13) desc, status", null); //where Habilitado = 1

            data = null;
            data = new ArrayList<Entity_CuponesHoja>();

            if (c != null ) {
                if  (c.moveToFirst()) {

                    do {
                        Entity_CuponesHoja datanum = new Entity_CuponesHoja();
                        datanum.numCupon = c.getString(c.getColumnIndex("numCupon"));
                        datanum.Huesped = c.getString(c.getColumnIndex("Huesped"));
                        datanum.numAdultos = c.getInt(c.getColumnIndex("numAdultos"));
                        datanum.numNinos = c.getInt(c.getColumnIndex("numNinos"));
                        datanum.numInfantes = c.getInt(c.getColumnIndex("numInfantes"));
                        datanum.Incentivos = c.getInt(c.getColumnIndex("Incentivos"));
                        datanum.Hotel = c.getString(c.getColumnIndex("Hotel")).trim();
                        datanum.Habitacion = c.getString(c.getColumnIndex("Habitacion"));
                        datanum.Idioma = c.getString(c.getColumnIndex("Idioma"));
                        datanum.PickUpLobby = c.getString(c.getColumnIndex("PickUpLobby"));
                        datanum.nombreAgencia = c.getString(c.getColumnIndex("nombreAgencia"));
                        datanum.nombreRepresentante = c.getString(c.getColumnIndex("nombreRepresentante"));
                        datanum.Observaciones = c.getString(c.getColumnIndex("Observaciones"));
                        datanum.status = c.getInt(c.getColumnIndex("status"));
                        datanum.color = c.getString(c.getColumnIndex("color"));
                        if(!datanum.color.equals("N")){datanum.apoyo="apoyo";}
                        else{datanum.apoyo="";}
                        datanum.idDetalleOpVehi = c.getInt(c.getColumnIndex("idDetalleOpVehi"));


                        data.add(datanum);

                    }while (c.moveToNext());
                    //Toast.makeText(this,"Cupones cargados localmente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "No existen más cupones pendientes", Toast.LENGTH_SHORT).show();
                }
            }

            c.close();

            bd.close();

            ListAdapter_Cupones customAdapter = new ListAdapter_Cupones(this, R.layout.cupones, data);
            Lista.setAdapter(customAdapter);
            //Lista.setOnItemClickListener(onListClick);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void CargaCuponesLocales(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            Cursor c = bd.rawQuery("select idReservaDetalle, idOpVehi, idDetalleOpVehi, numCupon, Huesped, numAdultos, numNinos , numInfantes, Incentivos, Hotel, Habitacion, Idioma, PickUpLobby, nombreAgencia, nombreRepresentante, Observaciones, status,color from cupones  order by (color='N') desc,color,  status", null);



            data = null;
            data = new ArrayList<Entity_CuponesHoja>();




            if (c != null ) {
                if  (c.moveToFirst()) {

                    do {
                        Entity_CuponesHoja datanum = new Entity_CuponesHoja();
                        datanum.idReservaDetalle=c.getInt(c.getColumnIndex("idReservaDetalle"));
                        datanum.idOpVehi=c.getInt(c.getColumnIndex("idOpVehi"));
                        datanum.numCupon = c.getString(c.getColumnIndex("numCupon"));
                        datanum.Huesped = c.getString(c.getColumnIndex("Huesped"));
                        datanum.numAdultos = c.getInt(c.getColumnIndex("numAdultos"));
                        datanum.numNinos = c.getInt(c.getColumnIndex("numNinos"));
                        datanum.numInfantes = c.getInt(c.getColumnIndex("numInfantes"));
                        datanum.Incentivos = c.getInt(c.getColumnIndex("Incentivos"));
                        datanum.Hotel = c.getString(c.getColumnIndex("Hotel")).trim();
                        datanum.Habitacion = c.getString(c.getColumnIndex("Habitacion"));
                        datanum.Idioma = c.getString(c.getColumnIndex("Idioma"));
                        datanum.PickUpLobby = c.getString(c.getColumnIndex("PickUpLobby"));
                        datanum.nombreAgencia = c.getString(c.getColumnIndex("nombreAgencia"));
                        datanum.nombreRepresentante = c.getString(c.getColumnIndex("nombreRepresentante"));
                        datanum.Observaciones = c.getString(c.getColumnIndex("Observaciones"));
                        datanum.status = c.getInt(c.getColumnIndex("status"));
                        datanum.color = c.getString(c.getColumnIndex("color"));

                        if(!datanum.color.equals("N")){datanum.apoyo="apoyo";}
                        else{datanum.apoyo="";}


                        datanum.idDetalleOpVehi = c.getInt(c.getColumnIndex("idDetalleOpVehi"));

                        variables_publicas.id_op_vehi=datanum.idOpVehi;
                        data.add(datanum);

                    }while (c.moveToNext());
                    //Toast.makeText(this,"Cupones cargados localmente", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Toast.makeText(this, "No existen más cupones pendientes", Toast.LENGTH_SHORT).show();
                }
            }

            c.close();

            bd.close();




        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }



    private class TareaWSObtenerCupones extends AsyncTask<String,Integer,Boolean> {

        private Entity_CuponesHoja[] CuponesHoja;
        ProgressDialog progressDialog = new ProgressDialog(Operacion.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando datos, por favor espere...");
            progressDialog.show();
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonaturalDev/WS.asmx";
            final String METHOD_NAME = "ObtenerCupones";
            final String SOAP_ACTION = "http://suarpe.com/ObtenerCupones";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idOpVehi", Long.parseLong(idOpVehi));
            request.addProperty("apoyo", variables.apoyo);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapObject resSoap =(SoapObject)envelope.getResponse();

                CuponesHoja = new Entity_CuponesHoja[resSoap.getPropertyCount()];

                for (int i = 0; i < CuponesHoja.length; i++)
                {
                    SoapObject ic = (SoapObject)resSoap.getProperty(i);


                    Entity_CuponesHoja cuponesHoja = new Entity_CuponesHoja();
                    cuponesHoja.idReservaDetalle = Integer.parseInt((ic.getProperty(0).toString()));
                    cuponesHoja.idOpVehi = Integer.parseInt((ic.getProperty(1).toString()));
                    cuponesHoja.idDetalleOpVehi = Integer.parseInt((ic.getProperty(2).toString()));
                    cuponesHoja.numCupon = ic.getProperty(3).toString();
                    cuponesHoja.Huesped = ic.getProperty(4).toString();
                    cuponesHoja.numAdultos = Integer.parseInt((ic.getProperty(5).toString()));
                    cuponesHoja.numNinos = Integer.parseInt((ic.getProperty(6).toString()));
                    cuponesHoja.numInfantes = Integer.parseInt((ic.getProperty(7).toString()));
                    cuponesHoja.Incentivos = Integer.parseInt((ic.getProperty(8).toString()));
                    cuponesHoja.Hotel = ic.getProperty(9).toString().trim();
                    cuponesHoja.Habitacion = ic.getProperty(10).toString();
                    cuponesHoja.Idioma = ic.getProperty(11).toString();
                    cuponesHoja.PickUpLobby = ic.getProperty(12).toString();
                    cuponesHoja.nombreAgencia = ic.getProperty(13).toString();
                    cuponesHoja.nombreRepresentante = ic.getProperty(14).toString();
                    cuponesHoja.Observaciones = ic.getProperty(15).toString();
                    cuponesHoja.status = Integer.parseInt((ic.getProperty(16).toString()));
                    cuponesHoja.tour_padre = Integer.parseInt((ic.getProperty(17).toString()));
                    cuponesHoja.idIdioma = Integer.parseInt((ic.getProperty(18).toString()));
                    cuponesHoja.color = ic.getProperty(19).toString();

                    CuponesHoja[i] = cuponesHoja;
                }
                ArrayList<String> l_touridi= new ArrayList<String>();

                connection=conexion();
                for(int i=0; i<CuponesHoja.length; i++){ //dar alta
                    String ti_data = Integer.toString(CuponesHoja[i].tour_padre)+Integer.toString(CuponesHoja[i].idIdioma);
                    Set<String> set = new HashSet<String>(l_touridi);
                    if (!set.contains(ti_data))
                    {
                        l_touridi.add(ti_data);
                        actualizar_preguntas(CuponesHoja[i].tour_padre,CuponesHoja[i].idIdioma);
                    }
                    alta_cupones(CuponesHoja[i].idReservaDetalle,CuponesHoja[i].idOpVehi,CuponesHoja[i].idDetalleOpVehi, CuponesHoja[i].numCupon, CuponesHoja[i].Huesped, CuponesHoja[i].numAdultos, CuponesHoja[i].numNinos, CuponesHoja[i].numInfantes, CuponesHoja[i].Incentivos, CuponesHoja[i].Hotel, CuponesHoja[i].Habitacion, CuponesHoja[i].Idioma, CuponesHoja[i].PickUpLobby, CuponesHoja[i].nombreAgencia, CuponesHoja[i].nombreRepresentante,CuponesHoja[i].Observaciones,  Boolean.TRUE, CuponesHoja[i].status,CuponesHoja[i].tour_padre,CuponesHoja[i].idIdioma,CuponesHoja[i].color);

                }
                actualizar_datos_resp_mensaje();
                CargaCuponesLocales();



            }
            catch (Exception e)
            {

                resul = false;

            }

            return resul;

        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {

                visualiza_cupones();
                progressDialog.dismiss();


            }
            else
            {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(),"Ocurrió un error.. :(",Toast.LENGTH_LONG).show();
            }
        }
    }

    Boolean ExitoWS;

    private class TareaWSAbordar extends AsyncTask<String,Integer,Boolean> {

        String error = "";
        Boolean OK = false;
        int pruebaDios = WOfflineID;

        ProgressDialog progressDialog = new ProgressDialog(Operacion.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando datos, por favor espere...");
            progressDialog.show();
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL = "http://desarrollo19.cloudapp.net/WSGonaturalDev/WS.asmx";
            final String METHOD_NAME = "Abordar";
            final String SOAP_ACTION = "http://suarpe.com/Abordar";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idOpVehi", variables_publicas.id_op_vehi);
            request.addProperty("idReservaDetalle", WSidReservaDetalle);
            request.addProperty("status", 14);
            request.addProperty("numAdulto", WSa);
            request.addProperty("numNino", WSn);
            request.addProperty("numInfante", WSi);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml = (SoapPrimitive) envelope.getResponse();
                OK = Boolean.parseBoolean(resultado_xml.toString());



            } catch (Exception e) {
                error = e.toString();
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result)
            {
                ExitoWS = OK;
                if (ExitoWS){
                    BorrarElementoOffline(pruebaDios);
                    //Toast.makeText(getBaseContext(),"Borrar de la tabla" + pruebaDios ,Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    private class TareaWSMarcarNoSwhow extends AsyncTask<String,Integer,Boolean> {

        String error = "";
        Boolean OK = false;
        int pruebaDios = WOfflineID;
        ProgressDialog progressDialog = new ProgressDialog(Operacion.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando datos, por favor espere...");
            progressDialog.show();
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonaturalDev/WS.asmx";
            final String METHOD_NAME = "MarcarNoShow";
            final String SOAP_ACTION = "http://suarpe.com/MarcarNoShow";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("status", 11);
            request.addProperty("idOpVehi", variables_publicas.id_op_vehi);
            request.addProperty("idReservaDetalle", WSidReservaDetalle);
            request.addProperty("NoFolio",WSfolioNoShow);
            request.addProperty("AQuienSeEntrega",WSrecibeNoShow);
            request.addProperty("Motivo",WSobservacion);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                OK = Boolean.parseBoolean(resultado_xml.toString());

            }
            catch (Exception e)
            {
                error = e.toString();
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result)
            {
                ExitoWS = OK;
                if (ExitoWS){
                    BorrarElementoOffline(pruebaDios);
                    //Toast.makeText(getBaseContext(),"Borrar de la tabla" + pruebaDios ,Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    private class TareaWSCambiarCupon extends AsyncTask<String,Integer,Boolean> {

        String error = "";
        Boolean OK = false;
        int pruebaDios = WOfflineID;
        ProgressDialog progressDialog = new ProgressDialog(Operacion.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando datos, por favor espere...");
            progressDialog.show();
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonaturalDev/WS.asmx";
            final String METHOD_NAME = "CambiarCupon";
            final String SOAP_ACTION = "http://suarpe.com/CambiarCupon";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("numCupon", ""); //INUTIL
            request.addProperty("idOpVehi", variables_publicas.id_op_vehi);
            request.addProperty("idReservaDetalle", WSidReservaDetalle);
            request.addProperty("nuevoNumCupon", WScupon);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                OK = Boolean.parseBoolean(resultado_xml.toString());


            }
            catch (Exception e)
            {
                error = e.toString();
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result)
            {
                ExitoWS = OK;
                if (ExitoWS){
                    BorrarElementoOffline(pruebaDios);
                    //Toast.makeText(getBaseContext(),"Borrar de la tabla" + pruebaDios ,Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    private class TareaWSCambiarPAX extends AsyncTask<String,Integer,Boolean> {

        String error = "";
        Boolean OK = false;
        int pruebaDios = WOfflineID;

        ProgressDialog progressDialog = new ProgressDialog(Operacion.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando datos, por favor espere...");
            progressDialog.show();
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonaturalDev/WS.asmx";
            final String METHOD_NAME = "CambiarPAX";
            final String SOAP_ACTION = "http://suarpe.com/CambiarPAX";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idOpVehi", variables_publicas.id_op_vehi);
            request.addProperty("idReservaDetalle", WSidReservaDetalle);
            request.addProperty("numAdulto", WSa);
            request.addProperty("numNino", WSn);
            request.addProperty("numInfante", WSi);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                OK = Boolean.parseBoolean(resultado_xml.toString());


            }
            catch (Exception e)
            {
                error = e.toString();
                resul = false;

            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result)
            {
                ExitoWS = OK;
                if (ExitoWS){
                    BorrarElementoOffline(pruebaDios);
                    //Toast.makeText(getBaseContext(),"Borrar de la tabla" + pruebaDios ,Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    private class TareaWSAbordarSinCupon extends AsyncTask<String,Integer,Boolean> {

        String error = "";
        Boolean OK = false;
        int pruebaDios = WOfflineID;

        ProgressDialog progressDialog = new ProgressDialog(Operacion.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando datos, por favor espere...");
            progressDialog.show();
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonaturalDev/WS.asmx";
            final String METHOD_NAME = "AbordarSinCupon";
            final String SOAP_ACTION = "http://suarpe.com/AbordarSinCupon";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idOpVehi", variables_publicas.id_op_vehi);
            request.addProperty("idReservaDetalle", WSidReservaDetalle);
            request.addProperty("status", 12);
            request.addProperty("numAdulto", WSa);
            request.addProperty("numNino", WSn);
            request.addProperty("numInfante", WSi);
            request.addProperty("sinCuponAutoriza", WSsincuponAutoriza);
            request.addProperty("obs", WSobservacion);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                OK = Boolean.parseBoolean(resultado_xml.toString());


            }
            catch (Exception e)
            {
                error = e.toString();
                resul = false;

            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result)
            {
                ExitoWS = OK;
                if (ExitoWS){
                    BorrarElementoOffline(pruebaDios);
                    //Toast.makeText(getBaseContext(),"Borrar de la tabla" + pruebaDios ,Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    private class TareaWSInsertaencuesta extends AsyncTask<String,Integer,Boolean> {

        String error = "";
        Boolean OK = false;
        int pruebaDios = WOfflineID;

        ProgressDialog progressDialog = new ProgressDialog(Operacion.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando datos, por favor espere...");
            progressDialog.show();
        }
        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonaturalDev/WS.asmx";
            final String METHOD_NAME = "InsertaEncuesta";
            final String SOAP_ACTION = "http://suarpe.com/InsertaEncuesta";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idReservaDetalle", WSidReservaDetalle);
            request.addProperty("cuestionario", WScuestionario);
            request.addProperty("pregunta",WSpregunta);
            request.addProperty("respuesta",WSrespuesta);
            request.addProperty("califica",WScalifica);
            request.addProperty("fecha",WSfecha);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                OK = Boolean.parseBoolean(resultado_xml.toString());

            }
            catch (Exception e)
            {
                error = e.toString();
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (!result)
            {

                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    private class TareaWSInsertaencuestaEnc extends AsyncTask<String,Integer,Boolean> {

        String error = "";
        Boolean OK = false;
        int pruebaDios = WOfflineID;

        ProgressDialog progressDialog = new ProgressDialog(Operacion.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando datos, por favor espere...");
            progressDialog.show();
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;


            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonaturalDev/WS.asmx";
            final String METHOD_NAME = "InsertaEncuestaEnc";
            final String SOAP_ACTION = "http://suarpe.com/InsertaEncuestaEnc";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idReservaDetalle", WSidReservaDetalle);
            request.addProperty("cupon", WScupon);
            request.addProperty("comentario",WScomentario);
            request.addProperty("email",Wsemail);
            request.addProperty("califica",WScalifica);
            request.addProperty("fecha",WSfecha);
            request.addProperty("firma",WSfirma);
            request.addProperty("pais",WSpais);
            request.addProperty("estado",WSestado);
            request.addProperty("tel",WStel);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            new MarshalBase64().register(envelope); // serialization
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                OK = Boolean.parseBoolean(resultado_xml.toString());

            }
            catch (Exception e)
            {
                error = e.toString();
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (!result)
            {

                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    private class TareaWSupdatepickups extends AsyncTask<String,Integer,Boolean> {

        String error = "";
        Boolean OK = false;
        int pruebaDios = WOfflineID;

        ProgressDialog progressDialog = new ProgressDialog(Operacion.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando datos, por favor espere...");
            progressDialog.show();
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;


            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://desarrollo19.cloudapp.net/WSGonaturalDev/WS.asmx";
            final String METHOD_NAME = "Updatepickups";
            final String SOAP_ACTION = "http://suarpe.com/Updatepickups";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idOpVehi", variables_publicas.id_op_vehi);
            request.addProperty("idReservaDetalle", WSidReservaDetalle);
            request.addProperty("hentrada", WSentrada);
            request.addProperty("hsalida",WSsalida);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            new MarshalBase64().register(envelope); // serialization
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                OK = Boolean.parseBoolean(resultado_xml.toString());

            }
            catch (Exception e)
            {
                error = e.toString();
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (!result)
            {

                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
            }

        }
    }

    public boolean BorrarElementoOffline(int offlineID){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        boolean deleted = true;
        try{
            bd.delete("offline", "offlineID =" + offlineID, null);

        }
        catch (Exception e){
            deleted = false;
        }

        bd.close();

        return deleted;
    }

    public void BorrarFiltro(View view){
        txtSearch.setText("");
    }
    @SuppressLint("NewApi")
    private Connection conexion() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;

        String url = "jdbc:jtds:sqlserver://sqlintep.cloudapp.net;instance=SQLEXPRESS;DatabaseName=GoNaturalV2";
        String driver = "net.sourceforge.jtds.jdbc.Driver";
        String userName = "sa";
        String password = "Tamalito2017";

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);

        } catch (SQLException sqlException) {
            Toast.makeText(getApplicationContext(), sqlException.toString(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        return conn;
    }

    public void actualizar_preguntas(int tour_padre,int idioma){
        // To dismiss the dialog

        SQL_Preguntas = ("select C.idCuestionario,C.idCategoriaPregunta,cP.nombreCategoriaPregunta, C.pregunta, C.idTipo, cP.idIdioma, cP.orden,C.orden orden_pregunta,cP.idTourPadre " +
                "from Cuestionario C " +
                "inner join categoriaPregunta cP on cP.idCategoriaPregunta = C.idCategoriaPregunta " +
                "where CP.idTourPadre = "+tour_padre+" and cP.idIdioma = "+idioma+" and C.status=1");
        ObtenerPreguntasOnline(SQL_Preguntas);

    }

    public void actualizar_datos_resp_mensaje(){

        String sql_respuestas= "select idCuestionario,Respuesta,orden from cuestionarioRespuesta ";
        inserta_respuesta(sql_respuestas);

        String sql_mensaje= "select titulo,mensaje,idIdioma from encuestaMensaje ";
        inserta_mensaje(sql_mensaje);


    }

    public void ObtenerPreguntasOnline(String comandosql){
        ResultSet rs;
        try{
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(comandosql);

            //alta(1,"hola",1,2);

            while(rs.next()) {

                alta_preguntas(rs.getLong("idCuestionario"),rs.getInt("idCategoriaPregunta"),rs.getString("nombreCategoriaPregunta"), rs.getString("pregunta"),rs.getInt("idTipo"), rs.getInt("idIdioma"),rs.getInt("orden"),rs.getInt("orden_pregunta"),rs.getInt("idTourPadre"));
            }

            //Toast.makeText(this, "Datos cargados exitosamente", Toast.LENGTH_SHORT).show();

        }catch (Exception e){

        }
    }

    public void inserta_respuesta(String sql_respuesta){
        ResultSet rs;
        try{
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql_respuesta);

            //alta(1,"hola",1,2);

            while(rs.next()) {

                alta_respuestas(rs.getInt("idCuestionario"),rs.getString("Respuesta"), rs.getInt("orden"));
            }

            //Toast.makeText(this, "Datos cargados exitosamente", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            String t = e.getMessage();
        }
    }

    public void inserta_mensaje(String sql_mensaje){
        ResultSet rs;
        try{
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql_mensaje);

            //alta(1,"hola",1,2);

            while(rs.next()) {

                alta_mensaje(rs.getString("titulo"),rs.getString("mensaje"), rs.getInt("idIdioma"));
            }

            //Toast.makeText(this, "Datos cargados exitosamente", Toast.LENGTH_SHORT).show();

        }catch (Exception e){

        }
    }

    // Damos de alta los usuarios en nuestra aplicación
    public void alta_preguntas(long id,int id_categoria,String nombre_cate, String pregunta, int tipo, int idioma,int ordencate,int ordenpreg, int idTourPadre ) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        registro.put("idCuestionarios", id);
        registro.put("idCategoriaPregunta", id_categoria);
        registro.put("nombreCategoriaPregunta", nombre_cate);
        registro.put("pregunta", pregunta);
        registro.put("tipo", tipo);
        registro.put("idIdioma", idioma);
        registro.put("orden_categoria", ordencate);
        registro.put("orden_pregunta", ordenpreg);
        registro.put("tour_padre", idTourPadre);


        // los inserto en la base de datos
        bd.insert("cuestionarios",null, registro);

        bd.close();

    }

    public void alta_respuestas(long idpregunta,String respuesta, int orden ) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        registro.put("idCuestionario", idpregunta);
        registro.put("respuesta", respuesta);
        registro.put("orden", orden);


        // los inserto en la base de datos
        bd.insert("categoria_respuesta",null, registro);

        bd.close();

    }

    public void alta_mensaje(String titulo,String mensaje, int idioma ) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        registro.put("titulo", titulo);
        registro.put("mensaje", mensaje);
        registro.put("idioma", idioma);


        // los inserto en la base de datos
        bd.insert("encuestaMensaje",null, registro);

        bd.close();

    }

    public void sincroniza_encuesta () {

        boolean err= false;
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try {
            //Sincroniza encuestas
            Cursor c = bd.rawQuery("select idEncuestaDetalle,idReservaDetalle,idCuestionario,pregunta,valor_respuesta,fechaDetalle from encuestaDetalle where enviado = 0", null); //where Habilitado = 1
            Cursor cd = bd.rawQuery("select idReservaDetalle,idCupon,comentario,email,fecha, firma,pais,estado,tel  from encuesta a ", null);
            Cursor cdt = bd.rawQuery("select  idReservaDetalle,ifnull(hentrada,'00:00:00') hentrada,ifnull(hsalida,'00:00:00') hsalida  from cupones", null);

            if (c != null) {
                if (c.moveToFirst()) {

                    do {
                        WSidReservaDetalle = c.getInt(c.getColumnIndex("idReservaDetalle"));
                        WScuestionario = c.getInt(c.getColumnIndex("idCuestionario"));
                        WSpregunta = c.getString(c.getColumnIndex("pregunta"));
                        WSrespuesta=c.getString(c.getColumnIndex("valor_respuesta"));
                        WSfecha = c.getString( c.getColumnIndex("fechaDetalle"));

                        if(WSrespuesta.equals("")){WSrespuesta = "na";}
                        if(WSrespuesta.length()>1){
                            WScalifica=0;
                        }else{
                            WScalifica=Integer.parseInt(WSrespuesta);
                        }
                        TareaWSInsertaencuesta tareaWSInsertaencuesta = new TareaWSInsertaencuesta();
                        boolean str_result= tareaWSInsertaencuesta.execute().get();


                        if(str_result) {
                            ContentValues vu = new ContentValues();
                            vu.put("enviado", 1);
                            bd.update("encuestaDetalle", vu, "idEncuestaDetalle=" + c.getInt(c.getColumnIndex("idEncuestaDetalle")), null);
                        }else{ err=true;}

                    } while (c.moveToNext());

                    //Sincroniza encabezado encuesta
                    if( cd.moveToFirst()) {
                        do {
                            WSidReservaDetalle = cd.getInt(cd.getColumnIndex("idReservaDetalle"));
                            WScupon =cd.getString(cd.getColumnIndex("idCupon"));

                            WScomentario = cd.getString(cd.getColumnIndex("comentario"));
                            Wsemail = cd.getString(cd.getColumnIndex("email"));
                            WSfecha = cd.getString(cd.getColumnIndex("fecha"));
                            WSpais = cd.getString(cd.getColumnIndex("pais"));
                            WSestado = cd.getString(cd.getColumnIndex("estado"));
                            WStel = cd.getString(cd.getColumnIndex("tel"));
                            WSfirma = cd.getBlob(cd.getColumnIndex("firma"));




                            TareaWSInsertaencuestaEnc tareaWSInsertaencuestaenc = new TareaWSInsertaencuestaEnc();
                            boolean str_result1= tareaWSInsertaencuestaenc.execute().get();

                            if(!str_result1){err=true;}

                        } while (cd.moveToNext());
                    }


                }
            }


            // Sincroniza pickups
            if( cdt.moveToFirst()) {
                do {
                    WSidReservaDetalle = cdt.getInt(cdt.getColumnIndex("idReservaDetalle"));
                    WSentrada=cdt.getString(cdt.getColumnIndex("hentrada"));
                    WSsalida=cdt.getString(cdt.getColumnIndex("hsalida"));


                    TareaWSupdatepickups tareaWSupdatepickups = new TareaWSupdatepickups();
                    boolean str_result=tareaWSupdatepickups.execute().get();



                    if(!str_result){err=true;}

                } while (cdt.moveToNext());
            }

            if(!err) {
                Toast.makeText(getApplicationContext(), "Sincronizado...", Toast.LENGTH_LONG).show();
               /* if (BorrarBDLocal()) {
                    Intent intent_restart = new Intent(Operacion.this, EncuestaAgregarFolio.class);
                    startActivity(intent_restart);
                    SalirActividad();
                } else {
                    Toast.makeText(Operacion.this, "HUBO UN ERROR AL ELIMINAR LA OPERACIÓN DE SU DISPOSITIVO", Toast.LENGTH_SHORT).show();
                }*/
            }else{ Toast.makeText(Operacion.this, "Problema al sincronizar, revise conexion he intente", Toast.LENGTH_SHORT).show();}
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean verifica_sinc_encuesta () {

        boolean ban= false;
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try {
            //Sincroniza encuestas
            Cursor c = bd.rawQuery("select idEncuestaDetalle,idReservaDetalle,idCuestionario,pregunta,valor_respuesta,fechaDetalle from encuestaDetalle where enviado = 0", null); //where Habilitado = 1



            if (c.getCount()>0 ) {
                    ban=true;
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        return ban;
    }
    public boolean verifica_sinc_cupones(){

        boolean ban = false;

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            Cursor c = bd.rawQuery("select offlineID, idReservaDetalle, tipoSolicitud, status, folioNoShow, recibeNoShow, sincuponAutoriza, observacion, a, n, i, cupon from offline where habilitado = 1", null); //where Habilitado = 1
            Lista_Offline = null;
            Lista_Offline = new ArrayList<Entity_offline>();

            if (c.getCount()>0 ) {
                    ban=true;
            }

            c.close();

            bd.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        verifica_sinc_encuesta();
        //progressDialog.dismiss();
        return ban;
    }
    public void verifica_sincro(){

        if(verifica_sinc_cupones()){
            lblcupones.setText("Cupones por sincronizar");
            lblcupones.setTextColor(Color.RED);
        }else{
            lblcupones.setText("No hay cupones por sincronizar");
            lblcupones.setTextColor(Color.GREEN);
        }

        if(verifica_sinc_encuesta()){
            lblencuesta.setText("Encuestas por sincronizar");
            lblencuesta.setTextColor(Color.RED);
        }else{
            lblencuesta.setText("No hay encuestas por sincronizar");
            lblencuesta.setTextColor(Color.GREEN);
        }

    }
    private void visualiza_cupones(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getApplicationContext(), "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        ListAdapter_Cupones customAdapter = new ListAdapter_Cupones(getApplicationContext(), R.layout.cupones, data);
        Lista.setAdapter(customAdapter);
        //Lista.setOnItemClickListener(onListClick);


        int Pendientes = 0;

        bd = admin.getWritableDatabase();
        Cursor c = bd.rawQuery("select numCupon status from cupones where status not in(10,11, 12, 14)", null); //where Habilitado = 1
        if (c != null ) {
            if  (c.moveToFirst()) {

                do {
                    Pendientes = c.getCount();


                }while (c.moveToNext());
                //Toast.makeText(this,"Cupones cargados localmente", Toast.LENGTH_SHORT).show();
            }

        }

        c.close();

        bd.close();


        txtEncuestasRestantes.setText("" + Pendientes);
    }

    private void cabecera(){
        int apoyob=0;
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor v = bd.rawQuery("select idopveh, guia, camioneta, operador,obs,apoyo from vehiculo", null);




        if (v != null ) {
            if (v.moveToFirst()) {
                lblorden.setText(Integer.toString(v.getInt(v.getColumnIndex("idopveh"))));
                lblguia.setText(v.getString(v.getColumnIndex("guia")));
                lbltrans.setText(v.getString(v.getColumnIndex("camioneta")));
                lbloperador.setText(v.getString(v.getColumnIndex("operador")));
                lblobs.setText(v.getString(v.getColumnIndex("obs")));
                apoyob=v.getInt(v.getColumnIndex("apoyo"));
            }
        }

        variables_publicas.apoyo= apoyob==1?true:false;
    }

    public void popup_window(View view){
        final PopupWindow pwindo;


        LayoutInflater inflat = (LayoutInflater) Operacion.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout_popup = inflat.inflate(R.layout.popup_datos_transportadora,
                (ViewGroup) findViewById(R.id.layout_principal_pop));




        pwindo = new PopupWindow(layout_popup, 800, 700, true);


        Button btn_aceptar = (Button) layout_popup.findViewById(R.id.btn_info_aceptar);
        TextView txt_razon = (TextView)  layout_popup.findViewById(R.id.txt_razon);
        TextView txt_dir    = (TextView)  layout_popup.findViewById(R.id.txt_dir);
        TextView txt_rfc    = (TextView)  layout_popup.findViewById(R.id.txt_rfc);

        txt_razon.setText(variables_publicas.razon);
        txt_dir.setText(variables_publicas.dir);
        txt_rfc.setText(variables_publicas.rfc);



        // Triggers de Popup
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwindo.dismiss();

            }
        });

        pwindo.showAtLocation(layout_popup, Gravity.CENTER, 0, -20);

    }

}

