package cz.cvut.fit.splitee.security;

import cz.cvut.fit.splitee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        // securedEnabled = true,
        // jsr250Enabled = true,
        prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    AccountService accountService;
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(accountService).passwordEncoder(passwordEncoder());
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint((AuthenticationEntryPoint) unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/groups/{code}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/groups/{code}/members/noacc").authenticated()
                .antMatchers("/api/groups/{code}/**").access("@guard.isInGroup(authentication, #code)")
                // ACOUNT
                .antMatchers("/api/account/{id}/**").access("@guard.isUser(authentication, #id)")
                // MEMBERSHIP
                .antMatchers(HttpMethod.POST,"/api/memberships/{code}/**").access("@guard.isInGroup(authentication, #code)")
                .antMatchers("/api/memberships/{mId}/**").access("@guard.isInGroupMember(authentication, #mId)")
                // BILL
                .antMatchers(HttpMethod.POST,"/api/bills/{code}/**").access("@guard.isInGroup(authentication, #code)")
                .antMatchers("/api/bills/{bId}/**").access("@guard.isInGroupBill(authentication, #bId)")
                // SPLIT
                .antMatchers("/api/splits/{bId}/**").access("@guard.isInGroupBill(authentication, #bId)")
                // DEBT
                .antMatchers(HttpMethod.POST,"/api/debts/{code}/**").access("@guard.isInGroup(authentication, #code)")
                .antMatchers("/api/debts/{mId}/**").access("@guard.isInGroupMember(authentication, #bId)")
                .anyRequest().authenticated();
        http.addFilterBefore((Filter) authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
