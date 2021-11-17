package com.yoonjoy.myrestapi.events;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EventValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Event.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventDto.Create dto = (EventDto.Create) target;
        if (dto.getBasePrice() > dto.getMaxPrice() && dto.getMaxPrice() != 0) {
//            errors.rejectValue("basePrice", "wrongValue", "BasePrices is wrong");
//            errors.rejectValue("maxPrice", "wrongValue", "BasePrices is wrong");
            errors.reject("wrongPrices", "value of prices are wrong");
        }

        LocalDateTime endEventDateTime = dto.getEndEventDateTime();
        LocalDateTime beginEventDateTime = dto.getBeginEventDateTime();
        LocalDateTime closeEnrollmentDateTime = dto.getCloseEnrollmentDateTime();

        if (endEventDateTime.isBefore(beginEventDateTime) ||
            endEventDateTime.isBefore(closeEnrollmentDateTime) ||
            endEventDateTime.isBefore(dto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
        }
    }

}
