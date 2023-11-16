package com.tianqj.tjstorageservice.controllers;

import com.tianqj.tjstorageservice.services.CreateResourceService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CreateResourceController {

    private final CreateResourceService createResourceService;

    @PostMapping("/create")
    public String createResource() {}
}
