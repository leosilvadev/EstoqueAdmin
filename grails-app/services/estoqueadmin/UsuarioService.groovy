package estoqueadmin

import dominios.Usuario
import grails.transaction.Transactional
import grails.plugins.springsecurity.SpringSecurityService;

@Transactional
class UsuarioService {

    def springSecurityService

    Usuario getUsuarioLogado() throws Exception{
        def user = springSecurityService.principal
        Usuario usuario = Usuario.findByLogin(user?.getUsername())
        if(usuario==null) throw new Exception("Usuário invalido");
        return usuario;
    }

    def salvar(Usuario usuario){
        if(usuario.validate()){
            usuario.save()
        }else{
            throw new Exception("Usuario nao foi cadastrado, erro na validacao de campos!")
        }
    }

}
