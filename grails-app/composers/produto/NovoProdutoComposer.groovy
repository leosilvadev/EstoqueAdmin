package produto

import dominios.Fornecedor
import dominios.Produto
import enums.Unidade
import exceptions.ParamatroInvaloException
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Combobox
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Intbox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Messagebox
import org.zkoss.zul.Spinner
import org.zkoss.zul.Textbox
import org.zkoss.zul.Window
import util.EAComposer


class NovoProdutoComposer extends EAComposer {

    def produtoService

    @Wire Window modalNovoProduto
    @Wire Textbox txtCodigo
    @Wire Textbox txtDescricao
    @Wire Textbox txtMarca
    @Wire Combobox cbUnidades
    @Wire Doublebox dbPreco
    @Wire Intbox intQuantidade
    @Wire Intbox intQuantidadeMinima
    @Wire Checkbox ckCadastrarMais
    @Wire Button btnSalvar

    def afterCompose = { window ->
        Clients.evalJavaScript("aplicarMascaras();")
        listarUnidades()
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
        intQuantidade.focus()
        intQuantidade.select()
    }

    @Listen("onOK = #intQuantidade")
    def setarQuantidade(){
        intQuantidadeMinima.focus()
        intQuantidadeMinima.select()
    }

    @Listen("onOK = #intQuantidadeMinima")
    def setarQuantidadeMinima(){
        btnSalvar.focus()
    }

    @Listen("onClick = #btnSalvar")
    def salvarProduto(){
        try{
            Produto produto = new Produto(
                    codigo: txtCodigo.value,
                    descricao: txtDescricao.value,
                    marca: txtMarca.value,
                    unidade: cbUnidades.selectedItem.value,
                    precoVenda: dbPreco.value,
                    quantidade: intQuantidade.value,
                    quantidadeMinima: intQuantidadeMinima.value
            )

            produtoService.salvar(produto)
            modalNovoProduto.detach()

            if(ckCadastrarMais.checked){
                modalNovoProduto.detach()
                exibirModal("novoProduto.zul")
            }else{
                redirect(uri: "listaProduto.zul"+RESULTADO_SUCESSO)
            }

        }catch(ParamatroInvaloException e){
            adicionarMensagemAvisoModal(e.message)

        }catch(NullPointerException e){
            adicionarMensagemAvisoModal("Preencha todos os campos corretamente")

        }catch(Exception e){
            adicionarMensagemErroModal(e.message)

        }

    }

    @Listen("onClick = #btnCancelar")
    def cancelar(){
        modalNovoProduto.detach()
    }

    @Listen("onCancel = #modalNovoProduto")
    def fecharModal(){
        cancelar()
    }

    def listarUnidades(){
        cbUnidades.append {
            Unidade.values().each { unidade ->
                comboitem(label: unidade.toString(), value: unidade)
            }
        }
    }
}
