package com.yoonjoy.myrestapi.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class EventModelAssembler implements RepresentationModelAssembler<Event, EntityModel<Event>> {

    @Override
    public EntityModel<Event> toModel( Event event) {
        Links links = generateLinks(event.getId());
        return EntityModel.of(event, links);
    }

    private Links generateLinks(int id) {
        final Class<EventController> controllerClass = EventController.class;
        return Links.of(linkTo(controllerClass).slash(id).withSelfRel(),
                linkTo(controllerClass).slash(id).withRel("update-event"),
                linkTo(controllerClass).withRel("query-events"),
                Link.of("/docs/index.html#resources-events-create").withRel("profile"));
    }

}
