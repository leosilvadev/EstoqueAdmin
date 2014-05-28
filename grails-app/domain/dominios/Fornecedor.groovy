package dominios

class Fornecedor {

    String nome
    String telefone
    String endereco
    String email
    boolean ativo

    Set<Produto> produtos
    Empresa empresa

    static hasOne = [empresa:Empresa]
    static hasMany = [produtos:Produto]

    static belongsTo = [Empresa]

    static constraints = {
        nome (nullable:false, blank: false)
        telefone (nullable: false, blank:false, matches: "\\([0-9]{2}\\)[0-9]+")
        endereco (nullable:false, blank: false)
        email (email: true, nullable: true, blank: true)
    }

    String toString() {
        nome
    }

    static mapping = {
        nome(index: 'idx_for_nome')
        email(index: 'idx_for_email')
    }

}
