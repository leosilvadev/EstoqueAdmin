package pedido

import dominios.Fornecedor
import dominios.ItemPedido
import dominios.Pedido
import dominios.Produto
import exceptions.FaltaParametrosException
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Combobox
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Intbox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listitem
import org.zkoss.zul.Window
import util.EAComposer


class NovoPedidoComposer extends EAComposer {

    def produtoService
    def pedidoService
    def fornecedorService

    @Wire Window modalNovoPedido
    @Wire Doublebox txtValorPedido
    @Wire Listbox lstProdutosAdicionados
    @Wire Combobox cbFornecedores
    @Wire Intbox idFornecedor
    @Wire Combobox cbProdutos
    @Wire Intbox idProduto
    @Wire Intbox txtQuantidade
    @Wire Button btnAdicionarProduto
    @Wire Button btnSalvar

    def afterCompose = { window ->
        listarFornecedores()
        listarProdutosDisponiveis()
        cbFornecedores.focus()
    }

    def listarFornecedores() {
       cbFornecedores.append{
            fornecedorService.listarTodos().each {
                comboitem(label: it.nome, value: it)
            }
        }
    }

    def listarProdutosDisponiveis(){
        cbProdutos.append {
            produtoService.listarTodos().each { produto ->
                comboitem(label: produto.descricao, value: produto)
            }
        }
    }

    @Listen("onOK = #cbFornecedores")
    def selecionarFornecedor(){
        if(!cbFornecedores.selectedItem){
            gerarErroValidacao(cbFornecedores, "Selecione um fornecedor")
        }
        cbProdutos.focus()
    }

    @Listen("onOK = #cbProdutos")
    def selecionarProduto(){
        txtQuantidade.focus()
    }

    @Listen("onOK = #txtQuantidade")
    def adicionarQuantidadeProduto(){
        if(cbProdutos.selectedItem){
            boolean adicionou = adicionarProdutoNoPedido()

        }else{
            btnSalvar.focus()
        }
    }

    @Listen("onCancel = #form")
    def cancelarCadastro(){
        cancelar()
    }

    @Listen("onClick = #btnAdicionarProduto")
    def adicionarProdutoNoPedido(){
        if(cbProdutos.selectedItem && txtQuantidade.value){
            Produto produto = cbProdutos.selectedItem.value

            boolean novoProduto = true

            for(Listitem iterator : lstProdutosAdicionados.items){
                if(iterator.value.equals(produto)){
                    produto.quantidade = iterator.value.quantidade + txtQuantidade.value
                    iterator.detach()
                    adicionarItemPedido(produto)
                    novoProduto = false
                    break
                }
            }

            if(novoProduto){
                produto.quantidade = txtQuantidade.value
                adicionarItemPedido(produto)
            }

            txtQuantidade.value = null
            cbProdutos.value = ""
            cbProdutos.focus()

        }else{
            adicionarMensagemAvisoModal("Selecione um produto e uma quantidade valida!")

        }

    }

    @Listen("onClick = #btnSalvar")
    def registrarPedido(){
        Pedido pedido
        try{
            pedido = construirPedido()
            pedidoService.registrarPedido(pedido)
            redirect(uri:"listaPedido.zul"+RESULTADO_SUCESSO)

        }catch(FaltaParametrosException e){
            adicionarMensagemAvisoModal(e.getMessage())
            cbProdutos.focus()

        }catch(Exception e){
            adicionarMensagemErroModal(e.getMessage())

        }
    }

    def construirPedido(){
        Pedido pedido = new Pedido()
        pedido.fornecedor = cbFornecedores.selectedItem.value
        pedido.dataHora = Calendar.getInstance()

        Set<ItemPedido> itensPedido = new HashSet<ItemPedido>()
        lstProdutosAdicionados.items.each { item ->
            int quantidadeProduto = item.value.quantidade

            ItemPedido itemPedido = new ItemPedido(produto: item.value, pedido: pedido, quantidade: quantidadeProduto)
            itensPedido.add(itemPedido)
        }

        pedido.itensPedidos = itensPedido

        if(pedido.itensPedidos.size()<=0) throw new FaltaParametrosException("Você não adicionou nenhum produto no seu pedido")

        return pedido
    }


    def adicionarItemPedido(Produto produto){
        lstProdutosAdicionados.append{
            listitem(value: produto) { item ->
                listcell(
                        label: produto.descricao.length()>=30 ? produto.descricao.substring(0,30)+"..." : produto.descricao,
                        tooltiptext: produto.descricao
                )
                listcell(label: produto.marca)
                listcell(label: produto.quantidade)
                listcell(label: ""){
                    hlayout{
                        toolbarbutton(image:"/images/trash.png", onClick: {
                            e -> this.removerProduto(item)
                        })
                    }
                }
            }
        }
    }

    def removerProduto(Listitem itemProduto){
        Produto produto = itemProduto.value
        itemProduto.detach()
    }

    @Listen("onClick = #btnCancelar")
    def cancelar(){
        modalNovoPedido.detach()
    }
}
