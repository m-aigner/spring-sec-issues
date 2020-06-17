package com.github.m_aigner.springsecissues;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

public class LoggingUserDetailsMapper extends LdapUserDetailsMapper {
    private Logger log = LoggerFactory.getLogger(LoggingUserDetailsMapper.class);

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
            Collection<? extends GrantedAuthority> authorities) {
        Optional<String> snValue = Optional.empty();
        Iterator<? extends Attribute> attrs = ctx.getAttributes().getAll().asIterator();

        try {
            while (attrs.hasNext()) {
                Attribute a = attrs.next();
                if (a.getID().equals("sn")) {
                    snValue = Optional.of(a.get(0).toString());
                    break;
                }
            }
        } catch (NamingException e) {
            // convert to unchecked & rethrow; of course we might want to handle it
            // if this weren't a sample application
            throw LdapUtils.convertLdapException(e);
        }

        String message = snValue
                .map(v -> "Found sn attribute (for " + username + ") whose first value is \"" + v +"\"")
                .orElse("Did not find sn attribute for " + username);
        log.info(message);

        return super.mapUserFromContext(ctx, username, authorities);
    }
}