package org.example.theblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DefaultController {

    @GetMapping(value = "/")
    public String index() {
        return "index";
    }

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET}, value = "/**/{:[^.]*}")
    public String redirectToIndex() {
        return "forward:/";
    }
}
