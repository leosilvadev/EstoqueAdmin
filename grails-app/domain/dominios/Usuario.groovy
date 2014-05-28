package dominios

class Usuario {

    transient springSecurityService

    String login
    String senha
    boolean ativo
    boolean contaExpirada
    boolean contaBloqueada
    boolean senhaExpirada

    Empresa empresa
    Set<Log> logs

    static hasOne = [empresa:Empresa]
    static hasMany = [logs:Log]

    static belongsTo = [Empresa]

    static constraints = {
        login (blank: false, unique: true)
        senha (blank: false)
    }

    static mapping = {
        login(index: 'idx_usu_login')
    }

    Set<Permissao> getAuthorities() {
        UsuarioPermissao.findAllByUsuario(this).collect { it.permissao } as Set
    }

    def beforeInsert() {
        codificarSenha()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            codificarSenha()
        }
    }

    def codificarSenha() {
        senha = springSecurityService.encodePassword(senha)
    }
}
