package com.tahoecn.bo.controller.sample;

import com.tahoecn.core.json.JSONResult;
import com.tahoecn.bo.controller.TahoeBaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@RestController
public class RestSampleController extends TahoeBaseController {


    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public JSONResult hello(){
        JSONResult jsonResult = new JSONResult();
        jsonResult.setCode(0);
        jsonResult.setMsg("SUCCESS");
        jsonResult.setData(null);
        return jsonResult;
    }
}