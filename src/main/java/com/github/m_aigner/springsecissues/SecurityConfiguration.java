package com.github.m_aigner.springsecissues;

import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.formLogin().successForwardUrl("/loginsuccessful");
    }

    @Bean
    LdapContextSource contextSource() {
        LdapContextSource source = new LdapContextSource();
        source.setBase("dc=example,dc=com");
        source.setUrl("ldap://localhost");

        return source;
    }

    @Bean
    UserDetailsContextMapper mapper() {
        return new LoggingUserDetailsMapper();
    }

    @Bean
    LdapAuthenticator authenticator() {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource());
        authenticator.setUserDnPatterns(new String[]{"uid={0},ou=people"});
        // we don't do authenticator.setUserAttributes; the default is "return all you find"

        return authenticator;
    }

    @Bean
    LdapAuthenticationProvider provider() {
        LdapAuthenticationProvider provider = new LdapAuthenticationProvider(authenticator());
        provider.setUserDetailsContextMapper(mapper());
        return provider;
    }

    @Override
    public void configure(AuthenticationManagerBuilder amb) {
        amb.authenticationProvider(provider());
    }
}
