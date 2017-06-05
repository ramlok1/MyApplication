package com.example.mobilesdblack.ejemplo2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.jtds.jdbc.DateTime;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
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
    TextView txtEncuestasRestantes,lblguia,lblorden,lbltrans,lbloperador,lblobs;

    ImageView imgEncuesta;

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
    public void alta_cupones(long idDetalleOpVehi, String numCupon, String Huesped, int numAdultos, int numNinos, int numInfantes, int Incentivos, String Hotel,String Habitacion , String Idioma ,String PickUpLobby ,String nombreAgencia , String nombreRepresentante ,String Observaciones,  boolean habilitado, int status, int tour_padre, int ididioma,String color ) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

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
        lblobs = (TextView)findViewById(R.id.txt_obs_veh);
        imgEncuesta = (ImageView)findViewById(R.id.imgEncuesta);
        Lista = (ListView) findViewById(R.id.lstCupones);
        this.registerForContextMenu(Lista);
        txtSearch = (EditText)findViewById(R.id.txtsearch);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){

        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId() == R.id.lstCupones){
            this.getMenuInflater().inflate(R.menu.menu_options_cupones, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item){

        int selectedItemId = item.getItemId();

        int idDetalleOpVehi = 0;

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
                    idDetalleOpVehi = data.get(info.position).idDetalleOpVehi;

                    Intent intent_cupon = new Intent(Operacion.this,Abordar.class);
                    //intent_cupon.putExtra("cupon4",numero_cupon.substring(numero_cupon.length() - 4,numero_cupon.length()));
                    //intent_cupon.putExtra("cupon",numero_cupon);

                    intent_cupon.putExtra("numAdultos",NumAdultos);
                    intent_cupon.putExtra("numNinos",NumNinos);
                    intent_cupon.putExtra("numInfantes",NumInfantes);
                    intent_cupon.putExtra("cupon4",numero_cupon.substring(numero_cupon.length() - 4,numero_cupon.length()));
                    intent_cupon.putExtra("cupon",numero_cupon);
                    intent_cupon.putExtra("idDetalleOpVehi",idDetalleOpVehi);


                    startActivity(intent_cupon);
                    this.finish();
                    break;

                case R.id.mniCambiarCupon:

                    numero_cupon = data.get(info.position).numCupon.toString();
                    idDetalleOpVehi = data.get(info.position).idDetalleOpVehi;

                    Intent intent = new Intent(Operacion.this,CambioCupon.class);
                    intent.putExtra("cupon",numero_cupon);
                    intent.putExtra("idDetalleOpVehi",idDetalleOpVehi);
                    intent.putExtra("vieneDeOperacion", true);

                    startActivity(intent);
                    this.finish();
                    break;

                case R.id.mniNoShow:

                    numero_cupon = data.get(info.position).numCupon.toString();
                    idDetalleOpVehi = data.get(info.position).idDetalleOpVehi;

                    Intent intentv = new Intent(Operacion.this,NoShow.class);

                    intentv.putExtra("cupon",numero_cupon);
                    intentv.putExtra("idDetalleOpVehi",idDetalleOpVehi);
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
                    idDetalleOpVehi = data.get(info.position).idDetalleOpVehi;

                    Intent intentPAX = new Intent(Operacion.this,CambiarPAX.class);
                    //intent_cupon.putExtra("cupon4",numero_cupon.substring(numero_cupon.length() - 4,numero_cupon.length()));
                    //intent_cupon.putExtra("cupon",numero_cupon);

                    intentPAX.putExtra("numAdultos",NumAdultos);
                    intentPAX.putExtra("numNinos",NumNinos);
                    intentPAX.putExtra("numInfantes",NumInfantes);
                    intentPAX.putExtra("numCupon",numero_cupon);
                    intentPAX.putExtra("idDetalleOpVehi",idDetalleOpVehi);

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
                    idDetalleOpVehi = data.get(info.position).idDetalleOpVehi;

                    Intent intent4 = new Intent(Operacion.this,AbordarSinCupon.class);
                    //intent4.putExtra("cupon4",numero_cupon.substring(numero_cupon.length() - 4,numero_cupon.length()));
                    //intent4.putExtra("cupon",numero_cupon);
                    intent4.putExtra("numAdultos",NumAdultos);
                    intent4.putExtra("numNinos",NumNinos);
                    intent4.putExtra("numInfantes",NumInfantes);
                    intent4.putExtra("numCupon",numero_cupon);
                    intent4.putExtra("idDetalleOpVehi",idDetalleOpVehi);

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

                case R.id.mniVerObservaciones:
                    builder = new AlertDialog.Builder(Operacion.this);
                    builder.setTitle("OBSERVACIONES");
                    builder.setMessage(data.get(info.position).Observaciones);
                    builder.setPositiveButton("Aceptar", null);
                    builder.show();
                    break;

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

    int WSidDetalleOpVehi;
    String WSfolioNoShow;
    String WSrecibeNoShow;
    String WSsincuponAutoriza;
    String WSobservacion;
    int WSa;
    int WSn;
    int WSi;
    String WScupon;
    int WOfflineID;


    public void MasterActualizar(View view){




        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            Cursor c = bd.rawQuery("select offlineID, idDetalleOpVehi, tipoSolicitud, status, folioNoShow, recibeNoShow, sincuponAutoriza, observacion, a, n, i, cupon from offline where habilitado = 1", null); //where Habilitado = 1
            Lista_Offline = null;
            Lista_Offline = new ArrayList<Entity_offline>();

            if (c != null ) {
                if  (c.moveToFirst()) {

                    do {
                        Entity_offline data_off = new Entity_offline();
                        data_off.idDetalleOpVehi = c.getInt(c.getColumnIndex("idDetalleOpVehi"));
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
                //Toast.makeText(this, Lista_Offline.get(i).idDetalleOpVehi + "" + Lista_Offline.get(i).tipoSolicitud, Toast.LENGTH_SHORT).show();
                switch (Lista_Offline.get(i).tipoSolicitud){
                    case 1:
                        WSidDetalleOpVehi = Lista_Offline.get(i).idDetalleOpVehi;
                        WSfolioNoShow = Lista_Offline.get(i).folioNoShow;
                        WSrecibeNoShow = Lista_Offline.get(i).recibeNoShow;
                        WSobservacion = Lista_Offline.get(i).observacion;
                        WOfflineID = Lista_Offline.get(i).offlineID;

                        TareaWSMarcarNoSwhow tareaWSMarcarNoSwhow = new TareaWSMarcarNoSwhow();
                        tareaWSMarcarNoSwhow.execute();
                        break;
                    case 2:
                        WSidDetalleOpVehi = Lista_Offline.get(i).idDetalleOpVehi;
                        WScupon = Lista_Offline.get(i).cupon;

                        TareaWSCambiarCupon tareaWSCambiarCupon = new TareaWSCambiarCupon();
                        tareaWSCambiarCupon.execute();
                        break;
                    case 3:

                        WSidDetalleOpVehi = Lista_Offline.get(i).idDetalleOpVehi;
                        WSa = Lista_Offline.get(i).a;
                        WSn = Lista_Offline.get(i).n;
                        WSi = Lista_Offline.get(i).i;

                        TareaWSCambiarPAX tareaWSCambiarPAX = new TareaWSCambiarPAX();
                        tareaWSCambiarPAX.execute();
                        break;
                    case 4:
                        WSidDetalleOpVehi = Lista_Offline.get(i).idDetalleOpVehi;
                        WSa = Lista_Offline.get(i).a;
                        WSn = Lista_Offline.get(i).n;
                        WSi = Lista_Offline.get(i).i;
                        WSsincuponAutoriza = Lista_Offline.get(i).sincuponAutoriza;
                        WSobservacion = Lista_Offline.get(i).observacion;

                        TareaWSAbordarSinCupon tareaWSAbordarSinCupon = new TareaWSAbordarSinCupon();
                        tareaWSAbordarSinCupon.execute();
                        break;
                    case 5:

                        WSidDetalleOpVehi = Lista_Offline.get(i).idDetalleOpVehi;
                        WSa = Lista_Offline.get(i).a;
                        WSn = Lista_Offline.get(i).n;
                        WSi = Lista_Offline.get(i).i;
                        WOfflineID = Lista_Offline.get(i).offlineID;

                        TareaWSAbordar tareaWSAbordar = new TareaWSAbordar();
                        tareaWSAbordar.execute();
                        break;
                    default: break;
                }

            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
            Toast.makeText(getApplicationContext(),"Sincronizando Encuestas",Toast.LENGTH_SHORT).show();
        sincroniza_encuesta();

    }



    public void CargaCuponesLocalesSearch(String cadena){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            Cursor c = bd.rawQuery("select idDetalleOpVehi, numCupon, Huesped, numAdultos, numNinos , numInfantes, Incentivos, Hotel, Habitacion, Idioma, PickUpLobby, nombreAgencia, nombreRepresentante, Observaciones, status from cupones where Habilitado = 1 and Hotel like '%"+ cadena+"%' order by (status = 13) desc, status", null); //where Habilitado = 1

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

            Cursor c = bd.rawQuery("select idDetalleOpVehi, numCupon, Huesped, numAdultos, numNinos , numInfantes, Incentivos, Hotel, Habitacion, Idioma, PickUpLobby, nombreAgencia, nombreRepresentante, Observaciones, status,color from cupones  order by (color='N') desc,color,  status", null);



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

                        datanum.idDetalleOpVehi = c.getInt(c.getColumnIndex("idDetalleOpVehi"));


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
            final String URL="http://sql2mobilesd.cloudapp.net/MyWebService/ServicioClientes.asmx";
            final String METHOD_NAME = "ObtenerCupones";
            final String SOAP_ACTION = "http://suarpe.com/ObtenerCupones";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idOpVehi", Long.parseLong(idOpVehi));

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
                    cuponesHoja.idDetalleOpVehi = Integer.parseInt((ic.getProperty(0).toString()));
                    cuponesHoja.numCupon = ic.getProperty(1).toString();
                    cuponesHoja.Huesped = ic.getProperty(2).toString();
                    cuponesHoja.numAdultos = Integer.parseInt((ic.getProperty(3).toString()));
                    cuponesHoja.numNinos = Integer.parseInt((ic.getProperty(4).toString()));
                    cuponesHoja.numInfantes = Integer.parseInt((ic.getProperty(5).toString()));
                    cuponesHoja.Incentivos = Integer.parseInt((ic.getProperty(6).toString()));
                    cuponesHoja.Hotel = ic.getProperty(7).toString();
                    cuponesHoja.Habitacion = ic.getProperty(8).toString();
                    cuponesHoja.Idioma = ic.getProperty(9).toString();
                    cuponesHoja.PickUpLobby = ic.getProperty(10).toString();
                    cuponesHoja.nombreAgencia = ic.getProperty(11).toString();
                    cuponesHoja.nombreRepresentante = ic.getProperty(12).toString();
                    cuponesHoja.Observaciones = ic.getProperty(13).toString();
                    cuponesHoja.status = Integer.parseInt((ic.getProperty(14).toString()));
                    cuponesHoja.tour_padre = Integer.parseInt((ic.getProperty(15).toString()));
                    cuponesHoja.idIdioma = Integer.parseInt((ic.getProperty(16).toString()));
                    cuponesHoja.color = ic.getProperty(17).toString();

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
                    alta_cupones(CuponesHoja[i].idDetalleOpVehi, CuponesHoja[i].numCupon, CuponesHoja[i].Huesped, CuponesHoja[i].numAdultos, CuponesHoja[i].numNinos, CuponesHoja[i].numInfantes, CuponesHoja[i].Incentivos, CuponesHoja[i].Hotel, CuponesHoja[i].Habitacion, CuponesHoja[i].Idioma, CuponesHoja[i].PickUpLobby, CuponesHoja[i].nombreAgencia, CuponesHoja[i].nombreRepresentante,CuponesHoja[i].Observaciones,  Boolean.TRUE, CuponesHoja[i].status,CuponesHoja[i].tour_padre,CuponesHoja[i].idIdioma,CuponesHoja[i].color);

                }
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

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL = "http://sql2mobilesd.cloudapp.net/MyWebService/ServicioClientes.asmx";
            final String METHOD_NAME = "Abordar";
            final String SOAP_ACTION = "http://suarpe.com/Abordar";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idDetalleOpVehi", WSidDetalleOpVehi);
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

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://sql2mobilesd.cloudapp.net/MyWebService/ServicioClientes.asmx";
            final String METHOD_NAME = "MarcarNoShow";
            final String SOAP_ACTION = "http://suarpe.com/MarcarNoShow";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("status", 11);
            request.addProperty("idDetalleOpVehi", WSidDetalleOpVehi);
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


        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://sql2mobilesd.cloudapp.net/MyWebService/ServicioClientes.asmx";
            final String METHOD_NAME = "CambiarCupon";
            final String SOAP_ACTION = "http://suarpe.com/CambiarCupon";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("numCupon", ""); //INUTIL
            request.addProperty("idDetalleOpVehi", WSidDetalleOpVehi);
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

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://sql2mobilesd.cloudapp.net/MyWebService/ServicioClientes.asmx";
            final String METHOD_NAME = "CambiarPAX";
            final String SOAP_ACTION = "http://suarpe.com/CambiarPAX";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idDetalleOpVehi", WSidDetalleOpVehi);
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

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://suarpe.com/";
            final String URL="http://sql2mobilesd.cloudapp.net/MyWebService/ServicioClientes.asmx";
            final String METHOD_NAME = "AbordarSinCupon";
            final String SOAP_ACTION = "http://suarpe.com/AbordarSinCupon";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idDetalleOpVehi", WSidDetalleOpVehi);
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

        String url = "jdbc:jtds:sqlserver://mobilesdSQLIT.cloudapp.net;instance=SQLEXPRESS;DatabaseName=GoNaturalV2";
        String driver = "net.sourceforge.jtds.jdbc.Driver";
        String userName = "usercs";
        String password = "";

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

    public void sincroniza_encuesta () {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();
        try {
            //Sincroniza encuestas
            Cursor c = bd.rawQuery("select idEncuestaDetalle,idDetalleOpVehi,idCuestionario,pregunta,valor_respuesta,fechaDetalle from encuestaDetalle where enviado = 0", null); //where Habilitado = 1
            Cursor cd = bd.rawQuery("select a.idDetalleOpVehi,a.idCupon,a.comentario,a.email,a.fecha,ifnull(b.hentrada,'00:00:00') hentrada,ifnull(b.hsalida,'00:00:00') hsalida  from encuesta a,cupones b where b.idDetalleOpVehi=a.idDetalleOpVehi and b.numCupon=a.idCupon ", null);

            if (c != null) {
                if (c.moveToFirst()) {
                    connection = conexion();
                    do {
                        int opVehi = c.getInt(c.getColumnIndex("idDetalleOpVehi"));
                        int cuestionario = c.getInt(c.getColumnIndex("idCuestionario"));
                        String pregunta = c.getString(c.getColumnIndex("pregunta"));
                        String respuesta=c.getString(c.getColumnIndex("valor_respuesta"));
                        String fecha = c.getString( c.getColumnIndex("fechaDetalle"));
                        int califica;
                        if(respuesta.length()>1){
                        califica=0;
                        }else{
                            califica=Integer.parseInt(respuesta);
                        }
                        String sql_inserta_pregunta = ("insert into OpVehiEncuesta (idDetalleOpVehi,idCuestionario,pregunta,respuesta,calificacion,fecha,status) " +
                                "values  ("+opVehi+","+cuestionario+",'"+pregunta+"','"+respuesta+"',"+califica+",'"+fecha+"',1)" );
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(sql_inserta_pregunta);


                        ContentValues vu = new ContentValues();
                        vu.put("enviado",1);
                        bd.update("encuestaDetalle", vu, "idEncuestaDetalle=" + c.getInt(c.getColumnIndex("idEncuestaDetalle")), null);

                    } while (c.moveToNext());

                    //Sincroniza encabezado encuesta
                   if( cd.moveToFirst()) {
                       do {

                           int opVehi = cd.getInt(cd.getColumnIndex("idDetalleOpVehi"));
                           int cupon = cd.getInt(cd.getColumnIndex("idCupon"));
                           String hora_entrada=cd.getString(cd.getColumnIndex("hentrada"));
                           String hora_salida=cd.getString(cd.getColumnIndex("hsalida"));
                           String comentario = cd.getString(cd.getColumnIndex("comentario"));
                           String email = cd.getString(cd.getColumnIndex("email"));
                           String fecha = cd.getString(cd.getColumnIndex("fecha"));

                           String upd_time_es = "update detalleOpVehi set pickUpIn='"+hora_entrada+"', pickUpOut='"+hora_salida+
                                   "' where  idDetalleOpVehi="+opVehi;

                           String sql_inserta_encabezado = "insert into OpVehiEncuestaEnc (idDetalleOpVehi,numCupon,comentario,email,fecha) " +
                                   "values  (" + opVehi + "," + cupon + ",'" + comentario + "','" + email + "','" + fecha + "')";
                           Statement statement = connection.createStatement();
                           statement.executeUpdate(sql_inserta_encabezado);
                           statement.executeUpdate(upd_time_es);

                       } while (cd.moveToNext());
                   }

                    connection.close();
                    Toast.makeText(getApplicationContext(),"Sincronizado...", Toast.LENGTH_LONG).show();
                    if (BorrarBDLocal()){
                        Intent intent_restart = new Intent(Operacion.this,EncuestaAgregarFolio.class);
                        startActivity(intent_restart);
                        SalirActividad();
                    }
                    else {
                        Toast.makeText(Operacion.this, "HUBO UN ERROR AL ELIMINAR LA OPERACIÓN DE SU DISPOSITIVO", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Nada que sincronizar...", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
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
        imgEncuesta.setEnabled(true);
        imgEncuesta.setVisibility(View.VISIBLE);
    }

    private void cabecera(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor v = bd.rawQuery("select idopveh, guia, camioneta, operador,obs from vehiculo", null);




        if (v != null ) {
            if (v.moveToFirst()) {
                lblorden.setText(Integer.toString(v.getInt(v.getColumnIndex("idopveh"))));
                lblguia.setText(v.getString(v.getColumnIndex("guia")));
                lbltrans.setText(v.getString(v.getColumnIndex("camioneta")));
                lbloperador.setText(v.getString(v.getColumnIndex("operador")));
                lblobs.setText(v.getString(v.getColumnIndex("obs")));
                //v.getString(v.getColumnIndex("operador"));
            }
        }
    }

}
