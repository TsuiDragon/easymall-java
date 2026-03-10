package com.easymall.entity.constants;

public class Constants {
    private final static String REDIS_KEY_PREFIX = "easymall:";

    public final static String REDIS_KEY_CHECK_CODE = REDIS_KEY_PREFIX + "checkcode:";

    public final static String REDIS_KEY_TOKEN_ADMIN = REDIS_KEY_PREFIX + "token:admin:";
}
