package estoque

import dominios.Produto


class StatusJob {

    def estoqueService

    def mensagemService

    static triggers = {
        simple repeatInterval: 5000l
    }

    def execute() {
        List<Produto> produtos = estoqueService.listarProdutosBaixoNivelEstoque()
        mensagemService.adicionarMensagens(produtos)
    }

}
