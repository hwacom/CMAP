package com.cmap.utils;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.cmap.exception.ServiceLayerException;

public interface DataExportUtils {

    public String output2Web(
            HttpServletResponse response, String fileName, boolean zipped, List<? extends Object> dataList, String[] fieldNames, String[] columnTitles) throws ServiceLayerException;

    public void output2File(
            String outputDirPath, String fileName, boolean zipped, List<? extends Object> dataList, String[] fieldNames, String[] columnTitles) throws ServiceLayerException;
}
