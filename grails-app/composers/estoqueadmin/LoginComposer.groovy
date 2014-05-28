package estoqueadmin

import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Textbox
import util.EAComposer


class LoginComposer extends EAComposer {

    @Wire Textbox j_username
    @Wire Textbox j_password
    @Wire Button btnEntrar

    def afterCompose = { window ->
        j_username.focus()
    }

    @Listen("onOK = #j_username")
    def inserirLogin(){
        j_password.focus()
    }

    @Listen("onOK = #j_password")
    def inserirSenha(){
        btnEntrar.focus()
    }
}
