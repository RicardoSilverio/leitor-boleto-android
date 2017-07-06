package br.com.silvr.leitorboleto;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by silvr on 05/07/17.
 */

public class BarrasTrackerFactory implements MultiProcessor.Factory<Barcode> {

    private NovaBarraListener listener;

    public BarrasTrackerFactory(NovaBarraListener listener) {
        this.listener = listener;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        return new BarrasTracker(listener);
    }
}
