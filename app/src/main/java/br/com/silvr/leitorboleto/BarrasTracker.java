package br.com.silvr.leitorboleto;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by silvr on 05/07/17.
 */

public class BarrasTracker extends Tracker<Barcode> {

    private NovaBarraListener listener;

    public BarrasTracker(NovaBarraListener listener) {
        this.listener = listener;
    }

    @Override
    public void onNewItem(int id, Barcode barcode) {
        listener.novaBarraDetectada(new BarrasHolder(id, barcode));
    }
}
