package br.com.silvr.leitorboleto.model;

import java.math.BigDecimal;
import java.util.Calendar;

public final class BoletoComercial implements Boleto {
	
	private boolean valido;

	private RepresentacaoNumerica linhaDigitavel;
	private RepresentacaoNumerica codigoBarras;
	private Calendar dataVencimento;
	private BigDecimal valor;


	public BoletoComercial(RepresentacaoNumerica representacaoNumerica) {
		
		if(representacaoNumerica == null) {
			valido = false;
			return;
		}
		
		if(representacaoNumerica.getRepresentacaoNumerica().startsWith(Constantes.CONSTANTE_BOLETO_CONCESSIONARIA)) {
			valido = false;
			return;
		}
			
		if(representacaoNumerica instanceof LinhaDigitavel) {
			linhaDigitavel = representacaoNumerica;
			valido = processarLinhaDigitavel();
	
		} else if(representacaoNumerica instanceof CodigoBarras) {
			codigoBarras = representacaoNumerica;
			valido = processarCodigoBarras();
		
		} else {
			valido = false;
			return;
		}
		
	}
	
	private boolean processarLinhaDigitavel() {
		
		if(!linhaDigitavel.isRepresentacaoValida() || linhaDigitavel.getRepresentacaoNumerica().length() != 47) {
			return false;
		}
		
		String dvCodigoBarras = getDVBarras((LinhaDigitavel)linhaDigitavel);
		
		// DV do codigo de barras nunca deve ser igual a zero
		if(dvCodigoBarras.equals(Constantes.DV_BARRAS_INVALIDO)) {
			return false;
		}

		String campo1 = getCampo1(linhaDigitavel);
		int dv1 = getDV1(linhaDigitavel);
		
		if(!validarDVCampoLinhaDigitavel(dv1, campo1)) {
			return false;
		}		
		
		String campo2 = getCampo2(linhaDigitavel);
		int dv2 = getDV2(linhaDigitavel);
		
		if(!validarDVCampoLinhaDigitavel(dv2, campo2)) {
			return false;
		}
		
		String campo3 = getCampo3(linhaDigitavel);
		int dv3 = getDV3(linhaDigitavel);
		
		if(!validarDVCampoLinhaDigitavel(dv3, campo3)) {
			return false;
		}
		
		String fatorVencimento = getFatorVencimento((LinhaDigitavel)linhaDigitavel);
		String representacaoValor = getRepresentacaoValor((LinhaDigitavel)linhaDigitavel);
		
		// O codigo de barras deve ser montado e validado para se certificar que a linha digitavel informada eh valida 	
		codigoBarras = new CodigoBarras(montarCodigoBarras(dvCodigoBarras, fatorVencimento, 
			representacaoValor, campo1, campo2, campo3));
		String codigoBarrasSemDV = getCodigoBarrasSemDV(codigoBarras);
		
		if(!validarCodigoBarras(dvCodigoBarras, codigoBarrasSemDV)) {
			return false;
		}
		
		// Campo de fator de vencimento foi sobreposto por um valor maior que as 10 posicoes normais
		if(Integer.parseInt(fatorVencimento) < 1000) { 
			representacaoValor = fatorVencimento + representacaoValor;
		} else {
			dataVencimento = Calculadora.determinarVencimento(Calendar.getInstance(), fatorVencimento);
		}
				
		valor = Formatador.getValor(representacaoValor);
		
		return true;
		
	}
	
	private boolean processarCodigoBarras() {
		
		if(!codigoBarras.isRepresentacaoValida()) {
			return false;
		}
		
		String dvCodigoBarras = getDVBarras((CodigoBarras)codigoBarras);
		String codigoBarrasSemDV = getCodigoBarrasSemDV(codigoBarras);
		
		if(!validarCodigoBarras(dvCodigoBarras, codigoBarrasSemDV)) {
			return false;
		}
		
		String fatorVencimento = getFatorVencimento((CodigoBarras)codigoBarras);
		String representacaoValor = getRepresentacaoValor((CodigoBarras)codigoBarras);
		
		// Campo de fator de vencimento foi sobreposto por um valor maior que as 10 posicoes normais
		if(Integer.parseInt(fatorVencimento) < 1000) { 
			representacaoValor = fatorVencimento + representacaoValor;
		} else {
			dataVencimento = Calculadora.determinarVencimento(Calendar.getInstance(), fatorVencimento);
		}
				
		valor = Formatador.getValor(representacaoValor);
				
		linhaDigitavel = new LinhaDigitavel(montarLinhaDigitavel(codigoBarras.getRepresentacaoNumerica(),
			dvCodigoBarras, fatorVencimento, representacaoValor));
		
		return true;
		
	}
	
	
	private static boolean validarCodigoBarras(String dvCodigoBarras, String codigoBarrasSemDV) {
		
		// DV do codigo de barras nunca deve ser igual a zero
		if(dvCodigoBarras.equals(Constantes.DV_BARRAS_INVALIDO)) {
			return false;
		}
		
		if(!validarDVCodigoBarras(dvCodigoBarras, codigoBarrasSemDV)) {
			return false;
		}
		
		return true;
		
	}
	
	
	private static String montarCodigoBarras(String dvCodigoBarras, String fatorVencimento,
		String representacaoValor, String campo1, String campo2, String campo3) {
		
		StringBuilder representacaoCodigoBarras = new StringBuilder(campo1.substring(0, 3)) // codIF
			.append(campo1.substring(3,4)) // codMoeda
			.append(dvCodigoBarras)
			.append(fatorVencimento)
			.append(representacaoValor)
			.append(campo1.substring(4))
			.append(campo2)
			.append(campo3);
		
		return representacaoCodigoBarras.toString();
	}
	
