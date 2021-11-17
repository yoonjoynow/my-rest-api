package com.yoonjoy.myrestapi.error;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ErrorResponse {

    private String message;
    private int status;
    private List<FieldError> errors;
    private String code;

}
