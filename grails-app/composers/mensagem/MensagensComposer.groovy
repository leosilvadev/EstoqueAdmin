package mensagem

import dominios.Mensagem
import org.zkoss.zhtml.Div
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Listbox
import org.zkoss.zul.Window
import util.EAComposer


class MensagensComposer extends EAComposer {

    @Wire Window modalMensagens
    @Wire Listbox lstMensagens

    def afterCompose = { window ->
        listarMensagens()
    }

    @Listen("onCancel = #modalMensagens")
    def fechar(){
        modalMensagens.detach()
    }

    def listarMensagens(){
        lstMensagens.append {
            Mensagem.list().each { mensagem ->
                listitem(value: mensagem){ item ->
                    listcell(label: ""){
                        hlayout(style: "text-align:center;"){
                            toolbarbutton(image:"/images/message.png", label: mensagem.mensagem, href: mensagem.link)
                        }
                    }
                }
            }
        }
    }
}