	private static String montarLinhaDigitavel(String codigoBarras, String dvCodigoBarras, 
		String fatorVencimento, String representacaoValor) {
		
		String campo1 = new StringBuilder(codigoBarras.substring(0, 4)) // codIF + moeda
			.append(codigoBarras.substring(19, 24)).toString();
		int dv1 = getDVCampoLinhaDigitavel(campo1);
		
		String campo2 = codigoBarras.substring(24, 34);
		int dv2 = getDVCampoLinhaDigitavel(campo2);
		
		String campo3 = codigoBarras.substring(34, 44);
		int dv3 = getDVCampoLinhaDigitavel(campo3);
		
		return new StringBuilder(campo1) 
			.append(Integer.toString(dv1))
			.append(campo2)
			.append(Integer.toString(dv2))
			.append(campo3)
			.append(Integer.toString(dv3))
			.append(dvCodigoBarras)
			.append(fatorVencimento)
			.append(representacaoValor).toString();
	}
	
	private static String getCodigoBarrasSemDV(RepresentacaoNumerica codigoBarras) {
		return new StringBuilder(codigoBarras.getRepresentacaoNumerica().substring(0, 4))
			.append(codigoBarras.getRepresentacaoNumerica().substring(5)).toString();
	}
	
	private static boolean validarDVCampoLinhaDigitavel(int dv, String campo) {
		int dvCalculado = getDVCampoLinhaDigitavel(campo);
		return dvCalculado == dv;
	}
	
	private static int getDVCampoLinhaDigitavel(String campo) {
		int dvCalculado = 10 - Calculadora.calcularModulo10(campo);
		return (dvCalculado == 10) ? 0 : dvCalculado;
	}
	
	private static boolean validarDVCodigoBarras(String dvCodigoBarras, String codigoBarrasSemDV) {
		int dvCalculado = 11 - Calculadora.calcularModulo11(codigoBarrasSemDV);
		dvCalculado = (dvCalculado == 0 || dvCalculado == 10 || dvCalculado == 11) ? 1 : dvCalculado;
		
		return dvCalculado == Integer.parseInt(dvCodigoBarras);
	}
	
	private static String getCampo1(RepresentacaoNumerica linhaDigitavel) {
		return linhaDigitavel.getRepresentacaoNumerica().substring(0, 9);
	}
	
	private static int getDV1(RepresentacaoNumerica linhaDigitavel) {
		return Integer.parseInt(linhaDigitavel.getRepresentacaoNumerica().substring(9, 10));
	}
	
	private static String getCampo2(RepresentacaoNumerica linhaDigitavel) {
		return linhaDigitavel.getRepresentacaoNumerica().substring(10, 20);
	}
	
	private static int getDV2(RepresentacaoNumerica linhaDigitavel) {
		return Integer.parseInt(linhaDigitavel.getRepresentacaoNumerica().substring(20, 21));
	}
	
	private static String getCampo3(RepresentacaoNumerica linhaDigitavel) {
		return linhaDigitavel.getRepresentacaoNumerica().substring(21, 31);
	}
	
	private static int getDV3(RepresentacaoNumerica linhaDigitavel) {
		return Integer.parseInt(linhaDigitavel.getRepresentacaoNumerica().substring(31, 32));
	}
	
	private static String getDVBarras(LinhaDigitavel linhaDigitavel) {
		return linhaDigitavel.getRepresentacaoNumerica().substring(32, 33);
	}
	
	private static String getDVBarras(CodigoBarras codigoBarras) {
		return codigoBarras.getRepresentacaoNumerica().substring(4,5);
	}
	
	private static String getFatorVencimento(LinhaDigitavel linhaDigitavel) {
		return linhaDigitavel.getRepresentacaoNumerica().substring(33, 37);
	}
	
	private static String getRepresentacaoValor(LinhaDigitavel linhaDigitavel) {
		return linhaDigitavel.getRepresentacaoNumerica().substring(37);
	}
	
	private static String getFatorVencimento(CodigoBarras codigoBarras) {
		return codigoBarras.getRepresentacaoNumerica().substring(5, 9);
	}
	
	private static String getRepresentacaoValor(CodigoBarras codigoBarras) {
		return codigoBarras.getRepresentacaoNumerica().substring(9, 19);
	}

	public String getLinhaDigitavelFormatada() {
		if(valido) {
			String campo1 = getCampo1(linhaDigitavel);
			int dv1 = getDV1(linhaDigitavel);
			String campo2 = getCampo2(linhaDigitavel);
			int dv2 = getDV2(linhaDigitavel);
			String campo3 = getCampo3(linhaDigitavel);
			int dv3 = getDV3(linhaDigitavel);
			return new StringBuilder(campo1.substring(0, 5)).append(".").append(campo1.substring(5)).append(dv1).append(" ")
				.append(campo2.substring(0, 5)).append(".").append(campo2.substring(5)).append(dv2).append(" ")
				.append(campo3.substring(0, 5)).append(".").append(campo3.substring(5)).append(dv3).append(" ")
				.append(getDVBarras((LinhaDigitavel) linhaDigitavel)).append(" ")
				.append(getFatorVencimento((LinhaDigitavel) linhaDigitavel))
				.append(getRepresentacaoValor((LinhaDigitavel) linhaDigitavel)).toString();
		}
		return "";
	}

	@Override
	public Calendar getDataVencimento() {
		return dataVencimento;
	}

	@Override
	public BigDecimal getValor() {
		return valor;
	}

	@Override
	public boolean isValido() {
		return valido;
	}

	@Override
	public RepresentacaoNumerica getLinhaDigitavel() {
		return linhaDigitavel;
	}

	@Override
	public RepresentacaoNumerica getCodigoBarras() {
		return codigoBarras;
	}	
	

}