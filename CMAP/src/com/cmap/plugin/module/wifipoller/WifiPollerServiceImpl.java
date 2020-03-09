package com.cmap.plugin.module.wifipoller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.plugin.module.netflow.NetFlowVO;
import com.cmap.service.impl.CommonServiceImpl;

@Service("wifiPollerService")
public class WifiPollerServiceImpl extends CommonServiceImpl implements WifiPollerService {
    @Log
    private static Logger log;

    @Autowired
    private  WifiPollerDAO  wifiPollerDAO;

    @Override
    public List<WifiPollerVO> findModuleWifiTraceMst(WifiPollerVO searchVO, Integer startRow, Integer pageLength) throws ServiceLayerException {
    	List<WifiPollerVO> retList = new ArrayList();
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
}
