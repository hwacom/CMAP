package com.cmap.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.UserDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.model.UserRightSetting;
import com.cmap.security.SecurityUtil;
import com.cmap.service.PrtgService;
import com.cmap.service.UserService;
import com.cmap.service.vo.UserRightServiceVO;
import com.cmap.utils.impl.CommonUtils;
import com.cmap.utils.impl.EncryptUtils;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
	@Log
	private static Logger log;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private PrtgService prtgService;
	
	@Override
	public boolean checkUserCanAccess(HttpServletRequest request, String account, String[] roles) {
		boolean canAccess = false;
		try {
		    UserRightSetting userRught = userDAO.findUserRightSetting(account);

		    if(roles != null) {
		    	boolean isAdmin = false;
		    	
		    	for(String title :Env.OIDC_ADMIN_TITLE) {
		    		for(String role:roles) {
		    			if(StringUtils.equals(role, title)) {
		    				isAdmin = true;
		    				break;
		    			}
		    		}
		    		if(isAdmin) {
		    			break;
		    		}
		    	}
		    	
		    	request.getSession().setAttribute(Constants.ISADMIN, isAdmin);
				request.getSession().setAttribute(Constants.USERROLE, isAdmin?Constants.USERROLE_ADMIN:Constants.USERROLE_USER);
		    }else {
		    	request.getSession().setAttribute(Constants.ISADMIN, StringUtils.equals(userRught.getIsAdmin(), Constants.DATA_Y)?true:false);
		    	request.getSession().setAttribute(Constants.USERROLE, StringUtils.equals(userRught.getIsAdmin(), Constants.DATA_Y)?Constants.USERROLE_ADMIN:Constants.USERROLE_USER);
		    }
		    
			return userRught != null;

		} catch (Exception e) {
			log.error(e.toString(), e);
			canAccess = false;
		}
		return canAccess;
	}

	@Override
	public UserRightSetting getUserRightSetting(String account) {
		return userDAO.findUserRightSetting(account);
	}
	
	@Override
	public long countUserRightSettingsByVO(UserRightServiceVO vo) throws ServiceLayerException {
		long retVal = 0;

		try {
			retVal = userDAO.countUserRightSettingsByVO(vo);

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException(e);
		}
		return retVal;
	}
	
	@Override
	public List<UserRightServiceVO> findUserRightSettingsByVO(UserRightServiceVO vo, Integer startRow, Integer pageLength) throws ServiceLayerException {
		List<UserRightServiceVO> retList = new ArrayList<>();

		//查找DB設定內容
		List<UserRightSetting> entityList = userDAO.findUserRightSettingByVO(vo, startRow, pageLength);

		UserRightServiceVO retVO;
		for (UserRightSetting entity : entityList) {
			try {
				//轉換頁面呈顯用VO
				retVO = new UserRightServiceVO();
				BeanUtils.copyProperties(entity, retVO);
				retVO.setCreateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MI.format(new Date(entity.getCreateTime().getTime())));
				retVO.setUpdateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MI.format(new Date(entity.getUpdateTime().getTime())));

				PrtgAccountMapping mapping = prtgService.getMappingByAccount(entity.getUserGroup());
				retVO.setUserGroupStr(mapping.getPrtgUsername());
				
//				retVO.setPassword(Base64.encode(retVO.getPassword().getBytes()));
				
				retList.add(retVO);

			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		}

		return retList;
	}
	

	@Override
	public String deleteSettings(List<String> ids) throws ServiceLayerException {
		Integer totalCount = ids.size();
		Integer successCount = 0;

		try {
			successCount = userDAO.deleteUserRightSettingById(ids, SecurityUtil.getSecurityUser().getUsername());

		} catch (Exception e) {
			log.error(e.toString(), e);

		}

		String msg = "選取刪除資料 {0} 筆；成功 {1} 筆、失敗 {2} 筆";
		Object[] args = new Object[] {
				totalCount, successCount, (totalCount-successCount)
		};

		return CommonUtils.converMsg(msg, args);

	}

	@Override
	public String addOrModifyUserRightSettings(List<UserRightServiceVO> urVOs) throws ServiceLayerException {
		Integer totalCount = urVOs.size();
		Integer successCount = 0;

		try {
			UserRightSetting entity;
			for (UserRightServiceVO urVO : urVOs) {
				entity = userDAO.findUserRightSetting(urVO.getAccount());

				final String username = SecurityUtil.getSecurityUser().getUsername();
				final Timestamp nowTimestamp = new Timestamp((new Date()).getTime());

				if (entity == null) {
					entity = new UserRightSetting();
					entity.setCreateBy(username);
					entity.setCreateTime(nowTimestamp);
					entity.setAccount(urVO.getAccount());
				}
			
				entity.setUserName(urVO.getUserName());
				entity.setPassword(StringUtils.upperCase(EncryptUtils.getSha256(urVO.getPassword())));
				
				if(urVO.getUserGroup() != null) {
					entity.setUserGroup(urVO.getUserGroup());
				}
				if(urVO.getLoginMode() != null) {
					entity.setLoginMode(urVO.getLoginMode());
				}
				if(urVO.getIsAdmin() != null) {
					entity.setIsAdmin(StringUtils.equals(urVO.getIsAdmin().toUpperCase(), Constants.DATA_Y)?Constants.DATA_Y:Constants.DATA_N);
				}
				
				entity.setUpdateBy(username);
				entity.setUpdateTime(nowTimestamp);

				userDAO.saveUserRightSetting(entity);
				successCount++;

			}

		} catch (Exception e) {
			log.error(e.toString(), e);

		}

		String msg = "異動資料 {0} 筆；成功 {1} 筆、失敗 {2} 筆";
		Object[] args = new Object[] {
				totalCount, successCount, (totalCount-successCount)
		};

		return CommonUtils.converMsg(msg, args);
	}

}
