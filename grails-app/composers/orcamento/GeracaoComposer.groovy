package orcamento

import dominios.Orcamento
import dominios.ProdutoOrcamento
import org.zkoss.zk.ui.Executions
import util.EAComposer
import util.StringUtil

import javax.servlet.http.HttpServletResponse
import java.text.DecimalFormat


class GeracaoComposer extends EAComposer {

    def usuarioService
    def exportService

    def afterCompose = { window ->
        int idOrcamento = Integer.parseInt(param.orcamento[0])
        if(idOrcamento>0){
            gerarPDF(Orcamento.findById(idOrcamento))
        }
    }

    def gerarPDF(Orcamento orcamento){
        if(orcamento.empresa.equals(usuarioService.usuarioLogado.empresa)){
            HttpServletResponse response = (HttpServletResponse)Executions.current.nativeResponse
            response.setContentType("application/pdf")
            response.setHeader("Content-disposition", "attachment;filename=orcamento_${StringUtil.gerarStringDataHora("ddMMyyyy_hhmmss")}.pdf")

            List fields = ["produto.descricao", "quantidade", "valorUnitario", "valor"]
            Map labels = ["produto.descricao": "Produto", quantidade: "Quantidade", "valorUnitario": "Valor unitário" ,valor: "Valor Total"]
            Map formatters = [valor: exibeDinheiro]
            Map parameters = [title: definirTitulo(orcamento)]

            exportService.export('pdf', response.outputStream, ProdutoOrcamento.findAllByOrcamento(orcamento), fields, labels, formatters, parameters)
        }
    }

    def definirTitulo = { orcamento ->
        "Orçamento gerado em ${StringUtil.gerarStringDataHora("dd/MM/yyyy hh:mm:ss")} - Valor: ${formataDinheiro(orcamento.valorTotal)} - Cliente: ${orcamento.cliente}"
    }

    def exibeProduto = { domain, value ->
        return value.descricao
    }
    def exibeDinheiro = { domain, value ->
        return formataDinheiro(value)
    }
}
