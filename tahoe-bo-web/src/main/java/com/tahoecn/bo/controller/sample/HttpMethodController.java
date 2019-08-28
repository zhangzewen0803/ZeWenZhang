package com.tahoecn.bo.controller.sample;

import com.tahoecn.bo.controller.TahoeBaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@RestController
public class HttpMethodController extends TahoeBaseController {
    @RequestMapping(value="/http/method/put",method = RequestMethod.PUT)
    public String put(String name){
        System.out.println("request put!");
        return name;
    }

    @RequestMapping(value="/http/method/post",method = RequestMethod.POST)
    public String post(String name){
        System.out.println("request post!");
        return name;
    }

    @RequestMapping(value="/http/method/get",method = RequestMethod.GET)
    public String get(String name){
        System.out.println("request get!");
        return name;
    }

    @RequestMapping(value="/http/method/delete",method = RequestMethod.DELETE)
    public String delete(){
        System.out.println("request delete!");
        return "delete finish!";
    }

    @RequestMapping(value="/http/method/head",method = RequestMethod.HEAD)
    public String head(){
        System.out.println("request head!");
        return "head finish!";
    }
}