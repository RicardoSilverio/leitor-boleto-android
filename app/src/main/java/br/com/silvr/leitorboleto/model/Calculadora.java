package br.com.silvr.leitorboleto.model;

import java.util.Calendar;

class Calculadora {
	
	private static final Calendar DATA_REF_ORIGINAL_FATOR_VENCIMENTO;
	private static final Calendar DATA_RESET_FATOR_VENCIMENTO;
	
	static {
		Calendar data = Calendar.getInstance();
		data.set(2025, 1, 22);
		data = zerarHora(data);
		DATA_RESET_FATOR_VENCIMENTO = data;
		
		data = Calendar.getInstance();
		data.set(2000, 6, 3);
		data = zerarHora(data);
		DATA_REF_ORIGINAL_FATOR_VENCIMENTO = data;
	}
	
	public static Calendar determinarVencimento(Calendar dataAtual, String fatorVencimento) {
		
		Calendar dataReferencia = null;
		if(dataAtual.after(DATA_RESET_FATOR_VENCIMENTO)) {
			dataReferencia = Calendar.getInstance();
			dataReferencia.setTime(DATA_RESET_FATOR_VENCIMENTO.getTime());
		} else {
			dataReferencia = Calendar.getInstance();
			dataReferencia.setTime(DATA_REF_ORIGINAL_FATOR_VENCIMENTO.getTime());
		}
		
		int dias = Integer.parseInt(fatorVencimento);
		dataReferencia.add(Calendar.DAY_OF_YEAR, dias - 1000);
		return dataReferencia;		
		
	}
	
	public static int calcularModulo11(String representacaoNumerica) {
		
		int soma = 0;
		int valor = 0;
		int modulo = 11;
		int multiplicador = 2;
		char[] caracteres = representacaoNumerica.toCharArray();
		
		for(int n = caracteres.length - 1; n>= 0; n--) {
			valor = Character.getNumericValue(caracteres[n]) * multiplicador;
			soma += valor;
			multiplicador = (multiplicador < 9) ? multiplicador + 1 : 2;
		}
			
		return soma % modulo;
	}
	
	public static int calcularModulo10(String representacaoNumerica) {
		
		int soma = 0;
		int valor = 0;
		int modulo = 10;
		int multiplicador = 2;
		char[] caracteres = representacaoNumerica.toCharArray();
		
		for(int n = caracteres.length - 1; n>= 0; n--) {
			valor = Character.getNumericValue(caracteres[n]) * multiplicador;
			soma += (valor > 9) ? somaAlgarismos(Integer.toString(valor)) : valor;
			multiplicador = (multiplicador == 2) ? 1 : 2;
		}
		
		return soma % modulo;
		
	}
	
	private static int somaAlgarismos(String representacaoNumerica) {
		int soma = 0;
		char[] caracteres = representacaoNumerica.toCharArray();
		for(char c : caracteres) {
			soma += Character.getNumericValue(c);
		}
		return soma;
	}
	
	private static Calendar zerarHora(Calendar dataHora) {
		dataHora.set(Calendar.HOUR_OF_DAY, 0);
		dataHora.set(Calendar.MINUTE, 0);
		dataHora.set(Calendar.SECOND, 0);
		return dataHora;
	}

}
