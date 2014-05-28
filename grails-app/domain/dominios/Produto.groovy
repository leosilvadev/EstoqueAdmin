package dominios

import enums.Unidade
import exceptions.EstoqueException

import java.util.List;
import java.util.Set;

class Produto {

    String codigo
    String descricao
    String marca
    Unidade unidade
    double precoCompra
    double precoVenda
    int quantidade
    int quantidadeMinima

    Set<ProdutoVenda> produtosVendas
    Set<ProdutoOrcamento> produtosOrcamentos
    Set<ItemPedido> itensPedidos

    boolean ativo
    Empresa empresa

    static belongsTo = [Empresa]

    static hasOne = [empresa:Empresa]

    static hasMany = [produtosVendas:ProdutoVenda, produtosOrcamentos:ProdutoOrcamento, itensPedidos:ItemPedido]

    static constraints = {
        codigo (nullable:false, blank: false)
        descricao (nullable: false, blank:false)
        marca (nullable: false, blank:false)
        unidade (nullable: false)
    }

    static mapping = {
        unidade(enumType: "ordinal")
        codigo(index: 'idx_pro_codigo')
    }

    static void baixaEstoque(Long id, double quantidade){
        Produto produto = Produto.get(id)

        if(produto.quantidade < quantidade) throw new EstoqueException("Quantidade de produtos indisponíveis no estoque")

        produto.quantidade = produto.quantidade-quantidade
        produto.save()
    }

    static void adicionaEstoque(Long id, double quantidade){
        Produto produto = Produto.get(id)

        produto.quantidade = produto.quantidade+quantidade
        produto.save()
    }

}
