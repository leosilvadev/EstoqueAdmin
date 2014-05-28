package estoqueadmin

import dominios.Empresa
import dominios.Log
import dominios.Orcamento
import dominios.Produto
import dominios.Usuario
import dominios.Venda
import enums.AcaoLog
import enums.TipoDadoLog
import grails.transaction.Transactional
import org.zkoss.zul.Paging

@Transactional
class OrcamentoService {

    def usuarioService

    def salvar(Orcamento orcamento){
        Usuario usuario = usuarioService.getUsuarioLogado()
        orcamento.empresa = usuario.empresa

        if(orcamento.validate()){
            orcamento.ativo = true
            orcamento.save()
            new Log(acaoLog:AcaoLog.INSERCAO,
                    usuario: usuario,
                    codigoObjeto: orcamento.id,
                    tipoDadoLog: TipoDadoLog.VENDA,
                    descricao: "Salvando venda com id "+orcamento.id
            ).save(flush:true)

        }else{
            throw new Exception("Venda nao foi cadastrada, erro na validacao de campos!")

        }
    }

    def listar(Calendar dtInicio, Calendar dtFinal, Paging paginacao, pagina = 0){
        Empresa empresa = usuarioService.usuarioLogado.empresa
        int offset = 0 * paginacao.pageSize
        if(dtInicio && dtFinal){
            paginacao.totalSize = Orcamento.countByDatahoraBetweenAndEmpresaAndAtivo(dtInicio, dtFinal, empresa, true)
            return Orcamento.findAllByDatahoraBetweenAndEmpresaAndAtivo(dtInicio, dtFinal, empresa, true, [sort:"id", offset: offset, max: paginacao.pageSize])

        }else{
            paginacao.totalSize = Orcamento.countByEmpresaAndAtivo(empresa, true)
            return Orcamento.findAllByEmpresaAndAtivo(empresa, true, [sort:"id", offset: offset, max: paginacao.pageSize])
        }
    }

    def desativar(Serializable id){
        if(id>0){
            Venda venda = Venda.findById(id)
            venda.ativo = false
            venda.save()
        }
    }
}
