package com.example.mobilesdblack.ejemplo2;

/**
 * Created by MobileSD Black on 06/09/2016.
 */

import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Entity_OrdenServicio implements KvmSerializable {
    public String guia;
    public String vehiculo;
    public String transportadora;
    public int idGuia;
    public String chofer;
    public String obs;
    public String rfc;
    public String razon;
    public String dir;
    public String licencia;
    public String tour;

    //public int telefono;

    public Entity_OrdenServicio()
    {
        guia = "";
        vehiculo = "";
        transportadora = "";
        idGuia = 0;
        chofer = "";
        obs = "";
        rfc = "";
        razon = "";
        dir = "";
        licencia = "";
        tour = "";
        //telefono = 0;
    }

    @Override
    public Object getProperty(int arg0) {

        switch(arg0)
        {
            case 0:
                return guia;
            case 1:
                return vehiculo;
            case 2:
                return transportadora;
            case 3:
            return idGuia;
            case 4:
                return chofer;
            case 5:
                return obs;
            case 6:
                return rfc;
            case 7:
                return razon;
            case 8:
                return dir;
            case 9:
                return licencia;
            case 10:
                return tour;
            default:
                break;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 3;
    }

    @Override
    public void getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
        switch(ind)
        {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Guia";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Vehiculo";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Transportadora";
                break;
            case 3:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "idGuia";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "chofer";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "obs";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "rfc";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "razon";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "dir";
                break;
            /*
            case 2:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "Telefono";
                break;
            */
            default:break;
        }
    }

    @Override
    public void setProperty(int ind, Object val) {
        switch(ind)
        {
            case 0:
                guia = val.toString();
                break;
            case 1:
                vehiculo = val.toString();
                break;
            case 2:
                transportadora = val.toString();
                break;
            case 3:
                idGuia = Integer.parseInt(val.toString());
                break;
            case 4:
                chofer = val.toString();
                break;
            case 5:
                obs = val.toString();
                break;
            case 6:
                rfc = val.toString();
                break;
            case 7:
                razon = val.toString();
                break;
            case 8:
                dir = val.toString();
                break;
            case 9:
                licencia = val.toString();
                break;
            case 10:
                tour = val.toString();
                break;
            /*
            case 2:
                telefono = Integer.parseInt(val.toString());
                break;
            */
            default:
                break;
        }
    }
}