package com.cmap.utils.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.exception.ServiceLayerException;
import com.cmap.service.ResourceService;
import com.cmap.service.vo.ResourceServiceVO;
import com.cmap.utils.DataExportUtils;

public class CsvExportUtils implements DataExportUtils {
    private static Logger log = LoggerFactory.getLogger(CsvExportUtils.class);

    private ResourceService resourceService;

    @Override
    public String output2Web(
            HttpServletResponse response, String fileName, boolean zipped, List<? extends Object> dataList, String[] fieldNames, String[] columnTitles) throws ServiceLayerException {
        String retVal = null;
        try {
            // Step 1. 先輸出到暫存資料夾
            String csvFileName = fileName + ".csv";
            String csvFilePath = Env.DEFAULT_DATA_EXPORT_TEMP_LOCATION + File.separator + csvFileName;
            String tempFileLocation = csvFilePath;
            //FileWriter writer = new FileWriter(tempFileLocation);
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(tempFileLocation),
                    Charset.forName("Big5").newEncoder());

            List<String> values = new ArrayList<>();

            // 寫入標題列
            for (String title : columnTitles) {
                values.add(title);
            }
            writeLine(writer, values);

            // 寫入資料內容
            String value = null;
            for (Object dataObj : dataList) {
                values = new ArrayList<>();

                for (String fieldName : fieldNames) {
                    value = BeanUtils.getProperty(dataObj, fieldName);
                    values.add(value.replaceAll("\r\n", "		"));
                }

                writeLine(writer, values);
            }

            writer.flush();
            writer.close();

            // Step 2. 壓縮成ZIP檔
            List<File> srcfileList = new ArrayList<>();
            File srcfile = new File(csvFilePath);
            srcfileList.add(srcfile);

            String zipFileName = fileName + ".zip";
            String zipFilePath = Env.DEFAULT_DATA_EXPORT_TEMP_LOCATION + File.separator + zipFileName;
            File targetZipfile = new File(zipFilePath);

            CommonUtils.zipFiles(srcfileList, targetZipfile);

            // Step 3. 寫入紀錄
            ResourceServiceVO rsVO = new ResourceServiceVO();
            rsVO.setFileName(fileName);
            rsVO.setFileExtName("zip");
            rsVO.setFileFullName(zipFileName);
            rsVO.setContentType("application/zip");

            resourceService = (ResourceService)ApplicationContextUtil.getBean("resourceService");
            String fileId = resourceService.addResourceInfo(rsVO);
            retVal = fileId;

            /*
            // Step 3. 輸出至Response彈出下載視窗
            String downloadFilePath = zipFilePath;
            CommonUtils.downFile(response, downloadFilePath, zipFileName);
            */

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException(e.getMessage());
        }
        return retVal;
    }

    @Override
    public void output2File(
            String outputDirPath, String fileName, boolean zipped, List<? extends Object> dataList, String[] fieldNames, String[] columnTitles) throws ServiceLayerException {
        try {

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException(e.getMessage());
        }
    }

    //https://tools.ietf.org/html/rfc4180
    private String followCVSformat(String value) {
        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        if (result.contains(",")) {
            result = "\"".concat(result).concat("\"");
        }
        return result;
    }

    public void writeLine(OutputStreamWriter w, List<String> values) throws IOException {
        writeLine(w, values, Constants.DEFAULT_CSV_SEPARATOR, ' ');
    }

    public void writeLine(OutputStreamWriter w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    private void writeLine(OutputStreamWriter w, List<String> values, char separators, char customQuote) throws IOException {
        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = Constants.DEFAULT_CSV_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }

            value = value != null ? followCVSformat(value) : "";
            if (customQuote == ' ') {
                sb.append(value);
            } else {
                sb.append(customQuote).append(value).append(customQuote);
            }

            first = false;
        }
        sb.append(Constants.DEFAULT_CSV_LINE_BREAK_SYMBOL);
        w.append(sb.toString());
    }
}
