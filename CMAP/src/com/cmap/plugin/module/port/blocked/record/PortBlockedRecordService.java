package com.cmap.plugin.module.port.blocked.record;

import java.util.List;
import com.cmap.exception.ServiceLayerException;

public interface PortBlockedRecordService {

    /**
     * 取得符合條件資料筆數
     * @param irVO
     * @return
     * @throws ServiceLayerException
     */
    public long countModuleBlockedPortList(PortBlockedRecordVO irVO) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param irVO
     * @return
     * @throws ServiceLayerException
     */
    public List<PortBlockedRecordVO> findModuleBlockedPortList(PortBlockedRecordVO irVO, Integer startRow, Integer pageLength) throws ServiceLayerException;
}
