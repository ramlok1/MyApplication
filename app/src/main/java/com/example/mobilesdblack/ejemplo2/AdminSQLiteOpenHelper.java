package com.example.mobilesdblack.ejemplo2;

/**
 * Created by MobileSD Black on 08/08/2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;



public class AdminSQLiteOpenHelper extends SQLiteOpenHelper{

    public AdminSQLiteOpenHelper(Context context, String nombre, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, nombre, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //aquí creamos la tabla de usuario (dni, nombre, ciudad, numero)

        db.execSQL("create table cuestionarios(idCuestionarios integer ,idCategoriaPregunta integer ,nombreCategoriaPregunta text,pregunta text, tipo integer, idIdioma integer, orden_categoria integer,orden_pregunta integer,tour_padre integer )");

        db.execSQL("create table vehiculo (idopveh integer,guia text, camioneta text, operador text,entrada datetime, salida datetime, obs text)");

        db.execSQL("create table categoria_respuesta (idCuestionario integer, respuesta text, orden integer)");

        db.execSQL("create table encuestaMensaje (titulo text, mensaje text, idioma integer)");

        db.execSQL("create table encuesta (idEncuesta integer primary key,idDetalleOpVehi integer, idCupon text, comentario text, email text, fecha datetime, firma blob)");

        db.execSQL("create table cupones(idDetalleOpVehi integer, numCupon text, Huesped text, numAdultos integer, numNinos integer, numInfantes integer, Incentivos integer, Hotel text, Habitacion text, Idioma text, PickUpLobby text, nombreAgencia text, nombreRepresentante text, Observaciones text, Habilitado bit, status integer,tour_padre integer, ididioma integer, hentrada datetime,hsalida datetime,color text )");

        db.execSQL("create table encuestaDetalle(idEncuestaDetalle integer primary key, idDetalleOpVehi integer,idCupon integer, idCuestionario integer, pregunta text, valor_respuesta text,  fechaDetalle datetime, enviado bit,email text)");

        db.execSQL("create table offline(offlineID integer PRIMARY KEY, idDetalleOpVehi integer, tipoSolicitud integer, status integer, folioNoShow text, recibeNoShow text, sincuponAutoriza text, observacion text , a integer, n integer, i integer, habilitado boolean, cupon text )");

    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int version1, int version2) {

        db.execSQL("drop table if exists vehiculo");
        db.execSQL("drop table if exists cuestionarios");
        db.execSQL("drop table if exists categoria_respuesta");
        db.execSQL("drop table if exists encuesta");
        db.execSQL("drop table if exists cupones");
        db.execSQL("drop table if exists encuestaDetalle");
        db.execSQL("drop table if exists offline");
        onCreate(db);


    }

}
