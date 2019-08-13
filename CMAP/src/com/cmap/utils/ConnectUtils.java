package com.cmap.utils;

import java.util.List;
import com.cmap.service.vo.ConfigInfoVO;
import com.cmap.service.vo.ScriptServiceVO;
import com.cmap.service.vo.StepServiceVO;

public interface ConnectUtils {

    enum SNMP {
        GET
       ,WALK
       ,TABLE_VIEW
    }

    /**
     * 連線設備 By IP + Port
     * @param ipAddress
     * @param port
     * @return
     * @throws Exception
     */
	public boolean connect(final String ipAddress, final Integer port) throws Exception;

	/**
	 * 連線設備 By UPD address + Community string
	 * @param udpAddress
	 * @param community
	 * @return
	 * @throws Exception
	 */
	public boolean connect(final String udpAddress, final String community) throws Exception;

	/**
	 * 登入設備
	 * @param account
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public boolean login(final String account, final String password) throws Exception;

	/**
	 * 發送指令
	 * @param scriptList
	 * @param configInfoVO
	 * @param ssVO
	 * @return
	 * @throws Exception
	 */
	public List<String> sendCommands(List<ScriptServiceVO> scriptList, ConfigInfoVO configInfoVO, StepServiceVO ssVO) throws Exception;

	/**
	 * Poll data by Mib OID
	 * @param oids
	 * @return
	 * @throws Exception
	 */
	public Object pollData(List<String> oids, SNMP pollMethod) throws Exception;

	/**
	 * 登出設備
	 * @return
	 * @throws Exception
	 */
	public boolean logout() throws Exception;

	/**
	 * 斷開與設備連線
	 * @return
	 * @throws Exception
	 */
	public boolean disconnect() throws Exception;
}
