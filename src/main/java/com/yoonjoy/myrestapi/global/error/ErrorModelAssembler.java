package com.yoonjoy.myrestapi.global.error;

import com.yoonjoy.myrestapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ErrorModelAssembler implements RepresentationModelAssembler<Errors, EntityModel<Errors>> {

    @Override
    public EntityModel<Errors> toModel(Errors errors) {
        return EntityModel.of(errors, linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }

}
