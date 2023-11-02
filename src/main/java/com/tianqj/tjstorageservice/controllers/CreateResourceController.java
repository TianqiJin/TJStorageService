package com.tianqj.tjstorageservice.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateResourceController {

    @PostMapping("/create")
    public String createResource() {}
}
