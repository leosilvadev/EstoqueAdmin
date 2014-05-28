package estoqueadmin

import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.Label
import org.zkoss.zul.Textbox
import util.EAComposer


class IndexComposer extends EAComposer {

    @Wire Textbox txtEmail
    @Wire Button btnExperimentar
    @Wire Label lblMensagem

    def afterCompose = { window ->
        txtEmail.focus()
    }

    @Listen("onOK = #txtEmail")
    def inserirEmailParaExperimentacao(){
        enviarEmailParaExperimentacao()
    }

    @Listen("onClick = #btnExperimentar")
    def enviarEmailParaExperimentacao(){
        if(validarEmail()){
            lblMensagem.value = "Agradecemos o interesse, em breve entraremos em contato com você!"
            txtEmail.value = null
            lblMensagem.style = "color:white;"
            redirect(uri: 'cadastro.zul')

        }else{
            lblMensagem.value = "E-mail inválido, digite-o corretamente para que possamos entrar com contato com você!"
            lblMensagem.style = "color:navajowhite;"

        }
    }

    def validarEmail(){
        String emailPattern = /[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})/
        return txtEmail.value ==~ emailPattern
    }

}
