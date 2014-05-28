package dominios

class Permissao {

    String nivelPermissao

    static mapping = {
        cache true
    }

    static constraints = {
        nivelPermissao (blank: false, unique: true)
    }
}
