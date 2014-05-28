package dominios

import enums.AcaoLog
import enums.TipoDadoLog

import javax.persistence.EnumType


class Log {

    AcaoLog acaoLog
    TipoDadoLog tipoDadoLog
    Long codigoObjeto

    Calendar dataAcao = Calendar.getInstance()
    Usuario usuario

    String descricao

    static belongsTo = [Usuario]

    static hasOne = [usuario:Usuario]

    static constraints = {
        acaoLog nullable:false
        dataAcao nullable:false
    }

    static mapping = {
        tipoDadoLog(enumType: "ordinal")
        acaoLog(enumType: "ordinal")
        dataAcao(index: 'idx_log_data')
    }

}
