package venda

import dominios.ProdutoVenda
import dominios.Venda
import dominios.Produto
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Label
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listitem
import org.zkoss.zul.Window
import util.EAComposer


class EstornoVendaComposer extends EAComposer {

    def vendaService

    @Wire
    Window modalEstornoVenda

    @Wire
    Label lblValorVenda

    @Wire
    Label lblDesconto

    @Wire
    Label lblValorEstorno

    @Wire
    Listbox lstProdutosAdicionadosEstorno

    private Venda vendaSelecionada

    def afterCompose = { window ->
        if(arg.idObjeto){
            vendaSelecionada = Venda.findById(arg.idObjeto)
            lblValorVenda.value = vendaSelecionada.valorTotal
            lblDesconto.value = vendaSelecionada.desconto
            lblValorEstorno.value = vendaSelecionada.valorLiquido
            listarProdutos(vendaSelecionada)
        }
    }

    private void listarProdutos(Venda venda){
        lstProdutosAdicionadosEstorno.append{
            venda.produtosVendas.each { produtoVenda ->
                listitem(value: produtoVenda.produto) { item ->
                    listcell(
                        label: produtoVenda.produto.descricao.length()>=20 ? produtoVenda.produto.descricao.substring(0,20)+"..." : produtoVenda.produto.descricao,
                        tooltiptext:  produtoVenda.produto.descricao
                    )
                    listcell(label: produtoVenda.produto.marca)
                    listcell(label: ""){
                        hlayout{
                            textbox(value: produtoVenda.quantidade, style:"width:95%; text-align:center;", onChange:{
                                this.removerQuantidadeProduto(produtoVenda, Integer.parseInt(it.value))
                            })
                        }
                    }
                    listcell(label: produtoVenda.produto.precoVenda)

                    listcell(label: ""){
                        hlayout{
                            toolbarbutton(image:"/images/trash.png", onClick: {
                                e -> this.removerProduto(item, produtoVenda.produto)
                            })
                        }
                    }
                }
            }
        }
    }

    private void removerQuantidadeProduto(ProdutoVenda produtoVenda, int quantidade){
        produtoVenda.quantidade = quantidade
        gerarValorTotalEstorno()
    }

    private void removerProduto(Listitem item, Produto produto){
        item.detach()
        vendaSelecionada.produtosVendas.each { produtoVenda ->
            if(produtoVenda.produto.equals(produto)) {
                vendaSelecionada.produtosVendas.remove(produtoVenda)
            }
        }
        gerarValorTotalEstorno()

    }

    @Listen("onClick = #btnCancelar")
    void cancelar(){
        modalEstornoVenda.detach()
    }

    @Listen("onClick = #btnEstornar")
    def estornar(){
        vendaService.estornarVenda(vendaSelecionada)
        redirect(uri: "listaVenda.zul"+RESULTADO_SUCESSO)

    }

    def gerarValorTotalEstorno(){

    }

    private String formatarValor(double valor){
        String valorStr = String.valueOf(valor.round(2))

        if(valorStr.split("\\.")[1].length()==1){
            valorStr += "0"
        }

        return "R\$ "+valorStr
    }

}
