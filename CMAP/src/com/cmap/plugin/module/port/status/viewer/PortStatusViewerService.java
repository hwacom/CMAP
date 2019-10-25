package com.cmap.plugin.module.port.status.viewer;

import java.util.List;

import com.cmap.exception.ServiceLayerException;

public interface PortStatusViewerService {

	/**
	 * 取得設備Port狀態清單
	 * @param psvVO
	 * @return
	 * @throws ServiceLayerException
	 */
	public List<PortStatusViewerVO> getPortStatusList(PortStatusViewerVO psvVO) throws ServiceLayerException;
}
