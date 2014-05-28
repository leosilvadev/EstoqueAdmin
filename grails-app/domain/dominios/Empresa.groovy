package dominios

class Empresa {

    String nome
    String documento
    String endereco
    Calendar dataRegistro

    Plano plano

    Set<Usuario> usuarios
    Set<Fornecedor> fornecedores
    Set<Produto> produtos
    Set<Pedido> pedidos
    Set<Orcamento> orcamentos
    Set<Venda> vendas
    Set<Mensagem> mensagens
    boolean ativo

    static hasMany = [
            usuarios:Usuario,
            fornecedores:Fornecedor,
            produtos:Produto,
            pedidos:Pedido,
            orcamentos:Orcamento,
            vendas:Venda,
            mensagens:Mensagem
    ]

    static hasOne = [plano:Plano]

    static belongsTo = [Plano]

    static constraints = {
        nome(unique: true, nullable: false, blank: false)
        documento(nullable: false, blank: false)
        usuarios (nullable: true)
    }

    static mapping = {
        nome(index: 'idx_emp_nome')
        documento(index: 'idx_emp_documento')
    }
}
