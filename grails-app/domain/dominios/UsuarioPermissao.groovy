package dominios

import org.apache.commons.lang.builder.HashCodeBuilder

class UsuarioPermissao implements Serializable {

    Usuario usuario
    Permissao permissao

    boolean equals(other) {
        if (!(other instanceof UsuarioPermissao)) {
            return false
        }
        other.usuario?.id == usuario?.id &&
                other.permissao?.id == permissao?.id
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        if (usuario) builder.append(usuario.id)
        if (permissao) builder.append(permissao.id)
        builder.toHashCode()
    }

    static UsuarioPermissao get(long usuarioId, long permissaoId) {
        find 'from UsuarioPermissao where usuario.id=:usuarioId and permissao.id=:permissaoId',
                [usuarioId: usuarioId, permissaoId: permissaoId]
    }

    static UsuarioPermissao create(Usuario usuario, Permissao permissao, boolean flush = true) {
        new UsuarioPermissao(usuario: usuario, permissao: permissao).save(flush: flush, insert: true)
    }

    static boolean remove(Usuario usuario, Permissao permissao, boolean flush = true) {
        UsuarioPermissao instance = UsuarioPermissao.findByUsuarioAndPermissao(usuario, permissao)
        if (!instance) {
            return false
        }

        instance.delete(flush: flush)
        true
    }

    static void removeAll(Usuario usuario) {
        executeUpdate 'DELETE FROM UsuarioPermissao WHERE usuario=:usuario', [usuario: usuario]
    }

    static void removeAll(Permissao permissao) {
        executeUpdate 'DELETE FROM UsuarioPermissao WHERE permissao=:permissao', [permissao: permissao]
    }

    static mapping = {
        id composite: ['permissao', 'usuario']
        permissao lazy:false
        usuario lazy:false
        version false
    }
}
