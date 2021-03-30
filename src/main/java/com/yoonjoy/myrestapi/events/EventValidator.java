package com.yoonjoy.myrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
            errors.rejectValue("basePrice", "wrongValue", "BasePrice의 값이 이상합니다.");
            errors.rejectValue("basePrice", "wrongValue", "MaxPrice의 값이 이상합니다.");
        }

        //TODO 나머지 데이터들도 검증이 필요함
    }
}
