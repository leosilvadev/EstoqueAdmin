import grails.plugins.springsecurity.SecurityConfigType

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    pdf:           'application/pdf',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
 
grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}


// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'controleacesso.Usuario'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'controleacesso.UsuarioPermissao'

grails.plugins.springsecurity.userLookup.usernamePropertyName = 'login'
grails.plugins.springsecurity.userLookup.passwordPropertyName = 'senha'
grails.plugins.springsecurity.userLookup.authoritiesPropertyName = 'autoridades'
grails.plugins.springsecurity.userLookup.enabledPropertyName = 'ativo'
grails.plugins.springsecurity.userLookup.accountExpiredPropertyName = 'contaExpirada'
grails.plugins.springsecurity.userLookup.accountLockedPropertyName = 'contaBloqueada'
grails.plugins.springsecurity.userLookup.passwordExpiredPropertyName = 'senhaExpirada'

grails.plugins.springsecurity.authority.className = 'controleacesso.Permissao'
grails.plugins.springsecurity.authority.nameField = 'nivelPermissao'

grails.plugins.springsecurity.auth.loginFormUrl = '/login.zul'
grails.plugins.springsecurity.logout.afterLogoutUrl = '/login.zul?msg=LOGOUT'
grails.plugins.springsecurity.successHandler.defaultTargetUrl = '/fornecedor/listaFornecedor.zul'
grails.plugins.springsecurity.failureHandler.defaultFailureUrl = '/login.zul?msg=ERROR'
grails.plugins.springsecurity.dao.hideUserNotFoundExceptions = false
grails.plugins.springsecurity.adh.errorPage="/j_spring_security_logout"

grails.plugins.springsecurity.providerNames = ['autenticarProviderService']

grails.plugins.springsecurity.securityConfigType = SecurityConfigType.InterceptUrlMap
grails.plugins.springsecurity.interceptUrlMap = [
        '/fornecedor/listaFornecedor.zul': ['ROLE_ADMIN'],
        '/fornecedor/novoFornecedor.zul': ['ROLE_ADMIN'],
        '/fornecedor/editarFornecedor.zul': ['ROLE_ADMIN'],

        '/orcamento/listaOrcamento.zul': ['ROLE_ADMIN'],
        '/orcamento/geracao.zul': ['ROLE_ADMIN'],
        '/orcamento/novoOrcamento.zul': ['ROLE_ADMIN'],

        '/pedido/listaPedido.zul': ['ROLE_ADMIN'],
        '/pedido/novoPedido.zul': ['ROLE_ADMIN'],
        '/pedido/efetivarPedido.zul': ['ROLE_ADMIN'],

        '/produto/listaProduto.zul': ['ROLE_ADMIN'],
        '/produto/novoProduto.zul': ['ROLE_ADMIN'],

        '/venda/listaVenda.zul': ['ROLE_ADMIN'],
        '/venda/novaVenda.zul': ['ROLE_ADMIN'],
        '/venda/detalhes.zul': ['ROLE_ADMIN'],

        '/mensagem/mensagens.zul': ['ROLE_USER'],
        '/template/cabecalho.zul': ['ROLE_USER'],
        '/template/template.zul': ['ROLE_USER']
]