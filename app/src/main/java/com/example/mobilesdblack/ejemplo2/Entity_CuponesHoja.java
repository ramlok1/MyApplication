package com.example.mobilesdblack.ejemplo2;

/**
 * Created by MobileSD Black on 08/09/2016.
 */

import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Entity_CuponesHoja implements KvmSerializable {

    public int idReservaDetalle;
    public int idOpVehi;
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
    public String apoyo;


    public Entity_CuponesHoja()
    {
        idReservaDetalle = 0;
        idOpVehi = 0;
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
        apoyo = "";

    }

    public Entity_CuponesHoja(int idReservaDetalle,int idOpVehi,int iddetalleopvehi, String numcupon, String huesped, int numadultos, int numniños, int numinfantes, int incentivos, String hotel, String habitacion, String idioma, String pickuplobby, String nombreagencia, String nombrerepresentante, String observaciones, int status,int tour_padre, int idIdioma, String color, String apoyo)
    {
        this.idReservaDetalle = idReservaDetalle;
        this.idOpVehi = idOpVehi;
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
        this.apoyo = apoyo;

    }

    @Override
    public Object getProperty(int arg0) {

        switch(arg0)
        {
            case 0:
                return idReservaDetalle;
            case 1:
                return idOpVehi;
            case 2:
                return idDetalleOpVehi;
            case 3:
                return numCupon;
            case 4:
                return Huesped;
            case 5:
                return numAdultos;
            case 6:
                return numNinos;
            case 7:
                return numInfantes;
            case 8:
                return Incentivos;
            case 9:
                return Hotel;
            case 10:
                return Habitacion;
            case 11:
                return Idioma;
            case 12:
                return PickUpLobby;
            case 13:
                return nombreAgencia;
            case 14:
                return nombreRepresentante;
            case 15:
                return Observaciones;
            case 16:
                return status;
            case 17:
            return tour_padre;
            case 18:
                return idIdioma;
            case 19:
            return color;
            case 20:
                return apoyo;




        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 20;
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
            case 18:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "apoyo";
                break;

            default:break;
        }
    }

    @Override
    public void setProperty(int ind, Object val) {
        switch(ind)
        {
            case 0:
                idReservaDetalle = Integer.parseInt(val.toString());
                break;
            case 1:
                idOpVehi = Integer.parseInt(val.toString());
                break;
            case 2:
                idDetalleOpVehi = Integer.parseInt(val.toString());
                break;
            case 3:
                numCupon = val.toString();
                break;
            case 4:
                Huesped = val.toString();
                break;
            case 5:
                numAdultos = Integer.parseInt(val.toString());
                break;
            case 6:
                numNinos = Integer.parseInt(val.toString());
                break;
            case 7:
                numInfantes = Integer.parseInt(val.toString());
                break;
            case 8:
                Incentivos = Integer.parseInt(val.toString());
                break;
            case 9:
                Hotel = val.toString();
                break;
            case 10:
                Habitacion = val.toString();
                break;
            case 11:
                Idioma = val.toString();
                break;
            case 12:
                PickUpLobby = val.toString();
                break;
            case 13:
                nombreAgencia = val.toString();
                break;
            case 14:
                nombreRepresentante = val.toString();
                break;
            case 15:
                Observaciones = val.toString();
                break;
            case 16:
                status = Integer.parseInt(val.toString());
                break;
            case 17:
                tour_padre = Integer.parseInt(val.toString());
                break;
            case 18:
                idIdioma = Integer.parseInt(val.toString());
                break;
            case 19:
                color = val.toString();
                break;
            case 20:
                apoyo = val.toString();
                break;

            default:break;
        }
    }
}
