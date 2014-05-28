package venda

import dominios.Venda
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Textbox
import org.zkoss.zul.Window
import util.EAComposer


class DetalhesComposer extends EAComposer {

    @Wire Textbox txtFormaPagamento
    @Wire Doublebox txtValorVenda
    @Wire Doublebox txtDesconto
    @Wire Doublebox txtValorComDesconto
    @Wire Listbox lstProdutosAdicionados
    @Wire Window modalDetalhesVenda

    def afterCompose = { window ->
        if(arg.idObjeto){
            def vendaSelecionada = Venda.findById(arg.idObjeto)
            txtFormaPagamento.value = vendaSelecionada.formaPagamento.label
            txtValorVenda.value = vendaSelecionada.valorTotal
            txtDesconto.value = vendaSelecionada.desconto
            txtValorComDesconto.value = vendaSelecionada.valorLiquido

            lstProdutosAdicionados.append{
                vendaSelecionada.produtosVendas.each { produtoVenda ->
                    listitem(){
                        listcell(
                                label: produtoVenda.produto.descricao.length()>25 ? produtoVenda.produto.descricao.substring(0,25)+"..." : produtoVenda.produto.descricao,
                                tooltiptext: produtoVenda.produto.descricao
                        )
                        listcell(label: produtoVenda.produto.marca)
                        listcell(label: produtoVenda.quantidade)
                        listcell(label: produtoVenda.produto.precoVenda)
                    }
                }
            }

        }
    }

    @Listen("onCancel = #form")
    def fecharModal(){
        modalDetalhesVenda.detach()
    }

}
