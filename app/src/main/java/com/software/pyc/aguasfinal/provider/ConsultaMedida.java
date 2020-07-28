package com.software.pyc.aguasfinal.provider;


/**
 * Created by pablo on 2/8/2018.
 * Clase que guarda las opciones del where y el orderby
 */

public class ConsultaMedida {
    String where;
    String orderBy;
    String limite;
    String orderByMetodo="desc";
    String ruta;

    Boolean next = false;

    double paginas;

    int pagActual=1;

    int ini = 1;
    int fin = 21;

    int canMostrar = 20;

    int offset = ini;

    private static ConsultaMedida singleton;


    public void addWhereAnd(String w){
        this.where = this.ruta+" and "+w;
        pagActual=1;
    }

    public void addOrderBy(String o){
        this.orderBy = o;
    }

    public ConsultaMedida() {
        this.where = null;
        this.orderBy = null;
        this.limite = String.valueOf(offset)+","+String.valueOf(canMostrar);
        setPaginas();
    }

    public ConsultaMedida(String where) {
        this.where = where;
    }

    public ConsultaMedida(String where, String orderBy) {
        this.where = where;
        setOrderBy(orderBy);
    }

    public static synchronized ConsultaMedida getInstance() {
        if (singleton == null) {
            singleton = new ConsultaMedida();
        }
        return singleton;
    }

    public Boolean nextPg(){
        Boolean respuesta = false;
        next = true;
        int cant = canMostrar;
        // if (offset < fin-canMostrar) {
                  if (pagActual < paginas) {
/*                      if (pagActual == paginas-1) {
                          canMostrar = canMostrar+canMostrar;
                      }*/

            this.setOffset((this.getOffset() + cant));

            pagActual ++;
            respuesta  = true;
        }
        next = false;
        return respuesta;
    }

    public Boolean previusPg(){
        Boolean respuesta = false;
        next = false;
        int cant = canMostrar;
        //   if (offset > canMostrar) {
                if (pagActual > 1) {
/*                    if (pagActual == paginas-1) {
                        canMostrar = canMostrar-canMostrar;
                    }*/

            this.setOffset(this.getOffset() - cant);

            pagActual --;
            respuesta  = true;
        }
        return respuesta;
    }

    public double getPaginas() {
        return paginas;
    }

    public void setPaginas() {
        this.paginas =  (Math.ceil(fin/canMostrar));

    }

    public int getPagActual() {
        return pagActual;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
        this.where = ruta;
        next=false;
        setOffset(ini);
        pagActual=1;
    }

    public String getOrderByMetodo() {
        return orderByMetodo;

    }

    public void setOrderByMetodo(String orderByMetodo) {
        this.orderByMetodo = orderByMetodo;
        next=false;
        setOffset(ini);
        pagActual=1;

    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
        next=false;
        setOffset(ini);
        pagActual=1;
    }

    public String getLimite() {
        return limite;
    }

    public void setLimite(String limite) {

        this.limite = limite;
    }

    public int getIni() {
        return ini;
    }

    public void setIni(int ini) {
        this.ini = ini;
    }

    public int getFin() {
        return fin;
    }

    public void setFin(int fin) {
        this.fin = fin;
        setPaginas();
    }

    public int getCanMostrar() {
        return canMostrar;
    }

    public void setCanMostrar(int canMostrar) {
        this.canMostrar = canMostrar;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int os) {
        this.offset = os;
        if (next){os = (os-2);}
        if (pagActual==paginas){
            this.limite = String.valueOf(os)+","+String.valueOf(canMostrar*2);
        }else {
            this.limite = String.valueOf(os) + "," + String.valueOf(canMostrar);
        }
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy+" "+this.getOrderByMetodo();
        next=false;
        setOffset(ini);
        pagActual=1;

    }
}
