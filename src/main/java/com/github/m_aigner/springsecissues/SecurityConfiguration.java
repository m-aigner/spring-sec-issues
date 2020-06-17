package com.github.m_aigner.springsecissues;

import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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

    @Override
    public void configure(AuthenticationManagerBuilder amb) throws Exception {
        amb.ldapAuthentication()
                .contextSource(contextSource())
                .groupSearchBase("ou=groups")
                .userDetailsContextMapper(mapper())
                .userSearchBase("ou=people")
                .userSearchFilter("uid={0}");
    }
}
