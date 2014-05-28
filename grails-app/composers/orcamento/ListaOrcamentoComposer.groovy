package orcamento

import de.andreasschmitt.export.ExportService
import dominios.Orcamento
import dominios.ProdutoOrcamento
import dominios.Usuario
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Datebox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listitem
import org.zkoss.zul.Paging
import util.EAComposer
import util.StringUtil

import javax.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat


class ListaOrcamentoComposer extends EAComposer {

    def orcamentoService

    @Wire Datebox dtInicialPesquisa
    @Wire Datebox dtFinalPesquisa
    @Wire Button btnPesquisa
    @Wire Listbox lstOrcamentos
    @Wire Paging paginacao

    def afterCompose = { window ->
        listar(0)
        exibirMensagens()
        ativarRemocaoMensagensAutomatica()
    }

    def exibirMensagens(){
        if(param.sts!=null){
            if(param.sts.contains("0")){
                adicionarMensagemInformacao("Orcamento registrado com sucesso")

            }else if(param.sts.contains("1")){
                adicionarMensagemErro("Verifique as informações e tente novamente")

            }else if(param.sts.contains("2")){
                adicionarMensagemInformacao("Informações atualizadas com sucesso")

            }
        }
    }

    @Listen("onClick = #btnNovoOrcamento")
    def showModal(){
        exibirModal("novoOrcamento.zul")
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



    @Listen("onChange = #dtInicialPesquisa")
    def selecionarCalendarioInicial(){
        dtFinalPesquisa.focus()
    }

    @Listen("onChange = #dtFinalPesquisa")
    def selecionarCalendarioFinal(){
        btnPesquisa.focus()
    }




    def listar(int pagina = 0) {
        lstOrcamentos.items.clear()
        adicionarOrcamentos(orcamentoService.listar(getCalendarInicial(), getCalendarFinal(), paginacao, pagina))
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

    def adicionarOrcamentos(Collection<Orcamento> orcamentos){
        lstOrcamentos.append {
            orcamentos.each{ orcamento ->
                listitem(value: orcamento) { item ->
                    listcell(label: orcamento.id)
                    listcell(label: orcamento.datahora.format("dd/MM/yyyy hh:mm a"))
                    listcell(label: StringUtil.formataPadraoDinheiro(orcamento.valorTotal))
                    listcell(label: orcamento.desconto)
                    listcell(label: StringUtil.formataPadraoDinheiro(orcamento.valorLiquido))
                    listcell(label: ""){
                        hlayout(style: "text-align:center;"){
                            toolbarbutton(label: "", image:"/images/details.png",style:"padding-bottom:25px;", onClick: {
                                e -> this.exibirDetalhes(item)
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

}
