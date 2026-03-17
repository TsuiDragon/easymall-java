package com.easymall.component;

import com.easymall.entity.constants.Constants;
import com.easymall.entity.dto.LogisticsSendDTO;
import com.easymall.entity.dto.RagDataDTO;
import com.easymall.entity.dto.TokenUserInfoDTO;
import com.easymall.entity.po.SysCategory;
import com.easymall.redis.RedisUtils;
import com.easymall.utils.StringTools;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private RedissonClient redissonClient;

    public String saveCheckCode(String code) {
        String checkCodeKey = "check:code:" + UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey, code, 300);
        return checkCodeKey;
    }

    public String getCheckCode(String checkCodeKey) {
        return (String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public void clearCheckCode(String checkCodeKey) {
        redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public String saveTokenInfo4Admin(String account){
        String token = UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_ADMIN + token, account, 60*60*24);
        return token;
    }


    public String clearTokenInfo4Admin(String token) {
        redisUtils.delete(Constants.REDIS_KEY_TOKEN_ADMIN + token);
        return "ok";
    }

    public void saveCategoryList(List<SysCategory> categoryInfoList) {
        redisUtils.set(com.easymall.constants.Constants.REDIS_KEY_CATEGORY_LIST, categoryInfoList);
    }

    public String getLoginInfo4Admin(String token) {
        return (String) redisUtils.get(com.easymall.constants.Constants.REDIS_KEY_TOKEN_ADMIN + token);
    }

    public TokenUserInfoDTO getTokenInfo(String token) {
        return (TokenUserInfoDTO) redisUtils.get(com.easymall.constants.Constants.REDIS_KEY_TOKEN_WEB + token);
    }

    public void saveTokenInfo(TokenUserInfoDTO tokenUserInfoDto) {
        cleanUserTokenInfo(tokenUserInfoDto.getUserId());
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenUserInfoDto.setToken(token);
        redisUtils.setex(com.easymall.constants.Constants.REDIS_KEY_TOKEN_WEB + token, tokenUserInfoDto, com.easymall.constants.Constants.REDIS_KEY_EXPIRES_DAY * 7);

        redisUtils.setex(com.easymall.constants.Constants.REDIS_KEY_TOKEN_USERID_WEB + tokenUserInfoDto.getUserId(), token, com.easymall.constants.Constants.REDIS_KEY_EXPIRES_DAY * 7);
    }

    public void cleanUserTokenInfo(String userId) {
        String token = (String) redisUtils.get(com.easymall.constants.Constants.REDIS_KEY_TOKEN_USERID_WEB + userId);
        if (StringTools.isEmpty(token)) {
            return;
        }
        redisUtils.delete(com.easymall.constants.Constants.REDIS_KEY_TOKEN_WEB + token);
    }

    public void cleanCheckCode(String checkCodeKey) {
        redisUtils.delete(com.easymall.constants.Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public void cleanToken(String token) {
        if (StringTools.isEmpty(token)) {
            return;
        }
        TokenUserInfoDTO tokenUserInfoDTO = getTokenInfo(token);
        redisUtils.delete(com.easymall.constants.Constants.REDIS_KEY_TOKEN_WEB + token);
        if (tokenUserInfoDTO != null) {
            redisUtils.delete(com.easymall.constants.Constants.REDIS_KEY_TOKEN_USERID_WEB + tokenUserInfoDTO.getUserId());
        }
    }

    public void updateTokenInfo(TokenUserInfoDTO tokenUserInfoDto) {
        redisUtils.setex(com.easymall.constants.Constants.REDIS_KEY_TOKEN_WEB + tokenUserInfoDto.getToken(), tokenUserInfoDto, com.easymall.constants.Constants.REDIS_KEY_EXPIRES_DAY * 7);
        redisUtils.setex(com.easymall.constants.Constants.REDIS_KEY_TOKEN_USERID_WEB + tokenUserInfoDto.getUserId(), tokenUserInfoDto.getToken(), com.easymall.constants.Constants.REDIS_KEY_EXPIRES_DAY * 7);
    }

    public void forceLogout(String userId) {
        String token = (String) redisUtils.get(com.easymall.constants.Constants.REDIS_KEY_TOKEN_USERID_WEB + userId);
        if (!StringTools.isEmpty(token)) {
            redisUtils.delete(com.easymall.constants.Constants.REDIS_KEY_TOKEN_WEB + token);
        }
        redisUtils.delete(com.easymall.constants.Constants.REDIS_KEY_TOKEN_USERID_WEB + userId);
    }

    public List<SysCategory> getCategoryList() {
        List<SysCategory> categoryInfoList = (List<SysCategory>) redisUtils.get(com.easymall.constants.Constants.REDIS_KEY_CATEGORY_LIST);
        return categoryInfoList == null ? new ArrayList<>() : categoryInfoList;
    }

    //消息队列
    public void sendMessage(String queueName, RagDataDTO data) {
        RBlockingQueue<RagDataDTO> queue = redissonClient.getBlockingQueue(queueName, JsonJacksonCodec.INSTANCE);
        try {
            log.info("开始发送消息");
            queue.put(data);
            log.info("发送消息成功");
        } catch (InterruptedException e) {
            log.error("消息发送失败", e);
        }
    }

    public Boolean hasCancelMessage(String userId, Integer messageId) {
        return redisUtils.get(com.easymall.constants.Constants.REDIS_KEY_CANCEL_AGENT_MESSAGE + userId + messageId) != null;
    }

    public void saveUserHeartBeat(String userId) {
        redisUtils.setex(com.easymall.constants.Constants.REDIS_KEY_WS_USER_HEART_BEAT + userId, System.currentTimeMillis(), com.easymall.constants.Constants.REDIS_KEY_EXPIRES_HEART_BEAT);
    }

    //提示词
    public void savePrompt(String key, String value) {
        redisUtils.set(com.easymall.constants.Constants.REDIS_KEY_PROMPT + key, value);
    }

    public String getPrompt(String key) {
        return (String) redisUtils.get(com.easymall.constants.Constants.REDIS_KEY_PROMPT + key);
    }

    public void cleanPrompt(String key) {
        redisUtils.delete(com.easymall.constants.Constants.REDIS_KEY_PROMPT + key);
    }


    public void saveLogisticsInfo(LogisticsSendDTO logisticsSendDTO) {
        redisUtils.set(com.easymall.constants.Constants.REDIS_KEY_SETTING_LOGISTICS, logisticsSendDTO);
    }

    public LogisticsSendDTO getLogisticsInfo() {
        return (LogisticsSendDTO) redisUtils.get(com.easymall.constants.Constants.REDIS_KEY_SETTING_LOGISTICS);
    }
}
