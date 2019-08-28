package com.tahoecn.bo.common.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.tahoecn.core.json.JSONResult;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    public static void listToExcel(List<?> list,String title, OutputStream outputStream) throws Exception {
        if(list!=null){
            if(list.size()!=0){
                ExportParams exportParams = new ExportParams(title,"SheetData");
                Workbook workbook = ExcelExportUtil.exportExcel(exportParams, list.get(0).getClass(), list);
                workbook.write(outputStream);
            }
        }
    }

    public static void listToExcel(List<ExcelExportEntity> entity, List<Map<String,Object>> list, String title, OutputStream outputStream) throws Exception {
        if(list!=null){
            if(list.size()!=0){
                Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(title,"SheetData"), entity,list);
                workbook.write(outputStream);
            }
        }
    }

    public static JSONResult excelToList(InputStream inputStream, Class<?> cls, int titleRows, int headRows) throws Exception {
        ImportParams importParams = new ImportParams();
        importParams.setTitleRows(titleRows);
        importParams.setHeadRows(headRows);

        List<Object> list = ExcelImportUtil.importExcel(inputStream, cls, importParams);
        long index = titleRows + headRows;

        List<String> errors = new ArrayList();
        for(Object obj : list){
            index++;
            List<String> rowErrors = ModelValidatorUtil.modelValidator(obj);
            if(rowErrors.size()!=0){
                errors.add("第"+index+"行"+rowErrors);
            }
        }

        JSONResult jsonResult = new JSONResult();

        //有一个错误整个EXCEL都不能导入到LIST中
        if(errors.size()!=0){
            jsonResult.setCode(-1);
            jsonResult.setMsg("Excel读取失败！");
            jsonResult.setData(errors);
        }else{
            jsonResult.setCode(0);
            jsonResult.setMsg("Excel读取成功！");
            jsonResult.setData(list);
        }

        return jsonResult;
    }
}