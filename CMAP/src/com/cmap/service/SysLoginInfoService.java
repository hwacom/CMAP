package com.cmap.service;

import java.util.List;

import com.cmap.exception.ServiceLayerException;
import com.cmap.service.vo.SysLoginInfoVO;

public interface SysLoginInfoService {

	List<SysLoginInfoVO> findLoginInfo(SysLoginInfoVO sliVO) throws ServiceLayerException;

}
