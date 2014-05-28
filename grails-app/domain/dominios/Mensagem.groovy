package dominios

import enums.TipoMensagem


class Mensagem {

    String mensagem
    String link

    TipoMensagem tipoMensagem
    Calendar datahoraDeteccao

    Empresa empresa

    static hasOne = [empresa:Empresa]

    static belongsTo = [Empresa]

    static constraints = {
        link nullable: true
    }

    static mapping = {
        tipoMensagem(enumType: "ordinal")
    }
}
