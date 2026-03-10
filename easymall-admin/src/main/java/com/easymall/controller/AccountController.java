package com.easymall.controller;

import com.easymall.component.RedisComponent;
import com.easymall.entity.config.AppConfig;
import com.easymall.entity.constants.Constants;
import com.easymall.entity.vo.CheckCodeVo;
import com.easymall.entity.vo.ResponseVO;
import com.easymall.exception.BusinessException;
import com.easymall.utils.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Slf4j
@RequestMapping("/account")
public class AccountController extends ABaseController{

    @Resource
    private RedisComponent redisComponent;
    @Resource
    private AppConfig appConfig;

    @RequestMapping("/checkCode")
    public CheckCodeVo checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100 ,42);
        String checkCode = captcha.text();
        String checkCodeBase64 = captcha.toBase64();
        String checkCodeKey = redisComponent.saveCheckCode(checkCode);
        CheckCodeVo checkCodeVo = new CheckCodeVo();
        checkCodeVo.setCheckCodeKey(checkCodeKey);
        checkCodeVo.setCheckCode(checkCodeBase64);
        log.info("checkCode: {}", checkCode);
        return checkCodeVo;
    }

    @RequestMapping("/login")
    public ResponseVO login(@NotEmpty String account, @NotEmpty String password, @NotEmpty String checkCode, @NotEmpty String checkCodeKey) {
        try {
            if (!redisComponent.getCheckCode(checkCodeKey).equalsIgnoreCase(checkCode)){
                throw new BusinessException("验证码错误");
            }
            if (!account.equals(appConfig.getAdminAccount()) || !password.equals(StringTools.encodeByMD5(appConfig.getAdminPassword()))){
                throw new BusinessException("账号或密码错误");
            }
            String token = redisComponent.saveTokenInfo4Admin(account);
            return getSuccessResponseVO(token);
        }finally {
            redisComponent.clearCheckCode(checkCodeKey);
        }
    }

    @RequestMapping("/logout")
    public ResponseVO logout(@RequestHeader(Constants.ADMIN_TOKEN) String token) {
        redisComponent.clearTokenInfo4Admin(token);
        return getSuccessResponseVO(null);
    }
}
