
import  dominios.Plano
import dominios.Empresa
import dominios.Permissao
import dominios.Usuario
import dominios.UsuarioPermissao

class BootStrap {

    def init = { servletContext ->
        def planoBronze = Plano.findByNome('Bronze') ?: new Plano(
                nome: "Bronze",
                descricao: "Plano ideal para você que está abrindo seu negócio.",
                limiteUsuarios: 1,
                limiteFornecedores: 10,
                limiteProdutos: 50,
                limiteServicos: 0,
                boletos: 0,
                dashboard: false,
                valor: 40.00)
                .save(failOnError: true)

        def planoPrata = Plano.findByNome('Prata') ?: new Plano(
                nome: "Prata",
                descricao: "Plano ideal para você que possui mais funcionários e quer ter mais pessoas vendendo ao mesmo.",
                limiteUsuarios: 5,
                limiteFornecedores: 30,
                limiteProdutos: 150,
                limiteServicos: 20,
                boletos: 50,
                dashboard: false,
                valor: 100.00)
                .save(failOnError: true)

        def planoOuro = Plano.findByNome('Ouro') ?: new Plano(
                nome: "Ouro",
                descricao: "Plano ideal para você que deseja gerenciar seu estoque e serviços em alta escala. ",
                limiteUsuarios: 10,
                limiteFornecedores: 50,
                limiteProdutos: 300,
                limiteServicos: 50,
                boletos: 200,
                dashboard: true,
                valor: 150.00)
                .save(failOnError: true)

        def permissaoAdmin = Permissao.findByNivelPermissao('ROLE_ADMIN') ?: new Permissao(nivelPermissao: 'ROLE_ADMIN').save(failOnError: true)

        def empresa = Empresa.findByNome('Vaiftech') ?: new Empresa(
                nome: "Vaiftech",
                documento: "123412312312",
                endereco: "Lorena/São José dos Campos",
                dataRegistro: Calendar.getInstance(),
                plano: 3)
                .save(failOnError: true)


        def usuarioAdmin = Usuario.findByLogin('admin') ?: new Usuario(
                login: 'admin',
                senha: 'admin123',
                ativo: true,
                empresa: empresa)
                .save(failOnError: true)

        def usuarioAllanAdmin = Usuario.findByLogin('allan') ?: new Usuario(
                login: 'allan',
                senha: 'allan123',
                ativo: true,
                empresa: empresa)
                .save(failOnError: true)

        def usuarioHeitorAdmin = Usuario.findByLogin('heitor') ?: new Usuario(
                login: 'heitor',
                senha: 'heitor123',
                ativo: true,
                empresa: empresa)
                .save(failOnError: true)

        def usuarioLeoAdmin = Usuario.findByLogin('leonardo') ?: new Usuario(
                login: 'leonardo',
                senha: 'leonardo123',
                ativo: true,
                empresa: empresa)
                .save(failOnError: true)

        empresa.addToUsuarios(usuarioAdmin)
        empresa.addToUsuarios(usuarioAllanAdmin)
        empresa.addToUsuarios(usuarioHeitorAdmin)
        empresa.addToUsuarios(usuarioLeoAdmin)

        if (!usuarioAdmin.authorities.contains(permissaoAdmin)) {
            UsuarioPermissao.create usuarioAdmin, permissaoAdmin
        }
        if (!usuarioAllanAdmin.authorities.contains(permissaoAdmin)) {
            UsuarioPermissao.create usuarioAllanAdmin, permissaoAdmin
        }
        if (!usuarioHeitorAdmin.authorities.contains(permissaoAdmin)) {
            UsuarioPermissao.create usuarioHeitorAdmin, permissaoAdmin
        }
        if (!usuarioLeoAdmin.authorities.contains(permissaoAdmin)) {
            UsuarioPermissao.create usuarioLeoAdmin, permissaoAdmin
        }
    }
    def destroy = {
    }
}
