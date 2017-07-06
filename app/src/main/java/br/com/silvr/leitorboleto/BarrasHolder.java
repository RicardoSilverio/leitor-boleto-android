package br.com.silvr.leitorboleto;

import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by silvr on 05/07/17.
 */

public class BarrasHolder {

    private int id;
    private Barcode barras;

    public BarrasHolder(int id, Barcode barras) {
        this.id = id;
        this.barras = barras;
    }

    public int getId() {
        return id;
    }

    public Barcode getBarras() {
        return barras;
    }

}
