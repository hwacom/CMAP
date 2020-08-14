package com.cmap.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.SysLoginInfoDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.SysLoginInfo;
import com.cmap.service.SysLoginInfoService;
import com.cmap.service.vo.SysLoginInfoVO;

@Service("sysLoginInfoService")
@Transactional
public class SysLoginInfoServiceImpl extends CommonServiceImpl implements SysLoginInfoService {
	@Log
	private static Logger log;

	@Autowired
	private SysLoginInfoDAO sysLoginInfoDAO;

	@Override
	public List<SysLoginInfoVO> findLoginInfo(SysLoginInfoVO sliVO) throws ServiceLayerException {
		
		List<SysLoginInfo> infoList = sysLoginInfoDAO.findSysLoginInfo(sliVO);
		
		if(infoList == null || infoList.isEmpty()) {
			throw new ServiceLayerException("查無紀錄!!");
		}
		
		List<SysLoginInfoVO> result = new ArrayList<>();
		SysLoginInfoVO currVO = null;
		
		for(SysLoginInfo info : infoList) {
			currVO = new SysLoginInfoVO();
			BeanUtils.copyProperties(info, currVO);
			
			currVO.setLoginTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(info.getLoginTime()));
			currVO.setLogoutTimeStr(info.getLogoutTime() == null ? "":Constants.FORMAT_YYYYMMDD_HH24MISS.format(info.getLogoutTime()));
			
			result.add(currVO);
		}
		
		return result;
	}

}
