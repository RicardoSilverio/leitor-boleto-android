package br.com.silvr.leitorboleto.model;

import android.content.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import br.com.silvr.leitorboleto.BoletoDetalhes;
import br.com.silvr.leitorboleto.R;

public class Formatador {
	
	private static final Pattern VALOR_ZERADO = Pattern.compile("^0+$");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final NumberFormat FORMATADOR_VALOR = DecimalFormat.getCurrencyInstance(new Locale("pt-BR", "BR"));
	
	public static Calendar getDataVencimento(String representacao) {
		
		int dia = Integer.parseInt(representacao.substring(6));
		int mes = Integer.parseInt(representacao.substring(4, 6));
		int ano = Integer.parseInt(representacao.substring(0, 4));
		
		Calendar dataVencimento = Calendar.getInstance();
		dataVencimento.setLenient(false);
		
		try {
			dataVencimento.set(ano, mes - 1, dia);
			DATE_FORMAT.format(dataVencimento.getTime());
			return dataVencimento;
		} catch(Exception e) {
			return null;
		}
		
	}
	
	public static BigDecimal getValor(String representacao) {
		
		if(VALOR_ZERADO.matcher(representacao).matches()) {
			return null;
		}
		
		return new BigDecimal(converteRepresentacaoEmDecimal(representacao));		
	}

	public static String getValorFormatado(BigDecimal valor) {
		return FORMATADOR_VALOR.format(valor.doubleValue());
	}
	
	private static String converteRepresentacaoEmDecimal(String representacao) {
		return new StringBuilder(representacao.substring(0, representacao.length() - 2))
			.append(".").append(representacao.substring(representacao.length() - 2))
			.toString();
	}

	public static BoletoDetalhes getBoletoDetalhes(Context context, Boleto boleto) {
		return new BoletoDetalhes(
			boleto instanceof BoletoConcessionaria ? context.getString(R.string.txt_pagamento) :
				context.getString(R.string.txt_titulo),
			boleto.getLinhaDigitavelFormatada(),
			boleto.getCodigoBarras().getRepresentacaoNumerica(),
			boleto.getDataVencimento() != null ? DATE_FORMAT.format(boleto.getDataVencimento().getTime()) : null,
			boleto.getValor() != null ? getValorFormatado(boleto.getValor()) : null
		);
	}

}
