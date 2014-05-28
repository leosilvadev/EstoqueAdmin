package template

import dominios.Mensagem
import dominios.Usuario
import org.zkoss.zhtml.A
import org.zkoss.zhtml.Div
import org.zkoss.zhtml.Li
import org.zkoss.zhtml.Span
import org.zkoss.zk.grails.composer.*

import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Label
import util.EAComposer

class CabecalhoComposer extends EAComposer {

    def mensagemService
    def usuarioService

    @Wire Div divMensagens
    @Wire A linkMensagens
    @Wire Span spanMensagens

    def afterCompose = { window ->
        verificarSeHaMensagens()
        Usuario usuario = usuarioService.usuarioLogado
    }

    def verificarSeHaMensagens(){
        if(mensagemService.contemMensagem()){
            linkMensagens.style = "color: yellow; font-weight: bold;"
        }else{
            linkMensagens.style = "pointer-events: none;opacity: 0.5;"
        }
    }

    @Listen("onClick = #linkMensagens")
    def exibirMensagens(){
        exibirModal("../mensagem/mensagens.zul")
    }
}
