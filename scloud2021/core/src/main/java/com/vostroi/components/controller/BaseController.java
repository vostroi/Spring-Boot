package com.vostroi.components.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.vostroi.components.entity.BaseEntity;
import com.vostroi.components.service.BaseService;
import com.vostroi.util.EnumConstant;
import com.vostroi.util.ResultData;
import com.vostroi.util.spring.ParamDateEditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author Administrator
 * @date 2021/5/15 15:35
 * @projectName scloud2021
 * @title: BaseController
 * @description: Controller基础抽象类，提供公共 API 方法
 */
@RestController
@CrossOrigin("*")
@Slf4j
@DefaultProperties(defaultFallback = "circuitBreakerHandler" , commandProperties = {
        // 是否开启断路器熔断功能
        @HystrixProperty(name = "circuitBreaker.enabled" , value = "true"),
        // 最低请求次数（时间窗口期内达到最低请求次数才生效）
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "20"),
        // 时间窗口期
        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "60000"),
        // 失败率达到该值后熔断
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "80")
})
public abstract class BaseController<T extends BaseEntity, ID extends Serializable> {

    /**
     * 获取redis对象，子类实现
     * @return
     */
//    public RedisUtil getRedisUtil();

    @Autowired private RestTemplate restTemplate;

    public abstract BaseService<T, ID> getService();

    public RestTemplate getRestTemplate(){
        return this.restTemplate;
    }

    /**
     * 根据id获取对象
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}")
    public ResultData<T> get(@PathVariable("id") ID id){
       T t = getService().get(id);
       return ResultData.getResultData(EnumConstant.RESULT_CODE.SU_0000,t);
    }

    @PostMapping(value = {"/crt","/mdf"})
    public ResultData<T> create(@RequestBody T t){
        t = getService().save(t);
        return ResultData.getResultData(t);
    }

    /**
     * 更新对象部分字段
     * @param id
     * @param properties
     * @param request
     * @return
     */
    @PostMapping("/multiset/{id}")
    public ResultData<T> setProperties(@PathVariable(value = "id") ID id, @RequestBody Map<String, Object> properties, HttpServletRequest request) {
        T t = getService().setProperties(id, properties);
        if (log.isDebugEnabled()) {
            log.debug("{} set : {}", id, t);
        }
        return ResultData.getResultData(EnumConstant.RESULT_CODE.SU_0000 ,t);
    }

    /**
     * 判断是否登录
     * @param request
     * @return
     */
    protected boolean isLogin(HttpServletRequest request) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(null!=authentication && authentication.isAuthenticated()) {
//            return true;
//        }
//
//        String accessToken = OAuthUtils.extractToken(request);
//        if(StringUtils.isBlank(accessToken)) {
//            return false;
//        }
//        return oauthService.checkAccessToken(accessToken);
        return true;
    }

    // 初始化分页对象

    /**
     * 日期注册
     */
    @InitBinder
    public void initControllerBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        /*
        不要删除下行注释!!! 将来"yyyy-MM-dd"将配置到properties文件中
        SimpleDateFormat dateFormat = new SimpleDateFormat(getText("date.format", request.getLocale()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        */
        binder.registerCustomEditor(Date.class, new ParamDateEditor());
    }


    /**
     * HystrixCommand fallback调用，针对方法定制fallback要求参数与 @HystrixCommand 所注解的方法参数一致
     * @param skuId
     * @return
     */
    private ResultData<String> timeOutHandler(Long skuId){
        log.info("timeOutHandler Thread={}", Thread.currentThread().getName());
        return ResultData.getResultData(EnumConstant.RESULT_CODE.ER_3333_2222 , EnumConstant.RESULT_CODE.ER_3333_2222.getCode());
    }

    private ResultData<String> timeOutHandler(){
        log.info("timeOutHandler Thread={}", Thread.currentThread().getName());
        return ResultData.getResultData(EnumConstant.RESULT_CODE.ER_3333_2222 , EnumConstant.RESULT_CODE.ER_3333_2222.getCode());
    }

    /**
     * 熔断的fallback
     * @return
     */
    private ResultData<String> circuitBreakerHandler(){
        log.info("circuitBreakerHandler Thread={}", Thread.currentThread().getName());
        return ResultData.getResultData(EnumConstant.RESULT_CODE.ER_3333_1111 , EnumConstant.RESULT_CODE.ER_3333_1111.getCode());
    }

}
