package pedido

import dominios.Fornecedor
import dominios.Pedido
import enums.StatusPedido
import org.hibernate.criterion.MatchMode
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Combobox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listitem
import org.zkoss.zul.Paging
import org.zkoss.zul.Textbox
import org.zkoss.zul.Window
import util.EAComposer


class ListaPedidoComposer extends EAComposer {

    def pedidoService
    def fornecedorService

    @Wire Listbox lstPedidos
    @Wire Paging paginacao
    @Wire Combobox cbFornecedor

    def afterCompose = { window ->
        listarFornecedores()
        listar(0, null)
        exibirMensagens()
        ativarRemocaoMensagensAutomatica()
        cbFornecedor.focus()
    }

    def exibirMensagens(){
        if(param.sts!=null){
            if(param.sts.contains("0")){
                adicionarMensagemInformacao("Pedido registrado com sucesso")

            }else if(param.sts.contains("1")){
                adicionarMensagemErro("Verifique as informações e tente novamente")

            }else if(param.sts.contains("2")){
                adicionarMensagemInformacao("Informações atualizadas com sucesso")

            }
        }
    }

    def listarFornecedores(){
        cbFornecedor.append {
            fornecedorService.listarTodos().each{ fornecedor ->
                comboitem(label: fornecedor.nome, value: fornecedor)
            }
        }
    }

    @Listen("onClick = #btnNovoPedido")
    def showModal(){
        exibirModal("novoPedido.zul")
    }

    def efetivarPedido(long idPedido){
        exibirModal("efetivarPedido.zul", idPedido)
    }

    @Listen("onPaging = #paginacao")
    def paginar(Event e){
        int pagina = e.activePage;
        listar(pagina, null)
    }

    @Listen("onSelect = #cbFornecedor")
    def pesquisarPorFornecedor(){
        if(cbFornecedor.selectedItem)
            listar(paginacao._actpg, cbFornecedor.selectedItem.value)

        else
            listar(paginacao._actpg, null)
    }

    def listar(int pagina = 0, Fornecedor fornecedor) {
        lstPedidos.items.clear()
        adicionarPedidos(pedidoService.listar(fornecedor, paginacao, pagina))
    }

    def adicionarPedidos(Collection<Pedido> pedidos){
        lstPedidos.append {
            pedidos.each {  pedido ->
                listitem(value: pedido, sclass: "item_pedido") { item ->
                    listcell(label: pedido.id)
                    listcell(label: pedido.fornecedor.nome)
                    listcell(label: pedido.dataHora.format("dd/MM/yyyy hh:mm a"))
                    listcell(label: pedido.valorTotal)
                    listcell(label: pedido.status)

                    listcell(label: ""){
                        hlayout{
                            if(pedido.status.equals(StatusPedido.ABERTO)){
                                toolbarbutton(label: "Efetivar", image:"/images/confirm.png", onClick: {
                                    e -> this.efetivarPedido(pedido.id)
                                })
                            }
                        }
                    }
                    listcell(label: ""){
                        hlayout{
                            if(pedido.status.equals(StatusPedido.ABERTO)){
                                toolbarbutton(label: "Cancelar", image:"/images/cancel.png", onClick: {
                                    e -> this.removerPedido(item)
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    def removerPedido(Listitem item){
        confirmar("Tem certeza que deseja cancelar este pedido?", new org.zkoss.zk.ui.event.EventListener() {
            @Override
            void onEvent(Event event) throws Exception {
                if (event.getName().equals("onYes")) {
                    Pedido pedido = item.value
                    pedido.ativo = false
                    pedido.merge()
                    listar(0, null)
                }
            }
        })
    }

}
