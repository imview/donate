package com.donate.api.common;

import com.alibaba.fastjson.JSONArray;
import com.donate.commmon.model.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler
    @ResponseBody
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    private ServiceResult ExceptionHandler(Exception e) {
        LOGGER.error(JSONArray.toJSONString(e));
        ServiceResult result = new ServiceResult();
        return result.failed("服务器开小差，请稍后再试");
    }
}
