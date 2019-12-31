package net.ys.controller;

import net.ys.schedule.Main;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * User: NMY
 * Date: 2019-12-26
 * Time: 9:29
 */
@RestController
public class TestController {

    @Resource
    private Main main;

    @GetMapping("/test")
    public String test() throws IOException {
        main.genUrl();
        return "success";
    }
}
