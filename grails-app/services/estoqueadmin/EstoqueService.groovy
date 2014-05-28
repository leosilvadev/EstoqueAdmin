package estoqueadmin

import dominios.Produto
import grails.transaction.Transactional

@Transactional
class EstoqueService {

    List<Produto> listarProdutosBaixoNivelEstoque(){
        return Produto.createCriteria().list {
            ltProperty("quantidade", "quantidadeMinima")
        }
    }
}
