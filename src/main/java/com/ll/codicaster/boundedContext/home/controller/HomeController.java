package com.ll.codicaster.boundedContext.home.controller;

import java.util.Enumeration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ll.codicaster.base.rq.Rq;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final Rq rq;

    @GetMapping("/main")
    public String showMain() {
        return "/usr/home/main";
    }

    @GetMapping("/")
    public String redirectToMain() {
        if (rq.isLogout()) {
            return "redirect:/main";
        }
        if (rq.getNickname() == null) {
            return "redirect:/usr/member/newInfo";
        }
        return "redirect:/main";
    }

    @GetMapping("/usr/home/about")
    public String showAbout() {
        return "usr/home/about";
    }

    @GetMapping("/usr/debugSession")
    @ResponseBody
    public String showDebugSession(HttpSession session) {
        StringBuilder sb = new StringBuilder("Session content:\n");

        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);
            sb.append(String.format("%s: %s\n", attributeName, attributeValue));
        }

        return sb.toString().replaceAll("\n", "<br>");
    }

}
