package com.vostroi.api.feign.order.mobile;

import com.vostroi.util.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Administrator
 * @date 2021/7/9 0:03
 * @projectName scloud2021
 * @title: OrderMobileClient
 * @description: TODO
 */
@FeignClient(value = "service-order" )
@RequestMapping(value = "/ord")
public interface OrderMobileClient {
    @GetMapping(value = "/conn")
    public ResultData<String> connect();
}
