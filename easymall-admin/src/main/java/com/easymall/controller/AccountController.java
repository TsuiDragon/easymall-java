package com.easymall.controller;

import com.easymall.component.RedisComponent;
import com.easymall.entity.vo.CheckCodeVo;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/checkCode")
    public CheckCodeVo checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100 ,42);
        String checkCode = captcha.text();
        String checkCodeBase64 = captcha.toBase64();
        String checkCodeKey = redisComponent.saveCheckCode(checkCode);
        CheckCodeVo checkCodeVo = new CheckCodeVo();
        checkCodeVo.setCheckCodeKey(checkCodeKey);
        checkCodeVo.setCheckCode(checkCodeBase64);
        return checkCodeVo;
    }
}
