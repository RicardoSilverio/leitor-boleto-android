package br.com.silvr.leitorboleto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import br.com.silvr.leitorboleto.model.Boleto;
import br.com.silvr.leitorboleto.model.BoletoFactory;
import br.com.silvr.leitorboleto.model.CodigoBarras;
import br.com.silvr.leitorboleto.model.Constantes;
import br.com.silvr.leitorboleto.model.Formatador;

public class CapturaBarrasActivity extends AppCompatActivity implements ActivityGerenciada, NovaBarraListener {

    private CameraPreview cameraPreview;
    private CameraSource cameraSource;
    private ChecagemPermissoes checagemPermissoes;
    private boolean tarefaIniciada;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturabarras);

        cameraPreview = (CameraPreview) findViewById(R.id.preview);
        cameraPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tarefaIniciada) {
                    if(checagemPermissoes.verificarDepedenciasPermissoes()) {
                        iniciarTarefa();
                    }
                }
            }
        });

        checagemPermissoes = new ChecagemPermissoes(this);
        if(checagemPermissoes.verificarDepedenciasPermissoes()) {
            iniciarTarefa();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checagemPermissoes.receberResultadosPermissoes(requestCode, permissions, grantResults);
        if(checagemPermissoes.permissoesSatisfeitas()) {
            iniciarTarefa();
        }
    }

    public void iniciarTarefa() {
        criarCameraSource();
        tarefaIniciada = true;
    }

    private void criarCameraSource() {

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        BarrasTrackerFactory factory = new BarrasTrackerFactory(this);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(factory).build());

        if (!barcodeDetector.isOperational()) {
            checagemPermissoes.alertaArmazenamentoInsuficiente();
        }

        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true);

        cameraSource = builder.build();

    }

    private void startCameraSource() throws SecurityException {
        if (cameraSource != null) {
            try {
                cameraPreview.start(cameraSource);
            } catch (IOException e) {
                Log.e("CapturaBarrasActivity", "Unable to start camera source.", e);
                cameraPreview.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void novaBarraDetectada(BarrasHolder holder) {

        Barcode barras = holder.getBarras();
        String codigoBarras = barras.rawValue;
        final Boleto boleto = BoletoFactory.criarBoleto(new CodigoBarras(codigoBarras));
        final Context activity = this;
        if(boleto.isValido()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(activity, BoletoValidadoActivity.class);
                    intent.putExtra(Constantes.KEY_BOLETO_DETALHES, Formatador.getBoletoDetalhes(activity, boleto));
                    setResult(Constantes.RESULT_CODIGO_BARRAS_VALIDO, intent);
                    finish();
                }
            });

        }




    }

    @Override
    protected void onResume() {
        super.onResume();
        if(tarefaIniciada) {
            startCameraSource();
        } else if(checagemPermissoes.permissoesSatisfeitas()) {
            iniciarTarefa();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraSource != null) {
            cameraSource.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(checagemPermissoes != null) {
            checagemPermissoes.removerReferencias();
        }
        if(cameraSource != null) {
            cameraPreview.release();
        }
    }

    @Override
    public AppCompatActivity getContext() {
        return this;
    }



}
