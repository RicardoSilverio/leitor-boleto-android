package br.com.silvr.leitorboleto.model;

import java.util.regex.Pattern;

public final class CodigoBarras implements RepresentacaoNumerica {
	
	private static final Pattern REGEX = Pattern.compile("^\\d{44}$");
	
	private boolean representacaoValida;
	private String codigoBarras;

	public CodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras == null ? "" : codigoBarras;
		representacaoValida = REGEX.matcher(codigoBarras).matches();
	}

	public String getRepresentacaoNumerica() {
		return codigoBarras;
	}

	public boolean isRepresentacaoValida() {
		return representacaoValida;
	}
	
	@Override
	public String toString() {
		return codigoBarras;
	}

}