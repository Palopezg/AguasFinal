package com.software.pyc.aguasfinal.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by pablo on 14/10/2018.
 * Clase para generar los logs.
 */

public class LogMedida {

    public static void grabaLog(String fileName,String mensajeLog, Context context) {
        Date d = Calendar.getInstance().getTime();
        mensajeLog = d+" - "+mensajeLog;

        File ruta_sd = Environment.getExternalStoragePublicDirectory("Aguas");
        ruta_sd.mkdirs();
        ruta_sd.setReadable(true,false);

        File f = new File(ruta_sd.getAbsolutePath(), fileName);
        f.setReadable(false,false);
        f.setReadable(true,false);

        try
        {

            //f.getParentFile().mkdirs();
            OutputStreamWriter fout=
                    new OutputStreamWriter(
                            new FileOutputStream(f, true));

            //fout.append(mensajeLog);
            fout.write(mensajeLog);
            fout.append("\r\n");
            fout.flush();
            fout.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria externa "+ex.toString());
        }

/*        try
        {

            OutputStreamWriter fout=
                    new OutputStreamWriter(
                            context.openFileOutput(fileName, Context.MODE_APPEND));

            //fout.append(mensajeLog);
            fout.write(mensajeLog);
            fout.append("\r\n");
            fout.flush();
            fout.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }*/






    }

    public static boolean copiaBD() {
        Date d = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
        String formattedDate = df.format(d);
        File ruta_sd = Environment.getExternalStoragePublicDirectory("Aguas");



        String DBName = "aguas.db";

        String from = Constantes.PATH_APP+DBName;
        String to=Constantes.PATH_LOG+DBName+"-"+formattedDate;
        boolean result = false;
        try{
            File dir = new File(to.substring(0, to.lastIndexOf('/')));
            dir.mkdirs();
            File tof = new File(ruta_sd.getAbsolutePath(),DBName+"-"+formattedDate+".db");
            tof.setReadable(true,false);

//            File tof = new File(dir, to.substring(to.lastIndexOf('/')));
            int byteread;
            File oldfile = new File(from);
            if(oldfile.exists()){
                InputStream inStream = new FileInputStream(from);
                FileOutputStream fs = new FileOutputStream(tof);
                byte[] buffer = new byte[1024];
                while((byteread = inStream.read(buffer)) != -1){
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
            result = true;
        }catch (Exception e){
            Log.e("copyFile", "Error copiando archivo: " + e.getMessage());
        }
        try {

            File log = new File(ruta_sd.getAbsolutePath(),Constantes.LOG_CARGA);
            File logBk = new File(ruta_sd.getAbsolutePath(),Constantes.LOG_CARGA+"-"+formattedDate+".txt");
            log.renameTo(logBk);
            File logBaja = new File(ruta_sd.getAbsolutePath(),Constantes.LOG_BAJA_TABLA);
            File logBajaBk = new File(ruta_sd.getAbsolutePath(),Constantes.LOG_BAJA_TABLA+"-"+formattedDate+".txt");
            logBaja.renameTo(logBajaBk);
            File logSubir = new File(ruta_sd.getAbsolutePath(),Constantes.LOG_SUBIR_TABLA);
            File logSubirBk = new File(ruta_sd.getAbsolutePath(),Constantes.LOG_SUBIR_TABLA+"-"+formattedDate+".txt");
            logSubir.renameTo(logSubirBk);
        }catch (Exception e){
            Log.e("copyFile", "Error borrando el LogMedida " + e.getMessage());
        }

        return result;
    }
}

