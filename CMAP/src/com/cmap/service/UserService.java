package com.cmap.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cmap.exception.ServiceLayerException;
import com.cmap.model.UserRightSetting;
import com.cmap.service.vo.UserRightServiceVO;

public interface UserService {

	/**
	 * 檢核登入者帳號是否可使用系統 & 是否為管理者並加設管理者腳色
	 * @param request
	 * @param account
	 * @param loginMode
	 * @return
	 * @throws ServiceLayerException
	 */
	boolean checkUserCanAccess(HttpServletRequest request, String account, String loginMode, String[] roles);

	/**
	 * 檢核登入者帳號是否可使用系統 & 是否為管理者並加設管理者腳色
	 * @param account
	 * @param loginMode
	 * @return
	 * @throws ServiceLayerException
	 */
	UserRightSetting getUserRightSetting(String account, String loginMode);

	long countUserRightSettingsByVO(UserRightServiceVO vo) throws ServiceLayerException;

	List<UserRightServiceVO> findUserRightSettingsByVO(UserRightServiceVO vo, Integer startRow, Integer pageLength)
			throws ServiceLayerException;

	String deleteSettings(List<String> ids) throws ServiceLayerException;

	String addOrModifyUserRightSettings(List<UserRightServiceVO> urVOs) throws ServiceLayerException;

}
