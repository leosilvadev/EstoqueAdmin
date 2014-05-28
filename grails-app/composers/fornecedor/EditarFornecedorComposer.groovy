package fornecedor

import dominios.Fornecedor
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.Textbox
import org.zkoss.zul.Window
import util.EAComposer


class EditarFornecedorComposer extends EAComposer {

    private Fornecedor fornecedorSelecionado
    def fornecedorService

    @Wire Window modalEditarFornecedor
    @Wire Textbox txtNome
    @Wire Textbox txtTelefone
    @Wire Textbox txtEndereco
    @Wire Textbox txtEmail
    @Wire Button btnSalvar

    def afterCompose = { window ->
        Clients.evalJavaScript("aplicarMascaras();")
        if(arg.idObjeto){
            fornecedorSelecionado = Fornecedor.findById(arg.idObjeto)
            txtNome.value = fornecedorSelecionado.nome

            if(fornecedorSelecionado.email)
                txtEmail.value = fornecedorSelecionado.email

            txtEndereco.value = fornecedorSelecionado.endereco
            txtTelefone.value = fornecedorSelecionado.telefone
        }
    }

    @Listen("onClick = #btnSalvar")
    def atualizarFornecedor(){
        fornecedorSelecionado.nome = txtNome.value
        fornecedorSelecionado.telefone = txtTelefone.value
        fornecedorSelecionado.endereco = txtEndereco.value
        fornecedorSelecionado.email = txtEmail.value
        try{
            fornecedorService.atualizar(fornecedorSelecionado)
            redirect(uri:'listaFornecedor.zul'+RESULTADO_ALTERACAO_SUCESSO)

        }catch(Exception e){
            adicionarMensagemErro(e.getMessage())

        }
    }

    @Listen("onOK = #txtNome")
    def focarTelefone(){
        txtTelefone.focus()
    }

    @Listen("onOK = #txtTelefone")
    def focarEndereco(){
        txtEndereco.focus()
    }

    @Listen("onOK = #txtEndereco")
    def focarEmail(){
        txtEmail.focus()
    }

    @Listen("onOK = #txtEmail")
    def focarBotaoSalvar(){
        btnSalvar.focus()
    }

    @Listen("onClick = #btnCancelar")
    def cancelar(){
        modalEditarFornecedor.detach()
    }

    @Listen("onCancel = #modalEditarFornecedor")
    def fecharModal(){
        modalEditarFornecedor.detach()
    }

}
