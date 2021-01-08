package com.fl.exception;


import com.fl.utils.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @Author : 傅化韩
 * @create 2020/11/2 15:07
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /*
    *
    * 处理所有不清楚异常
    * */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleException(Throwable e){
        e.printStackTrace();
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getMessage()));
    }

    /**
     * BadCredentialsException
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> badCredentialsException(BadCredentialsException e){
        e.printStackTrace();
        // 打印堆栈信息
        String message = "坏的凭证".equals(e.getMessage()) ? "用户名或密码不正确" : e.getMessage();
        log.error(message);
        return buildResponseEntity(ApiError.error(message));
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ApiError> badRequestException(BadRequestException e) {
        e.printStackTrace();

        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getStatus(),e.getMessage()));
    }

    /**
     * 处理 EntityNotFound
     */
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ApiError> entityNotFoundException(EntityNotFoundException e) {
        e.printStackTrace();

        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(NOT_FOUND.value(),e.getMessage()));
    }

    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
     //   e.printStackTrace();

        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        String[] str = Objects.requireNonNull(e.getBindingResult().getAllErrors().get(0).getCodes())[1].split("\\.");
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
      //  String msg = "不能为空";
        //if(msg.equals(message)){
            message = str[1] + ":" + message;
       // }
        return buildResponseEntity(ApiError.error(message));
    }

//
//    /**
//     * 校验错误拦截处理
//     *
//     * @param exception 错误信息集合
//     * @return 错误信息
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity validationBodyException(MethodArgumentNotValidException exception) {
//        BindingResult result = exception.getBindingResult();
//        String message = "";
//        if (result.hasErrors()) {
//            List<ObjectError> errors = result.getAllErrors();
//            if (errors != null) {
//                errors.forEach(p -> {
//                    FieldError fieldError = (FieldError) p;
//                    log.error("Data check failure : object{" + fieldError.getObjectName() + "},field{" + fieldError.getField() +
//                            "},errorMessage{" + fieldError.getDefaultMessage() + "}");
//
//                });
//                if (errors.size() > 0) {
//                    FieldError fieldError = (FieldError) errors.get(0);
//                    message = fieldError.getDefaultMessage();
//                }
//            }
//        }
//        return buildResponseEntity(ApiError.error("".equals(message) ? "请填写正确信息" : message));
//    }

    /**
     * 参数类型转换错误
     *
     * @param exception 错误
     * @return 错误信息
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity parameterTypeException(HttpMessageConversionException exception) {
        log.error(exception.getCause().getLocalizedMessage());
        return buildResponseEntity(ApiError.error("类型转换错误"));

    }

    /**
     * 统一返回
     */
    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, HttpStatus.valueOf(apiError.getStatus()));
    }
}
