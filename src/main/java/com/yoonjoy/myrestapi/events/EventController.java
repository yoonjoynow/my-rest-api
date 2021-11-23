package com.yoonjoy.myrestapi.events;

import com.yoonjoy.myrestapi.global.error.ErrorModelAssembler;
import java.net.URI;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final EventValidator eventValidator;
    private final EventModelAssembler eventModelAssembler;
    private final ErrorModelAssembler errorModelAssembler;

    public EventController(EventRepository eventRepository, EventValidator eventValidator, EventModelAssembler eventModelAssembler, ErrorModelAssembler errorModelAssembler) {
        this.eventRepository = eventRepository;
        this.eventValidator = eventValidator;
        this.eventModelAssembler = eventModelAssembler;
        this.errorModelAssembler = errorModelAssembler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Integer id) {
        Optional<Event> result = this.eventRepository.findById(id);
        Event event = result.orElseThrow(() -> new IllegalStateException("존재하지 않는 이벤트입니다"));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> queryEvents(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<Event> eventPage = this.eventRepository.findAll(pageable);
        PagedModel pagedModel = assembler.toModel(eventPage, this.eventModelAssembler);
        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody @Valid EventDto.Create dto, Errors errors) {
        this.eventValidator.validate(dto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event savedEvent = this.eventRepository.save(dto.toEntity());
        EntityModel<Event> eventModel = this.eventModelAssembler.toModel(savedEvent);
        URI createdUri = eventModel.getRequiredLink(IanaLinkRelations.SELF).toUri();
        return ResponseEntity.created(createdUri).body(eventModel);
    }

    private ResponseEntity<?> badRequest(Errors errors) {
        EntityModel<Errors> errorModel = this.errorModelAssembler.toModel(errors);
        return ResponseEntity.badRequest().body(errorModel);
    }
}
