package venda

import dominios.Produto
import dominios.ProdutoVenda
import dominios.Venda
import enums.FormaPagamento
import exceptions.EstoqueException
import exceptions.FaltaParametrosException
import org.zkoss.zk.ui.event.InputEvent
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Combobox
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Intbox
import org.zkoss.zul.ListModel
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listitem
import org.zkoss.zul.Messagebox
import org.zkoss.zul.SimpleListModel
import org.zkoss.zul.Window
import util.EAComposer


class NovaVendaComposer extends EAComposer {

    def produtoService
    def vendaService

    @Wire Window modalNovaVenda
    @Wire Doublebox txtValorVenda
    @Wire Doublebox txtDesconto
    @Wire Doublebox txtValorComDesconto
    @Wire Combobox cbFormasPagamento
    @Wire Combobox cbProdutos
    @Wire Listbox lstProdutosAdicionados
    @Wire Button btnAdicionarProduto
    @Wire Intbox txtQuantidade
    @Wire Button btnSalvar

    def afterCompose = { window ->
        listarFormasPagamento()
        listarProdutosDisponiveis()
        cbFormasPagamento.focus()
    }

    def listarFormasPagamento(){
        cbFormasPagamento.append{
            FormaPagamento.values().each {
                comboitem(label: it.label, value: it)
            }
        }
    }

    def listarProdutosDisponiveis(){
        cbProdutos.append {
            produtoService.listarTodos().each {
                comboitem(label: it.descricao, value: it)
            }
        }
    }

    @Listen("onOK = #cbFormasPagamento")
    def selecionarFormaPagamento(){
        validarFormaPagamento()
        cbProdutos.focus()
    }

    def validarFormaPagamento(){
        if(!cbFormasPagamento.selectedItem){
            gerarErroValidacao(cbFormasPagamento, "Selecione uma forma de pagamento")
        }
    }

    @Listen("onOK = #cbProdutos")
    def selecionarProduto(){
        txtQuantidade.focus()
    }

    @Listen("onOK = #txtQuantidade")
    def adicionarQuantidadeProduto(){
        if(cbProdutos.selectedItem){
            adicionarProdutoNaVenda()
            txtQuantidade.value = null
            cbProdutos.value = ""
            cbProdutos.focus()
        }else{
            txtDesconto.focus()
        }
    }

    @Listen("onOK = #txtDesconto")
    def definirDesconto(){
        btnSalvar.focus()
    }

    @Listen("onCancel = #form")
    def cancelarCadastro(){
        cancelar()
    }

    @Listen("onClick = #btnAdicionarProduto")
    def adicionarProdutoNaVenda(){
        if(cbProdutos.selectedItem && txtQuantidade.value){
            Produto produto = cbProdutos.selectedItem.value

            boolean novoProduto = true

            for(Listitem iterator : lstProdutosAdicionados.items){
                if(iterator.value.equals(produto)){
                    produto.quantidade = iterator.value.quantidade + txtQuantidade.value
                    iterator.detach()
                    adicionarProdutoVenda(produto)
                    novoProduto = false
                    adicionarValorVenda(txtQuantidade.value, produto.precoVenda)
                    break
                }
            }

            if(novoProduto){
                produto.quantidade = txtQuantidade.value
                adicionarValorVenda(produto.quantidade, produto.precoVenda)
                adicionarProdutoVenda(produto)
            }

        }else{
            adicionarMensagemAvisoModal("Selecione um produto e uma quantidade valida!")

        }

    }

    @Listen("onClick = #btnSalvar")
    def registrarVenda(){
        validarFormaPagamento()
        Venda venda
        try{
            venda = construirVenda()
            vendaService.salvar(venda)
            redirect(uri: "listaVenda.zul"+RESULTADO_SUCESSO)

        }catch(Exception e){
            adicionarMensagemErroModal(e.message)

        }
    }

    def construirVenda(){
        Venda venda = new Venda()

        venda.valorTotal = txtValorVenda.value
        venda.desconto = txtDesconto.value
        venda.calcularValorLiquido()

        venda.formaPagamento = cbFormasPagamento.value.toUpperCase()
        venda.datahora = Calendar.getInstance()

        Set<ProdutoVenda> produtosVenda = new HashSet<ProdutoVenda>()
        lstProdutosAdicionados.items.each { item ->
            Produto produto = item.value
            int quantidadeProduto = produto.quantidade

            if(produtoService.possuiQuantidadeNoEstoque(produto.id, quantidadeProduto)){
                double precoVenda = produto.precoVenda
                double valorTotal = quantidadeProduto * precoVenda

                ProdutoVenda produtoVenda = new ProdutoVenda(
                        produto: item.value,
                        venda: venda,
                        quantidade: quantidadeProduto,
                        valorUnitario: produto.precoVenda,
                        valor: valorTotal
                )
                produtosVenda.add(produtoVenda)
            }else{
                throw new EstoqueException("O produto "+produto.descricao+" não possui unidades suficientes no estoque para concluir a venda.")
            }
        }

        if(produtosVenda.size()<=0) throw new FaltaParametrosException("Você deve adicionar ao menos um produto na venda")

        venda.produtosVendas = produtosVenda

        return venda
    }

    def adicionarProdutoVenda(Produto produto){
        lstProdutosAdicionados.append{
            listitem(value: produto) { item ->
                listcell(
                        label: produto.descricao.length()>20 ? produto.descricao.substring(0,20)+"..." : produto.descricao,
                        tooltiptext: produto.descricao
                )
                listcell(label: produto.marca)
                listcell(label: produto.quantidade)
                listcell(label: produto.precoVenda)
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

    @Listen("onBlur = #txtDesconto")
    def atualizarValorDesconto(){
        double desconto = 0.0
        if(txtDesconto.value) {
            desconto = txtDesconto.value
        }

        double valorDesconto = (txtValorVenda.value * desconto)/100
        txtValorComDesconto.value = txtValorVenda.value - valorDesconto
    }

    def adicionarValorVenda(int quantidade, double precoVenda){
        txtValorVenda.value += quantidade * precoVenda
        atualizarValorDesconto()
    }

    def removerValorVenda(int quantidade, double precoVenda){
        txtValorVenda.value -= quantidade * precoVenda
        atualizarValorDesconto()
    }

    def removerProduto(Listitem itemProduto){
        removerValorVenda(itemProduto.value.quantidade, itemProduto.value.precoVenda)
        Produto produto = itemProduto.value
        itemProduto.detach()
    }

    @Listen("onClick = #btnCancelar")
    def cancelar(){
        modalNovaVenda.detach()
    }
}
