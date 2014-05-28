package estoqueadmin

import dominios.Empresa
import dominios.Log
import dominios.Usuario
import enums.AcaoLog
import enums.TipoDadoLog
import grails.transaction.Transactional

@Transactional
class EmpresaService {

    def usuarioService

    def salvar(Empresa empresa){
        if(empresa.validate()){
            empresa.ativo = true
            empresa.save()

        }else{
            throw new Exception("Empresa nao foi cadastrada, erro na validacao de campos!")

        }
    }

}
