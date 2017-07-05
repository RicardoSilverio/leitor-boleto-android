package br.com.silvr.leitorboleto.model;

import java.math.BigDecimal;
import java.util.Calendar;

public interface Boleto {

	Calendar getDataVencimento();
	BigDecimal getValor();
	boolean isValido();
	RepresentacaoNumerica getLinhaDigitavel();
	RepresentacaoNumerica getCodigoBarras();
	String getLinhaDigitavelFormatada();

}
