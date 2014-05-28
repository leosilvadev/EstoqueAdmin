package estoqueadmin

import dominios.Empresa
import dominios.Fornecedor
import dominios.Log
import dominios.Pedido
import dominios.Produto
import dominios.Usuario
import enums.AcaoLog
import enums.FormaPagamento
import enums.StatusPedido
import enums.TipoDadoLog
import grails.transaction.Transactional
import org.zkoss.zul.Paging

@Transactional
class PedidoService {

    def usuarioService

    void registrarPedido(Pedido pedido){
        Usuario usuario = usuarioService.usuarioLogado
        pedido.empresa = usuario.empresa
        pedido.status = StatusPedido.ABERTO
        if(pedido.validate()){
            pedido.ativo = true
            pedido.save()
            new Log(acaoLog:AcaoLog.INSERCAO,
                    usuario: usuario,
                    codigoObjeto: pedido.id,
                    tipoDadoLog: TipoDadoLog.PEDIDO,
                    descricao: "Registrando pedido "+pedido.id+" com status Aberto").save(flush:true)
        } else
            throw new Exception("Pedido nao foi cadastrado, erro na validacao de campos!")


    }

    void efetivarPedido(Pedido pedido){
        pedido.status = StatusPedido.EFETIVADO
        if(pedido.validate()){
            pedido.itensPedidos.each { itemPedido ->
                Produto.adicionaEstoque(itemPedido.produto.id, itemPedido.quantidade)
            }
            pedido.merge()
            Usuario usuario = usuarioService.usuarioLogado
            new Log(acaoLog:AcaoLog.ALTERACAO,
                    usuario: usuario,
                    codigoObjeto: pedido.id,
                    tipoDadoLog: TipoDadoLog.PEDIDO,
                    descricao: "Efetivando pedido com código "+pedido.id).save(flush:true)

        }else{
            throw new Exception("Pedido nao foi cadastrado, erro na validacao de campos!")

        }
    }

    List<Pedido> listar(Fornecedor fornecedor, Paging paginacao, pagina = 0){
        List<Pedido> pedidos
        Empresa empresa = usuarioService.usuarioLogado.empresa
        int offset = pagina * paginacao.pageSize

        if(fornecedor){
            paginacao.totalSize = Pedido.countByFornecedorAndEmpresaAndAtivo(fornecedor, empresa, true)
            pedidos = Pedido.findAllByFornecedorAndEmpresaAndAtivo(fornecedor, empresa, true, [sort:"id", offset: offset, max: paginacao.pageSize])

        }else{
            paginacao.totalSize = Pedido.countByEmpresaAndAtivo(empresa, true)
            pedidos = Pedido.findAllByEmpresaAndAtivo(empresa, true, [sort:"id", offset: offset, max: paginacao.pageSize])

        }

        return pedidos
    }
}
