package com.yoonjoy.myrestapi.person;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/person", produces = MediaTypes.HAL_JSON_VALUE)
public class PersonController {

    @GetMapping("/people")
    public HttpEntity<PersonModel> showAll() {
        return null;
    }

}