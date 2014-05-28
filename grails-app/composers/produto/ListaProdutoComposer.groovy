package produto

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.grails.composer.*
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import dominios.Produto
import util.EAComposer

class ListaProdutoComposer extends EAComposer {

    def estoqueService
    def produtoService

    @Wire Listbox lstProdutos
    @Wire Paging paginacao
    @Wire Textbox txtPesquisaDescricao

    def afterCompose = { window ->
        if(param.ESTOQUE.toString().equals("[BAIXO]")){
            listarProdutosAComprar()
            apagarMensagensDeProdutosAComprar()
        }else{
            listar(0, null)
        }
        exibirMensagens()
        ativarRemocaoMensagensAutomatica()
        txtPesquisaDescricao.focus()
    }

    def exibirMensagens(){
        if(param.sts!=null){
            if(param.sts.contains("0")){
                adicionarMensagemInformacao("Produto registrado com sucesso")

            }else if(param.sts.contains("1")){
                adicionarMensagemErro("Verifique as informações e tente novamente")

            }else if(param.sts.contains("2")){
                adicionarMensagemInformacao("Informações atualizadas com sucesso")

            }
        }
    }

    def listarProdutosAComprar(){
        lstProdutos.items.clear()
        adicionarProdutos(estoqueService.listarProdutosBaixoNivelEstoque())
    }

    def apagarMensagensDeProdutosAComprar(){
        produtoService.apagarMensagemEstoqueBaixo()
    }

    @Listen("onClick = #btnNovoProduto")
    def showModal() {
        exibirModal("novoProduto.zul")
    }

    @Listen("onPaging = #paginacao")
    def paginar(Event e){
        int pagina = e.activePage
        listar(pagina, null)
    }

    @Listen("onChange = #txtPesquisaDescricao")
    def pesquisarPorDescricao(){
        listar(paginacao._actpg, txtPesquisaDescricao.value)
    }

    def listar(int pagina = 0, String descricao) {
        lstProdutos.items.clear()
        adicionarProdutos(produtoService.listar(descricao, paginacao, pagina))
    }

    def adicionarProdutos(Collection<Produto> produtos){
        lstProdutos.append {
            produtos.each{ produto ->
                listitem(value: produto, onDoubleClick: {
                    this.selecionarProduto(produto.id)
                }) { item ->
                    listcell(label: produto.codigo)
                    listcell(
                            label: produto.descricao.length() >= 25 ? produto.descricao.substring(0,25)+"..." : produto.descricao,
                            tooltiptext: produto.descricao
                    )
                    listcell(
                            label: produto.marca.length()>=10 ? produto.marca.substring(0,10)+"..." : produto.marca,
                            tooltiptext: produto.marca
                    )
                    listcell(label: produto.unidade)
                    listcell(label: produto.precoVenda)
                    listcell(label: produto.quantidade)
                    listcell(label: produto.quantidadeMinima)
                    listcell(label: ""){
                        hlayout(style: "text-align:center;"){
                            toolbarbutton(image:"/images/edit.png", onClick: {
                                e -> this.selecionarProduto(produto.id)
                            })
                        }
                    }
                    listcell(label: ""){
                        hlayout(style: "text-align:center;"){
                            toolbarbutton(image:"/images/trash.png", onClick: {
                                e -> this.removerProduto(produto.id)
                            })
                        }
                    }
                }
            }
        }
    }

    private void selecionarProduto(long id){
        exibirModal("editarProduto.zul", id)
    }

    private void removerProduto(long id){
        confirmar("Tem certeza que deseja remover este produto?", new org.zkoss.zk.ui.event.EventListener() {
            @Override
            void onEvent(Event event) throws Exception {
                if (event.getName().equals("onYes")) {
                    produtoService.desativar(id)
                    listar(0, null)
                }
            }
        })
    }

}
