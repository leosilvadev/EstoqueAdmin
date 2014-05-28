package estoqueadmin

import dominios.Usuario
import dominios.UsuarioPermissao
import grails.transaction.Transactional
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.StringUtils

@Transactional
class AutenticarProviderService extends AbstractUserDetailsAuthenticationProvider {

    def springSecurityService

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken token) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String login,
                                       UsernamePasswordAuthenticationToken token) throws AuthenticationException {

        String senha = (String) token.getCredentials();

        if (!StringUtils.hasText(senha)) {
            throw new BadCredentialsException("Insira uma senha");
        }

        senha=springSecurityService.encodePassword(senha)

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        Usuario usuario = Usuario.findByLoginAndSenha(login,senha)
        if (usuario != null){

            UsuarioPermissao.findAllByUsuario(usuario).each{ usuarioPermissao ->
                authorities.add(new GrantedAuthorityImpl(usuarioPermissao.permissao.nivelPermissao))
            }

        }else{
            throw new BadCredentialsException("Usuario ou senha inválido!");
        }

        return new User(login, senha, true, true, true, true,	authorities);
    }
}
