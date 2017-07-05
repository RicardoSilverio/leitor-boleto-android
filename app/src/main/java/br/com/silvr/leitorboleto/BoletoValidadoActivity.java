package br.com.silvr.leitorboleto;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import br.com.silvr.leitorboleto.model.Constantes;

public class BoletoValidadoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boleto_validado);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(Constantes.KEY_BOLETO_DETALHES)) {

            BoletoDetalhes boleto = extras.getParcelable(Constantes.KEY_BOLETO_DETALHES);
            ((AppCompatTextView)findViewById(R.id.txt_tipoboleto)).setText(boleto.getTipo());
            ((AppCompatTextView)findViewById(R.id.txt_linhadigitavel)).setText(boleto.getLinhaDigitavel());
            ((AppCompatTextView)findViewById(R.id.txt_codigobarras)).setText(boleto.getCodigoBarras());
            ((AppCompatTextView)findViewById(R.id.txt_linhadigitavel)).setText(boleto.getLinhaDigitavel());

            String valor = boleto.getValor() != null && !boleto.getValor().isEmpty() ?
                boleto.getValor() : getString(R.string.txt_indisponivel);
            ((AppCompatTextView)findViewById(R.id.txt_valor)).setText(valor);

            String vencimento = boleto.getDataVencimento() != null && !boleto.getDataVencimento().isEmpty() ?
                boleto.getDataVencimento() : getString(R.string.txt_indisponivel);
            ((AppCompatTextView)findViewById(R.id.txt_datavencimento)).setText(vencimento);


        }

    }

    public void voltar(View v) {
        finish();
    }
}
