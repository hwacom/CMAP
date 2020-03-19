package com.cmap.plugin.module.iptracepoller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.service.impl.CommonServiceImpl;

@Service("ipTracePollerService")
public class IpTracePollerServiceImpl extends CommonServiceImpl implements IpTracePollerService {
    @Log
    private static Logger log;

    @Autowired
    private  IpTracePollerDAO  ipTracePollerDAO;

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
}
