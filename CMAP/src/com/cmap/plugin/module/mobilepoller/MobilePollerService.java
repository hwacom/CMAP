package com.cmap.plugin.module.mobilepoller;

import java.util.List;
import java.util.Map;

import com.cmap.exception.ServiceLayerException;

public interface MobilePollerService {

    /**
     * 查找MOBILE_MST連線資料
     * @param searchVO
     * @param startRow 分頁起始列
     * @param pageLength 分頁長度
     * @return Mobile主檔資料List(VO格式)
     */
    public List<MobilePollerVO> findModuleMobileTraceMst(MobilePollerVO searchVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 查找符合條件資料筆數
     * @param searchVO
     * @return
     * @throws ServiceLayerException
     */
	public long countMobileMstRecordFromDB(MobilePollerVO searchVO) throws ServiceLayerException;

    /**
     * 查找MOBILE_DETAIL連線資料
     * 分兩組 trafficDataList, qualityDataList 
     * 資料均為JSON格式 (x放時間, y放觀測值)
     * @param searchVO
     * @return List
     */
    public Map<String, List<Map<String, String>>> findModuleMobileTraceDetail(MobilePollerVO searchVO) throws ServiceLayerException;
}
