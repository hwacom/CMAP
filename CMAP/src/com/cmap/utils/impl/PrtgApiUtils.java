package com.cmap.utils.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.User;
import com.cmap.security.SecurityUtil;
import com.cmap.service.vo.PrtgUserDeviceMainVO;
import com.cmap.service.vo.PrtgUserGroupMainVO;
import com.cmap.service.vo.PrtgUserSensorMainVO;
import com.cmap.utils.ApiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrtgApiUtils implements ApiUtils {
	private static Logger log = LoggerFactory.getLogger(PrtgApiUtils.class);

	private String PRTG_ROOT = null;
	private String API_LOGIN = null;
	private String API_SENSOR_TREE = null;
	private String API_USER_GROUP_LIST = null;
	private String API_USER_DEVICE_LIST = null;
	private String API_USER_SENSOR_LIST = null;

	public PrtgApiUtils() {
		PRTG_ROOT = Env.PRTG_SERVER_IP;
		API_LOGIN = Env.PRTG_API_LOGIN;
		API_SENSOR_TREE = Env.PRTG_API_SENSOR_TREE;
		API_USER_GROUP_LIST = Env.PRTG_API_USER_GROUP_LIST;
		API_USER_DEVICE_LIST = Env.PRTG_API_USER_DEVICE_LIST;
		API_USER_SENSOR_LIST = Env.PRTG_API_USER_SENSOR_LIST;
	}

	@Override
    public void init() {
	    PRTG_ROOT = Env.PRTG_SERVER_IP;
        API_LOGIN = Env.PRTG_API_LOGIN;
        API_SENSOR_TREE = Env.PRTG_API_SENSOR_TREE;
        API_USER_GROUP_LIST = Env.PRTG_API_USER_GROUP_LIST;
        API_USER_DEVICE_LIST = Env.PRTG_API_USER_DEVICE_LIST;
        API_USER_SENSOR_LIST = Env.PRTG_API_USER_SENSOR_LIST;
	}

	/**
	 * 解析 PRTG API 回傳 XML for HA架構模式
	 * @param prtgDoc
	 * @return
	 * @throws ServiceLayerException
	 */
	private Object[] parsePrtgDocument4HA(com.cmap.prtg.ha.PrtgDocument prtgDoc) throws ServiceLayerException {
		Object[] retObj = new Object[4];
		List<com.cmap.prtg.ha.PrtgDocument.Prtg.Sensortree.Nodes.Group.Probenode.Group2> groups = new ArrayList<>();

		try {
			//HA架構
			com.cmap.prtg.ha.PrtgDocument.Prtg.Sensortree.Nodes.Group.Probenode[] pbNodes = prtgDoc.getPrtg().getSensortree().getNodes().getGroup().getProbenodeArray();

			for (com.cmap.prtg.ha.PrtgDocument.Prtg.Sensortree.Nodes.Group.Probenode pbNode : pbNodes) {
				String pbNodeID = Objects.toString(pbNode.getId(), null);

				if (pbNodeID == null || (pbNodeID != null && Env.PRTG_EXCLUDE_PROBENODE_ID.contains(pbNodeID))) {
					continue;
				}

				for (com.cmap.prtg.ha.PrtgDocument.Prtg.Sensortree.Nodes.Group.Probenode.Group2 group2 : pbNode.getGroupArray()) {
					groups.add(group2);
				}
			}

		} catch (Exception e) {
			throw new ServiceLayerException("取得PRTG API >> groupArray異常 :: "+e.toString());
		}

		Map<String, Map<String, Map<String, String>>> groupDeviceMap = new HashMap<>();
		Map<String, Map<String, String>> deviceMap = null;
		Map<String, String> deviceInfoMap = null;
		Map<String, String> groupInfoMap = new TreeMap<>();

		List<String> groupLabelList = new ArrayList<>();
		List<String> groupValueList = new ArrayList<>();
		for (com.cmap.prtg.ha.PrtgDocument.Prtg.Sensortree.Nodes.Group.Probenode.Group2 group : groups) {
			if (group == null) {
				continue;
			}
			if (!Env.PRTG_EXCLUDE_GROUP_NAME.contains(group.getName())) {

				String groupId = String.valueOf(group.getId());	//群組ID
				String groupName = group.getName();	//群組名稱

				groupInfoMap.put(groupId, groupName);

				groupLabelList.add(groupName);
				groupValueList.add(groupId);

				com.cmap.prtg.ha.PrtgDocument.Prtg.Sensortree.Nodes.Group.Probenode.Group2.Device[] devices = group.getDeviceArray();

				for (com.cmap.prtg.ha.PrtgDocument.Prtg.Sensortree.Nodes.Group.Probenode.Group2.Device device : devices) {
					deviceInfoMap = composeDeviceInfoMap4HA(group, device);	//組成裝置資訊MAP

					if (groupDeviceMap.containsKey(groupId)) {
						groupDeviceMap.get(groupId).put(String.valueOf(device.getId()), deviceInfoMap);

					} else {
						deviceMap = new HashMap<>();
						deviceMap.put(String.valueOf(device.getId()), deviceInfoMap);
						groupDeviceMap.put(groupId, deviceMap);
					}
				}
			}
		}

		retObj[0] = groupDeviceMap;
		retObj[1] = deviceMap;
		retObj[2] = deviceInfoMap;
		retObj[3] = groupInfoMap;

		return retObj;
	}

	@Override
    public Map[] getGroupAndDeviceMenu(String prtgLoginAccount, String prtgLoginPassword,
            String prtgPashhash) throws Exception {
	    Map[] retObj = null;
        /*
         * groupDeviceMap >> MAP<Group_ID, Map<Device_ID, Map<Device詳細內容key, 詳細內容value>>>
         * deviceMap >> Map<Device_ID, Map<Device詳細內容key, 詳細內容value>>
         * deviceInfoMap >> Map<Device詳細內容key, 詳細內容value>
         * groupInfoMap >> Map<Group_ID, Group_Name>
         */
        Map<String, Map<String, Map<String, String>>> groupDeviceMap = new HashMap<>();
        Map<String, Map<String, String>> deviceMap = null;
        Map<String, String> deviceInfoMap = null;
        Map<String, String> groupInfoMap = null;
        try {
            prtgPashhash = checkPasshash(prtgLoginAccount, prtgLoginPassword, prtgPashhash);

            prtgLoginAccount = StringUtils.trim(prtgLoginAccount);
            prtgPashhash = StringUtils.trim(prtgPashhash);

            API_SENSOR_TREE = StringUtils.replace(API_SENSOR_TREE, "{username}", prtgLoginAccount);
            API_SENSOR_TREE = StringUtils.replace(API_SENSOR_TREE, "{passhash}", prtgPashhash);

            String apiUrl = PRTG_ROOT.concat(API_SENSOR_TREE);

            String retVal = callPrtg(apiUrl);
            if (StringUtils.isNotBlank(retVal)) {

                Object[] obj = null;
                if (Env.PRTG_HA) {
                    com.cmap.prtg.ha.PrtgDocument prtgDoc = com.cmap.prtg.ha.PrtgDocument.Factory.parse(retVal);
                    obj = parsePrtgDocument4HA(prtgDoc);


                } else {
                    /*
                    com.cmap.prtg.PrtgDocument prtgDoc = com.cmap.prtg.PrtgDocument.Factory.parse(retVal);
                    obj = parsePrtgDocument(prtgDoc);
                    */
                }

                if (obj != null) {
                    /*
                        retObj[0] = groupDeviceMap;
                        retObj[1] = deviceMap;
                        retObj[2] = deviceInfoMap;
                        retObj[3] = groupInfoMap;
                     */
                    groupInfoMap = (Map<String, String>)obj[3];
                    groupDeviceMap = (Map<String, Map<String, Map<String, String>>>)obj[0];
                }
            }

        } catch (Exception e) {
            throw e;

        } finally {
            retObj = new Map[] {
                    groupInfoMap
                    ,groupDeviceMap
            };
        }

        return retObj;
    }

	@Override
    public PrtgUserGroupMainVO getUserGroupList(String prtgLoginAccount, String prtgLoginPassword,
            String prtgPashhash) throws Exception {
	    PrtgUserGroupMainVO retVO = null;
        try {
            prtgPashhash = checkPasshash(prtgLoginAccount, prtgLoginPassword, prtgPashhash);

            prtgLoginAccount = StringUtils.trim(prtgLoginAccount);
            prtgPashhash = StringUtils.trim(prtgPashhash);

            API_USER_GROUP_LIST = StringUtils.replace(API_USER_GROUP_LIST, "{username}", prtgLoginAccount);
            API_USER_GROUP_LIST = StringUtils.replace(API_USER_GROUP_LIST, "{passhash}", prtgPashhash);

            String apiUrl = PRTG_ROOT.concat(API_USER_GROUP_LIST);

            String retVal = callPrtg(apiUrl);
            if (StringUtils.isNotBlank(retVal)) {
                ObjectMapper oMapper = new ObjectMapper();
                try {
                    retVO = oMapper.readValue(retVal, PrtgUserGroupMainVO.class);

                } catch (Exception e) {
                    log.error(e.toString(), e);
                    return null;
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retVO;
    }

    @Override
    public PrtgUserDeviceMainVO getUserDeviceList(String prtgLoginAccount, String prtgLoginPassword,
            String prtgPashhash, String groupId) throws Exception {
        PrtgUserDeviceMainVO retVO = null;
        try {
            prtgPashhash = checkPasshash(prtgLoginAccount, prtgLoginPassword, prtgPashhash);

            prtgLoginAccount = StringUtils.trim(prtgLoginAccount);
            prtgPashhash = StringUtils.trim(prtgPashhash);

            API_USER_DEVICE_LIST = StringUtils.replace(API_USER_DEVICE_LIST, "{username}", prtgLoginAccount);
            API_USER_DEVICE_LIST = StringUtils.replace(API_USER_DEVICE_LIST, "{passhash}", prtgPashhash);

            if (StringUtils.isNotBlank(groupId)) {
                API_USER_DEVICE_LIST = StringUtils.replace(API_USER_DEVICE_LIST, "{groupId}", groupId);
            } else {
                API_USER_DEVICE_LIST = StringUtils.substring(API_USER_DEVICE_LIST, 0, StringUtils.lastIndexOf(API_USER_DEVICE_LIST, "&"));
            }

            String apiUrl = PRTG_ROOT.concat(API_USER_DEVICE_LIST);

            String retVal = callPrtg(apiUrl);

            if (StringUtils.isNotBlank(retVal)) {
                ObjectMapper oMapper = new ObjectMapper();
                try {
                    retVO = oMapper.readValue(retVal, PrtgUserDeviceMainVO.class);

                } catch (Exception e) {
                    log.error(e.toString(), e);
                    return null;
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retVO;
    }

	@Override
	public PrtgUserSensorMainVO getUserSensorList(String prtgLoginAccount, String prtgLoginPassword,
			String prtgPashhash) throws Exception {
		PrtgUserSensorMainVO retVO = null;
		try {
			prtgPashhash = checkPasshash(prtgLoginAccount, prtgLoginPassword, prtgPashhash);

			prtgLoginAccount = StringUtils.trim(prtgLoginAccount);
			prtgPashhash = StringUtils.trim(prtgPashhash);

			API_USER_SENSOR_LIST = StringUtils.replace(API_USER_SENSOR_LIST, "{username}", prtgLoginAccount);
			API_USER_SENSOR_LIST = StringUtils.replace(API_USER_SENSOR_LIST, "{passhash}", prtgPashhash);

			String apiUrl = PRTG_ROOT.concat(API_USER_SENSOR_LIST);

			String retVal = callPrtg(apiUrl);
			if (StringUtils.isNotBlank(retVal)) {
				ObjectMapper oMapper = new ObjectMapper();
				try {
					retVO = oMapper.readValue(retVal, PrtgUserSensorMainVO.class);

				} catch (Exception e) {
					log.error(e.toString(), e);
					return null;
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return retVO;
	}

	@Override
	public Map[] getGroupAndDeviceMenu(HttpServletRequest request) throws Exception {
		Map[] retObj = null;
		try {
			User user = SecurityUtil.getSecurityUser().getUser();
			String prtgLoginAccount = SecurityUtil.getSecurityUser().getUser().getPrtgLoginAccount();
			String prtgPasshash = SecurityUtil.getSecurityUser().getUser().getPasshash();

			retObj = getGroupAndDeviceMenu(prtgLoginAccount, null, prtgPasshash);

		} catch (Exception e) {
			throw e;
		}

		return retObj;
	}

	private Map<String, String> composeDeviceInfoMap4HA(
			com.cmap.prtg.ha.PrtgDocument.Prtg.Sensortree.Nodes.Group.Probenode.Group2 group,
			com.cmap.prtg.ha.PrtgDocument.Prtg.Sensortree.Nodes.Group.Probenode.Group2.Device device) {
		Map<String, String> deviceInfoMap = null;
		try {
			deviceInfoMap = new HashMap<>();
			deviceInfoMap.put(Constants.GROUP_ID, String.valueOf(group.getId()));
			deviceInfoMap.put(Constants.GROUP_NAME, getName(group.getName(), Env.PRTG_WRAPPED_SYMBOL_FOR_GROUP_NAME));
			deviceInfoMap.put(Constants.GROUP_ENG_NAME, getName(group.getName(), Env.PRTG_WRAPPED_SYMBOL_FOR_GROUP_ENG_NAME));
			deviceInfoMap.put(Constants.DEVICE_ID, String.valueOf(device.getId()));
			deviceInfoMap.put(Constants.DEVICE_NAME, getName(device.getName(), Env.PRTG_WRAPPED_SYMBOL_FOR_DEVICE_NAME));
			deviceInfoMap.put(Constants.DEVICE_ENG_NAME, getName(device.getName(), Env.PRTG_WRAPPED_SYMBOL_FOR_DEVICE_ENG_NAME));
			deviceInfoMap.put(Constants.DEVICE_IP, device.getHost());

			String tags = device.getTags();

			if (StringUtils.isNotBlank(tags)) {
			    /*
	             * tags格式 = [型號] [層級(L3/L2)] (中間用空格隔開)
	             */
	            String[] tag = StringUtils.split(tags);
	            if (tag != null) {
	                deviceInfoMap.put(Constants.DEVICE_MODEL, tag[0]);
	                deviceInfoMap.put(Constants.DEVICE_LAYER, tag.length > 1 ? tag[1] : null);
	            } else {
	                deviceInfoMap.put(Constants.DEVICE_MODEL, null);
	                deviceInfoMap.put(Constants.DEVICE_LAYER, null);
	            }
			} else {
			    //如果PRTG內沒設定tag情況則預設塞入null
			    deviceInfoMap.put(Constants.DEVICE_MODEL, null);
                deviceInfoMap.put(Constants.DEVICE_LAYER, null);
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return deviceInfoMap;
	}

	@Override
	public boolean login(HttpServletRequest request, String username, String password) throws Exception {
		try {
			request.getSession().setAttribute(Constants.ERROR, null);

			if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
				throw new Exception("[Login failed] >> username or password is blank.");

			} else {
			    username = StringUtils.trim(username);
			    password = StringUtils.trim(password);

				API_LOGIN = StringUtils.replace(API_LOGIN, "{username}",URLEncoder.encode(username, "UTF-8") );
                API_LOGIN = StringUtils.replace(API_LOGIN, "{password}",URLEncoder.encode(password, "UTF-8") );
                
                log.debug("for debug username :" + username +" , password:" + password+",===="+API_LOGIN);
				String apiUrl = PRTG_ROOT.concat(API_LOGIN);

				String retVal = null;
				try {
					retVal = callPrtg(apiUrl);
					
				} catch (ConnectTimeoutException cte) {
					request.getSession().setAttribute(Constants.ERROR, new ConnectTimeoutException("ERROR.connectionTimeOut"));
				}

				if (StringUtils.isNotBlank(retVal)) {
					//PRTG驗證成功後將密碼hash_code存入Spring security USER物件內，供後續作業使用
					//					SecurityUtil.getSecurityUser().getUser().setPasshash(retVal);
					request.getSession().setAttribute(Constants.PRTG_LOGIN_ACCOUNT, username);
					request.getSession().setAttribute(Constants.PRTG_LOGIN_PASSWORD, password);
					request.getSession().setAttribute(Constants.PASSHASH, retVal);

					//判斷OIDC_SCHOOL_ID有無值，無值則塞login username(非走OPENID)
//					String sourceId = Objects.toString(request.getSession().getAttribute(Constants.OIDC_SCHOOL_ID), null);
//
//					if (StringUtils.isBlank(sourceId)) {
//					    request.getSession().setAttribute(Constants.OIDC_SCHOOL_ID, username);
//					}

					return true;
				}
			}

		} catch (Exception e) {
			throw e;
		}

		return false;
	}

	/**
	 * 取得PRTG pashhash
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
    public String getPasshash(String username, String password) throws ServiceLayerException {
	    String retVal = null;
	    try {
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                throw new Exception("[Login failed] >> username or password is blank.");

            } else {
                username = StringUtils.trim(username);
                password = StringUtils.trim(password);

                API_LOGIN = StringUtils.replace(API_LOGIN, "{username}",URLEncoder.encode(username, "UTF-8") );
                API_LOGIN = StringUtils.replace(API_LOGIN, "{password}",URLEncoder.encode(password, "UTF-8") );

                String apiUrl = PRTG_ROOT.concat(API_LOGIN);

                try {
                    retVal = callPrtg(apiUrl);

                } catch (ConnectTimeoutException cte) {
                    throw new ServiceLayerException("PRTG連線超時");
                }

                if (StringUtils.isBlank(retVal)) {
                    throw new ServiceLayerException("PRTG認證失敗");
                }
            }

        } catch (ServiceLayerException sle) {
            throw sle;

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("PRTG認證異常");
        }
        return retVal;
	}

	private String checkPasshash(String prtgLoginAccount, String prtgLoginPassword, String prtgPasshash) throws ServiceLayerException {
        if (StringUtils.isBlank(prtgPasshash)) {
            prtgPasshash = getPasshash(prtgLoginAccount, prtgLoginPassword);
        }
        return prtgPasshash;
    }

	private String getName(String oriName, final String WRAPPED_SYMBOL) throws Exception {
		String retVal = "";

		try {
			/*
			 * 原格式: 192.168.1.3 (1樓大廳) {1F_Lobby} [Cisco Device Cisco IOS]
			 * >>>>> ip_address (裝置中文名稱) {裝置英文名稱} [裝置作業系統]
			 * >>>>> 分析取得(裝置中文名稱)
			 */
			if (StringUtils.isNotBlank(oriName)) {
				if (StringUtils.isNotBlank(WRAPPED_SYMBOL)) {
					if (WRAPPED_SYMBOL.length() == 2) {
						final String head = WRAPPED_SYMBOL.substring(0, 1);
						final String tail = WRAPPED_SYMBOL.substring(1, 2);

						if (oriName.indexOf(head) != -1) {
							oriName = oriName.substring(oriName.indexOf(head)+1, oriName.length());
						}
						if (oriName.indexOf(tail) != -1) {
							oriName = oriName.substring(0, oriName.indexOf(tail));
						}

					} else if (WRAPPED_SYMBOL.length() == 1) {
						if (oriName.indexOf(WRAPPED_SYMBOL) != -1) {
							oriName = oriName.substring(0, oriName.indexOf(WRAPPED_SYMBOL)).trim();
						}
					}
				}
			}

			retVal = oriName;

		} catch (Exception e) {
			throw e;
		}

		return retVal;
	}

	private String callPrtg(String apiUrl) throws Exception {
		String resultStr = "";

		try {
			CloseableHttpClient httpclient = CloseableHttpClientUtils.prepare();
			
			HttpGet httpGet = new HttpGet(apiUrl);
			
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(Env.HTTP_CONNECTION_TIME_OUT)				//設置連接逾時時間，單位毫秒。
					.setConnectionRequestTimeout(Env.HTTP_CONNECTION_TIME_OUT)	//設置從connect Manager獲取Connection 超時時間，單位毫秒。這個屬性是新加的屬性，因為目前版本是可以共用連接池的。
					.setSocketTimeout(Env.HTTP_SOCKET_TIME_OUT)					//請求獲取資料的超時時間，單位毫秒。 如果訪問一個介面，多少時間內無法返回資料，就直接放棄此次調用。
					.build();
			httpGet.setConfig(requestConfig);

			log.info("Executing request " + httpGet.getRequestLine());

			// Create a custom response handler
			ResponseHandler<String> responseHandler = response -> {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode >= 200 && statusCode < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + statusCode);
				}
			};
			resultStr = httpclient.execute(httpGet, responseHandler);

		} catch (ConnectTimeoutException cte) {
			log.error(cte.toString(), cte);
			throw cte;

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return resultStr;
	}

}
