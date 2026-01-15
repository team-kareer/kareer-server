package org.sopt.kareer.global.auth.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.global.auth.config.LoginCodeProperties;
import org.sopt.kareer.global.auth.dto.LoginCodePayload;
import org.sopt.kareer.global.auth.exception.AuthErrorCode;
import org.sopt.kareer.global.auth.exception.AuthException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginCodeService {

    private static final String FIELD_MEMBER_ID = "memberId";
    private static final String FIELD_ONBOARDING_REQUIRED = "onboardingRequired";
    private static final List<String> REQUIRED_FIELDS = List.of(
            FIELD_MEMBER_ID,
            FIELD_ONBOARDING_REQUIRED
    );
    private static final int INDEX_MEMBER_ID = 0;
    private static final int INDEX_ONBOARDING_REQUIRED = 1;

    private final RedisTemplate<String, String> redisTemplate;
    private final LoginCodeProperties loginCodeProperties;

    public String issue(Member member, boolean onboardingRequired) {
        String code = UUID.randomUUID().toString();
        String key = loginCodeProperties.buildKey(code);
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        hashOperations.putAll(key, Map.of(
                FIELD_MEMBER_ID, String.valueOf(member.getId()),
                FIELD_ONBOARDING_REQUIRED, String.valueOf(onboardingRequired)
        ));
        redisTemplate.expire(key, loginCodeProperties.ttlDuration());

        return code;
    }

    public LoginCodePayload consume(String code) {
        String key = loginCodeProperties.buildKey(code);
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        List<String> values = hashOperations.multiGet(key, REQUIRED_FIELDS);

        if (values == null || values.stream().anyMatch(Objects::isNull)) {
            throw new AuthException(AuthErrorCode.LOGIN_CODE_NOT_FOUND);
        }

        Boolean deleted = redisTemplate.delete(key);
        if (deleted == null || !deleted) {
            throw new AuthException(AuthErrorCode.LOGIN_CODE_ALREADY_USED);
        }

        Long memberId = Long.parseLong(values.get(INDEX_MEMBER_ID));
        boolean onboardingRequired = Boolean.parseBoolean(values.get(INDEX_ONBOARDING_REQUIRED));
        return new LoginCodePayload(memberId, onboardingRequired);
    }
}
