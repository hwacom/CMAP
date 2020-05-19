package com.cmap.plugin.module.iptracepoller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.plugin.module.ip.maintain.IpMaintainDAO;
import com.cmap.plugin.module.ip.maintain.IpMaintainServiceVO;
import com.cmap.plugin.module.ip.maintain.ModuleIpDataSetting;
import com.cmap.service.impl.CommonServiceImpl;

@Service("ipTracePollerService")
public class IpTracePollerServiceImpl extends CommonServiceImpl implements IpTracePollerService {
    @Log
    private static Logger log;

    @Autowired
    private  IpTracePollerDAO  ipTracePollerDAO;
    
    @Autowired
    private IpMaintainDAO ipMaintainDAO;
    
    @Autowired
    private DatabaseMessageSourceBase messageSource;

    @Override
    public List<IpTracePollerVO> findModuleIpTrace(IpTracePollerVO searchVO, Integer startRow, Integer pageLength) throws ServiceLayerException {
    	List<IpTracePollerVO> retList = new ArrayList();
    	try {
        	//DAO取回DB資料
        	 retList = ipTracePollerDAO.findModuleIpTrace( searchVO, startRow, pageLength);
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢異常，請重新操作!");
        }
        return retList;
    }
    
	@Override
	public long countIpTraceDataFromDB(IpTracePollerVO searchVO)  throws ServiceLayerException {
		long retCount = 0;
		try {
			retCount = ipTracePollerDAO.countIpTraceDataFromDB(searchVO);

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢失敗，請重新操作");
		}
		return retCount;
	}
	
	@Override
	public IpTracePollerVO findModuleIpTraceFromNetFlow(IpTracePollerVO searchVO) throws ServiceLayerException {
		IpTracePollerVO retVO = new IpTracePollerVO();
		String msg = ""; //顯示執行結果訊息
		try {
			String groupId = searchVO.getQueryGroupId();
			String fromDateTime = searchVO.getQueryFromDateTime();
			String ipAddress = searchVO.getQueryClientIp();
			if (StringUtils.isBlank(ipAddress)) {
				// 若 IP 為空則無法繼續流程
				throw new ServiceLayerException("NET_FLOW 資料 IP_ADDRESS 為空!! (fromDateTime: " + fromDateTime + ", ipAddress: " + ipAddress + ")");
			}
			//轉換日期格式 for SQL
			SimpleDateFormat FORMAT_YYYYMMDD_HH24MISS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			SimpleDateFormat FORMAT_YYYY_MM_DD_HH24_MI_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			
			Date dateTime = FORMAT_YYYYMMDD_HH24MISS.parse(fromDateTime);
			searchVO.setQueryFromDateTime(FORMAT_YYYY_MM_DD_HH24_MI_SS.format(dateTime));
		
			List<IpTracePollerVO> dataList = ipTracePollerDAO.findModuleIpTraceFromNetFlow(searchVO);

			if (dataList == null || (dataList != null && dataList.isEmpty())) {
				// 若IpTrace查無IP資料,填入預設值
				retVO.setDeviceName("N/A");
				retVO.setDeviceModel("N/A");
				retVO.setClientIp(ipAddress);

				// 查詢IP備註groupId
				IpMaintainServiceVO imsVO = new IpMaintainServiceVO();
				imsVO.setQueryGroup(groupId);
				imsVO.setQueryIp(ipAddress);
				//是否查詢條件為sensorId
			  	boolean isSensorSearchMode = StringUtils.equalsIgnoreCase(Env.NET_FLOW_SEARCH_MODE_WITH_SENSOR, Constants.DATA_Y);
			  	imsVO.setIsSensorSearchMode(isSensorSearchMode);
				
			  	List<Object[]> ipMaintainObj = ipMaintainDAO.findModuleIpDataSetting(imsVO, null, null);

				if (ipMaintainObj != null && !ipMaintainObj.isEmpty()) {
				    ModuleIpDataSetting mids = (ModuleIpDataSetting)ipMaintainObj.get(0)[0];
				    retVO.setIpDesc(mids != null ? mids.getIpDesc() : Env.IP_DESC_NULL_SHOW_WHAT);
				} else {
				    retVO.setIpDesc(Env.IP_DESC_NULL_SHOW_WHAT);
				}

				retVO.setPortName("N/A");

				msg = messageSource.getMessage("msg.ip.not.found", Locale.TAIWAN, null);  // 查詢IP紀錄無資料
                retVO.setShowMsg(msg);
			} else {
				//使用IpTrace資料
				retVO = dataList.get(0);
				msg = messageSource.getMessage("msg.ip.found", Locale.TAIWAN, null);  // 查詢IP紀錄成功
				retVO.setShowMsg(msg);
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			msg = messageSource.getMessage("msg.query.exception.please.retry", Locale.TAIWAN, null); // 查找資料異常，請重新操作
            retVO.setShowMsg(msg);
		}
		return retVO;
	}
}
