package com.bibengine.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/regulamin")
    public String regulamin() { return "regulamin"; }

    @GetMapping("/docs")
    public String docs() { return "docs"; }
}
