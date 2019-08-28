package com.tahoecn.bo.controller.webapi;


import com.tahoecn.bo.common.utils.FileUtils;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.core.json.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "公共管理API", value = "公共管理API")
@RestController
@RequestMapping(value = "/api/common")
public class CommonDataApi extends TahoeBaseController {

    @Autowired
    private FileUtils fileUtils;

    @ApiOperation(value = "导入图片信息", notes = "导入图片信息")
    @RequestMapping(value ="/initImportComm", method = RequestMethod.POST)
    public Map<String, Object> initImportComm(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> uploadResult = fileUtils.fileUploadToLocal(file);

        return uploadResult;
    }


    @ApiOperation(value = "导入图片信息", notes = "导入图片信息")
    @RequestMapping(value ="/initImportComm1", method = RequestMethod.POST)
    public JSONResult initImportComm1(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        List<MultipartFile> list = new ArrayList<MultipartFile>();
        list.add(file);
        JSONResult jsonResult = uploadFiles(list);

        return jsonResult;
    }
}
