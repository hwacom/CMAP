package com.cmap.plugin.module.tickets;

import java.util.List;

import com.cmap.exception.ServiceLayerException;

public interface TicketListService {

    /**
     * 取得符合條件資料筆數
     * @param tlVO
     * @return
     * @throws ServiceLayerException
     */
    public long countModuleTicketList(TicketListVO tlVO) throws ServiceLayerException;

    /**
     * 查詢符合條件資料(頁面使用)
     * @param tlVO
     * @return
     * @throws ServiceLayerException
     */
    public List<TicketListVO> findModuleTicketList(TicketListVO tlVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param tlVO
     * @return
     * @throws ServiceLayerException
     */
    public List<ModuleTicketList> findModuleTicketList(TicketListVO tlVO) throws ServiceLayerException;

	ModuleTicketList findTicketList(Long listId) throws ServiceLayerException;

	ModuleTicketList findModuleTicketListByVO(TicketListVO tlVO) throws ServiceLayerException;
	
	List<ModuleTicketDetail> findModuleTicketDetail(Long listId) throws ServiceLayerException;
	
	void forwardTicket(ModuleTicketDetail mtd, String ownerType, String owner) throws ServiceLayerException;
	
    /**
     * 新增 OR 更新封鎖紀錄
     * @param tlVOs
     * @throws ServiceLayerException
     */
	void saveOrUpdateTicketList(TicketListVO tlVO) throws ServiceLayerException;

	void saveOrUpdateTicketDetail(ModuleTicketDetail mtd) throws ServiceLayerException;

}
