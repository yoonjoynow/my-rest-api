package com.yoonjoy.myrestapi.index;

import com.yoonjoy.myrestapi.events.EventController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public class IndexController {

    @GetMapping("/api")
    public ResponseEntity<?> index() {
        var indexModel = new RepresentationModel<>();
        indexModel.add(linkTo(EventController.class).withRel("events"));
        return ResponseEntity.ok(indexModel);
    }
}
