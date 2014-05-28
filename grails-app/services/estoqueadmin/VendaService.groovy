package estoqueadmin

import dominios.Empresa
import dominios.Log
import dominios.Produto
import dominios.Usuario
import dominios.Venda
import enums.AcaoLog
import enums.TipoDadoLog
import grails.transaction.Transactional
import org.zkoss.zul.Paging

@Transactional
class VendaService {

    def usuarioService

    def salvar(Venda venda){
        Usuario usuario = usuarioService.getUsuarioLogado()
        venda.empresa = usuario.empresa

        if(venda.validate()){
            venda.produtosVendas.each { produtoVenda ->
                Produto.baixaEstoque(produtoVenda.produto.id, produtoVenda.quantidade)
            }
            venda.ativo = true
            venda.save()
            new Log(acaoLog:AcaoLog.INSERCAO,
                    usuario: usuario,
                    codigoObjeto: venda.id,
                    tipoDadoLog: TipoDadoLog.VENDA,
                    descricao: "Salvando venda com id "+venda.id
            ).save(flush:true)

        }else{
            throw new Exception("Venda nao foi cadastrada, erro na validacao de campos!")

        }
    }

    def listar(Calendar dtInicio, Calendar dtFinal, Paging paginacao, pagina = 0){
        List<Venda> vendas
        Empresa empresa = usuarioService.usuarioLogado.empresa
        int offset = pagina * paginacao.pageSize

        if(dtInicio && dtFinal){
            println "Listando com filtro"
            paginacao.totalSize = Venda.countByDatahoraBetweenAndEmpresaAndAtivo(dtInicio, dtFinal, empresa, true)
            vendas = Venda.findAllByDatahoraBetweenAndEmpresaAndAtivo(dtInicio, dtFinal, empresa, true, [sort:"id", offset: offset, max: paginacao.pageSize])

        }else{
            println Venda.countByEmpresaAndAtivo(empresa, true)
            paginacao.totalSize = Venda.countByEmpresaAndAtivo(empresa, true)
            vendas = Venda.findAllByEmpresaAndAtivo(empresa, true, [sort:"id", offset: offset, max: paginacao.pageSize])

        }

        return vendas
    }

    def estornarVenda(Venda venda){
        Usuario usuario = usuarioService.getUsuarioLogado()
        venda.empresa = usuario.empresa

        Venda estorno = new  Venda()
        double valorEstorno = 0
        double valorDesconto = 0
        if(venda.validate()){
            venda.produtosVendas.each { produtoVenda ->
                valorEstorno += produtoVenda.quantidade * produtoVenda.valorUnitario

            }
            valorDesconto = (valorEstorno * venda.desconto)/100
            estorno.valorLiquido = valorEstorno - valorDesconto
            estorno.estorno = true
            estorno.ativo = true
            estorno.datahora = Calendar.getInstance()
            estorno.formaPagamento = venda.formaPagamento
            estorno.empresa = venda.empresa
            estorno.save()
            //adicionar condição para bloquear venda já estornada
            new Log(acaoLog:AcaoLog.ALTERACAO,
                    usuario: usuario,
                    codigoObjeto: venda.id,
                    tipoDadoLog: TipoDadoLog.ESTORNO,
                    descricao: "Estornando venda com id "+venda.id
            ).save(flush:true)

        }else{
            throw new Exception("Venda nao foi estornada, erro!")

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
