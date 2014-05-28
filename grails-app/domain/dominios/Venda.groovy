package dominios

import enums.FormaPagamento


class Venda {

    Calendar datahora
    double valorTotal
    double desconto
    double valorLiquido
    boolean ativo
    boolean estorno
    boolean foiEstornado

    FormaPagamento formaPagamento
    Set<ProdutoVenda> produtosVendas

    Empresa empresa

    static hasOne = [empresa:Empresa]
    static hasMany = [produtosVendas:ProdutoVenda]

    static belongsTo = [Empresa]

    static constraints = {
        datahora(nullable: false)
        formaPagamento(nullable: false)
        produtosVendas(size: 1..999999)
    }

    static mapping = {
        formaPagamento(enumType: "ordinal")
        datahora(index: 'idx_ven_data')
    }

    void calcularValorLiquido(){
        if(desconto>0){
            double valorDesconto = (valorTotal * desconto) / 100
            valorLiquido = valorTotal - valorDesconto
        } else {
            valorLiquido = valorTotal
        }
    }

}
