package com.github.m_aigner.springsecissues;

import java.util.Collection;
import java.util.Iterator;

import javax.naming.directory.Attribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

public class LoggingUserDetailsMapper extends LdapUserDetailsMapper {
    private Logger log = LoggerFactory.getLogger(LoggingUserDetailsMapper.class);

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
            Collection<? extends GrantedAuthority> authorities) {
        Iterator<? extends Attribute> attrs = ctx.getAttributes().getAll().asIterator();

        while (attrs.hasNext()) {
            Attribute a = attrs.next();
            log.info("Found attribute " + a.toString());
        }

        return super.mapUserFromContext(ctx, username, authorities);
    }
}