package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@PreAuthorize("permitAll()")
public class TeapotController {


    @RequestMapping(value = "/teapot", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
    public String teapot() {
        return "This is a teapot. In a video game store. Whatever you do, please do NOT tip it over and pour it out.";
    }
}