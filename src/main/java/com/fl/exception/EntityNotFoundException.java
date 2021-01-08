package com.fl.exception;

import org.springframework.util.StringUtils;

/**
 * @Author : 傅化韩
 * @create 2020/10/30 14:59
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class clazz, String field, String val) {
        super(EntityNotFoundException.generateMessage(clazz.getSimpleName(), field, val));
    }

    private static String generateMessage(String entity, String field, String val) {
        return StringUtils.capitalize(entity)
                + " with " + field + " "+ val + " does not exist";
    }
}
