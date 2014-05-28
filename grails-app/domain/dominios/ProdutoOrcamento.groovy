package dominios

class ProdutoOrcamento {

    Produto produto
    Orcamento orcamento
    int quantidade
    double valorUnitario
    double valor

    static belongsTo = [Orcamento]

    static hasOne = [orcamento:Orcamento, produto:Produto]

    static constraints = {
    }

}
