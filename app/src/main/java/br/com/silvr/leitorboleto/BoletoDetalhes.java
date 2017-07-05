package br.com.silvr.leitorboleto;

import android.os.Parcel;
import android.os.Parcelable;

public class BoletoDetalhes implements Parcelable {

    private String tipo;
    private String linhaDigitavel;
    private String codigoBarras;
    private String dataVencimento;
    private String valor;

    public BoletoDetalhes(String tipo, String linhaDigitavel, String codigoBarras, String dataVencimento, String valor) {
        this.tipo = tipo;
        this.linhaDigitavel = linhaDigitavel;
        this.codigoBarras = codigoBarras;
        this.dataVencimento = dataVencimento;
        this.valor = valor;
    }

    protected BoletoDetalhes(Parcel in) {
        tipo = in.readString();
        linhaDigitavel = in.readString();
        codigoBarras = in.readString();
        dataVencimento = in.readString();
        valor = in.readString();
    }

    public static final Creator<BoletoDetalhes> CREATOR = new Creator<BoletoDetalhes>() {
        @Override
        public BoletoDetalhes createFromParcel(Parcel in) {
            return new BoletoDetalhes(in);
        }

        @Override
        public BoletoDetalhes[] newArray(int size) {
            return new BoletoDetalhes[size];
        }
    };

    public String getTipo() {
        return tipo;
    }

    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tipo);
        dest.writeString(linhaDigitavel);
        dest.writeString(codigoBarras);
        dest.writeString(dataVencimento);
        dest.writeString(valor);
    }
}
