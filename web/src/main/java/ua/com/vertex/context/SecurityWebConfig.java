package ua.com.vertex.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import ua.com.vertex.logic.SpringDataUserDetailsService;
import ua.com.vertex.utils.LoginBruteForceDefender;

import static ua.com.vertex.utils.LoginBruteForceDefender.BLOCKED_NUMBER;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
public class SecurityWebConfig extends WebSecurityConfigurerAdapter {
    public static final String UNKNOWN_ERROR = "Unknown error during logging in. Database might be offline";
    public static final String RE_CAPTCHA = "ReCaptcha on logging in was missed";
    public static final String LOGIN_ATTEMPTS = "Login attempts counter has been exceeded for this username!";

    private final SpringDataUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginBruteForceDefender defender;

    @Value("${remember.me.validity.seconds}")
    private int validityTime;

    @Autowired
    public SecurityWebConfig(SpringDataUserDetailsService userDetailsService,
                             BCryptPasswordEncoder passwordEncoder, LoginBruteForceDefender defender) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.defender = defender;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        http.addFilterBefore(filter, CsrfFilter.class);

        http
                .authorizeRequests()
                .anyRequest().permitAll()

                .and()
                .formLogin()
                .loginPage("/logIn")
                .successHandler(((request, response, authentication) -> {
                    defender.clearEntry(authentication.getName());
                    response.sendRedirect("/loggedIn");
                }))
                .failureHandler((request, response, e) -> {
                    if (e instanceof BadCredentialsException) {
                        if (defender.verifyUsername(request.getParameter("username")) == BLOCKED_NUMBER) {
                            throw new RuntimeException(LOGIN_ATTEMPTS);
                        }
                        response.sendRedirect("/logIn?error");
                    } else if (e.getMessage().equals(RE_CAPTCHA)) {
                        throw new RuntimeException(RE_CAPTCHA);
                    } else {
                        throw new RuntimeException(UNKNOWN_ERROR, e);
                    }
                })
                .permitAll()

                .and()
                .logout()
                .logoutUrl("/logOut")
                .logoutSuccessUrl("/")

                .and()
                .rememberMe()
                .tokenValiditySeconds(validityTime)

                .and()
                .exceptionHandling().accessDeniedPage("/403")
        ;
    }
}
