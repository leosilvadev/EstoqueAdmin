package dominios

import enums.FormaPagamento
import enums.StatusPedido


class Pedido {

    Calendar dataHora
    double valorTotal

    StatusPedido status
    FormaPagamento formaPagamento

    boolean ativo

    Fornecedor fornecedor
    Set<ItemPedido> itensPedidos

    Empresa empresa

    static hasOne = [empresa:Empresa]
    static hasMany = [itensPedidos:ItemPedido]

    static belongsTo = [Empresa]

    static constraints = {
        dataHora (nullable:false)
        formaPagamento(nullable: true)
    }

    static mapping = {
        status(enumType: "ordinal")
        formaPagamento(enumType: "ordinal")
        dataHora(index: 'idx_ped_data')
    }

}