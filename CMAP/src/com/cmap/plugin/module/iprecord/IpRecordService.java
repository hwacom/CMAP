package com.cmap.plugin.module.iprecord;

import java.util.List;
import com.cmap.exception.ServiceLayerException;

public interface IpRecordService {

    /**
     * 取得符合條件資料筆數
     * @param irVO
     * @return
     * @throws ServiceLayerException
     */
    public long countModuleBlockedIpList(IpRecordVO irVO) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param irVO
     * @return
     * @throws ServiceLayerException
     */
    public List<IpRecordVO> findModuleBlockedIpList(IpRecordVO irVO, Integer startRow, Integer pageLength) throws ServiceLayerException;
}
