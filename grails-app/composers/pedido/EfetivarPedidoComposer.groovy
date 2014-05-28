package pedido

import dominios.ItemPedido
import dominios.Pedido
import dominios.Produto
import enums.StatusPedido
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Label
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listitem
import org.zkoss.zul.Window
import util.EAComposer


class EfetivarPedidoComposer extends EAComposer {

    private Pedido pedidoSelecionado

    def pedidoService

    @Wire Window modalEfetivarPedido
    @Wire Label lblValorTotal
    @Wire Listbox lstProdutosAdicionadosEfetivacao


    def afterCompose = { window ->
        if(arg.idObjeto){
            pedidoSelecionado = Pedido.findById(arg.idObjeto)
            listarProdutos(pedidoSelecionado)
        }
    }

    def listarProdutos(Pedido pedido){
        lstProdutosAdicionadosEfetivacao.append{
            pedido.itensPedidos.each { itemPedido ->
                listitem(value: itemPedido.produto) { item ->
                    listcell(
                            label: itemPedido.produto.descricao.length()>20 ? itemPedido.produto.descricao.substring(0,20)+"..." : itemPedido.produto.descricao,
                            tooltiptext: itemPedido.produto.descricao
                    )
                    listcell(label: itemPedido.produto.marca)
                    listcell(label: ""){
                        hlayout{
                            textbox(value: itemPedido.quantidade, style:"width:95%; text-align:center;", onChange:{
                                this.removerQuantidadeProduto(itemPedido, Integer.parseInt(it.value))
                            })
                        }
                    }
                    listcell(label: ""){
                        hlayout{
                            textbox(value: "0.00", style:"width:95%; text-align:center;", sclass: "money",  onChange:{
                                this.somarValorPedido(itemPedido, Double.parseDouble(it.value))
                            })
                        }
                    }
                    listcell(label: ""){
                        hlayout{
                            toolbarbutton(image:"/images/trash.png", onClick: {
                                e -> this.removerProduto(item, itemPedido.produto)
                            })
                        }
                    }
                }
            }
        }
    }

    def somarValorPedido(ItemPedido itemPedido, double valorUnitario){
        itemPedido.precoCompra = valorUnitario
        gerarValorTotal()
    }

    def removerQuantidadeProduto(ItemPedido itemPedido, int quantidade){
        itemPedido.quantidade = quantidade
        gerarValorTotal()
    }

    def removerProduto(Listitem item, Produto produto){
        item.detach()
        pedidoSelecionado.itensPedidos.each { itemPedido ->
            if(itemPedido.produto.equals(produto)) {
                pedidoSelecionado.itensPedidos.remove(itemPedido)
            }
        }
        gerarValorTotal()
    }

    @Listen("onClick = #btnCancelar")
    def cancelar(){
        modalEfetivarPedido.detach()
    }

    @Listen("onClick = #btnEfetivar")
    def efetivar(){
        gerarValorTotal()
        pedidoService.efetivarPedido(pedidoSelecionado)
        redirect(uri:'listaPedido.zul')
    }

    def gerarValorTotal(){
        pedidoSelecionado.valorTotal = 0

        pedidoSelecionado.itensPedidos.each { itemPedido ->
            pedidoSelecionado.valorTotal += (itemPedido.quantidade * itemPedido.precoCompra)
        }
        lblValorTotal.value = formatarValor(pedidoSelecionado.valorTotal)
    }

    def formatarValor(double valor){
        String valorStr = String.valueOf(valor.round(2))

        if(valorStr.split("\\.")[1].length()==1){
            valorStr += "0"
        }

        return "R\$ "+valorStr
    }

}
