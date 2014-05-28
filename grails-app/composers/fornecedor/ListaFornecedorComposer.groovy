package fornecedor

import dominios.Fornecedor
import org.zkoss.zhtml.Messagebox
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listitem
import org.zkoss.zul.Paging
import org.zkoss.zul.Textbox
import org.zkoss.zul.Window
import util.EAComposer


class ListaFornecedorComposer extends EAComposer {

    def fornecedorService
    def pedidoService

    @Wire Listbox lstFornecedores
    @Wire Paging paginacao
    @Wire Textbox txtPesquisaNome

    def afterCompose = { window ->
        listar(0, null)
        exibirMensagens()
        ativarRemocaoMensagensAutomatica()
        txtPesquisaNome.focus()
    }

    def exibirMensagens(){
        if(param.sts!=null){
            if(param.sts.contains("0")){
                adicionarMensagemInformacao("Fornecedor registrado com sucesso")

            }else if(param.sts.contains("1")){
                adicionarMensagemErro("Verifique as informações e tente novamente")

            }else if(param.sts.contains("2")){
                adicionarMensagemInformacao("Informações atualizadas com sucesso")

            }
        }
    }

    @Listen("onClick = #btnNovoFornecedor")
    def showModal(Event e) {
        exibirModal("novoFornecedor.zul")
    }

    @Listen("onPaging = #paginacao")
    def paginar(Event e){
        int pagina = e.activePage;
        listar(pagina, null)
    }

    @Listen("onChange = #txtPesquisaNome")
    def pesquisarPorNome(){
        listar(paginacao._actpg, txtPesquisaNome.value)
    }

    def listar(int pagina = 0, String nome) {
        lstFornecedores.items.clear()
        adicionarFornecedores(fornecedorService.listar(nome, paginacao, pagina))
    }

    def adicionarFornecedores(Collection<Fornecedor> fornecedores){
        lstFornecedores.append {
            fornecedores.each{ fornecedor ->
                listitem(value: fornecedor, sclass: "item_fornecedor", onDoubleClick: {
                    this.selecionarFornecedor(fornecedor.id)
                }) { item ->
                    listcell(label: fornecedor.id)
                    listcell(
                            label: fornecedor.nome.length() >= 20 ? fornecedor.nome.substring(0,20)+"..." : fornecedor.nome,
                            tooltiptext: fornecedor.nome
                    )
                    listcell(label: fornecedor.telefone)
                    listcell(
                            label: fornecedor.endereco.length() >= 25 ? fornecedor.endereco.substring(0,25)+"..." : fornecedor.endereco,
                            tooltiptext: fornecedor.endereco
                    )
                    listcell(label: fornecedor.email)
                    listcell(label: ""){
                        hlayout(style: "text-align:center;"){
                            toolbarbutton(image:"/images/edit.png", onClick: {
                                e -> this.selecionarFornecedor(fornecedor.id)
                            })
                        }
                    }
                    listcell(label: ""){
                        hlayout(style: "text-align:center;"){
                            toolbarbutton(image:"/images/trash.png", onClick: {
                                e -> this.removerFornecedor(fornecedor.id)
                            })
                        }
                    }
                }
            }
        }
    }

    def selecionarFornecedor(long id){
        exibirModal("editarFornecedor.zul", id)
    }

    def removerFornecedor(Serializable id){
        confirmar("Tem certeza que deseja remover este fornecedor?", new org.zkoss.zk.ui.event.EventListener() {
            @Override
            void onEvent(Event event) throws Exception {
                if (event.getName().equals("onYes")) {
                    fornecedorService.desativar(id)
                    listar(0, null)
                }
            }
        })
    }

}
