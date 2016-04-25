package com.tasker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.tasker.services.UserService;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserService userService;
	
	protected void configure(HttpSecurity http) throws Exception {
        
		http
			.authorizeRequests()
			.antMatchers("/resources/**").permitAll()
			.anyRequest().authenticated()					
			.and()
		.formLogin()
			.loginPage("/")
			.loginProcessingUrl("/login")
			.usernameParameter("username")
			.passwordParameter("password")
			.permitAll()
			.and()		
		.logout()
			.logoutUrl("/logout")
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.permitAll();

		http.csrf().disable();
	}

	@Autowired
	protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {	  
	    auth.userDetailsService(userService); //.passwordEncoder(encoder);
	}
}
