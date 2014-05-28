package estoqueadmin

import dominios.Empresa
import dominios.Mensagem
import dominios.Produto
import enums.TipoMensagem
import grails.transaction.Transactional

@Transactional
class MensagemService {

    def usuarioService

    int existemMensagensSobre(TipoMensagem tipoMensagem, Empresa empresa){
        return Mensagem.countByTipoMensagemAndEmpresa(tipoMensagem, empresa)
    }

    List<Mensagem> listarMensagens(){
        return Mensagem.findAllByEmpresa(usuarioService.usuarioLogado.empresa)
    }
    
    boolean contemMensagem(){
        return Mensagem.countByEmpresa(usuarioService.usuarioLogado.empresa)>0
    }

    void adicionarMensagens(List<Produto> produtos){
        for(Produto produto : produtos){
            Empresa empresa = Produto.findById(produto.id).empresa
            if(existemMensagensSobre(TipoMensagem.ESTOQUE_BAIXO, empresa)==0){
                Mensagem mensagem = new Mensagem(
                        tipoMensagem: TipoMensagem.ESTOQUE_BAIXO,
                        datahoraDeteccao: Calendar.getInstance(),
                        mensagem: "Há produtos com quantidade no estoque abaixo do esperado",
                        link: "../produto/listaProduto.zul?ESTOQUE=BAIXO",
                        empresa: empresa
                )
                mensagem.save()
            }
        }

    }


}
