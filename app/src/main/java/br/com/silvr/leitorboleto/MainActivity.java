package br.com.silvr.leitorboleto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import br.com.silvr.leitorboleto.model.Boleto;
import br.com.silvr.leitorboleto.model.BoletoComercial;
import br.com.silvr.leitorboleto.model.BoletoConcessionaria;
import br.com.silvr.leitorboleto.model.Constantes;
import br.com.silvr.leitorboleto.model.Formatador;
import br.com.silvr.leitorboleto.model.LinhaDigitavel;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout layoutLinhaDigitavel;
    private TextInputEditText txtLinhaDigitavel;
    private Boleto boleto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutLinhaDigitavel = (TextInputLayout) findViewById(R.id.layout_linhadigitavel);
        txtLinhaDigitavel = (TextInputEditText) findViewById(R.id.txt_linhadigitavel);

        txtLinhaDigitavel.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                layoutLinhaDigitavel.setError(null);
            }
        });
    }

    public void iniciarCamera(View v) {
        Intent intent = new Intent(this, CapturaBarrasActivity.class);
        startActivityForResult(intent, Constantes.REQUEST_LER_CODIGO_BARRAS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Constantes.REQUEST_LER_CODIGO_BARRAS && resultCode == Constantes.RESULT_CODIGO_BARRAS_VALIDO) {
            startActivity(data);
        }
    }

    public void validarBoleto(View v) {

        String linhaDigitavel = txtLinhaDigitavel.getText().toString();
        if(linhaDigitavel.indexOf(Constantes.CONSTANTE_BOLETO_CONCESSIONARIA) == 0) {
            boleto = new BoletoConcessionaria(new LinhaDigitavel(linhaDigitavel));
        } else {
            boleto = new BoletoComercial(new LinhaDigitavel(linhaDigitavel));
        }

        if(boleto.isValido()) {
            Intent intent = new Intent(this, BoletoValidadoActivity.class);
            intent.putExtra(Constantes.KEY_BOLETO_DETALHES, Formatador.getBoletoDetalhes(this, boleto));
            startActivity(intent);
        } else {
            layoutLinhaDigitavel.setError("Linha digitável inválida");
        }

    }


}
