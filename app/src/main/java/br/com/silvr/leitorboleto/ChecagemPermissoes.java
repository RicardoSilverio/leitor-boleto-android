package br.com.silvr.leitorboleto;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import br.com.silvr.leitorboleto.model.Constantes;

class ChecagemPermissoes {

    private ActivityGerenciada activity;
    private boolean temPermissaoCamera;
    private boolean apiDisponivel;

    public ChecagemPermissoes(ActivityGerenciada activity) {
        this.activity = activity;
    }

    public boolean verificarDepedenciasPermissoes() {

        if(!temPermissaoCamera) {
            temPermissaoCamera = verificarPermissaoCamera();
            if(!temPermissaoCamera) {
                solicitarPermissaoCamera();
                return false;
            }
        }

        if(!apiDisponivel) {
            apiDisponivel = checarGooglePlayServices();
            if(!apiDisponivel) {
                solicitarGooglePlayServices();
                return false;
            }
        }

        return true;

    }

    public boolean permissoesSatisfeitas() {
        temPermissaoCamera = verificarPermissaoCamera();
        apiDisponivel = checarGooglePlayServices();
        return temPermissaoCamera && apiDisponivel;
    }

    public void receberResultadosPermissoes(int requestCode, String[] permissions, int[] grantResults) {

        if(requestCode == Constantes.COD_PERMISSAO_CAMERA && grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            temPermissaoCamera = true;

        } else {
            Toast.makeText(activity.getContext(), R.string.msg_permissao_camera, Toast.LENGTH_LONG).show();
        }
    }

    private boolean verificarPermissaoCamera() {

        int resultado = ActivityCompat.checkSelfPermission(activity.getContext(), Manifest.permission.CAMERA);
        if(resultado == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    public void solicitarPermissaoCamera() {
        ActivityCompat.requestPermissions(activity.getContext(), new String[]{Manifest.permission.CAMERA},
                Constantes.COD_PERMISSAO_CAMERA);
    }

    public void alertaArmazenamentoInsuficiente() {

        IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
        boolean armazenamentoInsuficiente = activity.getContext().registerReceiver(null, lowstorageFilter) != null;

        if (armazenamentoInsuficiente) {
            Toast.makeText(activity.getContext(), R.string.msg_armazenamento_baixo, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checarGooglePlayServices() {

        int resultado = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                activity.getContext().getApplicationContext());

        if (resultado != ConnectionResult.SUCCESS) {
            return false;

        } else {
            return true;
        }
    }

    public void solicitarGooglePlayServices() {

        int resultado = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                activity.getContext().getApplicationContext());

        Dialog dialogPlayServices = GoogleApiAvailability.getInstance().getErrorDialog(activity.getContext(),
                resultado, Constantes.COD_DISPONIBILIDADE_PLAY_SERVICES);
        dialogPlayServices.show();
    }

    public void removerReferencias() {
        activity = null;
    }

}
