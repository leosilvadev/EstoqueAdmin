package dominios

class Plano {

    String nome
    String descricao
    int limiteUsuarios
    int limiteFornecedores
    int limiteProdutos
    int limiteServicos
    int boletos
    boolean dashboard
    double valor

    Set<Empresa> empresas

    static hasMany = [empresas:Empresa]

    static constraints = {
        nome(nullable: false, blank: false)
        descricao(nullable: false, blank: false)
    }
}
