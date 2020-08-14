package com.cmap.service;

import java.util.Map;

import com.cmap.model.ProvisionApiAccessLog;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author Owen
 *
 */
public interface ProvisionApiService {

	/**
	 * 查詢5分鐘內未完成之API供裝請求
	 * @param checkHash
	 * @return
	 */
	public ProvisionApiAccessLog findProvisionApiAccessLog(String checkHash);

	boolean saveProvisionApiLog(ProvisionApiAccessLog entity);

	Map<String, Object> doApiProvision(JsonNode jsonData, String ip);

	Map<String, Object> getDefaultScriptInfo(JsonNode jsonData, String ip);
}
