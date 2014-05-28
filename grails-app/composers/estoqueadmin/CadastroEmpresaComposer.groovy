package estoqueadmin

import com.budjb.requestbuilder.RequestBuilder
import com.sun.jersey.api.client.ClientResponse
import dominios.Empresa
import dominios.Permissao
import dominios.Plano
import dominios.Usuario
import dominios.UsuarioPermissao
import grails.converters.JSON
import org.zkoss.zhtml.Div
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.Datebox
import org.zkoss.zul.Label
import org.zkoss.zul.Popup
import org.zkoss.zul.Tab
import org.zkoss.zul.Tabbox
import org.zkoss.zul.Tabpanel
import org.zkoss.zul.Tabs
import org.zkoss.zul.Textbox
import util.EAComposer
import ws.Endereco

class CadastroEmpresaComposer extends EAComposer {

    def empresaService
    def usuarioService

    @Wire Tabbox tabFluxo
    @Wire Textbox txtNome
    @Wire Textbox txtDocumento
    @Wire Textbox txtCEP
    @Wire Textbox txtEndereco
    @Wire Textbox txtLogin
    @Wire Textbox txtSenha
    @Wire Textbox txtConfirmaSenha
    @Wire Div divTermosUso

    @Wire Button btnBronze
    @Wire Button btnPrata
    @Wire Button btnOuro

    @Wire Popup popupBronze
    @Wire Popup popupPrata
    @Wire Popup popupOuro

    @Wire Button btnInformacoesGerais
    @Wire Button btnInformacoesAcesso
    @Wire Button btnPlanos
    @Wire Button btnFinaliza
    @Wire Label lblPlanoEscolhido

    @Wire Tab tabInformacoesGerais
    @Wire Tab tabInformacoesAcesso
    @Wire Tab tabPlanos
    @Wire Tab tabTermos

    def afterCompose = { window ->
        txtNome.focus()
        inicializarPopups()
        inicializarTermosUso()
    }

    @Listen("onClick = #btnFinaliza")
    def registrarEmpresa(){
        try{
            Usuario usuario = new Usuario(
                    login: txtLogin.value,
                    senha: txtSenha.value,
                    ativo: true,

            )

            Empresa empresa = new Empresa(
                    nome: txtNome.value,
                    documento: txtDocumento.value,
                    endereco: txtEndereco.value,
                    plano: definirPlano(),
                    ativo: true,
                    dataRegistro: Calendar.getInstance()
            )
            empresaService.salvar(empresa)
            usuario.empresa = empresa
            usuarioService.salvar(usuario)
            UsuarioPermissao.create(usuario, Permissao.findByNivelPermissao('ROLE_ADMIN'))

            redirect(uri: 'login.zul', params:[status:0,codigo:empresa.id])

        }catch(Exception e){
            e.printStackTrace()
            notify(e.message)
        }
    }

    def definirPlano(){
        if(btnBronze.disabled) return Plano.findById(1)
        if(btnPrata.disabled) return Plano.findById(2)
        if(btnOuro.disabled) return Plano.findById(3)
    }

