package Drug_Management.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

	@Autowired
	CustomerUserDetailsService customerUserDetailsService;
	
	@Autowired
	JwtFilter jwtFilter;
	
	/*@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customerUserDetailsService);
	}
	*/
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return NoOpPasswordEncoder.getInstance();
//	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customerUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider; 
	}
	
	@Bean(name=BeanIds.AUTHENTICATION_MANAGER)
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
		.and()
		.csrf().disable()
		.authorizeHttpRequests()
		.requestMatchers("/user/login","/user/signup","/user/forgotPassword")
		.permitAll()
		.anyRequest()
		.authenticated()
		.and()
		.exceptionHandling()
		.and().sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		http.authenticationProvider(daoAuthenticationProvider());
		DefaultSecurityFilterChain  build=http.build();
		return build;
	}
	
	/*
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
		.and()
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/user/login","/user/signup","/user/forgotPassword")
		.permitAll()
		.anyRequest()
		.authenticated()
		.and()
		.exceptionHandling()
		.and().sessionManagement()
		.sessionCrestionPolicy(SessionCreationPolicy.STATELESS);
		
		http.addFilterBefore(jwtFilter,UserNamePasswordAuthenticationFilter.class);
		
		
	}
	*/
}
