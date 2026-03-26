package org.sopt.kareer.global.config.locale;

import static org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.i18n.AbstractLocaleContextResolver;

public class KareerLocaleResolver extends AbstractLocaleContextResolver {

    private static final String HEADER_NAME = "X-Preferred-Language";
    private final Locale defaultLocale;

    public KareerLocaleResolver(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        return determineLocale(request);
    }

    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        Locale locale = determineLocale(request);
        return () -> locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        if (locale == null) {
            request.removeAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
        } else {
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
        }
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        if (localeContext == null || localeContext.getLocale() == null) {
            request.removeAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
            return;
        }
        setLocale(request, response, localeContext.getLocale());
    }

    private Locale determineLocale(HttpServletRequest request) {
        Object attribute = request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
        if (attribute instanceof Locale locale) {
            return locale;
        }

        String header = request.getHeader(HEADER_NAME);
        if (header != null && !header.isBlank()) {
            return Locale.forLanguageTag(header);
        }
        return defaultLocale;
    }
}
