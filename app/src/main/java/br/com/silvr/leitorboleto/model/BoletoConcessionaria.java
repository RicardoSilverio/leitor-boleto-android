package br.com.silvr.leitorboleto.model;

import java.math.BigDecimal;
import java.util.Calendar;

public class BoletoConcessionaria implements Boleto {

	private boolean valido;

	private RepresentacaoNumerica linhaDigitavel;
	private RepresentacaoNumerica codigoBarras;
	private Calendar dataVencimento;
	private BigDecimal valor;
	
	public BoletoConcessionaria(RepresentacaoNumerica representacaoNumerica) {
		
		if(representacaoNumerica == null) {
			valido = false;
			return;
		}
		
		if(!representacaoNumerica.getRepresentacaoNumerica().startsWith(Constantes.CONSTANTE_BOLETO_CONCESSIONARIA)) {
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



	private boolean processarCodigoBarras() {
		
		if(!codigoBarras.isRepresentacaoValida()) {
			return false;
		}
		
		int modeloCalculo = getModeloCalculo(codigoBarras);
		
		if(modeloCalculo == -1) {
			return false;
		}
		
		boolean mod10 = modeloCalculo == Constantes.MODULO_10;
		int dvCodigoBarras = getDVCodigoBarras(codigoBarras);
		
		String codigoBarrasSemDV = getCodigoBarrasSemDV(codigoBarras);
		int dvCalculado = mod10 ? calcularModulo10(codigoBarrasSemDV) : calcularModulo11(codigoBarrasSemDV);
		
		if(dvCalculado != dvCodigoBarras) {
			return false;
		}
		
		linhaDigitavel = montarLinhaDigitavel(codigoBarras, mod10);
		valor = extrairValor(codigoBarras);
		dataVencimento = extrairVencimento(codigoBarras);
		
		return true;
	}



	private RepresentacaoNumerica montarLinhaDigitavel(RepresentacaoNumerica codigoBarras, boolean isModulo10) {
		
		String campo1 = getCampo(1, (CodigoBarras)codigoBarras);
		int dv1 = isModulo10 ? calcularModulo10(campo1) : calcularModulo11(campo1);
		
		String campo2 = getCampo(2, (CodigoBarras)codigoBarras);
		int dv2 = isModulo10 ? calcularModulo10(campo2) : calcularModulo11(campo2);
		
		String campo3 = getCampo(3, (CodigoBarras)codigoBarras);
		int dv3 = isModulo10 ? calcularModulo10(campo3) : calcularModulo11(campo3);
		
		String campo4 = getCampo(4, (CodigoBarras)codigoBarras);
		int dv4 = isModulo10 ? calcularModulo10(campo4) : calcularModulo11(campo4);
		
		return new LinhaDigitavel(new StringBuilder(campo1).append(dv1).append(campo2).append(dv2)
			.append(campo3).append(dv3).append(campo4).append(dv4).toString());
	}



	private boolean processarLinhaDigitavel() {
		
		if(!linhaDigitavel.isRepresentacaoValida() || linhaDigitavel.getRepresentacaoNumerica().length() != 48) {
			return false;
		}
		
		int dvCalculado = 0;
		int modeloCalculo = getModeloCalculo(linhaDigitavel);
		
		if(modeloCalculo == -1) {
			return false;
		}
		
		boolean mod10 = modeloCalculo == Constantes.MODULO_10;
		
		int dv1 = getDVCampo(1, linhaDigitavel);
		String campo1 = getCampo(1, (LinhaDigitavel)linhaDigitavel);
		
		dvCalculado = mod10 ? calcularModulo10(campo1) : calcularModulo11(campo1);
		if(dvCalculado != dv1) {
			return false;
		}
		
		int dv2 = getDVCampo(2, linhaDigitavel);
		String campo2 = getCampo(2, (LinhaDigitavel)linhaDigitavel);
		
		dvCalculado = mod10 ? calcularModulo10(campo2) : calcularModulo11(campo2);
		if(dvCalculado != dv2) {
			return false;
		}
		
		int dv3 = getDVCampo(3, linhaDigitavel);
		String campo3 = getCampo(3, (LinhaDigitavel)linhaDigitavel);
		
		dvCalculado = mod10 ? calcularModulo10(campo3) : calcularModulo11(campo3);
		if(dvCalculado != dv3) {
			return false;
		}
		
		int dv4 = getDVCampo(4, linhaDigitavel);
		String campo4 = getCampo(4, (LinhaDigitavel)linhaDigitavel);
		
		dvCalculado = mod10 ? calcularModulo10(campo4) : calcularModulo11(campo4);
		if(dvCalculado != dv4) {
			return false;
		}
		
		codigoBarras = montarCodigoBarras(campo1, campo2, campo3, campo4);
		valor = extrairValor(codigoBarras);
		dataVencimento = extrairVencimento(codigoBarras);
		
		return true;
	}

	public String getLinhaDigitavelFormatada() {
		if(valido) {
			StringBuilder linhaDigitavelFormatada = new StringBuilder();
			for(int n = 1; n <=4; n++) {
				linhaDigitavelFormatada.append(getCampo(n, (LinhaDigitavel) linhaDigitavel))
					.append("-").append(getDVCampo(n, (LinhaDigitavel) linhaDigitavel)).append(" ");
			}
			return linhaDigitavelFormatada.toString();
		}
		return "";
	}
	
	private static String getCodigoBarrasSemDV(RepresentacaoNumerica codigoBarras) {
		String representacao = codigoBarras.getRepresentacaoNumerica();
		return new StringBuilder(representacao.substring(0, 3)).append(representacao.substring(4)).toString();
	}
	
	private static BigDecimal extrairValor(RepresentacaoNumerica codigoBarras) {
		String representacaoValor = codigoBarras.getRepresentacaoNumerica().substring(4, 15);
		return Formatador.getValor(representacaoValor);
	}
	
	private static Calendar extrairVencimento(RepresentacaoNumerica codigoBarras) {
		String representacaoVencimento = codigoBarras.getRepresentacaoNumerica().substring(23, 32);
		return Formatador.getDataVencimento(representacaoVencimento);
	}
	
	private static int calcularModulo11(String representacaoNumerica) {
		int dvCalculado = Calculadora.calcularModulo11(representacaoNumerica);
		return (dvCalculado == 0 || dvCalculado == 1) ? 0 : (dvCalculado == 10) ? 1 : 11 - dvCalculado;
	}
	
	private static int calcularModulo10(String representacaoNumerica) {
		int dvCalculado = 10 - Calculadora.calcularModulo10(representacaoNumerica);
		return (dvCalculado == 10) ? 0 : dvCalculado;
	}
	
	private static RepresentacaoNumerica montarCodigoBarras(String campo1,String campo2, 
		String campo3, String campo4) {
		
		return new CodigoBarras(new StringBuilder(campo1).append(campo2).append(campo3).append(campo4).toString());
	}
	
	private static int getDVCodigoBarras(RepresentacaoNumerica codigoBarras) {
		return Integer.parseInt(codigoBarras.getRepresentacaoNumerica().substring(3, 4));
	}
	
	private static int getDVCampo(int numeroCampo, RepresentacaoNumerica linhaDigitavel) {
		int posicaoDV = (12 * numeroCampo) - 1;
		return Integer.parseInt(linhaDigitavel.getRepresentacaoNumerica().substring(posicaoDV, posicaoDV + 1));
	}

	private static String getCampo(int numeroCampo, LinhaDigitavel linhaDigitavel) {
		int indexInicial = 12 * (numeroCampo - 1);
		int indexFinal = (12 * numeroCampo) - 1;
		return linhaDigitavel.getRepresentacaoNumerica().substring(indexInicial, indexFinal);
	}
	
	private static String getCampo(int numeroCampo, CodigoBarras codigoBarras) {
		int indexInicial = 11 * (numeroCampo - 1);
		int indexFinal = 11 * numeroCampo;
		return codigoBarras.getRepresentacaoNumerica().substring(indexInicial, indexFinal);
	}
	
	private static int getModeloCalculo(RepresentacaoNumerica representacaoNumerica) {
		int identificacaoValorRef = Integer.parseInt(representacaoNumerica.getRepresentacaoNumerica().substring(2, 3));
		if(identificacaoValorRef == 6 || identificacaoValorRef == 7) {
			return Constantes.MODULO_10;
		} else if(identificacaoValorRef == 8 || identificacaoValorRef == 9) {
			return Constantes.MODULO_11;
		} else {
			return -1;
		}	
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
