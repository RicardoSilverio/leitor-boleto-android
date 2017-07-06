package br.com.silvr.leitorboleto.model;

/**
 * Created by silvr on 06/07/17.
 */

public class BoletoFactory {

    public static Boleto criarBoleto(RepresentacaoNumerica representacaoNumerica) {

        if(representacaoNumerica.getRepresentacaoNumerica()
            .startsWith(Constantes.CONSTANTE_BOLETO_CONCESSIONARIA)) {
            return new BoletoConcessionaria(representacaoNumerica);

        } else {
            return new BoletoComercial(representacaoNumerica);
        }
    }
}
