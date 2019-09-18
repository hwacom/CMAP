package com.cmap.utils;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.cmap.service.vo.PrtgUserDeviceMainVO;
import com.cmap.service.vo.PrtgUserGroupMainVO;

public interface ApiUtils extends Api {

    /**
     * 初始化區域變數值
     */
    public void init();

	public boolean login(HttpServletRequest request, String username, String password) throws Exception;

	/**
	 * 取得 PRTG passhash for 排程使用
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String getPasshash(String username, String password) throws Exception;

	/**
	 * 取得 PRTG 群組 & 設備清單 by request (適用於有經過登入步驟的)
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map[] getGroupAndDeviceMenu(HttpServletRequest request) throws Exception;

	/**
	 * 取得 PRTG 群組 & 設備清單 by 傳入PRTG登入帳密方式 (適用於排程更新用)
	 * @param prtgLoginAccount
	 * @param prtgLoginPassword
	 * @param prtgPashhash
	 * @return
	 * @throws Exception
	 */
	public Map[] getGroupAndDeviceMenu(String prtgLoginAccount, String prtgLoginPassword, String prtgPashhash) throws Exception;

	/**
	 * 取得 PRTG 中該 USER 所擁有的權限下的群組清單
	 * @param prtgLoginAccount
	 * @param prtgLoginPassword
	 * @param prtgPashhash
	 * @return
	 * @throws Exception
	 */
	public PrtgUserGroupMainVO getUserGroupList(String prtgLoginAccount, String prtgLoginPassword, String prtgPashhash) throws Exception;

	/**
	 * 取得 PRTG 中該 USER 所擁有的權限下的設備清單
	 * @param prtgLoginAccount
	 * @param prtgLoginPassword
	 * @param prtgPashhash
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public PrtgUserDeviceMainVO getUserDeviceList(String prtgLoginAccount, String prtgLoginPassword, String prtgPashhash, String groupId) throws Exception;
}
