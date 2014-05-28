package estoqueadmin

import dominios.Empresa
import dominios.Log
import dominios.Mensagem
import dominios.Produto
import dominios.Usuario
import enums.AcaoLog
import enums.TipoDadoLog
import enums.TipoMensagem
import grails.transaction.Transactional
import org.zkoss.zul.Paging

@Transactional
class ProdutoService {

    def usuarioService

    def listarTodos(){
        Empresa empresa = usuarioService.usuarioLogado.empresa
        return Produto.findAllByEmpresaAndAtivo(empresa, true)
    }


    def listar(String nome){
        if(nome.isEmpty()){
            return listarTodos()
        }
        Empresa empresa = usuarioService.usuarioLogado.empresa
        return Produto.findAllByDescricaoIlikeAndEmpresaAndAtivo(nome+"%", empresa, true)
    }
    
    def listar(String descricao, Paging paginacao, pagina = 0){
        List<Produto> produtos
        Empresa empresa = usuarioService.usuarioLogado.empresa
        int offset = pagina * paginacao.pageSize

        if(descricao){
            paginacao.totalSize = Produto.countByDescricaoIlikeAndEmpresaAndAtivo(descricao+"%", empresa, true)
            produtos = Produto.findAllByDescricaoIlikeAndEmpresaAndAtivo(descricao+"%", empresa, true, [offset: offset, max: paginacao.pageSize])

        }else{
            paginacao.totalSize = Produto.countByEmpresaAndAtivo(empresa, true)
            produtos = Produto.findAllByEmpresaAndAtivo(empresa, true, [sort:"descricao", offset: offset, max: paginacao.pageSize])

        }
        return produtos
    }

    def salvar(Produto produto){
        Usuario usuario = usuarioService.usuarioLogado
        produto.empresa = usuario.empresa
        if(produto.validate()){
            produto.ativo = true
            produto.save()
            new Log(acaoLog:AcaoLog.INSERCAO,
                    usuario: usuario,
                    codigoObjeto: produto.id,
                    tipoDadoLog: TipoDadoLog.PRODUTO,
                    descricao: "Salvando produto "+produto.descricao+" com id "+produto.id).save(flush:true)

        }else{
            throw new Exception("Produto nao foi cadastrado, erro na validacao de campos!")

        }
    }

    void atualizar(Produto produto){
        Usuario usuario = usuarioService.getUsuarioLogado()
        produto.empresa = usuario.empresa
        if(produto.validate()){
            produto.ativo = true
            produto.merge()
            new Log(acaoLog:AcaoLog.ALTERACAO,
                    usuario: usuario,
                    codigoObjeto: produto.id,
                    tipoDadoLog: TipoDadoLog.PRODUTO,
                    descricao: "Atualizando produto "+produto.descricao+" com id "+produto.id).save(flush:true)
        } else
            throw new Exception("Produto nao foi atualizado, erro na validacao de campos!")
    }
    
    def apagarMensagemEstoqueBaixo(){
        Mensagem.findAllByTipoMensagemAndEmpresa(TipoMensagem.ESTOQUE_BAIXO, usuarioService.usuarioLogado.empresa).each {
            it.delete(flush: true)
        }
    }

    def desativar(long id){
        if(id>0){
            Produto produto = Produto.findById(id)
            produto.ativo = false
            produto.save()
        }
    }

    def possuiQuantidadeNoEstoque(long idProduto, int quantidade){
        return Produto.findById(idProduto).quantidade >= quantidade
    }
}
