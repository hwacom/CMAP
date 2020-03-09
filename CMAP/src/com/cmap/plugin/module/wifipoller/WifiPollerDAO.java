package com.cmap.plugin.module.wifipoller;

import java.util.List;

import com.cmap.dao.BaseDAO;

public interface WifiPollerDAO extends BaseDAO {

    /**
     * 查找WIFI_MST連線資料
     * @param clientMac
     * @param startTime
     * @param endTime
     * @param clientIp
     * @param apName
     * @param ssid
     * @return
     */
    public List<ModuleWifiTraceMst> findModuleWifiTraceMst(String clientMac, String startTime, String endTime, String clientIp, String apName, String ssid);

    /**
     * 查找WIFI_MST連線資料 by UK
     * @param clientMac
     * @param startTime
     * @return
     */
    public ModuleWifiTraceMst findModuleWifiTraceMstByUK(String clientMac, String startTime);

    /**
     * 查找WIFI_MST連線資料
     * @param searchVO
     * @param startRow 分頁起始列
     * @param pageLength 分頁長度
     * @return VO格式
     */
    public List<WifiPollerVO> findModuleWifiTraceMst(WifiPollerVO searchVO, Integer startRow, Integer pageLength) ;
    
    /**
     * 查找符合條件資料筆數
     * @param searchVO
     * @return 資料筆數
     */
    public long countWifiMstDataFromDB(WifiPollerVO searchVO);
}
