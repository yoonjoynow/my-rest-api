package com.yoonjoy.myrestapi.person;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter @Setter
public class PersonModel extends RepresentationModel<PersonModel> {

    String firstName;
    String lastName;
}
