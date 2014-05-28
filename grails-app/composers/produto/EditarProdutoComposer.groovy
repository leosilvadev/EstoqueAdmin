package produto

import dominios.Produto
import enums.Unidade
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.Combobox
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Intbox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Textbox
import org.zkoss.zul.Window
import util.EAComposer


class EditarProdutoComposer extends EAComposer {

    def produtoService

    @Wire Window modalEditarProduto
    @Wire Textbox txtCodigo
    @Wire Textbox txtDescricao
    @Wire Textbox txtMarca
    @Wire Combobox cbUnidades
    @Wire Doublebox dbPreco
    @Wire Intbox intQuantidade
    @Wire Intbox intQuantidadeMinima
    @Wire Button btnSalvar

    private Produto produtoSelecionado


    def afterCompose = { window ->

        listarUnidades()
        Clients.evalJavaScript("aplicarMascaras();")
        if(arg.idObjeto ){
            produtoSelecionado = Produto.findById(arg.idObjeto)
            txtCodigo.value = produtoSelecionado.codigo
            txtDescricao.value = produtoSelecionado.descricao
            txtMarca.value = produtoSelecionado.marca
            cbUnidades.selectedItem = cbUnidades.items.find({it.value == produtoSelecionado.unidade})
            dbPreco.value = produtoSelecionado.precoVenda
            intQuantidade.value = produtoSelecionado.quantidade
            intQuantidadeMinima.value = produtoSelecionado.quantidadeMinima
        }
    }

    @Listen("onOK = #txtCodigo")
    def setarCodigo(){
        txtDescricao.focus()
        txtDescricao.select()
    }

    @Listen("onOK = #txtDescricao")
    def setarDescricao(){
        txtMarca.focus()
        txtMarca.select()
    }

    @Listen("onOK = #txtMarca")
    def setarMarca(){
        cbUnidades.focus()
        cbUnidades.select()
    }

    @Listen("onOK = #cbUnidades")
    def setarUnidade(){
        if(!cbUnidades.selectedItem){
            gerarErroValidacao(cbUnidades, "Escolha uma unidade")
        }
        dbPreco.focus()
        dbPreco.select()
    }

    @Listen("onOK = #dbPreco")
    def setarPreco(){
        intQuantidadeMinima.focus()
        intQuantidadeMinima.select()
    }

    @Listen("onOK = #intQuantidadeMinima")
    def setarQuantidadeMinima(){
        btnSalvar.focus()
    }

    @Listen("onClick = #btnSalvar")
    def atualizarProduto(){
        produtoSelecionado.codigo = txtCodigo.value
        produtoSelecionado.descricao = txtDescricao.value
        produtoSelecionado.marca = txtMarca.value
        produtoSelecionado.unidade = cbUnidades.selectedItem.value
        produtoSelecionado.precoVenda = dbPreco.value
        produtoSelecionado.quantidadeMinima = intQuantidadeMinima.value

        try{
            produtoService.atualizar(produtoSelecionado)
            modalEditarProduto.detach()
            redirect(uri:'listaProduto.zul'+RESULTADO_ALTERACAO_SUCESSO)

        }catch(Exception e){
            adicionarMensagemErro(e.getMessage())

        }

    }

    @Listen("onClick = #btnCancelar")
    def cancelar(){
        modalEditarProduto.detach()
    }

    def listarUnidades(){
        cbUnidades.append {
            Unidade.values().each { unidade ->
                comboitem(label: unidade.toString(), value: unidade)
            }
        }
    }

    @Listen("onCancel = #modalEditarProduto")
    def fecharModal(){
        cancelar()
    }

}