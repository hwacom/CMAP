package com.cmap.service.impl;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.PrtgDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.service.PrtgService;
import com.nimbusds.oauth2.sdk.util.StringUtils;

@Service("prtgService")
@Transactional
public class PrtgServiceImpl implements PrtgService {
	@Log
	private static Logger log;

	@Autowired
	private PrtgDAO prtgDAO;

	@Override
	public PrtgAccountMapping getMappingByAccount(String account) throws ServiceLayerException {
		try {
			if (StringUtils.isBlank(account)) {
				throw new ServiceLayerException("取得PRTG mapping表失敗! >> 傳入account為空");
			}

			PrtgAccountMapping mapping = prtgDAO.findPrtgAccountMappingByAccount(account);

			if (mapping == null) {
				throw new ServiceLayerException("取得PRTG mapping表失敗! >> 傳入account查無資料: " + account);
			}

			return mapping;

		} catch (ServiceLayerException sle) {
		    throw sle;

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("取得PRTG mapping表失敗!  >> 非預期錯誤(" + e.getMessage() + ")");
		}
	}
}
