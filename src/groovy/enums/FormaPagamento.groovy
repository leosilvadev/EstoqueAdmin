package enums

/**
 * Created by leonardo on 10/02/14.
 */
public enum FormaPagamento {

    CARTAO("Cartao"),
    DINHEIRO("Dinheiro"),
    CHEQUE("Cheque"),
    BOLETO("Boleto");

    String label

    private FormaPagamento(String label){
        this.label = label
    }

}
