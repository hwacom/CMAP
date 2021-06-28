package com.cmap.plugin.module.tickets;

import java.util.List;

import com.cmap.dao.BaseDAO;

public interface TicketListDAO extends BaseDAO {

    /**
     * 取得符合條件資料筆數
     * @param ibrVO
     * @return
     */
    public long countModuleTicketList(TicketListVO ibrVO);

    /**
     * 取得符合條件資料(頁面使用)
     * @param ibrVO
     * @param startRow
     * @param pageLength
     * @return Object[0]:ModuleTicketList
     *         Object[1]:DeviceList
     */
    public List<Object[]> findModuleTicketList(TicketListVO ibrVO, Integer startRow, Integer pageLength);

    /**
     * 取得符合條件資料
     * @param ibrVO
     * @return
     */
    public List<ModuleTicketList> findModuleTicketList(TicketListVO ibrVO);

    ModuleTicketList findModuleTicketListByVO(TicketListVO tlVO);

	ModuleTicketList findModuleTicketListByPK(Long listId);

	List<ModuleTicketDetail> findModuleTicketDetailByListId(Long listId);
	
	void saveOrUpdateTicketList(Object entity);

	void deleteTicketList(List<Object> entities);

}
