package com.cmap.plugin.module.wifipoller;

import java.util.List;

import com.cmap.exception.ServiceLayerException;
import com.cmap.plugin.module.netflow.NetFlowVO;

public interface WifiPollerService {

    /**
     * 查找WIFI_MST連線資料
     * @param searchVO
     * @param startRow 分頁起始列
     * @param pageLength 分頁長度
     * @return Wifi主檔資料List(VO格式)
     */
    public List<WifiPollerVO> findModuleWifiTraceMst(WifiPollerVO searchVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 查找符合條件資料筆數
     * @param searchVO
     * @return
     * @throws ServiceLayerException
     */
	public long countWifiMstRecordFromDB(WifiPollerVO searchVO) throws ServiceLayerException;

}
