package dominios

class ItemPedido {

    int quantidade
    double precoCompra

    Produto produto
    Pedido pedido

    static belongsTo = [Pedido]

    static hasOne = [pedido:Pedido, produto:Produto]

    static constraints = {
        quantidade (min: 0, nullable: false)
        precoCompra (min: new Double(0), nullable: true) //registrado apenas na efetivação do pedido
    }
}
