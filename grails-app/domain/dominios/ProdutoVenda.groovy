package dominios

class ProdutoVenda {

    Produto produto
    Venda venda
    int quantidade
    double valorUnitario
    double valor

    static belongsTo = [Venda]

    static hasOne = [venda:Venda, produto:Produto]

    static constraints = {
    }
}
