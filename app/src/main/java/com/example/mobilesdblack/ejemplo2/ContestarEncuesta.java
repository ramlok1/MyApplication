package com.example.mobilesdblack.ejemplo2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContestarEncuesta extends AppCompatActivity {

    PlaceholderFragment fragment_obj;

    FragmentParent fragmentParent;

    String numCupon = "";
    ViewPager viewPager;
    SignatureView signatureView;
    TextView txtView;
    Button btnFinalizaEncuesta;
    View viex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contestar_encuesta);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);


        Bundle b = getIntent().getExtras();
        numCupon = b.getString("numCupon");

        getIDs();

        Busca_categoria(variables_publicas.idioma,variables_publicas.tour_padre);
        fragment_obj = (PlaceholderFragment)getSupportFragmentManager().findFragmentById(R.id.idFinalizar);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.homeb);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_cupon = new Intent(ContestarEncuesta.this,SeleccionarCupon.class);
                intent_cupon.putExtra("idOpVehi",Integer.toString(variables_publicas.id_op_vehi));
                startActivity(intent_cupon);
            }
        });

        focusOnFirst();
    }

    public void EventoFinalizarEncuesta(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, variables_publicas.version_local_database);
        SQLiteDatabase bd = admin.getWritableDatabase();
        viex = fragmentParent.ObtenerVistaActual(fragmentParent.posicion);
        signatureView =  (SignatureView) viex.findViewById(R.id.signature_view);
        btnFinalizaEncuesta = (Button)viex.findViewById(R.id.btnFinalizarEncuesta);


            try {
                byte[] firma_imp =revisa_firma();
                if(firma_imp.length>2000) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    ContentValues cv = new ContentValues();
                    cv.put("idReservaDetalle", variables_publicas.idReservaDetalle);
                    cv.put("idCupon", variables_publicas.numcupon);
                    cv.put("comentario", "");
                    cv.put("email", variables_publicas.email);
                    cv.put("pais", variables_publicas.pais);
                    cv.put("estado", variables_publicas.estado);
                    cv.put("tel", variables_publicas.tel);
                    cv.put("fecha", dateFormat.format(date));
                    cv.put("firma", firma_imp);
                    cv.put("enviado", 0);
                    signatureView.clearCanvas();
                    bd.insert("encuesta", null, cv);

                    Intent intent = new Intent(ContestarEncuesta.this, Confirmacion.class);
                    intent.putExtra("numCupon", numCupon);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Signature empty.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                String test =e.getMessage();

            }



    }


    public void limpia_firma(View view){
        viex = fragmentParent.ObtenerVistaActual(fragmentParent.posicion);
        signatureView =  (SignatureView) viex.findViewById(R.id.signature_view);
        signatureView.clearCanvas();
    }

    public void ObtenerID(View view){

        txtView = (TextView)view.findViewById(R.id.textView13);

        btnFinalizaEncuesta = (Button)view.findViewById(R.id.btnFinalizarEncuesta);
    }

    private void focusOnFirst(){
        fragmentParent.focusFirst();
    }

    private void getIDs() {
        fragmentParent = (FragmentParent) this.getSupportFragmentManager().findFragmentById(R.id.fragmentParent);
    }

    private void InsertaFragment(String fragmentname, int tipo){

            fragmentParent.addPage(fragmentname, tipo);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Toast.makeText(this, id + "", Toast.LENGTH_SHORT).show();

        //noinsection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
    }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment{

        TextView textView13;
        Button finalizarEncuesta;

        public PlaceholderFragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_finalizar, container, false);

            textView13 = (TextView)rootView.findViewById(R.id.textView13);

            finalizarEncuesta = (Button)rootView.findViewById(R.id.btnFinalizarEncuesta);

            return rootView;
        }

        public void Prueba(int posicion){
            switch (posicion) {
                case 1: finalizarEncuesta.setText(":'v");
                break;
            }

        }


    }

    public void Busca_categoria(int idioma, int tour_padre){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, variables_publicas.version_local_database);
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            Cursor c = bd.rawQuery("select distinct idCategoriaPregunta ,nombreCategoriaPregunta from cuestionarios where idIdioma="+idioma+" and tour_padre="+tour_padre+" order by orden_categoria", null); //where Habilitado = 1

                    if (c != null ) {
                if  (c.moveToFirst()) {
                    InsertaFragment("Email",0);
                    do {

                        InsertaFragment(c.getString(c.getColumnIndex("nombreCategoriaPregunta")),c.getInt(c.getColumnIndex("idCategoriaPregunta")));

                    }while (c.moveToNext());
                    //Toast.makeText(this,"Cupones cargados localmente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "No existen más cupones pendientes", Toast.LENGTH_SHORT).show();
                }
                        InsertaFragment("Final",-1);
            }

            c.close();
            bd.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private byte[] revisa_firma(){

        Bitmap bmp= signatureView.getSignatureBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();


        return byteArray;
    }



}
