package venda

import dominios.Venda
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.Datebox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listitem
import org.zkoss.zul.Paging
import org.zkoss.zul.Window
import util.EAComposer
import util.StringUtil


class ListaVendaComposer extends EAComposer {

    def vendaService

    @Wire Datebox dtInicialPesquisa
    @Wire Datebox dtFinalPesquisa
    @Wire Button btnPesquisa
    @Wire Listbox lstVendas
    @Wire Paging paginacao

    def afterCompose = { window ->
        listar(0)
        exibirMensagens()
        ativarRemocaoMensagensAutomatica()
        dtInicialPesquisa.focus()
    }

    def exibirMensagens(){
        if(param.sts!=null){
            if(param.sts.contains("0")){
                adicionarMensagemInformacao("Venda registrada com sucesso")

            }else if(param.sts.contains("1")){
                adicionarMensagemErro("Verifique as informações e tente novamente")

            }else if(param.sts.contains("2")){
                adicionarMensagemInformacao("Informações atualizadas com sucesso")

            }
        }
    }

    @Listen("onClick = #btnNovaVenda")
    def showModal(){
        exibirModal("novaVenda.zul")
    }

    @Listen("onClick = #btnPesquisa")
    def listarPorData(){
        listar(0)
    }

    @Listen("onPaging = #paginacao")
    def paginar(Event e){
        int pagina = e.activePage
        listar(pagina)
    }

    @Listen("onChange = #dtFinalPesquisa")
    def definirDataFinal(){
        btnPesquisa.focus()
    }

    def listar(pagina = 0) {
        lstVendas.items.clear()
        adicionarVendas(vendaService.listar(getCalendarInicial(), getCalendarFinal(), paginacao, pagina))
    }

    def getCalendarInicial(){
        if(dtInicialPesquisa.value){
            Calendar calendar = Calendar.getInstance()
            calendar.time = dtInicialPesquisa.value
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            return calendar
        }
        return null
    }

    def getCalendarFinal(){
        if(dtFinalPesquisa.value){
            Calendar calendar = Calendar.getInstance()
            calendar.time = dtFinalPesquisa.value
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            return calendar
        }
        return null
    }

    def adicionarVendas(Collection<Venda> vendas){
        lstVendas.append {
            vendas.each{ venda ->
                listitem(value: venda) { item ->
                    listcell(label: venda.id)
                    listcell(label: venda.datahora.format("dd/MM/yyyy hh:mm a"))
                    listcell(label: venda.formaPagamento)
                    listcell(label: StringUtil.formataPadraoDinheiro(venda.valorTotal))
                    listcell(label: venda.desconto)
                    listcell(label: StringUtil.formataPadraoDinheiro(venda.valorLiquido))
                    listcell(label: ""){
                        hlayout(style: "text-align:center;"){
                            toolbarbutton(label: "", image:"/images/details3.png",style:"padding-bottom:25px;", onClick: {
                                e -> this.exibirDetalhes(item)
                            })
                        }
                    }
                    listcell(label: ""){
                        hlayout(style: "text-align:center;"){
                            toolbarbutton(label: "", image:"/images/reversal.png",style:"padding-bottom:25px;", onClick: {
                                e -> this.estornarVenda(item)
                            })
                        }
                    }
                    listcell(label: ""){
                        hlayout(style: "text-align:center;"){
                            toolbarbutton(image:"/images/trash.png", onClick: {
                                e -> this.removerVenda(item)
                            })
                        }
                    }
                }
            }
        }
    }

    def exibirDetalhes(Listitem item){
        exibirModal("detalhes.zul", item.value.id)
    }

    def estornarVenda(Listitem item){
        exibirModal("estornoVenda.zul", item.value.id)
    }

    def removerVenda(Listitem item){
        confirmar("Tem certeza que deseja remover esta venda?", new org.zkoss.zk.ui.event.EventListener() {
            @Override
            void onEvent(Event event) throws Exception {
                if (event.getName().equals("onYes")) {
                    vendaService.desativar(item.value.id)
                    item.detach()
                }
            }
        })
    }

}
