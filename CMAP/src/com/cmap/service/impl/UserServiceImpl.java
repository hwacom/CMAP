package com.cmap.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
	public boolean checkUserCanAccess(HttpServletRequest request, String account, String loginMode, String[] roles) {
		boolean canAccess = false;
		try {
		    UserRightSetting userRight = userDAO.findUserRightSetting(account, loginMode);

		    log.debug("for debug userRight = " + (userRight!=null) + ", account = " + account +", loginMode = " + loginMode);
		    if(userRight != null) {
		    	boolean isAdmin = StringUtils.equals(userRight.getIsAdmin(), Constants.DATA_Y)?true:false;
		    	if(roles != null && !isAdmin) {
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
			    	
		    	}
		    	
		    	request.getSession().setAttribute(Constants.ISADMIN, isAdmin);
				request.getSession().setAttribute(Constants.USERROLE, isAdmin?Constants.USERROLE_ADMIN:Constants.USERROLE_USER);
		    }else {
		    	request.getSession().setAttribute(Constants.ISADMIN, false);
		    	request.getSession().setAttribute(Constants.USERROLE, Constants.USERROLE_USER);
		    }
		    
			return userRight != null;

		} catch (Exception e) {
			log.error(e.toString(), e);
			canAccess = false;
		}
		return canAccess;
	}

	@Override
	public UserRightSetting getUserRightSetting(String account, String loginMode) {
		return userDAO.findUserRightSetting(account, loginMode);
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

		UserRightSetting entity;
		for (UserRightServiceVO urVO : urVOs) {
			//2021-01-18 Alvin modified 改pkey id找資料,找不到才新增Entity to add
			if(urVO.getId()!=null)
				entity = userDAO.findUserRightSetting(urVO.getId()); //update case
			else
				entity = null; //create case
			 //2021-01-15 Alvin modified 改抓取目前登入者的帳號+名稱
			final String account = SecurityUtil.getSecurityUser().getUser().getUserName();
			final String username = SecurityUtil.getSecurityUser().getUsername()+"("+account+")";
			final Timestamp nowTimestamp = new Timestamp((new Date()).getTime());

			String pwEncode = StringUtils.upperCase(EncryptUtils.getSha256(urVO.getPassword()));
			String pwAppendString = "";
			
			if (entity == null) {
				entity = new UserRightSetting();
				entity.setCreateBy(username);
				entity.setCreateTime(nowTimestamp);
				entity.setAccount(urVO.getAccount());
			} else {
				if(StringUtils.equalsIgnoreCase(Constants.DATA_Y, Env.PASSWORD_VALID_SETTING_FLAG)) {
					/**
					 * 增加密碼檢核
					 * N代不重覆
					 */
					String[] oriPW = entity.getPWRecord().split(Env.COMM_SEPARATE_SYMBOL);
					int checkTime = Integer.parseInt(Objects.toString(Env.PASSWORD_VALID_SETTING_NOT_REPEAT_TIMES, "0"));
					
					for(int i = 0; i < checkTime && i < oriPW.length; i++) {
						if(StringUtils.equals(oriPW[i], pwEncode)) {
							throw new ServiceLayerException("輸入的密碼已使用過，請勿重複使用密碼!!");
						}
						pwAppendString = pwAppendString.concat(Env.COMM_SEPARATE_SYMBOL).concat(oriPW[i]);
					}
				}
			}
		
			if(StringUtils.isNotBlank(urVO.getUserName())) {
				entity.setUserName(urVO.getUserName());
			}
			if((StringUtils.equals(urVO.getLoginMode(), Constants.LOGIN_AUTH_MODE_CM) || StringUtils.isBlank(urVO.getLoginMode()))
					&& StringUtils.isNotBlank(urVO.getPassword())
					&& !StringUtils.equals(urVO.getPassword(), pwEncode)) {
				entity.setPassword(pwEncode);
				pwAppendString = pwEncode.concat(pwAppendString);
				entity.setPWRecord(pwAppendString);
				entity.setLastPWUpdateTime(nowTimestamp);
			}
			if(StringUtils.isNotBlank(urVO.getUserGroup())) {
				entity.setUserGroup(urVO.getUserGroup());
			}
			if(StringUtils.isNotBlank(urVO.getLoginMode())) {
				entity.setLoginMode(urVO.getLoginMode());
			}
			if(StringUtils.isNotBlank(urVO.getRemark())) {
				entity.setRemark(urVO.getRemark());
			}
			if(StringUtils.isNotBlank(urVO.getIsAdmin())) {
				entity.setIsAdmin(StringUtils.equals(urVO.getIsAdmin().toUpperCase(), Constants.DATA_Y)?Constants.DATA_Y:Constants.DATA_N);
			}
			
			entity.setUpdateBy(username);
			entity.setUpdateTime(nowTimestamp);

			userDAO.saveUserRightSetting(entity);
			successCount++;

		}

		String msg = "異動資料 {0} 筆；成功 {1} 筆、失敗 {2} 筆";
		Object[] args = new Object[] {
				totalCount, successCount, (totalCount-successCount)
		};

		return CommonUtils.converMsg(msg, args);
	}

	@Override
	public boolean saveOrUpdateEntity(Object entity) {

		try {
			userDAO.saveOrUpdateEntity(entity);
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean checkPWRetryTimes(String userAccount) throws ServiceLayerException {
		long retVal = 0;

		try {
			if(!StringUtils.equalsIgnoreCase(Constants.DATA_Y, Env.PASSWORD_VALID_SETTING_FLAG)
					|| StringUtils.isBlank(Env.PASSWORD_VALID_SETTING_RETRY_TIMES)
					|| StringUtils.isBlank(Env.PASSWORD_VALID_SETTING_LOCK_TIME)) {
				return true;
			}
			int re = Integer.parseInt(Env.PASSWORD_VALID_SETTING_LOCK_TIME);
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date current = DateUtils.addMinutes(new Date(), -re);
			retVal = userDAO.countUserLoginFailTimes(userAccount, sdFormat.format(current));

			if(retVal >= Integer.parseInt(Env.PASSWORD_VALID_SETTING_RETRY_TIMES)) {
				return false;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException(e);
		}
		return true;
	}
}
