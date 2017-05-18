package com.example.mobilesdblack.ejemplo2;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by MobileSD Black on 11/10/2016.
 */

public class Entity_offline implements KvmSerializable {

    public int idDetalleOpVehi;
    public int tipoSolicitud;
    public int status;
    public String folioNoShow;
    public String recibeNoShow;
    public String sincuponAutoriza;
    public String observacion;
    public int a;
    public int n;
    public int i;
    public String cupon;
    public int offlineID;

    public Entity_offline()
    {

        idDetalleOpVehi = 0;
        tipoSolicitud = 0;
        status = 0;
        folioNoShow = "";
        recibeNoShow = "";
        sincuponAutoriza = "";
        observacion = "";
        a = 0;
        n = 0;
        i = 0;
        cupon = "";
        offlineID = 0;
    }

    public Entity_offline(int idDetalleOpVehi, int tipoSolicitud, int status, String folioNoShow, String recibeNoShow, String sincuponAutoriza, String observacion, int a, int n, int i, String cupon, int offlineID )
    {

        this.idDetalleOpVehi = idDetalleOpVehi;
        this.tipoSolicitud = tipoSolicitud;
        this.status = status;
        this.folioNoShow = folioNoShow;
        this.recibeNoShow = recibeNoShow;
        this.sincuponAutoriza = sincuponAutoriza;
        this.observacion = observacion;
        this.a = a;
        this.n = n;
        this.i = i;
        this.cupon = cupon;
        this.offlineID = offlineID;
    }

    @Override
    public Object getProperty(int arg0) {

        switch(arg0)
        {
            case 0:
                return idDetalleOpVehi;
            case 1:
                return tipoSolicitud;
            case 2:
                return status;
            case 3:
                return folioNoShow;
            case 4:
                return recibeNoShow;
            case 5:
                return sincuponAutoriza;
            case 6:
                return observacion;
            case 7:
                return a;
            case 8:
                return n;
            case 9:
                return i;
            case 10:
                return cupon;
            case 11:
                return offlineID;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 12;
    }


    @Override
    public void getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
        switch(ind)
        {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "idDetalleOpVehi";
                break;
            case 1:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "tipoSolicitud";
                break;
            case 2:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "status";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "folioNoShow";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "recibeNoShow";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "sincuponAutoriza";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "observacion";
                break;
            case 7:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "a";
                break;
            case 8:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "n";
                break;
            case 9:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "i";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "cupon";
                break;
            case 11:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "offlineID";
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
                tipoSolicitud = Integer.parseInt(val.toString());
                break;
            case 2:
                status = Integer.parseInt(val.toString());
                break;
            case 3:
                folioNoShow = val.toString();
                break;
            case 4:
                recibeNoShow = val.toString();
                break;
            case 5:
                sincuponAutoriza = val.toString();
                break;
            case 6:
                observacion = val.toString();
                break;
            case 7:
                a = Integer.parseInt(val.toString());
                break;
            case 8:
                n = Integer.parseInt(val.toString());
                break;
            case 9:
                i = Integer.parseInt(val.toString());
                break;
            case 10:
                cupon = val.toString();
                break;
            case 11:
                offlineID = Integer.parseInt(val.toString());
                break;

            default:break;
        }
    }

}
