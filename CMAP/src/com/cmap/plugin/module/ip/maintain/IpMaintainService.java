package com.cmap.plugin.module.ip.maintain;

import java.util.List;
import com.cmap.exception.ServiceLayerException;

public interface IpMaintainService {

    /**
     * 查詢符合條件的資料筆數
     * @param imsVO
     * @return
     * @throws ServiceLayerException
     */
    public long countIpDataSetting(IpMaintainServiceVO imsVO) throws ServiceLayerException;

    /**
     * 查詢符合條件的資料
     * @param imsVO
     * @return
     * @throws ServiceLayerException
     */
    public List<IpMaintainServiceVO> findIpDataSetting(IpMaintainServiceVO imsVO) throws ServiceLayerException;

    /**
     * 新增資料
     * @param addList
     * @return
     * @throws ServiceLayerException
     */
    public IpMaintainServiceVO addIpDataSetting(List<IpMaintainServiceVO> addList) throws ServiceLayerException;

    /**
     * 修改資料
     * @param updateList
     * @return
     * @throws ServiceLayerException
     */
    public IpMaintainServiceVO updateIpDataSetting(List<IpMaintainServiceVO> updateList) throws ServiceLayerException;

    /**
     * 刪除資料
     * @param deleteList
     * @return
     * @throws ServiceLayerException
     */
    public IpMaintainServiceVO deleteIpDataSetting(List<IpMaintainServiceVO> deleteList) throws ServiceLayerException;
}
