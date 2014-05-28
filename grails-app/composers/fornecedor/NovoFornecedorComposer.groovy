package fornecedor

import dominios.Fornecedor
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.Messagebox
import org.zkoss.zul.Textbox
import org.zkoss.zul.Window
import util.EAComposer


class NovoFornecedorComposer extends EAComposer {

    def fornecedorService

    @Wire Window modalNovoFornecedor
    @Wire Textbox txtNome
    @Wire Textbox txtTelefone
    @Wire Textbox txtEndereco
    @Wire Textbox txtEmail
    @Wire Button btnSalvar

    def afterCompose = { window ->
        Clients.evalJavaScript("aplicarMascaras();")
        txtNome.focus()
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

    @Listen("onCancel = #modalNovoFornecedor")
    def fecharModal(){
        modalNovoFornecedor.detach()
    }


    @Listen("onClick = #btnSalvar")
    def salvarFornecedor(){
        Fornecedor fornecedor = new Fornecedor(
                nome: txtNome.value,
                telefone: txtTelefone.value,
                endereco: txtEndereco.value,
                email: txtEmail.value)

        try{
            fornecedorService.salvar(fornecedor)
            redirect(uri:"listaFornecedor.zul"+RESULTADO_SUCESSO)

        }catch(Exception e){
            adicionarMensagemErroModal(e.getMessage())

        }
    }

    @Listen("onClick = #btnCancelar")
    def cancelar(){
        modalNovoFornecedor.detach()
    }
}
