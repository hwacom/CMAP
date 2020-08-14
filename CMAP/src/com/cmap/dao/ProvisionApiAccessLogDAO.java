package com.cmap.dao;

import com.cmap.model.ProvisionApiAccessLog;

public interface ProvisionApiAccessLogDAO extends BaseDAO {

	/**
	 * 查詢5分鐘內未完成之API供裝請求
	 * @param checkHash
	 * @return
	 */
	public ProvisionApiAccessLog findProvisionApiAccessLogByHash(String checkHash);

	void saveProvisionApiLog(ProvisionApiAccessLog entity);
}
