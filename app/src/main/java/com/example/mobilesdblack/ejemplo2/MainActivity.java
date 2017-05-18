package com.example.mobilesdblack.ejemplo2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView Lista;
    Button Ejecutar;
    EditText txtEmail;
    TextView txtNombre, txtHotel;

    private Integer versionBD = 5;

    private ArrayAdapter<Valoracion> adapter;
    private ArrayList<Valoracion> arrayList;

    private void inicializar() {
        Lista = (ListView) findViewById(R.id.list_opcion);
        Ejecutar = (Button)findViewById(R.id.btn_button);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtEmail.requestFocus();
        txtNombre = (TextView)findViewById(R.id.txtNombre);
        txtHotel = (TextView)findViewById(R.id.txtHotel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializar();

        //txtNombre.setText(getIntent().getExtras().getString("Huesped"));
        //txtHotel.setText(getIntent().getExtras().getString("Hotel"));

        Ejecutar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EncuestaActivity.class);
                startActivity(intent);
            }
        });


        CargaPreguntasLocales();

        adapter = new ListViewAdapter(this, R.layout.modelo, arrayList);
        Lista.setAdapter(adapter);
        //Lista.setOnItemClickListener(onItemClickListener());

    }

    private AdapterView.OnItemClickListener onItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.activity_main);
                dialog.setTitle("Movie details");

                TextView name = (TextView) dialog.findViewById(R.id.txtNombre);
                //TextView country = (TextView) dialog.findViewById(R.id.txtHotel);
                TextView starRate = (TextView) dialog.findViewById(R.id.txtHotel);

                Valoracion movie = (Valoracion) parent.getAdapter().getItem(position);
                name.setText("Movie name: " + movie.getName());
                //
                // .setText("Producing country: " + movie.getCountry());
                starRate.setText("Your rate: " + movie.getRatingStar());

                dialog.show();
            }
        };
    }


    public void CargaPreguntasLocales() {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "cuestionarios", null, versionBD);

        SQLiteDatabase bd = admin.getWritableDatabase();

        arrayList = new ArrayList<>();

        Cursor c = bd.rawQuery("select pregunta, tipo from cuestionarios ", null);

        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    //Toast.makeText(this, c.getString(c.getColumnIndex("pregunta")), Toast.LENGTH_SHORT).show();
                    arrayList.add(new Valoracion(0, c.getString(c.getColumnIndex("pregunta")), c.getString(c.getColumnIndex("pregunta"))));
                }while (c.moveToNext());
                Toast.makeText(this, "Se llena el arraylist :D", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "No existen encuestas cargadas localmente", Toast.LENGTH_SHORT).show();
            }
        }

        c.close();

        bd.close();

        //cuestionarios(idCuestionario bigint ,pregunta text, tipo int, idIdioma int)

    }

}
