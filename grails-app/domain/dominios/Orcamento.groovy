package dominios

class Orcamento {

    Calendar datahora
    double valorTotal
    double desconto
    double valorLiquido
    String cliente
    boolean ativo

    Set<ProdutoOrcamento> produtosOrcamentos

    Empresa empresa

    static hasOne = [empresa:Empresa]
    static hasMany = [produtosOrcamentos:ProdutoOrcamento]

    static belongsTo = [Empresa]

    static constraints = {
        cliente(nullable: false, blank: false)
        datahora(nullable: false)
        produtosOrcamentos(size: 1..999999)
    }

    static mapping = {
        datahora(index: 'idx_orc_data')
        cliente(index: 'idx_orc_cliente')
    }

    void calcularValorLiquido(){
        if (desconto>0) {
            double valorDesconto = (valorTotal * desconto) / 100
            valorLiquido = valorTotal - valorDesconto
        } else {
            valorLiquido = valorTotal
        }
    }

}
