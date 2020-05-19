package com.cmap.plugin.module.wifipoller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.service.impl.CommonServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("wifiPollerService")
public class WifiPollerServiceImpl extends CommonServiceImpl implements WifiPollerService {
    @Log
    private static Logger log;

    @Autowired
    private  WifiPollerDAO  wifiPollerDAO;

    @Override
    public List<WifiPollerVO> findModuleWifiTraceMst(WifiPollerVO searchVO, Integer startRow, Integer pageLength) throws ServiceLayerException {
    	List<WifiPollerVO> retList = new ArrayList<WifiPollerVO>();
    	try {
        	//DAO取回DB資料
        	 retList = wifiPollerDAO.findModuleWifiTraceMst( searchVO, startRow, pageLength);
            //填充VO並放入retList
            if (retList != null && !retList.isEmpty()) {
            	for (WifiPollerVO retVO : retList) {
            		String totalTraffic = retVO.getTotalTraffic();
                    String uploadTraffic = retVO.getUploadTraffic();
                    String downloadTraffic = retVO.getDownloadTraffic();
            		// 轉換流量顯示單位
                    retVO.setTotalTraffic(convertByteSizeUnit(new BigDecimal(totalTraffic), Env.NET_FLOW_SHOW_UNIT_OF_RESULT_DATA_SIZE));
                    retVO.setUploadTraffic(convertByteSizeUnit(new BigDecimal(uploadTraffic), Env.NET_FLOW_SHOW_UNIT_OF_RESULT_DATA_SIZE));
                    retVO.setDownloadTraffic(convertByteSizeUnit(new BigDecimal(downloadTraffic), Env.NET_FLOW_SHOW_UNIT_OF_RESULT_DATA_SIZE));
            	}
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢異常，請重新操作!");
        }
        return retList;
    }
    
	@Override
	public long countWifiMstRecordFromDB(WifiPollerVO searchVO)  throws ServiceLayerException {
		long retCount = 0;
		try {
			retCount = wifiPollerDAO.countWifiMstDataFromDB(searchVO);

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢失敗，請重新操作");
		}
		return retCount;
	}
	
	@Override
	public Map<String, List<Map<String, String>>> findModuleWifiTraceDetail(WifiPollerVO searchVO) throws ServiceLayerException{
		// 接收資料用
		List<WifiPollerDetailVO> dataList = new ArrayList<>();
		// 封裝使用, 回傳給Controller後才轉JSON
		// uploadTrafficDataList, downloadTrafficDataList, totalTrafficDataList
		// rssiDataList, noiseDataList, snrDataList
    	Map<String, List<Map<String, String>>> retMap = new HashMap<>();
    	//String retListJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(retList);
    	try {
        	// DAO取回DB資料
    		dataList = wifiPollerDAO.findModuleWifiTraceDetail( searchVO);
    		// 封裝容器宣告
    		List<Map<String, String>> uploadTrafficDataList = new ArrayList<>();
    		List<Map<String, String>> downloadTrafficDataList = new ArrayList<>();
    		List<Map<String, String>> totalTrafficDataList = new ArrayList<>();
    		List<Map<String, String>> rssiDataList = new ArrayList<>();
    		List<Map<String, String>> noiseDataList = new ArrayList<>();
    		List<Map<String, String>> snrDataList = new ArrayList<>();
            // 查詢有資料時將資料填充封裝為前端輸出所需格式
            if (dataList != null && !dataList.isEmpty()) {
            	for (WifiPollerDetailVO retVO : dataList) {
            		// 資料時間
            		String pollingTime = retVO.getPollingTime();
            		// Part1. traffic Data封裝
            		Map<String, String>  uploadTrafficData = new HashMap<>();
            		Map<String, String>  downloadTrafficData = new HashMap<>();
            		Map<String, String>  totalTrafficData = new HashMap<>();
            		// 轉換流量顯示單位Octets Byte
                    String uploadTraffic = retVO.getUploadTraffic();
                    String downloadTraffic =retVO.getDownloadTraffic();
                    String totalTraffic = retVO.getTotalTraffic();
                    uploadTrafficData.put("x", pollingTime);
                    uploadTrafficData.put("y", uploadTraffic);
                    downloadTrafficData.put("x", pollingTime);
                    downloadTrafficData.put("y", downloadTraffic);
                    totalTrafficData.put("x", pollingTime);
                    totalTrafficData.put("y", totalTraffic);
                    // Part2. quality Data封裝
                    Map<String, String>  rssiData = new HashMap<>();
            		Map<String, String>  noiseData = new HashMap<>();
            		Map<String, String>  snrData = new HashMap<>();
            		// 轉換流量顯示單位
                    String rssi = retVO.getRssi();
                    String noise = retVO.getNoise();
                    String snr = retVO.getSnr();
                    rssiData.put("x", pollingTime);
                    rssiData.put("y", rssi);
                    noiseData.put("x", pollingTime);
                    noiseData.put("y", noise);
                    snrData.put("x", pollingTime);
                    snrData.put("y", snr);
                    // Part3. 添加至DataList
                    uploadTrafficDataList.add(uploadTrafficData);
                    downloadTrafficDataList.add(downloadTrafficData);
                    totalTrafficDataList.add(totalTrafficData);
                    rssiDataList.add(rssiData);
                    noiseDataList.add(noiseData);
                    snrDataList.add(snrData);
            	}
            	retMap.put("uploadTrafficDataList", uploadTrafficDataList);
            	retMap.put("downloadTrafficDataList", downloadTrafficDataList);
            	retMap.put("totalTrafficDataList", totalTrafficDataList);
            	retMap.put("rssiDataList", rssiDataList);
            	retMap.put("noiseDataList", noiseDataList);
            	retMap.put("snrDataList", snrDataList);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢異常，請重新操作!");
        }
        return retMap;		
	}
}
