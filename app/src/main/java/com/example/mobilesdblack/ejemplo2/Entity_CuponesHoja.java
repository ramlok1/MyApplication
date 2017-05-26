package com.example.mobilesdblack.ejemplo2;

/**
 * Created by MobileSD Black on 08/09/2016.
 */

import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Entity_CuponesHoja implements KvmSerializable {

    public int idDetalleOpVehi;
    public String numCupon;
    public String Huesped;
    public int numAdultos;
    public int numNinos;
    public int numInfantes;
    public int Incentivos;
    public String Hotel;
    public String Habitacion;
    public String Idioma;
    public String PickUpLobby;
    public String nombreAgencia;
    public String nombreRepresentante;
    public String Observaciones;
    public int status;
    public int tour_padre;
    public int idIdioma;
    public String color;


    public Entity_CuponesHoja()
    {
        idDetalleOpVehi = 0;
        numCupon = "";
        Huesped = "";
        numAdultos = 0;
        numNinos = 0;
        numInfantes = 0;
        Incentivos = 0;
        Hotel = "";
        Habitacion = "";
        Idioma = "";
        PickUpLobby = "";
        nombreAgencia = "";
        nombreRepresentante = "";
        Observaciones = "";
        status = 0;
        tour_padre = 0;
        idIdioma = 0;
        color = "";

    }

    public Entity_CuponesHoja(int iddetalleopvehi, String numcupon, String huesped, int numadultos, int numniños, int numinfantes, int incentivos, String hotel, String habitacion, String idioma, String pickuplobby, String nombreagencia, String nombrerepresentante, String observaciones, int status,int tour_padre, int idIdioma, String color)
    {
        this.idDetalleOpVehi = iddetalleopvehi;
        this.numCupon = numcupon;
        this.Huesped = huesped;
        this.numAdultos = numadultos;
        this.numNinos = numniños;
        this.numInfantes = numinfantes;
        this.Incentivos = incentivos;
        this.Hotel = hotel;
        this.Habitacion = habitacion;
        this.Idioma = idioma;
        this.PickUpLobby = pickuplobby;
        this.nombreAgencia = nombreagencia;
        this.nombreRepresentante = nombrerepresentante;
        this.Observaciones = observaciones;
        this.status = status;
        this.tour_padre = tour_padre;
        this.idIdioma = idIdioma;
        this.color = color;

    }

    @Override
    public Object getProperty(int arg0) {

        switch(arg0)
        {
            case 0:
                return idDetalleOpVehi;
            case 1:
                return numCupon;
            case 2:
                return Huesped;
            case 3:
                return numAdultos;
            case 4:
                return numNinos;
            case 5:
                return numInfantes;
            case 6:
                return Incentivos;
            case 7:
                return Hotel;
            case 8:
                return Habitacion;
            case 9:
                return Idioma;
            case 10:
                return PickUpLobby;
            case 11:
                return nombreAgencia;
            case 12:
                return nombreRepresentante;
            case 13:
                return Observaciones;
            case 14:
                return status;
            case 15:
            return tour_padre;
            case 16:
                return idIdioma;
            case 17:
                return color;


        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 15;
    }


    @Override
    public void getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
        switch(ind)
        {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "IdDetalleOpVehi";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NumeroCupon";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Huesped";
                break;
            case 3:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "NumeroAdultos";
                break;
            case 4:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "NumeroNiños";
                break;
            case 5:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "NumeroInfantes";
                break;
            case 6:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "Incentivos";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Hotel";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Habitacion";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Idioma";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PickUpLobby";
                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NombreAgencia";
                break;
            case 12:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NombreRepresentante";
                break;
            case 13:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Observaciones";
                break;
            case 14:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "status";
                break;
            case 15:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "tour_padre";
                break;
            case 16:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "idIdioma";
                break;
            case 17:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "color";
                break;

            default:break;
        }
    }

    @Override
    public void setProperty(int ind, Object val) {
        switch(ind)
        {
            case 0:
                idDetalleOpVehi = Integer.parseInt(val.toString());
                break;
            case 1:
                numCupon = val.toString();
                break;
            case 2:
                Huesped = val.toString();
                break;
            case 3:
                numAdultos = Integer.parseInt(val.toString());
                break;
            case 4:
                numNinos = Integer.parseInt(val.toString());
                break;
            case 5:
                numInfantes = Integer.parseInt(val.toString());
                break;
            case 6:
                Incentivos = Integer.parseInt(val.toString());
                break;
            case 7:
                Hotel = val.toString();
                break;
            case 8:
                Habitacion = val.toString();
                break;
            case 9:
                Idioma = val.toString();
                break;
            case 10:
                PickUpLobby = val.toString();
                break;
            case 11:
                nombreAgencia = val.toString();
                break;
            case 12:
                nombreRepresentante = val.toString();
                break;
            case 13:
                Observaciones = val.toString();
                break;
            case 14:
                status = Integer.parseInt(val.toString());
                break;
            case 15:
                tour_padre = Integer.parseInt(val.toString());
                break;
            case 16:
                idIdioma = Integer.parseInt(val.toString());
                break;
            case 17:
                color = val.toString();
                break;

            default:break;
        }
    }
}
