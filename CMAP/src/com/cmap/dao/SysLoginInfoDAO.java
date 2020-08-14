package com.cmap.dao;

import java.util.List;

import com.cmap.model.SysLoginInfo;
import com.cmap.service.vo.SysLoginInfoVO;

public interface SysLoginInfoDAO extends BaseDAO {

	List<SysLoginInfo> findSysLoginInfoBySessionId(List<String> ids);
	
	void saveSysLoginInfo(SysLoginInfo model);

	void deleteSysLoginInfo(SysLoginInfo model);

	void updateLogoutTime(String sessionId);

	SysLoginInfo findLastSysLoginInfoBySessionId(String sessionId);

	List<SysLoginInfo> findSysLoginInfo(SysLoginInfoVO vo);
}
