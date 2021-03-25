package com.cmap.dao;

import java.util.List;

import com.cmap.model.UserRightSetting;
import com.cmap.service.vo.UserRightServiceVO;

public interface UserDAO extends BaseDAO {

	List<UserRightSetting> findUserRightSetting(String belongGroup, String[] roles, String account);
	
	UserRightSetting findUserRightSetting(String account, String loginMode);
	
	UserRightSetting findUserRightSetting(String id);

	long countUserRightSettingsByVO(UserRightServiceVO vo);

	List<UserRightSetting> findUserRightSettingByVO(UserRightServiceVO vo, Integer startRow, Integer pageLength);

	Integer deleteUserRightSettingById(List<String> ids, String actionBy);

	void saveUserRightSetting(UserRightSetting model);

	void saveOrUpdateEntities(List<Object> entities);

	void saveOrUpdateEntity(Object entity);

	long countUserLoginFailTimes(String userAccount, String checkTime);
}
