package estoqueadmin

import dominios.Empresa
import dominios.Fornecedor
import dominios.Log
import dominios.Usuario
import enums.AcaoLog
import enums.TipoDadoLog
import grails.transaction.Transactional
import org.zkoss.zul.Paging

@Transactional
class FornecedorService {

    def usuarioService

    def listar(String nome){
        Empresa empresa = usuarioService.usuarioLogado.empresa
        if(nome==null || nome.isEmpty()){
            return listarTodos()
        }
        return Fornecedor.findAllByNomeIlikeAndEmpresaAndAtivo(nome+"%", empresa, true)
    }

    def listar(String nome, Paging paginacao, pagina = 0){
        List<Fornecedor> fornecedores
        Usuario usuarioLogado = usuarioService.usuarioLogado
        int offset = pagina * paginacao.pageSize;

        if(nome){
            paginacao.totalSize = Fornecedor.countByNomeIlikeAndEmpresaAndAtivo(nome+"%", usuarioLogado.empresa, true)
            fornecedores = Fornecedor.findAllByNomeIlikeAndEmpresaAndAtivo(nome+"%", usuarioLogado.empresa, true, [offset: offset, max: paginacao.pageSize])

        }else{
            paginacao.totalSize = Fornecedor.countByEmpresaAndAtivo(usuarioLogado.empresa, true)
            fornecedores = Fornecedor.findAllByEmpresaAndAtivo(usuarioLogado.empresa, true, [sort:"nome", offset: offset, max: paginacao.pageSize])

        }
        return fornecedores
    }

    def listarTodos(){
        return Fornecedor.findAllByEmpresaAndAtivo(usuarioService.usuarioLogado.empresa, true)
    }

    def salvar(Fornecedor fornecedor){
        Usuario usuario = usuarioService.getUsuarioLogado()
        fornecedor.empresa = usuario.empresa
        if(fornecedor.validate()){
            fornecedor.ativo = true
            fornecedor.save()
            new Log(acaoLog:AcaoLog.INSERCAO,
                    usuario: usuario,
                    codigoObjeto: fornecedor.id,
                    tipoDadoLog: TipoDadoLog.FORNECEDOR,
                    descricao: "Salvando fornecedor "+fornecedor.nome+" com id "+fornecedor.id).save(flush:true)
        } else {
            throw new Exception("Fornecedor nao foi cadastrado, erro na validacao de campos!")

        }
    }

    def atualizar(Fornecedor fornecedor){
        Usuario usuario = usuarioService.getUsuarioLogado()
        fornecedor.empresa = usuario.empresa
        if(fornecedor.validate()){
            fornecedor.ativo = true
            fornecedor.merge()
            new Log(acaoLog:AcaoLog.ALTERACAO,
                    usuario: usuario,
                    codigoObjeto: fornecedor.id,
                    tipoDadoLog: TipoDadoLog.FORNECEDOR,
                    descricao: "Atualizando fornecedor "+fornecedor.nome+" com id "+fornecedor.id).save(flush:true)
        } else
            throw new Exception("Fornecedor nao foi atualizado, erro na validacao de campos!")
    }

    def desativar(Serializable id){
        if(id>0){
            Fornecedor fornecedor = Fornecedor.findById(id)
            fornecedor.ativo = false
            fornecedor.save()
        }
    }

}
