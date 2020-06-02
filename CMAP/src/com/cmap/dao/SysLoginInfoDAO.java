package com.cmap.dao;

import java.util.List;

import com.cmap.model.SysLoginInfo;

public interface SysLoginInfoDAO extends BaseDAO {

	List<SysLoginInfo> findSysLoginInfoBySessionId(List<String> ids);
	
	SysLoginInfo findSysLoginInfoBySessionId(String sessionId);
	
	void saveSysLoginInfo(SysLoginInfo model);

	void deleteSysLoginInfo(SysLoginInfo model);

	void updateLogoutTime(String sessionId);
}
