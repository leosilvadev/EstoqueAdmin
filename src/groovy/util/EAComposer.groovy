package util

import dominios.Fornecedor
import org.zkoss.zhtml.Div
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.WrongValueException
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Label
import org.zkoss.zul.Messagebox
import org.zkoss.zul.Textbox
import org.zkoss.zul.Window

import java.text.DecimalFormat

/**
 * Created by leonardo on 19/01/14.
 */
class EAComposer extends zk.grails.Composer {

    @Wire
    Div divMensagens

    @Wire
    Div divMensagensModal

    static final String RESULTADO_SUCESSO = "?sts=0"
    static final String RESULTADO_ERRO    = "?sts=1"
    static final String RESULTADO_ALTERACAO_SUCESSO = "?sts=2"

    def ativarRemocaoMensagensAutomatica(){
        Clients.evalJavaScript("removerMensagens();")
    }

    def formataDinheiro(double valor){
        return StringUtil.formataPadraoDinheiro(valor)
    }

    def adicionarMensagemInformacao(String mensagem){
        divMensagens.children.clear()
        divMensagens.sclass = "msgInfo mensagem"
        divMensagens.append{
            div(style: "margin-bottom:5px;"){
                span(sclass: " glyphicon glyphicon-info-sign")
                span(sclass: "divisoria")
                label(value: "Informação", style: "font-size:1.3em;")
            }
            div(){
                label(value: mensagem)
            }
        }
    }

    def adicionarMensagemAviso(String mensagem){
        divMensagens.children.clear()
        divMensagens.sclass = "msgAviso mensagem"
        divMensagens.append{
            div(style: "margin-bottom:5px;"){
                span(sclass: "glyphicon glyphicon-exclamation-sign")
                span(sclass: "divisoria")
                label(value: "Aviso", style: "font-size:1.3em;")
            }
            div(){
                label(value: mensagem)
            }
        }
    }

    def adicionarMensagemErro(String mensagem){
        divMensagens.children.clear()
        divMensagens.sclass = "msgErro mensagem"
        divMensagens.append{
            div(style: "margin-bottom:5px;"){
                span(sclass: " glyphicon glyphicon-thumbs-down")
                span(sclass: "divisoria")
                label(value: "Erro", style: "font-size:1.3em;")
            }
            div(){
                label(value: mensagem)
            }
        }
    }

    def adicionarMensagemInformacaoModal(String mensagem){
        divMensagensModal.children.clear()
        divMensagensModal.sclass = "msgInfo"
        divMensagensModal.append{
            span(sclass: " glyphicon glyphicon-info-sign")
            span(sclass: "divisoria")
            label(value: mensagem)
        }
    }

    def adicionarMensagemAvisoModal(String mensagem){
        divMensagensModal.children.clear()
        divMensagensModal.sclass = "msgAviso"
        divMensagensModal.append{
            span(sclass: "glyphicon glyphicon-exclamation-sign")
            span(sclass: "divisoria")
            label(value: mensagem)
        }
    }

    def adicionarMensagemErroModal(String mensagem){
        divMensagensModal.children.clear()
        divMensagensModal.sclass = "msgErro"
        divMensagensModal.append{
            span(sclass: " glyphicon glyphicon-thumbs-down")
            span(sclass: "divisoria")
            label(value: mensagem)
        }
    }

    def exibirModal(String urlPagina){
        Window window = (Window)Executions.createComponents(urlPagina, null, null)
        window.doModal()
    }

    def exibirModal(String urlPagina, long id){
        Window window = (Window)Executions.createComponents(urlPagina, null, [idObjeto: id])
        window.doModal()
    }

    def confirmar(String mensagem, org.zkoss.zk.ui.event.EventListener event){
        Messagebox.show(
                mensagem,
                "Confirmação",
                Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                event);
    }

    def gerarErroValidacao(Component componente, String mensagem){
        throw new WrongValueException(componente, mensagem)
    }

}
