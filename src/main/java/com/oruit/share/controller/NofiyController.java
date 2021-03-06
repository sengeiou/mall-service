package com.oruit.share.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j

public class NofiyController {

    @ResponseBody
    @RequestMapping(value = "/nofiy",method = RequestMethod.POST)
    public String index(HttpServletRequest request, String  component_verify_ticket) {
        log.info("nofiy1=====进入了，component_verify_ticket:{}",component_verify_ticket);
        String component_verify_ticket1 = request.getParameter("component_verify_ticket");
        log.info("nofiy2=====进入了，component_verify_ticket:{}",component_verify_ticket1);
        return "success";
    }
}
