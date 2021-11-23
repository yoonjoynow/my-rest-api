package com.yoonjoy.myrestapi.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class EventModelAssemblerSupport extends RepresentationModelAssemblerSupport<Event, EntityModel<Event>> {
    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     *
     * @param controllerClass must not be {@literal null}.
     * @param resourceType    must not be {@literal null}.
     */
    public EventModelAssemblerSupport(Class<?> controllerClass, Class<EntityModel<Event>> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    public EntityModel<Event> toModel(Event entity) {
        return null;
    }
}
