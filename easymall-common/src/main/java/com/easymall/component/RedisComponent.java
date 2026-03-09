package com.easymall.component;

import com.easymall.entity.constants.Constants;
import com.easymall.redis.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    public String saveCheckCode(String code) {
        String checkCodeKey = "check:code:" + UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey, code, 300);
        return checkCodeKey;
    }
}
