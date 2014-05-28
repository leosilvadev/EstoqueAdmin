package orcamento

import dominios.Orcamento
import dominios.Produto
import dominios.ProdutoOrcamento
import dominios.Usuario
import enums.FormaPagamento
import exceptions.EstoqueException
import exceptions.FaltaParametrosException
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Combobox
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Iframe
import org.zkoss.zul.Intbox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listitem
import org.zkoss.zul.Textbox
import org.zkoss.zul.Window
import util.EAComposer

import javax.servlet.http.HttpServletResponse


class NovoOrcamentoComposer extends EAComposer {

    def produtoService
    def orcamentoService

    @Wire Window modalNovoOrcamento
    @Wire Doublebox txtValorTotal
    @Wire Textbox txtCliente
    @Wire Doublebox txtDesconto
    @Wire Doublebox txtValorComDesconto
    @Wire Combobox cbProdutos
    @Wire Listbox lstProdutosAdicionados
    @Wire Button btnAdicionarProduto
    @Wire Intbox txtQuantidade
    @Wire Button btnSalvar

    def afterCompose = { window ->
        listarProdutosDisponiveis()
        txtCliente.focus()
    }

    def listarProdutosDisponiveis(){
        cbProdutos.append {
            produtoService.listarTodos().each {
                comboitem(label: it.descricao, value: it)
            }
        }
    }

    @Listen("onOK = #txtCliente")
    def inserirCliente(){
        if(!txtCliente.value){
            gerarErroValidacao(txtCliente, "Insira o nome ou CPF do cliente")
        }
        cbProdutos.focus()
    }

    @Listen("onOK = #cbFormasPagamento")
    def selecionarFormaPagamento(){
        cbProdutos.focus()
    }

    @Listen("onOK = #cbProdutos")
    def selecionarProduto(){
        txtQuantidade.focus()
    }

    @Listen("onOK = #txtQuantidade")
    def adicionarQuantidadeProduto(){
        if(cbProdutos.selectedItem){
            adicionarProdutoNoOrcamento()
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

    @Listen("onCancel = #modalNovoOrcamento")
    def cancelarCadastro(){
        cancelar()
    }

    @Listen("onClick = #btnAdicionarProduto")
    def adicionarProdutoNoOrcamento(){
        if(cbProdutos.selectedItem && txtQuantidade.value){
            Produto produto = cbProdutos.selectedItem.value

            boolean novoProduto = true

            for(Listitem iterator : lstProdutosAdicionados.items){
                if(iterator.value.equals(produto)){
                    produto.quantidade = iterator.value.quantidade + txtQuantidade.value
                    iterator.detach()
                    adicionarProdutoOrcamento(produto)
                    novoProduto = false
                    adicionarValorOrcamento(txtQuantidade.value, produto.precoVenda)
                    break
                }
            }

            if(novoProduto){
                produto.quantidade = txtQuantidade.value
                adicionarValorOrcamento(produto.quantidade, produto.precoVenda)
                adicionarProdutoOrcamento(produto)
            }

            txtQuantidade.value = null
            cbProdutos.value = ""
            cbProdutos.focus()

        }else{
            adicionarMensagemAvisoModal("Selecione um produto e uma quantidade valida!")
            cbProdutos.focus()

        }

    }

    @Listen("onClick = #btnSalvar")
    def registrarOrcamento(){
        try{
            Orcamento orcamento = construirOrcamento()
            orcamentoService.salvar(orcamento)
            modalNovoOrcamento.detach()
            redirect(uri:"geracao.zul?orcamento=${orcamento.id}")

        }catch(Exception e){
            adicionarMensagemErroModal(e.message)

        }
    }

    def construirOrcamento(){
        Orcamento orcamento = new Orcamento(
                valorTotal: txtValorTotal.value,
                desconto: txtDesconto.value,
                datahora: Calendar.getInstance(),
                cliente: txtCliente.value
        )
        orcamento.calcularValorLiquido()

        Set<ProdutoOrcamento> produtosOrcamento = new HashSet<ProdutoOrcamento>()
        lstProdutosAdicionados.items.each { item ->
            Produto produto = item.value
            int quantidadeProduto = produto.quantidade

            double precoOrcamento = produto.precoVenda
            double valorTotal = quantidadeProduto * precoOrcamento

            ProdutoOrcamento produtoOrcamento = new ProdutoOrcamento(
                    produto: item.value,
                    orcamento: orcamento,
                    quantidade: quantidadeProduto,
                    valorUnitario: produto.precoVenda,
                    valor: valorTotal
            )
            produtosOrcamento.add(produtoOrcamento)
        }

        if(produtosOrcamento.size()<=0) throw new FaltaParametrosException("Você deve adicionar ao menos um produto no orçamento")

        orcamento.produtosOrcamentos = produtosOrcamento

        return orcamento
    }

    def adicionarProdutoOrcamento(Produto produto){
        lstProdutosAdicionados.append{
            listitem(value: produto) { item ->
                listcell(
                        label: produto.descricao.length()>=20 ? produto.descricao.substring(0,20)+"..." : produto.descricao,
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

        double valorDesconto = (txtValorTotal.value * desconto)/100
        txtValorComDesconto.value = txtValorTotal.value - valorDesconto
    }

    def adicionarValorOrcamento(int quantidade, double precoVenda){
        txtValorTotal.value += quantidade * precoVenda
        atualizarValorDesconto()
    }

    def removerValorOrcamento(int quantidade, double precoOrcamento){
        txtValorTotal.value -= quantidade * precoOrcamento
        atualizarValorDesconto()
    }

    def removerProduto(Listitem itemProduto){
        removerValorOrcamento(itemProduto.value.quantidade, itemProduto.value.precoVenda)
        Produto produto = itemProduto.value
        itemProduto.detach()
    }

    @Listen("onClick = #btnCancelar")
    def cancelar(){
        modalNovoOrcamento.detach()
    }
}
