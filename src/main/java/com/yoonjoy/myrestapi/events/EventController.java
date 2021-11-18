package com.yoonjoy.myrestapi.events;

import com.yoonjoy.myrestapi.error.ErrorResource;
import com.yoonjoy.myrestapi.index.IndexController;
import java.net.URI;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody @Valid EventDto.Create dto, Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        this.eventValidator.validate(dto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event savedEvent = this.eventRepository.save(dto.toEntity());
        WebMvcLinkBuilder linkBuilder = linkTo(EventController.class);
        URI createdUri = linkBuilder.slash(savedEvent.getId()).toUri();

        EntityModel<Event> eventModel = EntityModel.of(savedEvent);
        eventModel.add(linkBuilder.slash(savedEvent.getId()).withSelfRel());
        eventModel.add(linkBuilder.withRel("query-events"));
        eventModel.add(linkBuilder.withRel("update-event"));
        eventModel.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventModel);
    }

    private ResponseEntity<?> badRequest(Errors errors) {
//        EntityModel<Errors> errorModel = EntityModel.of(errors);
//        errorModel.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
        return ResponseEntity.badRequest().body(ErrorResource.modelOf(errors));
    }
}
