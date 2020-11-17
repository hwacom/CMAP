package com.cmap.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cmap.exception.ServiceLayerException;
import com.cmap.service.vo.CommonServiceVO;
import com.cmap.service.vo.PrtgServiceVO;

public interface CommonService {

    public String convertByteSizeUnit(BigDecimal sizeByte, Integer targetUnit);

    /**
     * 取得 PRTG Group & Device 清單 & 更新至資料庫
     * @throws ServiceLayerException
     */
    public CommonServiceVO refreshGroupAndDeviceMenu() throws ServiceLayerException;

    /**
     * 更新 PRTG 使用者的 Group & Device 權限列表
     * @param prtgAccount
     * @return
     * @throws ServiceLayerException
     */
    public CommonServiceVO refreshPrtgUserRightSetting(String prtgAccount) throws ServiceLayerException;


    /**
     * 取得 GROUP 及 DEVICE 選單
     * @param request
     * @return
     */
	public Map<String, String> getGroupAndDeviceMenu(HttpServletRequest request);

	/**
     * 取得 PRTG 使用者有權限的群組&設備清單(含設備細部資訊)
     * @param prtgAccount
     * @return
     */
    public Map<String, Map<String, Map<String, String>>> getUserGroupAndDeviceFullInfo(String prtgAccount);

	/**
	 * 取得 PRTG 使用者的權限群組清單
	 * @param prtgLoginAccount
	 * @return
	 */
	public Map<String, String> getUserGroupList(String prtgAccount);

	/**
	 * 取得 PRTG 使用者的權限設備清單
	 * @param prtgLoginAccount
	 * @param groupId
	 * @return
	 */
	public Map<String, String> getUserDeviceList(String prtgAccount, String groupId);

	/**
	 *  取得 PRTG 使用者的權限sensor清單
	 * @param prtgAccount
	 * @param deviceId
	 * @return
	 */
	public Map<String, String> getUserSensorList(String prtgAccount, String deviceId);
	
	/**
	 * 取得選單資料 (Menu_Item)
	 * @param menuCode
	 * @param combineOrderDotLabel 設定選單序號跟選單呈顯值中間是否用點串聯
	 * @return
	 */
	public Map<String, String> getMenuItem(String menuCode, boolean combineOrderDotLabel);

	/**
	 * 取得腳本分類選單
	 * @param defaultFlag
	 * @return
	 */
	public Map<String, String> getScriptTypeMenu(String defaultFlag);

	/**
	 * 取得 PRTG 登入帳密資訊 (Prtg_Account_Mapping)
	 * @param prtgUsername
	 * @return
	 */
	public PrtgServiceVO findPrtgLoginInfo(String prtgUsername);

	/**
	 * 取得 IP protocol 規格表
	 * @return
	 */
	public Map<Integer, CommonServiceVO> getProtoclSpecMap();

	/**
	 * 寄送MAIL
	 * @param toAddress
	 * @param ccAddress
	 * @param bccAddress
	 * @param subject
	 * @param mailContent
	 * @param filePathList
	 * @throws Exception
	 */
	public void sendMail(String[] toAddress, String[] ccAddress, String[] bccAddress,
	        String subject, String mailContent, ArrayList<String> filePathList) throws Exception;

	/**
	 * 取得當前登入者名稱
	 * @return
	 */
	public String getUserName();

	/**
	 * 取得學校群組(GROUP)的IP網段設定
	 * @param groupId
	 * @param ipVersion
	 * @return
	 */
	public String getGroupSubnetSetting(String groupId, String ipVersion);

	/**
	 * 判斷IP是否在網段內
	 * @param gSubnet
	 * @param ipAddress
	 * @return
	 */
	public boolean chkIpInGroupSubnet(String cidr, String ip, String ipVersion);

	/**
	 * 查詢prtgAccountMapping列表
	 * @param prtgAccount
	 * @return
	 */
	public Map<String, String> findPrtgAccountMappingList(String prtgAccount);

	public Map<String, String> getDeviceModelMap(String prtgAccount);
}