    def inicializarTermosUso(){
        divTermosUso.append{
            div(){
                label(value: "Termos de Uso", style="font-size:1.2em; font-weight:bold;")
            }
            div(){
                label(value: "")
            }
            div(){
                label(value: "1. Aceitação dos Termos")
            }
            div(){
                label(value: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
            }
            div(){
                label(value: "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
            }
            div(){
                label(value: "2. Descrição dos Serviços")
            }
            div(){
                label(value: "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.")
            }
            div(){
                label(value: "3. Licença de Uso")
            }
            div(){
                label(value: "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
            }
            div(){
                label(value: "4. Restrições")
            }
            div(){
                label(value: "5. Pagamento")
            }
            div(){
                label(value: "6. Garantias e Isenções de Responsabilidade")
            }
            div(){
                label(value: "7. Obrigações do Licenciado")
            }
            div(){
                label(value: "8. Obrigações do Licenciante")
            }
            div(){
                label(value: "9. Nível de Serviço")
            }
            div(){
                label(value: "10. Outras Informações")
            }

        }
    }

    def inicializarPopups(){
        Plano planoBronze   = Plano.findById(1)
        Plano planoPrata    = Plano.findById(2)
        Plano planoOuro     = Plano.findById(3)
        adicionarPlano(popupBronze, planoBronze)
        adicionarPlano(popupPrata, planoPrata)
        adicionarPlano(popupOuro, planoOuro)
    }

    def adicionarPlano(Popup popup, Plano plano){
        popup.append{
            div(style:"padding:5px;"){
                label(value: "Plano: ${plano.nome}")
            }
            div(style:"padding:5px;"){
                label(value: "Descrição: ${plano.descricao}")
            }
            div(style:"padding:5px;"){
                label(value: "Limite de usuarios: ${plano.limiteUsuarios}")
            }
            div(style:"padding:5px;"){
                label(value: "Limite de fornecedores: ${plano.limiteFornecedores}")
            }
            div(style:"padding:5px;"){
                label(value: "Limite de produtos: ${plano.limiteProdutos}")
            }

            String labelServicos = plano.limiteServicos>0 ? "Limite de serviços: ${plano.limiteServicos}" : "Você não pode vender serviços"
            div(style:"padding:5px;"){
                label(value: labelServicos)
            }

            String labelBoletos = plano.boletos>0 ? "Limite de boletos por mês: ${plano.boletos}" : "Você não pode gerar nenhum boleto"
            div(style:"padding:5px;"){
                label(value: labelBoletos)
            }
        }
    }

    @Listen("onOK = #txtCEP")
    def buscarEnderecoOnOK(){
        buscarEndereco()
        btnInformacoesGerais.focus()
    }

    @Listen("onClick = #btnInformacoesGerais")
    def entrarEmInformacoesDeAcesso(){
        validarInformacoesGerais()
        tabInformacoesAcesso.visible = true
        tabFluxo.selectedIndex = 1
        txtLogin.focus()
    }

    def validarInformacoesGerais(){
        if(vazio(txtNome.value)) {
            txtNome.focus()
            gerarErroValidacao(txtNome, "Preencha corretamente, insira o nome da sua empresa")
        }
        if(vazio(txtDocumento.value) || !(txtDocumento.value ==~ /[0-9]+/) ) {
            txtDocumento.value = null
            txtDocumento.focus()
            gerarErroValidacao(txtDocumento, "Preencha corretamente, insira seu CPF ou o CNPJ da empresa")
        }
        if(vazio(txtEndereco.value)) {
            txtEndereco.focus()
            gerarErroValidacao(txtEndereco, "Preencha corretamente, insira o endereço atual da empresa")
        }
    }

    @Listen("onClick = #btnVoltarInformacoesGerais")
    def voltarEmInformacoesGerais(){
        tabFluxo.selectedIndex = 0
        txtNome.focus()
    }

    @Listen("onClick = #btnInformacoesAcesso")
    def entrarEmPlanos(){
        validarInformacoesAcesso()
        tabPlanos.visible = true
        tabFluxo.selectedIndex = 2
    }

    def validarInformacoesAcesso(){
        if(vazio(txtLogin.value)){
            txtLogin.focus()
            gerarErroValidacao(txtLogin, "Preencha corretamente, insira o nome de usuario para acesso")
        }
        if(vazio(txtSenha.value)){
            txtSenha.focus()
            gerarErroValidacao(txtSenha, "Preencha corretamente, insira a senha para acesso")
        }
        if(vazio(txtConfirmaSenha.value) || senhasDiferentes()){
            txtConfirmaSenha.value = null
            txtConfirmaSenha.focus()
            gerarErroValidacao(txtConfirmaSenha, "As senhas não são iguais, confirme e tente novamente")
        }
        if(Usuario.countByLogin(txtLogin.value)>0){
            txtLogin.value = null
            txtLogin.focus()
            gerarErroValidacao(txtLogin, "Já existe um usuário com este nome")
        }
    }

    def vazio(String valor){
        return valor==null || valor.empty
    }

    def senhasDiferentes(){
        return !txtSenha.value.equals(txtConfirmaSenha.value)
    }

    @Listen("onClick = #btnVoltarInformacoesAcesso")
    def voltarEmInformacoesAcesso(){
        tabFluxo.selectedIndex = 1
        txtLogin.focus()
    }

    @Listen("onClick = #btnPlanos")
    def entrarEmTermosDeUso(){
        tabTermos.visible = true
        tabFluxo.selectedIndex = 3
        btnFinaliza.focus()

    }

    @Listen("onClick = #btnVoltarPlanos")
    def voltarEmPlanos(){
        tabFluxo.selectedIndex = 2
    }

    @Listen("onClick = #btnBronze")
    def escolherPlanoBronze(){
        btnPrata.disabled = false
        btnOuro.disabled = false
        btnBronze.disabled = true
        btnPlanos.disabled = false
        lblPlanoEscolhido.value = "Você escolheu o plano Bronze"
        btnPlanos.focus()
    }

    @Listen("onClick = #btnPrata")
    def escolherPlanoPrata(){
        btnBronze.disabled = false
        btnOuro.disabled = false
        btnPrata.disabled = true
        btnPlanos.disabled = false
        lblPlanoEscolhido.value = "Você escolheu o plano Prata"
        btnPlanos.focus()
    }

    @Listen("onClick = #btnOuro")
    def escolherPlanoOuro(){
        btnBronze.disabled = false
        btnPrata.disabled = false
        btnOuro.disabled = true
        btnPlanos.disabled = false
        lblPlanoEscolhido.value = "Você escolheu o plano Ouro"
        btnPlanos.focus()
    }

    @Listen("onOK = #txtNome")
    def inserirNome(){
        txtDocumento.focus()
    }

    @Listen("onOK = #txtDocumento")
    def inserirDocumento(){
        txtCEP.focus()
    }

    @Listen("onOK = #txtEndereco")
    def inserirEndereco(){
        btnInformacoesGerais.focus()
    }

    @Listen("onOK = #txtLogin")
    def inserirLogin(){
        txtSenha.focus()
    }

    @Listen("onOK = #txtSenha")
    def inserirSenha(){
        txtConfirmaSenha.focus()
    }

    @Listen("onOK = #txtConfirmaSenha")
    def inserirConfirmacaoSenha(){
        btnInformacoesAcesso.focus()
    }

}
