package com.cmap.service;

import java.util.List;

import com.cmap.exception.ServiceLayerException;
import com.cmap.model.ProvisionLogConfigBackupError;
import com.cmap.service.vo.ProvisionAccessLogVO;
import com.cmap.service.vo.ProvisionServiceVO;
import com.cmap.service.vo.VersionServiceVO;

/**
 *
 * @author 不滅神話
 *
 */
public interface ProvisionService {

	/**
	 * 查詢供裝紀錄
	 * @param psVO ({@link com.cmap.service.vo.ProvisionServiceVO}
	 * @return
	 */
	public List<ProvisionServiceVO> findProvisionLog(ProvisionServiceVO psVO) throws ServiceLayerException;

	/**
	 * 新增供裝紀錄
	 * @param psVO ({@link com.cmap.service.vo.ProvisionServiceVO}
	 * @return
	 */
	public boolean insertProvisionLog(ProvisionServiceVO masterVO) throws ServiceLayerException;

	public ProvisionAccessLogVO checkOrInsertProvisionAccessLog(ProvisionAccessLogVO palVO, boolean isNew, boolean doChk) throws ServiceLayerException;

	/**
	 * 查詢組態備份失敗紀錄
	 * @param vsVO
	 * @return
	 * @throws ServiceLayerException
	 */
	public List<ProvisionLogConfigBackupError> findProvisionLogConfigBackupError(VersionServiceVO vsVO)
			throws ServiceLayerException;
}
