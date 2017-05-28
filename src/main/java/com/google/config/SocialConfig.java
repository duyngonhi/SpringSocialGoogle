package com.google.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;

import com.google.user.SecurityContext;
import com.google.user.SimpleConnectionSignUp;
import com.google.user.SimpleSignInAdapter;
import com.google.user.User;

@Configuration
public class SocialConfig {

	@Inject
	private DataSource dataSource;

	@Bean
	public ConnectionFactoryLocator connectionFactoryLocator() {
		ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
		registry.addConnectionFactory(new GoogleConnectionFactory("499305709961-uofksh2d9n5drhrt9khihsltrrurlrfr.apps.googleusercontent.com","U16saD11bjys_lGVQafkk5jI"));
		return registry;
	}

	@Bean
	public UsersConnectionRepository usersConnectionRepository() {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource,
				connectionFactoryLocator(), Encryptors.noOpText());
		repository.setConnectionSignUp(new SimpleConnectionSignUp());
		return repository;
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public ConnectionRepository connectionRepository() {
	    User user = SecurityContext.getCurrentUser();
	    return usersConnectionRepository().createConnectionRepository(user.getId());
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)	
	public Google google() {
	    return connectionRepository().getPrimaryConnection(Google.class).getApi();
	}
	
	@Bean
	public ProviderSignInController providerSignInController() {
		return new ProviderSignInController(connectionFactoryLocator(), usersConnectionRepository(),
				new SimpleSignInAdapter());
	}

}
