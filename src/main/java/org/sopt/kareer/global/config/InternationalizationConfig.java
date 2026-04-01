package org.sopt.kareer.global.config;

import java.util.Locale;
import org.sopt.kareer.global.config.locale.KareerLocaleResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InternationalizationConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        return new KareerLocaleResolver(Locale.ENGLISH);
    }
}
