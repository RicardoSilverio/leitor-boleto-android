package br.com.silvr.leitorboleto.model;

import java.util.regex.Pattern;

public final class LinhaDigitavel implements RepresentacaoNumerica {
	
	private static final Pattern REGEX = Pattern.compile("^\\d{47,48}$");
	
	private boolean representacaoValida;
	private String linhaDigitavel;

	public LinhaDigitavel(String linhaDigitavel) {
		this.linhaDigitavel = linhaDigitavel == null ? "" : linhaDigitavel;		
		representacaoValida = REGEX.matcher(linhaDigitavel).matches();
	}

	public String getRepresentacaoNumerica() {
		return linhaDigitavel;
	}

	public boolean isRepresentacaoValida() {
		return representacaoValida;
	}

	@Override
	public String toString() {
		return linhaDigitavel.toString();
	}
}