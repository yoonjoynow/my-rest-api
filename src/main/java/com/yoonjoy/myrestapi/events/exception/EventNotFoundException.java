package com.yoonjoy.myrestapi.events.exception;

import com.yoonjoy.myrestapi.global.error.exception.BusinessException;
import com.yoonjoy.myrestapi.global.error.exception.ErrorCode;

public class EventNotFoundException extends BusinessException {

    public EventNotFoundException(String message) {
        super(message, ErrorCode.EVENT_NOT_FOUND);
    }
}
