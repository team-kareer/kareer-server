package org.sopt.kareer.domain.member.util;

import org.sopt.kareer.domain.member.entity.enums.Country;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class CountryResolver {

    private final Map<String, Country> ISO3_MAP;

    public CountryResolver() {
        ISO3_MAP = buildIso3Map();
    }

    private Map<String, Country> buildIso3Map() {
        Map<String, Country> map = new HashMap<>();

        for (Country country : Country.values()) {
            try {
                Locale locale = findLocaleByCountryName(country.getCountryName());

                if (locale != null) {
                    String iso3 = locale.getISO3Country();
                    map.put(iso3, country);
                }

            } catch (Exception ignored) {
            }
        }

        return map;
    }

    private Locale findLocaleByCountryName(String countryName) {
        for (Locale locale : Locale.getAvailableLocales()) {
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry(Locale.ENGLISH))) {
                return locale;
            }
        }
        return null;
    }

    public Country resolveIso3(String iso3) {
        if (iso3 == null) return null;
        return ISO3_MAP.get(iso3.toUpperCase());
    }
}
