package org.unidue.ub.libintel.almaconnector.controller;

import org.apache.commons.io.IOExceptionWithCause;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.net.UnknownHostException;

/**
 * the simple page controllers
 */
@Controller
public class StartController {


    /**
     * displys the start page of the alma microservice
     * @return the start html page
     */
    @GetMapping("/start")
    public String getStartPage() {
        return "start";
    }

    /**
     * Exception handling if one of the following exceptions is thrown: MissingShibbolethDataException, MissingHisDataException, AlmaConnectionException
     *
     * @param ex Exception to be handled
     * @return the error page bound to the error message.
     */
    @ExceptionHandler({UnknownHostException.class, IOExceptionWithCause.class})
    public ModelAndView handleException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }
}
