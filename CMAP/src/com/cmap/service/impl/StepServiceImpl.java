package com.cmap.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.comm.enums.ConnectionMode;
import com.cmap.comm.enums.RestoreMethod;
import com.cmap.comm.enums.ScriptType;
import com.cmap.comm.enums.Step;
import com.cmap.dao.BaseDAO;
import com.cmap.dao.ConfigDAO;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.ScriptDefaultMappingDAO;
import com.cmap.dao.ScriptStepDAO;
import com.cmap.dao.vo.ConfigVersionInfoDAOVO;
import com.cmap.exception.FileOperationException;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.ConfigVersionDiffLog;
import com.cmap.model.ConfigVersionInfo;
import com.cmap.model.DeviceDetailInfo;
import com.cmap.model.DeviceDetailMapping;
import com.cmap.model.DeviceList;
import com.cmap.model.DeviceLoginInfo;
import com.cmap.model.ScriptInfo;
import com.cmap.plugin.module.blocked.record.BlockedRecordService;
import com.cmap.plugin.module.ip.mapping.IpMappingDAO;
import com.cmap.plugin.module.ip.mapping.ModuleArpTable;
import com.cmap.security.SecurityUtil;
import com.cmap.service.ConfigService;
import com.cmap.service.ProvisionService;
import com.cmap.service.ScriptService;
import com.cmap.service.StepService;
import com.cmap.service.VersionService;
import com.cmap.service.vo.ConfigInfoVO;
import com.cmap.service.vo.ConfigVO;
import com.cmap.service.vo.MatchVO;
import com.cmap.service.vo.ProvisionServiceVO;
import com.cmap.service.vo.ScriptServiceVO;
import com.cmap.service.vo.StepServiceVO;
import com.cmap.service.vo.VersionServiceVO;
import com.cmap.utils.ConnectUtils;
import com.cmap.utils.FileUtils;
import com.cmap.utils.impl.CommonUtils;
import com.cmap.utils.impl.FtpFileUtils;
import com.cmap.utils.impl.SshUtils;
import com.cmap.utils.impl.TFtpFileUtils;
import com.cmap.utils.impl.TelnetUtils;

@Service("stepService")
@Transactional
public class StepServiceImpl extends CommonServiceImpl implements StepService {
	@Log
	private static Logger log;

	@Autowired
	private ConfigDAO configDAO;

	@Autowired
	private DeviceDAO deviceDAO;

	@Autowired
	@Qualifier("scriptListDefaultDAOImpl")
	private ScriptDefaultMappingDAO scriptListDefaultDAO;

	@Autowired
	@Qualifier("scriptStepActionDAOImpl")
	private ScriptStepDAO scriptStepActionDAO;

	@Autowired
	@Qualifier("scriptStepCheckDAOImpl")
	private ScriptStepDAO scriptStepCheckDAO;

	@Autowired
	private VersionService versionService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private ConfigService configService;

	@Autowired
	private IpMappingDAO ipMappingDAO;
	
	@Autowired
	private ProvisionService provisionService;

	@Autowired
    private BlockedRecordService blockedRecordService;

	@Override
	public StepServiceVO doBackupStep(String deviceListId, boolean jobTrigger) {
		StepServiceVO retVO = new StepServiceVO();

		ProvisionServiceVO psMasterVO = new ProvisionServiceVO();
		ProvisionServiceVO psDetailVO = new ProvisionServiceVO();
		ProvisionServiceVO psStepVO = new ProvisionServiceVO();
		ProvisionServiceVO psRetryVO;
		ProvisionServiceVO psDeviceVO;

		// 設定 retry 次數，預設為1表示不做retry
		final int RETRY_TIMES = StringUtils.isNotBlank(Env.RETRY_TIMES) ? Integer.parseInt(Env.RETRY_TIMES) : 1;
		// 紀錄當前執行的回合數
		int round = 1;

		/*
		 * Provision_Log_Master & Step
		 */
		final String userName = jobTrigger ? Env.USER_NAME_JOB : SecurityUtil.getSecurityUser() != null ? SecurityUtil.getSecurityUser().getUsername() : Constants.SYS;
		final String userIp = jobTrigger ? Env.USER_IP_JOB : SecurityUtil.getSecurityUser() != null ? SecurityUtil.getSecurityUser().getUser().getIp() : Constants.UNKNOWN;
		
		DeviceList device = null;
		DeviceLoginInfo loginInfo = null;
		String deviceConfigBackupMode = Env.DEFAULT_DEVICE_CONFIG_BACKUP_MODE;
		try {
			device = deviceDAO.findDeviceListByDeviceListId(deviceListId);
			loginInfo = findDeviceLoginInfo(device.getDeviceListId(), device.getGroupId(), device.getDeviceId());
			if(StringUtils.isNotBlank(loginInfo.getConfigBackupMode())) {
				deviceConfigBackupMode = loginInfo.getConfigBackupMode();
			}
		}catch(NullPointerException e) {
			log.debug("執行備份作業deviceListId 查無device or loginInfo!");//沒有就算了
		}
		
		
		psDetailVO.setUserName(userName);
		psDetailVO.setUserIp(userIp);
		psDetailVO.setBeginTime(new Date());
		psDetailVO.setRemark(jobTrigger ? Env.PROVISION_REASON_OF_JOB : null);
		psStepVO.setBeginTime(new Date());

		retVO.setActionBy(userName);
		retVO.setActionFromIp(userIp);
		retVO.setBeginTime(new Date());

		ConnectUtils connectUtils = null;			// 連線裝置物件

		boolean retryRound = false;
		while (round <= RETRY_TIMES) {
			try {
				Step[] steps = null;
				ConnectionMode deviceMode = null;
				ConnectionMode fileServerMode = null;

				/*
				 * 依照設定決定以下參數的走法:
				 * (1) steps = 定義供裝的步驟 (定義在Env.java內，程式依照定義的步驟順序執行流程)
				 * (2) deviceMode = 定義預設的連線設備方式(by SSH/Telnet)；若[device_login_info]內有特別設定某個設備，則依照該table內connection_mode定義的連線方式執行
				 * (3) fileServerMode = 定義連線File_Server方式(by FTP/SFTP/TFTP)
				 *
				 * 目前設備組態檔備份方式大致分為兩種:
				 * (1) 於設備內下指令「copy running-config/startup-config tftp:」，直接由設備端將組態檔上傳到TFTP or FTP
				 * (2) 於設備內下指令「show running-config/startup-config」，再由程式將設備吐出的組態內容copy回來生成file上傳到TFTP or FTP
				 */
				switch (deviceConfigBackupMode) {
					case Constants.DEVICE_CONFIG_BACKUP_MODE_TELNET_SSH_FTP:
						steps = Env.BACKUP_BY_TELNET;
						deviceMode = ConnectionMode.SSH;
						fileServerMode = ConnectionMode.FTP;
						break;

					case Constants.DEVICE_CONFIG_BACKUP_MODE_TELNET_SSH_TFTP:
						steps = Env.BACKUP_BY_TELNET;
						deviceMode = ConnectionMode.SSH;
						fileServerMode = ConnectionMode.TFTP;
						break;

					case Constants.DEVICE_CONFIG_BACKUP_MODE_TELNET_TELNET_TFTP:
                        steps = Env.BACKUP_BY_TELNET;
                        deviceMode = ConnectionMode.TELNET;
                        fileServerMode = ConnectionMode.TFTP;
                        break;

					case Constants.DEVICE_CONFIG_BACKUP_MODE_TELNET_TELNET_FTP:
                        steps = Env.BACKUP_BY_TELNET;
                        deviceMode = ConnectionMode.TELNET;
                        fileServerMode = ConnectionMode.FTP;
                        break;

					case Constants.DEVICE_CONFIG_BACKUP_MODE_TFTP_SSH_TFTP:
						steps = Env.BACKUP_BY_TFTP;
						deviceMode = ConnectionMode.SSH;
						fileServerMode = ConnectionMode.TFTP;
						break;

					case Constants.DEVICE_CONFIG_BACKUP_MODE_TFTP_TELNET_TFTP:
						steps = Env.BACKUP_BY_TFTP;
						deviceMode = ConnectionMode.TELNET;
						fileServerMode = ConnectionMode.TFTP;
						break;

					case Constants.DEVICE_CONFIG_BACKUP_MODE_FTP_SSH_FTP:
						steps = Env.BACKUP_BY_FTP;
						deviceMode = ConnectionMode.SSH;
						fileServerMode = ConnectionMode.FTP;
						break;

					case Constants.DEVICE_CONFIG_BACKUP_MODE_FTP_TELNET_FTP:
						steps = Env.BACKUP_BY_FTP;
						deviceMode = ConnectionMode.TELNET;
						fileServerMode = ConnectionMode.FTP;
						break;
				}

				List<ScriptServiceVO> scripts = null;

				ConfigInfoVO ciVO = null;					// 裝置相關設定資訊VO
				List<String> outputList = null;				// 命令Output內容List
				List<ConfigInfoVO> outputVOList = null;		// Output VO
				FileUtils fileUtils = null;					// 連線FileServer物件

				for (Step _step : steps) {
				    // 依照定義的步驟開始執行
					switch (_step) {
						case LOAD_DEFAULT_SCRIPT:
							try {
								scripts = loadDefaultScript(deviceListId, ScriptType.BACKUP);

								/*
								 * Provision_Log_Step => for 最後寫入供裝紀錄Table使用
								 */
								final String scriptName = (scripts != null && !scripts.isEmpty()) ? scripts.get(0).getScriptName() : null;
								final String scriptCode = (scripts != null && !scripts.isEmpty()) ? scripts.get(0).getScriptCode() : null;

								psStepVO.setScriptCode(scriptCode);
								psStepVO.setRemark(scriptName);

								retVO.setScriptCode(scriptCode);

								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("讀取腳本資料時失敗 [ 錯誤代碼: LOAD_DEFAULT_SCRIPT ]");
							}

						case FIND_DEVICE_CONNECT_INFO:
							try {
								ciVO = findDeviceConfigInfo(ciVO, deviceListId, null, deviceMode);
								ciVO.setTimes(String.valueOf(round));

								/*
								 * Provision_Log_Device => for 最後寫入供裝紀錄Table使用
								 */
								if (!retryRound) {
									psDeviceVO = new ProvisionServiceVO();
									psDeviceVO.setDeviceListId(deviceListId);
									psDeviceVO.setOrderNum(1);
									psStepVO.getDeviceVO().add(psDeviceVO); // add DeviceVO to StepVO

									retVO.setDeviceName(ciVO.getDeviceName());
									retVO.setDeviceIp(ciVO.getDeviceIp());
								}

								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("取得設備資訊時失敗 [ 錯誤代碼: FIND_DEVICE_CONNECT_INFO ]");
							}

						case FIND_DEVICE_LOGIN_INFO:
							try {
								if(loginInfo != null) {
										if (StringUtils.isNotBlank(loginInfo.getLoginAccount())) {
											ciVO.setAccount(loginInfo.getLoginAccount());
										}
										if (StringUtils.isNotBlank(loginInfo.getLoginPassword())) {
											ciVO.setPassword(loginInfo.getLoginPassword());
										}
										if (StringUtils.isNotBlank(loginInfo.getEnablePassword())) {
											ciVO.setEnablePassword(loginInfo.getEnablePassword());
										}

										/**
							             * 判斷該設備是否有指定連線模式(Connection_Mode)，有的話則替換掉廣域設定
							             */
										String deviceConnectionMode = loginInfo.getConnectionMode();
										if (StringUtils.isNotBlank(deviceConnectionMode)) {
										    if (StringUtils.equals(deviceConnectionMode, Constants.SSH)) {
										        ciVO.setConnectionMode(ConnectionMode.SSH);

							                } else if (StringUtils.equals(deviceConnectionMode, Constants.TELNET)) {
							                    ciVO.setConnectionMode(ConnectionMode.TELNET);
							                }
										}
								}else {
									findDeviceLoginInfo(ciVO, deviceListId, ciVO.getGroupId(), ciVO.getDeviceId());
								}								
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("取得設備登入帳密設定時失敗 [ 錯誤代碼: FIND_DEVICE_LOGIN_INFO ]");
							}

						case CONNECT_DEVICE:
							try {
								connectUtils = connect2Device(connectUtils, ciVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("設備連線失敗 [ 錯誤內容: " + e.getMessage() + " ]");
							}

						case LOGIN_DEVICE:
							try {
								login2Device(connectUtils, ciVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("登入設備失敗 [ 錯誤代碼: LOGIN_DEVICE ]");
							}

						case SEND_COMMANDS:
							try {
								outputList = sendCmds(connectUtils, scripts, ciVO, retVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("派送設備命令失敗 [ 錯誤內容: " + e.getMessage() + " ]");
							}

						case COMPARE_CONTENTS:
							try {
								outputList = compareContents(ciVO, outputList, fileUtils, fileServerMode, retVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("組態檔內容比對過程失敗 [ 錯誤代碼: COMPARE_CONTENTS ]");
							}

						case ANALYZE_CONFIG_INFO:
							try {
								analyzeConfigInfo(ciVO, outputList, jobTrigger);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("分析取得設備明細內容時失敗 [ 錯誤代碼: ANALYZE_CONFIG_INFO ] ");
							}

						case DEFINE_OUTPUT_FILE_NAME:
							try {
								defineFileName(ciVO, fileServerMode);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("定義組態檔輸出檔名時失敗 [ 錯誤代碼: DEFINE_OUTPUT_FILE_NAME ]");
							}

						case COMPOSE_OUTPUT_VO:
							try {
								outputVOList = composeOutputVO(ciVO, outputList);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("建構組態檔輸出物件時失敗 [ 錯誤代碼: COMPOSE_OUTPUT_VO ]");
							}

						case CONNECT_FILE_SERVER_4_UPLOAD:
							try {
								fileUtils = connect2FileServer(fileUtils, fileServerMode, ciVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("File Server 連線失敗 [ 錯誤代碼: CONNECT_FILE_SERVER_4_UPLOAD ]");
							}

						case LOGIN_FILE_SERVER_4_UPLOAD:
							try {
								login2FileServer(fileUtils, ciVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("File Server 登入失敗 [ 錯誤代碼: LOGIN_FILE_SERVER_4_UPLOAD ]");
							}

						case UPLOAD_FILE_SERVER:
							try {
								upload2FTP(fileUtils, outputVOList);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("File Server 檔案上傳失敗 [ 錯誤代碼: UPLOAD_FILE_SERVER ]");
							}

						case RECORD_DB_OF_CONFIG_VERSION_INFO:
							try {
								record2DB4ConfigVersionInfo(outputVOList, jobTrigger);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("系統寫入組態備份紀錄時失敗 [ 錯誤代碼: RECORD_DB_OF_CONFIG_VERSION_INFO ]");
							}

						case CLOSE_DEVICE_CONNECTION:
							try {
								closeDeviceConnection(connectUtils);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("關閉與設備間連線時失敗 [ 錯誤代碼: CLOSE_DEVICE_CONNECTION ]");
							}

						case CLOSE_FILE_SERVER_CONNECTION:
							try {
								closeFileServerConnection(fileUtils);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("關閉與 File Server 間連線時失敗 [ 錯誤代碼: CLOSE_FILE_SERVER_CONNECTION ]");
							}

						case VERSION_DIFF_NOTIFY:
                            try {
                                // 有啟用版本異常通知時才執行
                                if (Env.ENABLE_CONFIG_DIFF_NOTIFY) {
                                    // 若先前比對步驟結果已無差異，此步驟就不需執行
                                    if (retVO.getResult() != Result.NO_DIFFERENT) {
                                        versionDiffNotify(ciVO, retVO, outputVOList);
                                    }
                                }

                            } catch (Exception e) {
                                String msg = "組態檔內容模板比對差異通知失敗 [ 錯誤代碼: VERSION_DIFF_NOTIFY ]";
                                retVO.setMessage(msg);
                                log.error(msg);
                            }

						default:
							break;
					}
				}

				retVO.setSuccess(true);

				if (retVO.getResult() == null) {
				    retVO.setResult(Result.SUCCESS);
				}

				break;

			} catch (ServiceLayerException sle) {
				/*
				 * Provision_Log_Retry => for 最後寫入供裝紀錄Table使用 (執行過程失敗，紀錄歷程)
				 */
				psRetryVO = new ProvisionServiceVO();
				psRetryVO.setResult(Result.ERROR.toString());
				psRetryVO.setMessage(sle.toString());
				psRetryVO.setRetryOrder(round);
				psStepVO.getRetryVO().add(psRetryVO); // add RetryVO to StepVO

				retVO.setSuccess(false);
				retVO.setResult(Result.ERROR);
				retVO.setMessage(sle.toString());
				retVO.setCmdProcessLog(sle.getMessage());

				retryRound = true;
				round++;

				if (connectUtils != null) {
					try {
						connectUtils.disconnect();
					} catch (Exception e1) {
						log.error(e1.toString(), e1);
					}
				}

			} catch (Exception e) {
				/*
				 * Provision_Log_Retry (執行過程失敗，紀錄歷程)
				 */
				psRetryVO = new ProvisionServiceVO();
				psRetryVO.setResult(Result.ERROR.toString());
				psRetryVO.setMessage(e.toString());
				psRetryVO.setRetryOrder(round);
				psStepVO.getRetryVO().add(psRetryVO); // add RetryVO to StepVO

				retVO.setSuccess(false);
				retVO.setResult(Result.ERROR);
				retVO.setMessage(e.toString());
				retVO.setCmdProcessLog(e.getMessage());

				retryRound = true;
				round++;

				if (connectUtils != null) {
					try {
						connectUtils.disconnect();
					} catch (Exception e1) {
						log.error(e1.toString(), e1);
					}
				}
			}
		}

		/*
		 * Provision_Log_Step
		 */
		psStepVO.setEndTime(new Date());
		psStepVO.setResult(retVO.getResult().toString());
		psStepVO.setMessage(retVO.getMessage());
		psStepVO.setRetryTimes(round-1);
		psStepVO.setProcessLog(retVO.getCmdProcessLog());

		/*
		 * Provision_Log_Detail
		 */
		psDetailVO.setEndTime(new Date());
		psDetailVO.setResult(retVO.getResult().toString());
		psDetailVO.setMessage(retVO.getMessage());
		psDetailVO.getStepVO().add(psStepVO); // add StepVO to DetailVO

		psMasterVO.getDetailVO().add(psDetailVO); // add DetailVO to MasterVO
		retVO.setPsVO(psMasterVO);

		retVO.setEndTime(new Date());
		retVO.setRetryTimes(round);

		return retVO;
	}

	@Override
	public StepServiceVO doBackupFileUpload2FTPStep(List<VersionServiceVO> vsVOs, ConfigInfoVO ciVO, boolean jobTrigger) {
		StepServiceVO retVO = new StepServiceVO();
		retVO.setJobExcuteResultRecords(Integer.toString(vsVOs.size()));

		// 設定 retry 次數，預設為1表示不做retry
		final int RETRY_TIMES = StringUtils.isNotBlank(Env.RETRY_TIMES) ? Integer.parseInt(Env.RETRY_TIMES) : 1;
		// 紀錄當前執行的回合數
		int round = 1;

		boolean success = true;
		while (round <= RETRY_TIMES) {
			try {
				Step[] steps = null;
				ConnectionMode downloadMode = null;
				ConnectionMode uploadMode = null;

				/*
				 * 此method是在做Local端(設備→PRTG Server)設備組態備份檔異地備份到Remote端(PRTG Server→Backup Server)設備
				 * 目前僅有一種執行流程:
				 * downloadMode = 從Local端下載備份檔 (目前設備→PRTG Server皆是使用TFTP服務)
				 * uploadMode = 將上述下載的備份檔上傳到Remote端 (目前Remote端皆是使用FTP服務)
				 */
				switch (Env.DEFAULT_BACKUP_FILE_BACKUP_MODE) {
					case Constants.BACKUP_FILE_BACKUP_MODE_NULL_FTP_FTP:
						steps = null;
						downloadMode = ConnectionMode.FTP;
						uploadMode = ConnectionMode.FTP;
						break;

					case Constants.BACKUP_FILE_BACKUP_MODE_STEP_TFTP_FTP:
						steps = Env.BACKUP_FILE_DOWNLOAD_FROM_TFTP_AND_UPLOAD_2_FTP;
						downloadMode = ConnectionMode.TFTP;
						uploadMode = ConnectionMode.FTP;
						break;
				}

				List<ConfigInfoVO> outputVOList = null;		// Output VO
				FileUtils fileUtils = null;					// 連線FileServer物件

				for (Step _step : steps) {
					switch (_step) {
						case CONNECT_FILE_SERVER_4_DOWNLOAD:
							fileUtils = connect2FileServer(fileUtils, downloadMode, ciVO);
							break;

						case DOWNLOAD_FILE:
							outputVOList = downloadFile(Constants.ACTION_TYPE_BACKUP, fileUtils, vsVOs, ciVO, true);
							break;

						case CONNECT_FILE_SERVER_4_UPLOAD:
							fileUtils = connect2FileServer(fileUtils, uploadMode, ciVO);
							break;

						case LOGIN_FILE_SERVER_4_UPLOAD:
							login2FileServer(fileUtils, ciVO);
							break;

						case UPLOAD_FILE_SERVER:
							upload2FTP(fileUtils, outputVOList);
							break;

						case CLOSE_FILE_SERVER_CONNECTION:
							closeFileServerConnection(fileUtils);
							break;

						default:
							break;
					}
				}

				success = true;

			} catch (Exception e) {
				log.error(e.toString(), e);

				success = false;

			} finally {
				if (success) {
					break;
				} else {
					round++;
				}
			}
		}

		return retVO;
	}

	/**
	 * 關閉與裝置的連線
	 * @param connectUtils
	 */
	private void closeDeviceConnection(ConnectUtils connectUtils) {
		try {
			if (connectUtils != null) {
				connectUtils.disconnect();
			}

		} catch (Exception e) {

		} finally {
			connectUtils = null;
		}
	}

	/**
	 * 關閉與FTP/TFTP的連線
	 * @param fileUtils
	 */
	private void closeFileServerConnection(FileUtils fileUtils) {
		try {
			if (fileUtils != null) {
				fileUtils.disconnect();
			}

		} catch (Exception e) {

		} finally {
			fileUtils = null;
		}
	}

	/**
	 * [Step] 取得預設腳本內容(備份、還原)
	 * @param script
	 * @return
	 * @throws ServiceLayerException
	 */
	private List<ScriptServiceVO> loadDefaultScript(String deviceListId, ScriptType type) throws ServiceLayerException {
		return scriptService.loadDefaultScript(deviceListId, type);
	}

	/**
	 * [Step] 取得指定腳本指令W內容
	 * @param script
	 * @return
	 * @throws ServiceLayerException
	 */
	private List<ScriptServiceVO> loadSpecifiedScript(String scriptInfoId, String scriptCode, List<Map<String, String>> varMapList, List<ScriptServiceVO> scripts, String scriptMode) throws ServiceLayerException {
		return scriptService.loadSpecifiedScript(scriptInfoId, scriptCode, varMapList, scripts, scriptMode);
	}

	/**
	 * [Step] 針對特定腳本寫入LOG table( 封鎖開通紀錄 blockList)
	 * @throws ServiceLayerException
	 */
    private void writeSpecifyLog(
            ConfigInfoVO ciVO, String scriptCode, List<Map<String, String>> varMapList, String remark) throws ServiceLayerException {
        try {
            blockedRecordService.writeModuleBlockListRecord(ciVO, scriptCode, varMapList, remark);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("寫入LOG table失敗 >> " + e.toString());
        }
    }

	/**
	 * [Step] 查找設備連線資訊
	 * @param configInfoVO
	 * @param deviceListId
	 * @param deviceInfo
	 * @return
	 * @throws ServiceLayerException
	 */
	private ConfigInfoVO findDeviceConfigInfo(ConfigInfoVO configInfoVO, String deviceListId, Map<String, String> deviceInfo, ConnectionMode connectionMode) throws ServiceLayerException {
		configInfoVO = new ConfigInfoVO();

		if (deviceListId != null) {
			DeviceList device = deviceDAO.findDeviceListByDeviceListId(deviceListId);

			if (device == null) {
				throw new ServiceLayerException("[device_id: " + deviceListId + "] >> 查無設備資料");
			}

			BeanUtils.copyProperties(device, configInfoVO);

			/*
			 *  先套用預設的帳密，下一步驟會再查詢是否有by設備設定帳密並覆蓋
			 */
			configInfoVO.setAccount(Env.DEFAULT_DEVICE_LOGIN_ACCOUNT);
			configInfoVO.setPassword(Env.DEFAULT_DEVICE_LOGIN_PASSWORD);
			configInfoVO.setEnablePassword(Env.DEFAULT_DEVICE_ENABLE_PASSWORD);

		} else {
			/*
			 * deviceListId 為空表示是直接指定要供裝的設備(可能不在CMAP組態備份名單內的設備)
			 * 因此相關連線資訊直接從 deviceInfo MAP 中取得
			 */
		    if (deviceInfo == null || (deviceInfo != null && deviceInfo.isEmpty())) {
		        log.error("取得要供裝的設備連線相關資訊失敗 >> 未傳入 deviceListId 且 deviceInfo 也為空");
		        throw new ServiceLayerException("取得要供裝的設備連線相關資訊失敗");
		    }

			String deviceIp = deviceInfo.containsKey(Constants.DEVICE_IP) ? deviceInfo.get(Constants.DEVICE_IP) : null;
			String devicePort = deviceInfo.containsKey(Constants.DEVICE_PORT) ? deviceInfo.get(Constants.DEVICE_PORT) : null;
			String deviceName = deviceInfo.containsKey(Constants.DEVICE_NAME) ? deviceInfo.get(Constants.DEVICE_NAME) : null;
			String loginAccount = deviceInfo.containsKey(Constants.DEVICE_LOGIN_ACCOUNT)
															? deviceInfo.get(Constants.DEVICE_LOGIN_ACCOUNT) : Env.DEFAULT_DEVICE_LOGIN_ACCOUNT;
			String loginPassword = deviceInfo.containsKey(Constants.DEVICE_LOGIN_PASSWORD)
															? deviceInfo.get(Constants.DEVICE_LOGIN_PASSWORD) : Env.DEFAULT_DEVICE_LOGIN_PASSWORD;
			String enablePassword = deviceInfo.containsKey(Constants.DEVICE_ENABLE_PASSWORD)
															? deviceInfo.get(Constants.DEVICE_ENABLE_PASSWORD) : Env.DEFAULT_DEVICE_ENABLE_PASSWORD;

			configInfoVO.setDeviceIp(deviceIp);
			configInfoVO.setDevicePort(StringUtils.isNotBlank(devicePort) ? Integer.parseInt(devicePort) : null);
			configInfoVO.setDeviceName(deviceName);
			configInfoVO.setAccount(loginAccount);
			configInfoVO.setPassword(loginPassword);
			configInfoVO.setEnablePassword(enablePassword);
		}

		/**
		 * TODO 預留裝置落地檔上傳FTP/TFTP位址BY設備設定
		 */
		configInfoVO.setFtpIP(Env.FTP_HOST_IP);
		configInfoVO.setFtpPort(Env.FTP_HOST_PORT);
		configInfoVO.setFtpAccount(Env.FTP_LOGIN_ACCOUNT);
		configInfoVO.setFtpPassword(Env.FTP_LOGIN_PASSWORD);
		configInfoVO.setFtpUrl(Env.FTP_HOST_IP + ":" + Env.FTP_HOST_PORT);

		configInfoVO.settFtpIP(Env.TFTP_HOST_IP);
		configInfoVO.settFtpPort(Env.TFTP_HOST_PORT);

		configInfoVO.setConnectionMode(connectionMode);

		return configInfoVO;
	}

	/**
	 * [Step] 查找設備連線帳密 & 連線方式(TELNET / SSH)
	 * @throws ServiceLayerException
	 */
	private void findDeviceLoginInfo(ConfigInfoVO ciVO, String deviceListId, String groupId, String deviceId) throws ServiceLayerException {
	    if (StringUtils.isBlank(deviceListId) && StringUtils.isBlank(groupId) && StringUtils.isBlank(deviceId)) {
	        // 若這三個參數都未傳值則不處理
	        return;
	    }

		DeviceLoginInfo loginInfo = findDeviceLoginInfo(deviceListId, groupId, deviceId);

		if (loginInfo != null) {
			if (StringUtils.isNotBlank(loginInfo.getLoginAccount())) {
				ciVO.setAccount(loginInfo.getLoginAccount());
			}
			if (StringUtils.isNotBlank(loginInfo.getLoginPassword())) {
				ciVO.setPassword(loginInfo.getLoginPassword());
			}
			if (StringUtils.isNotBlank(loginInfo.getEnablePassword())) {
				ciVO.setEnablePassword(loginInfo.getEnablePassword());
			}

			/**
             * 判斷該設備是否有指定連線模式(Connection_Mode)，有的話則替換掉廣域設定
             */
			String deviceConnectionMode = loginInfo.getConnectionMode();
			if (StringUtils.isNotBlank(deviceConnectionMode)) {
			    if (StringUtils.equals(deviceConnectionMode, Constants.SSH)) {
			        ciVO.setConnectionMode(ConnectionMode.SSH);

                } else if (StringUtils.equals(deviceConnectionMode, Constants.TELNET)) {
                    ciVO.setConnectionMode(ConnectionMode.TELNET);
                }
			}
		}
	}

	/**
	 * [Step] 連線設備
	 * @param connectUtils
	 * @param _mode
	 * @param ciVO
	 * @return
	 * @throws Exception
	 */
	private ConnectUtils connect2Device(ConnectUtils connectUtils, ConfigInfoVO ciVO) throws Exception {
	    final String deviceIp = ciVO.getDeviceIp();
	    final Integer devicePort = ciVO.getDevicePort();

	    ConnectionMode _mode = ciVO.getConnectionMode();
		switch (_mode) {
			case TELNET:
				connectUtils = new TelnetUtils();
				connectUtils.connect(deviceIp, devicePort);
				break;

			case SSH:
				connectUtils = new SshUtils();
				connectUtils.connect(deviceIp, devicePort);
				break;

			default:
				break;
		}

		return connectUtils;
	}

	/**
	 * [Step] 登入
	 * @param connectUtils
	 * @param ciVO
	 * @throws Exception
	 */
	private void login2Device(ConnectUtils connectUtils, ConfigInfoVO ciVO) throws Exception {
		connectUtils.login(
				StringUtils.isBlank(ciVO.getAccount()) ? Env.DEFAULT_DEVICE_LOGIN_ACCOUNT : ciVO.getAccount(),
						StringUtils.isBlank(ciVO.getPassword()) ? Env.DEFAULT_DEVICE_LOGIN_PASSWORD : ciVO.getPassword(),
								StringUtils.isBlank(ciVO.getEnablePassword()) ? Env.DEFAULT_DEVICE_ENABLE_PASSWORD : ciVO.getEnablePassword(),
										ciVO
				);

		/*
		Method method = obj.getClass().getMethod("login", new Class[]{String.class,String.class});
		method.invoke(
				obj,
				new String[] {
							StringUtils.isBlank(ciVO.getAccount()) ? Env.DEFAULT_DEVICE_LOGIN_ACCOUNT : ciVO.getAccount(),
							StringUtils.isBlank(ciVO.getPassword()) ? Env.DEFAULT_DEVICE_LOGIN_PASSWORD : ciVO.getPassword()
						});
		 */
	}

	/**
	 * [Step] 執行腳本指令並取回設定要輸出的指令回傳內容
	 * @param connectUtils
	 * @param scriptList
	 * @param configInfoVO
	 * @return
	 * @throws Exception
	 */
	private List<String> sendCmds(ConnectUtils connectUtils, List<ScriptServiceVO> scriptList,  ConfigInfoVO configInfoVO, StepServiceVO ssVO) throws Exception {
		return connectUtils.sendCommands(scriptList, configInfoVO, ssVO);
	}

	/**
	 * [Step] 跟前一版本比對內容，若內容無差異則不再新增備份檔
	 * @param ciVO
	 * @param outputList
	 * @param fileUtils
	 * @param _mode
	 * @return
	 * @throws Exception
	 */
	private List<String> compareContents(ConfigInfoVO ciVO, List<String> outputList, FileUtils fileUtils, ConnectionMode _mode, StepServiceVO ssVO)
	        throws Exception {
		String type = "";

		List<List<VersionServiceVO>> allVersionList = new ArrayList<>();  // 存放 Running & Startup 各自的前後版本 config 內容
		boolean haveDiffVersion = false;
		List<String> tmpList = outputList.stream().collect(Collectors.toList());
		
		for (final String output : tmpList) {

			if (output.indexOf(Env.COMM_SEPARATE_SYMBOL) != -1) {
				type = output.split(Env.COMM_SEPARATE_SYMBOL)[0];
			}

			// 查找此裝置前一版本組態檔資料
			ConfigVersionInfo configInfo = configDAO.getLastConfigVersionInfoByDeviceIdAndConfigType(ciVO.getDeviceId(), type);

			// 當前備份版本正確檔名
			final String nowVersionFileName = StringUtils.replace(ciVO.getConfigFileName(), Env.COMM_SEPARATE_SYMBOL, type);
			// 當前備份版本上傳於temp資料夾內檔名 (若TFTP Server與CMAP系統不是架設在同一台主機時)
			String tempFileName = nowVersionFileName;
			if (Env.ENABLE_TEMP_FILE_RANDOM_CODE) {
				tempFileName =
					_mode == ConnectionMode.TFTP ? (!Env.TFTP_SERVER_AT_LOCAL ? nowVersionFileName.concat(".").concat(ciVO.getTempFileRandomCode()) : null)
												 : (!Env.FTP_SERVER_AT_LOCAL ? nowVersionFileName.concat(".").concat(ciVO.getTempFileRandomCode()) : null);
			}

			final String nowVersionTempFileName = tempFileName;

			List<VersionServiceVO> vsVOs;
			if (configInfo != null) {
				vsVOs = new ArrayList<>();

				//前一版本VO
				VersionServiceVO preVersionVO = new VersionServiceVO();
				preVersionVO.setCheckEnableCurrentDateSetting(true);
				preVersionVO.setConfigFileDirPath(Objects.toString(configInfo.getConfigFileDirPath(), File.separator));
				preVersionVO.setFileFullName(configInfo.getFileFullName());
				preVersionVO.setCreateDate(configInfo.getCreateTime());
				vsVOs.add(preVersionVO);

				//當下備份上傳版本VO
				VersionServiceVO nowVersionVO = new VersionServiceVO();
				/*
				 * 若TFTP Server與CMAP系統不是架設在同一台主機上
				 * Config file從Device上傳時會先放置於temp資料夾內(Env.TFTP_TEMP_DIR_PATH)
				 * 比對版本內容時抓取的檔名(FileFullName)也必須調整為temp資料夾內檔名(nowVersionTempFileName，有加上時間細數碼)
				 */
				if (_mode == ConnectionMode.TFTP) {
					nowVersionVO.setConfigFileDirPath(ciVO.getConfigFileDirPath());
					nowVersionVO.setFileFullName(!Env.TFTP_SERVER_AT_LOCAL ? nowVersionTempFileName : nowVersionFileName);

				} else if (_mode == ConnectionMode.FTP) {
					nowVersionVO.setConfigFileDirPath(ciVO.getConfigFileDirPath());
					nowVersionVO.setFileFullName(!Env.FTP_SERVER_AT_LOCAL ? nowVersionTempFileName : nowVersionFileName);
				}

				vsVOs.add(nowVersionVO);
				allVersionList.add(vsVOs);

				VersionServiceVO compareRetVO = versionService.compareConfigFiles(vsVOs);

				if (StringUtils.isBlank(compareRetVO.getDiffPos())) {
					/*
					 * 版本內容比對相同:
					 * (1)若[Env.TFTP_SERVER_AT_LOCAL=true]，刪除本機已上傳的檔案;若為false則不處理(另外設定系統排程定期清整temp資料夾內檔案)
					 * (2)移除List內容
					 */
					outputList.remove(output);

					//TODO
//					if (_mode == ConnectionMode.FTP) {
//						if (Env.FTP_SERVER_AT_LOCAL) {
//							deleteLocalFile(Env.FTP_TEMP_DIR_PATH.concat(nowVersionFileName));
//						}
//					}
					if (_mode == ConnectionMode.TFTP) {
						if (Env.TFTP_SERVER_AT_LOCAL) {
							String targetFileName = "";				
							if(StringUtils.isBlank(nowVersionVO.getConfigFileDirPath()) || nowVersionVO.getConfigFileDirPath().length() == 1) {
								targetFileName = Env.TFTP_LOCAL_ROOT_DIR_PATH.concat(File.separator).concat(nowVersionVO.getFileFullName());
							}else {
								targetFileName = Env.TFTP_LOCAL_ROOT_DIR_PATH.concat(nowVersionVO.getConfigFileDirPath()).concat(nowVersionVO.getFileFullName());
							}
							targetFileName = targetFileName.replaceAll("/", Matcher.quoteReplacement(File.separator));
							deleteLocalFile(targetFileName);
						}
					}

				} else {
					// 版本內容不同
					haveDiffVersion = true;
				}

			} else {
				// 若沒有前一版本(系統首次備份)
				haveDiffVersion = true;
			}

			if (haveDiffVersion) {
				/*
				 * 版本內容不同 OR 若沒有前一版本(系統首次備份):
				 * (1)若[Env.TFTP_SERVER_AT_LOCAL=false]，將檔案從TFTP temp資料夾copy到Device對應目錄;若為true則不需再作處理
				 */
				if (_mode == ConnectionMode.TFTP) {
					if (Env.TFTP_SERVER_AT_LOCAL == null || !Env.TFTP_SERVER_AT_LOCAL) {
						final String sourceDirPath = Env.TFTP_TEMP_DIR_PATH;
						final String targetDirPath = ciVO.getConfigFileDirPath().concat(nowVersionFileName);
						ciVO.setFileFullName(nowVersionTempFileName);

						fileUtils.moveFiles(ciVO, sourceDirPath, targetDirPath);
					}

				} else if (_mode == ConnectionMode.FTP) {
					if (Env.FTP_SERVER_AT_LOCAL == null && !Env.FTP_SERVER_AT_LOCAL) {
						final String sourceDirPath = Env.FTP_TEMP_DIR_PATH;
						final String targetDirPath = ciVO.getConfigFileDirPath().concat(nowVersionFileName);
						ciVO.setFileFullName(nowVersionTempFileName);

						fileUtils.moveFiles(ciVO, sourceDirPath, targetDirPath);
					}
				}
			}
		}

		ssVO.setVersionList(allVersionList);

		if (!haveDiffVersion) {
			ssVO.setResult(Result.NO_DIFFERENT);
			ssVO.setMessage("版本無差異");
		} else {
			ssVO.setResult(Result.SUCCESS);
		}

		return outputList;
	}

	/**
	 * 比對前後版本是否有差異 & 發信通知
	 * @param ciVO
	 * @param ssVO
	 * @throws Exception
	 */
	private void versionDiffNotify(ConfigInfoVO ciVO, StepServiceVO ssVO, List<ConfigInfoVO> ciVOList) throws Exception {
	    String retMsg = null;
	    try {
	        final String CONFIG_DIFF_NOTIFY = Constants.CONFIG_CONTENT_SETTING_TYPE_CONFIG_DIFF_NOTIFY;
	        final List<List<VersionServiceVO>> versionList = ssVO.getVersionList();
	        final String groupId = ciVO.getGroupId();
	        final String deviceId = ciVO.getDeviceId();

	        ConfigVersionInfo cviEntity = null;
	        for (List<VersionServiceVO> typeConfig : versionList) {
	            try {
	                // Step 1. 取得兩個版本的Config內容
	                VersionServiceVO preVersionVO = typeConfig.get(0);
	                VersionServiceVO newVersionVO = typeConfig.get(1);

	                List<String> oriPreContentList = versionService.getConfigFileContent(preVersionVO, false).getConfigContentList();
	                List<String> oriNewContentList = versionService.getConfigFileContent(newVersionVO, false).getConfigContentList();

	                if ((oriPreContentList == null || (oriPreContentList != null && oriPreContentList.isEmpty()))
	                        || (oriNewContentList == null || (oriNewContentList != null && oriNewContentList.isEmpty()))) {
	                    throw new ServiceLayerException(
	                            "[versionDiffNotify] 版本內容為空 >> preVersion: " + preVersionVO.getFileFullName() +
	                            ", newVersion: " + newVersionVO.getFileFullName());
	                }

	                // Step 2. 依設定的比對模板，取出符合設定的Config內容片段
	                ConfigVO configVO = null;
	                ciVO.setConfigContentList(oriPreContentList);
	                List<String> preContentList = processConfigContentSetting(configVO, CONFIG_DIFF_NOTIFY, ciVO);

	                ciVO.setConfigContentList(oriNewContentList);
	                List<String> newContentList = processConfigContentSetting(configVO, CONFIG_DIFF_NOTIFY, ciVO);

	                boolean isDiff = versionService.compareConfigList(preContentList, newContentList);

	                if (isDiff) {
	                    // Step 3. 比對結果有差異，寫入差異LOG & 發信通知
	                    final String endPosSympol = "." + Env.CONFIG_FILE_EXTENSION_NAME;
	                    final String preVersion = subStr(preVersionVO.getFileFullName(), 0, endPosSympol);
	                    final String newVersion = subStr(newVersionVO.getFileFullName(), 0, endPosSympol);

	                    cviEntity = configDAO.getConfigVersionInfoByUK(groupId, deviceId, preVersion);
	                    final String preVersionId = cviEntity.getVersionId();

	                    cviEntity = configDAO.getConfigVersionInfoByUK(groupId, deviceId, newVersion);
	                    final String newVersionId = cviEntity.getVersionId();

	                    ConfigVersionDiffLog diffLogEntity = new ConfigVersionDiffLog(
	                            null
	                           ,groupId
	                           ,deviceId
	                           ,preVersionId
	                           ,preVersion
	                           ,newVersionId
	                           ,newVersion
	                           ,currentTimestamp()
	                           ,currentUserName()
	                           ,currentTimestamp()
	                           ,currentUserName()
	                    );

	                    configDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, diffLogEntity);

	                    // 發信
	                    //TODO
	                }

	            } catch (Exception e) {
	                retMsg += "[" + e.toString() + "]";
	                log.error(e.toString(), e);
	            }
	        }

	        if (StringUtils.isNotBlank(retMsg)) {
	            throw new ServiceLayerException(retMsg);
	        }

	    } catch (ServiceLayerException sle) {
	        throw sle;

	    } catch (Exception e) {
	        log.error(e.toString(), e);
	        throw new ServiceLayerException(e.toString());
	    }
	}

	private String subStr(String text, int beginIndex, String endPosSymbol) {
	    int endIndex = text.indexOf(endPosSymbol);

	    if (endIndex != -1) {
	        return text.substring(beginIndex, endIndex);
	    } else {
	        return text;
	    }
	}

	/**
	 * 取得指定版本號的組態檔內容
	 * @param configInfoVO
	 * @return
	 */
	private List<String> getConfigContent(ConfigInfoVO configInfoVO) {
		List<String> retList = new ArrayList<>();

		try {
			FileUtils fileUtils = null;
			String _hostIp = null;
			Integer _hostPort = null;
			String _loginAccount = null;
			String _loginPassword = null;

			if(Env.FILE_TRANSFER_MODE.equals(ConnectionMode.TFTP) && Env.TFTP_SERVER_AT_LOCAL) {
				
				String targetFileName = null;				
				if(StringUtils.isBlank(configInfoVO.getConfigFileDirPath()) || configInfoVO.getConfigFileDirPath().length() == 1) {
					targetFileName = Env.TFTP_LOCAL_ROOT_DIR_PATH.concat(File.separator).concat(configInfoVO.getFileFullName());
				}else {
					targetFileName = Env.TFTP_LOCAL_ROOT_DIR_PATH.concat(configInfoVO.getConfigFileDirPath()).concat(configInfoVO.getFileFullName());
				}
				
				//read file into stream, try-with-resources
				try {
					targetFileName = targetFileName.replaceAll("/", Matcher.quoteReplacement(File.separator));
					log.debug("for debug targetFileName = " + targetFileName);
					retList = Files.readAllLines(Paths.get(targetFileName), StandardCharsets.UTF_8);
					
				} catch (IOException e) {
					e.printStackTrace();
				}

			}else {
				// Step1. 建立FileServer傳輸物件
				switch (Env.FILE_TRANSFER_MODE) {
					case FTP:
						fileUtils = new FtpFileUtils();
						_hostIp = Env.FTP_HOST_IP;
						_hostPort = Env.FTP_HOST_PORT;
						_loginAccount = Env.FTP_LOGIN_ACCOUNT;
						_loginPassword = Env.FTP_LOGIN_PASSWORD;
						break;

					case TFTP:
						fileUtils = new TFtpFileUtils();
						_hostIp = Env.TFTP_HOST_IP;
						_hostPort = Env.TFTP_HOST_PORT;
						break;
				}

				// Step2. FTP連線
				fileUtils.connect(_hostIp, _hostPort);

				// Step3. FTP登入
				fileUtils.login(_loginAccount, _loginPassword);

				// Step3. 移動作業目錄至指定的裝置
				String fileDir = configInfoVO.getConfigFileDirPath();

				if (Env.ENABLE_LOCAL_BACKUP_USE_TODAY_ROOT_DIR) {
					SimpleDateFormat sdf = new SimpleDateFormat(Env.DIR_PATH_OF_CURRENT_DATE_FORMAT);
					fileDir = sdf.format(new Date()).concat(Env.FTP_DIR_SEPARATE_SYMBOL).concat(fileDir);
				}

				fileUtils.changeDir(fileDir, false);

				// Step4. 下載指定的Config落地檔
				retList = fileUtils.downloadFiles(configInfoVO);
			}
			
			
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return retList;
	}

	/**
	 * [Step] 解析組態檔內容取得特定資料寫入 Device_Detail_Info for 後續功能使用
	 * @param configInfoVO
	 * @param outputList
	 */
	private void analyzeConfigInfo(ConfigInfoVO configInfoVO, List<String> outputList, boolean jobTrigger) {

		if (outputList == null || (outputList != null && outputList.isEmpty())) {
			/*
			 * outputList 為空表示版本無差異，此時不做內容解析
			 */

		} else {
			try {
				List<DeviceDetailMapping> entities = deviceDAO.findDeviceDetailMapping(null);

				if (entities != null && !entities.isEmpty()) {
					final String userName = jobTrigger ? Env.USER_NAME_JOB : SecurityUtil.getSecurityUser().getUsername();

					/*
					 * 版本有差異情況下，先刪除前一次分析取出的內容
					 */
					deviceDAO.deleteDeviceDetailInfoByInfoName(null, configInfoVO.getGroupId(), configInfoVO.getDeviceId(), null, currentTimestamp(), userName);

					List<String> configContent = getConfigContent(configInfoVO);

					List<DeviceDetailInfo> insertEntities = new ArrayList<>();
					Map<String, Integer> orderMap = new HashMap<>();
					Map<String, DeviceDetailInfo> analyzeInfoName = new HashMap<>();

					for (final String configStr : configContent) {

						DeviceDetailInfo ddi = null;
						for (DeviceDetailMapping entity : entities) {
							final String sourceString = entity.getSourceString();
							final String splitBy = entity.getSplitBy();
							final Integer getValueIndex = entity.getGetValueIndex();
							final String targetInfoName = entity.getTargetInfoName();
							final String targetInfoRemark = entity.getTargetInfoRemark();
							final String deviceListId = configInfoVO.getDeviceListId();
							final String groupId = configInfoVO.getGroupId();
							final String deviceId = configInfoVO.getDeviceId();
							final Timestamp updateTime = currentTimestamp();

							/*
							 * Y190424, Ken Lin
							 * 若Mapping設定中，target_info_remark欄位有設定要排除的比對字樣 (以[-]開頭標示，若有多組排除字樣以「@~」區隔)
							 * 則判斷當前config字串內容是否符合排除字樣開頭，若符合則跳過此Mapping比對
							 * e.g.: VM切換中，關Port時需跳過管理Port (port ethernet 1/1)
							 */
							String[] excludeStrArray = null;
                            if (StringUtils.isNotBlank(targetInfoRemark)) {
                                final String excludeSymbol = "[-]";

                                if (StringUtils.contains(targetInfoRemark, excludeSymbol)) {
                                    String excludeStr = StringUtils.split(targetInfoRemark, excludeSymbol)[0];
                                    excludeStrArray = StringUtils.split(excludeStr, Env.COMM_SEPARATE_SYMBOL);
                                }
                            }

                            boolean excludeThisMapping = false;
                            if (excludeStrArray != null) {
                                for (String exStr : excludeStrArray) {
                                    if (StringUtils.contains(configStr, exStr)) {
                                        excludeThisMapping = true;
                                        break;
                                    }
                                }
                            }

                            if (excludeThisMapping) {
                                continue;
                            }

							if (StringUtils.startsWith(configStr, sourceString)) {
								String[] tmpArray = StringUtils.split(configStr, splitBy);

								String getTargetValue = null;
								if (tmpArray.length > getValueIndex) {
									getTargetValue = tmpArray[getValueIndex];
								}

								if (getTargetValue == null) {
									continue;
								}

								Integer targetInfoOrder = 1;
								if (orderMap.containsKey(targetInfoName)) {
									targetInfoOrder = orderMap.get(targetInfoName);
									targetInfoOrder++;
								}

								List<DeviceDetailInfo> info = deviceDAO.findDeviceDetailInfo(deviceListId, groupId, deviceId, targetInfoName);

								if (info != null && !info.isEmpty()) {
									continue;
								}

								ddi = new DeviceDetailInfo();
								ddi.setInfoId(UUID.randomUUID().toString());
								ddi.setDeviceListId(deviceListId);
								ddi.setGroupId(groupId);
								ddi.setDeviceId(deviceId);
								ddi.setInfoName(targetInfoName);
								ddi.setInfoValue(getTargetValue);
								ddi.setInfoOrder(targetInfoOrder);
								ddi.setInfoRemark(targetInfoRemark);
								ddi.setCreateTime(updateTime);
								ddi.setCreateBy(userName);
								ddi.setUpdateTime(updateTime);
								ddi.setUpdateBy(userName);

								insertEntities.add(ddi);
								orderMap.put(targetInfoName, targetInfoOrder);

								if (!analyzeInfoName.containsKey(targetInfoName)) {
									analyzeInfoName.put(targetInfoName, ddi);
								}
							}
						}
					}

					//再新增新資料
					if (insertEntities != null && !insertEntities.isEmpty()) {
						deviceDAO.insertEntities(BaseDAO.TARGET_PRIMARY_DB, insertEntities);
					}
				}

			} catch (Exception e) {
				log.error("更新設備明細資料時異常 >>> "+e.toString(), e);
			}
		}
	}

	/**
	 * [Step] 定義輸出檔案名稱
	 * @param configInfoVO
	 * @throws ServiceLayerException
	 */
	private void defineFileName(ConfigInfoVO configInfoVO, ConnectionMode fileServerMode) throws ServiceLayerException {
		ConfigVersionInfoDAOVO cviDAOVO = new ConfigVersionInfoDAOVO();
		cviDAOVO.setQueryGroup1(configInfoVO.getGroupId());
		cviDAOVO.setQueryDevice1(configInfoVO.getDeviceId());
		cviDAOVO.setQueryDateBegin1(Constants.FORMAT_YYYY_MM_DD.format(new Date()));
		cviDAOVO.setQueryDateEnd1(Constants.FORMAT_YYYY_MM_DD.format(new Date()));
		ConfigVersionInfo configInfo = configDAO.getLastConfigVersionInfoByDeviceIdAndConfigType(configInfoVO.getDeviceId(), null);
		
		int seqNo = 1;
		if (configInfo != null && StringUtils.equals(Constants.FORMAT_YYYY_MM_DD.format(configInfo.getCreateTime()), Constants.FORMAT_YYYY_MM_DD.format(new Date()))) {
			String currentSeq = StringUtils.isNotBlank(configInfo.getConfigVersion())
					? configInfo.getConfigVersion().substring(configInfo.getConfigVersion().length()-3, configInfo.getConfigVersion().length())
							: "0";

					seqNo += Integer.valueOf(currentSeq);
		}

		String fileName = CommonUtils.composeConfigFileName(configInfoVO, seqNo);
		String filePath = CommonUtils.composeConfigDirPath(configInfoVO, fileServerMode == ConnectionMode.TFTP);

		/*
		 * 若 TFTP Server 與 CMAP系統 不是架設在同一台主機上
		 * 因組態檔案名稱時間戳記僅有到「分」，若同一分鐘內備份多次，會因為檔名重複而命令執行失敗
		 * 因此，若此條件下，將上傳到temp資料夾的檔案名稱加上時間細數碼
		 */
		String tempFileRandomCode = "";
		//TODO
		String tempFilePath = filePath;
		if (Env.ENABLE_TEMP_FILE_RANDOM_CODE) {
			long miles = System.currentTimeMillis();
			long seconds = TimeUnit.MILLISECONDS.toSeconds(miles) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(miles));
			tempFileRandomCode = String.valueOf(seconds).concat(".").concat(configInfoVO.getTimes());
			tempFilePath = tempFilePath.concat(".").concat(tempFileRandomCode);
		}

		configInfoVO.setConfigFileName(fileName);
		configInfoVO.setFileFullName(fileName);
		configInfoVO.setConfigFileDirPath(filePath);
		configInfoVO.settFtpFilePath(filePath);
		configInfoVO.setFtpFilePath(filePath);
		configInfoVO.setTempFileRandomCode(tempFileRandomCode);
		configInfoVO.setTempFilePath(tempFilePath);
		
		log.debug("for debug filepath = " + filePath +", filename = " + fileName);
	}

	/**
	 * [Step] 查找此群組+設備今日是否已有備份紀錄，決定此次備份檔流水
	 * @param configInfoVO
	 * @param outputList
	 * @return
	 * @throws ServiceLayerException
	 * @throws CloneNotSupportedException
	 */
	private List<ConfigInfoVO> composeOutputVO(ConfigInfoVO configInfoVO, List<String> outputList) throws ServiceLayerException, CloneNotSupportedException {
		List<ConfigInfoVO> voList = new ArrayList<>();
		String type = "";
		String content = "";

		ConfigInfoVO vo;
		for (String output : outputList) {
			if (output.indexOf(Env.COMM_SEPARATE_SYMBOL) != -1) {
				type = output.split(Env.COMM_SEPARATE_SYMBOL)[0];
				content = output.split(Env.COMM_SEPARATE_SYMBOL).length > 1 ?output.split(Env.COMM_SEPARATE_SYMBOL)[1]: "";
			} else {
				content = output;
			}

			vo = (ConfigInfoVO)configInfoVO.clone();
			vo.setConfigType(type);
			vo.setConfigContent(content);
			vo.setConfigFileDirPath(configInfoVO.getConfigFileDirPath());
			
			String configFileName = vo.getConfigFileName();
			if (configFileName.indexOf(Env.COMM_SEPARATE_SYMBOL) != -1) {
				configFileName = StringUtils.replace(configFileName, Env.COMM_SEPARATE_SYMBOL, type);
				vo.setConfigFileName(configFileName);
			}

			voList.add(vo);
		}

		return voList;
	}

	/**
	 * [Step] 建立FTP/TFTP連線
	 * @param fileUtils
	 * @param _mode
	 * @param ciVO
	 * @return
	 * @throws Exception
	 */
	private FileUtils connect2FileServer(FileUtils fileUtils, ConnectionMode _mode, ConfigInfoVO ciVO) throws Exception {
		switch (_mode) {
			case FTP:
				// By FTP
				fileUtils = new FtpFileUtils();
				fileUtils.connect(ciVO.getFtpIP(), ciVO.getFtpPort());
				break;

			case TFTP:
				// By TFTP
				fileUtils = new TFtpFileUtils();
				fileUtils.connect(ciVO.gettFtpIP(), ciVO.gettFtpPort());

			default:
				break;
		}

		return fileUtils;
	}

	/**
	 * 刪除本機檔案
	 * @param ciVO
	 * @return
	 * @throws FileOperationException
	 */
	private boolean deleteLocalFile(String filePath) throws FileOperationException {
		try {
			Path path = Paths.get(filePath);
			if (Files.isRegularFile(path) & Files.isReadable(path) & Files.isExecutable(path)) {
				Files.delete(path);
				return true;

			} else {
				throw new FileOperationException("[組態檔內容相同，但無法刪除檔案] >> " + filePath);
			}

		} catch (Exception e) {
			throw new FileOperationException("[組態檔內容相同，但刪除檔案過程異常] >> " + e.toString());
		}
	}

	/**
	 * 移動本機檔案
	 * @param ciVO
	 * @return
	 * @throws FileOperationException
	 */
//	private boolean moveLocalFile(ConfigInfoVO ciVO) throws FileOperationException {
//		try {
//			final String source = Env.TFTP_LOCAL_ROOT_DIR_PATH.concat(Env.TFTP_TEMP_DIR_PATH).concat(ciVO.getFileFullName());
//			final String target = Env.TFTP_LOCAL_ROOT_DIR_PATH.concat(ciVO.getConfigFileDirPath()).concat(ciVO.getFileFullName());
//
//			final Path sourcePath = Paths.get(source);
//			final Path targetPath = Paths.get(target);
//
//			if (Files.isRegularFile(sourcePath) & Files.isReadable(sourcePath) & Files.isExecutable(sourcePath)) {
//				Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
//				return true;
//
//			} else {
//				throw new FileOperationException("[組態檔內容相同，但無法移動檔案] >> " + source);
//			}
//
//		} catch (Exception e) {
//			throw new FileOperationException("[組態檔內容相同，但移動檔案過程異常] >> " + e.toString());
//		}
//	}

	/**
	 * [Step] 登入FTP
	 * @param fileUtils
	 * @param ciVO
	 * @throws Exception
	 */
	private void login2FileServer(FileUtils fileUtils, ConfigInfoVO ciVO) throws Exception {
		fileUtils.login(
				StringUtils.isBlank(ciVO.getFtpAccount()) ? Env.FTP_LOGIN_ACCOUNT : ciVO.getFtpAccount(),
						StringUtils.isBlank(ciVO.getFtpPassword()) ? Env.FTP_LOGIN_PASSWORD : ciVO.getFtpPassword()
				);
	}

	/**
	 * [Step] 輸出檔案透過FTP落地保存
	 * @param ftpUtils
	 * @param ciVOList
	 * @throws Exception
	 */
	private void upload2FTP(FileUtils ftpUtils, List<ConfigInfoVO> ciVOList) throws Exception {

		String remoteFileDirPath = "";
		if (ciVOList != null && !ciVOList.isEmpty()) {
			// 8-4. 上傳檔案
			for (ConfigInfoVO ciVO : ciVOList) {
				remoteFileDirPath = ciVO.getRemoteFileDirPath();

				if (Env.ENABLE_REMOTE_BACKUP_USE_TODAY_ROOT_DIR) {
					SimpleDateFormat sdf = new SimpleDateFormat(Env.DIR_PATH_OF_CURRENT_DATE_FORMAT);
					remoteFileDirPath = sdf.format(new Date()).concat(Env.FTP_DIR_SEPARATE_SYMBOL).concat(remoteFileDirPath);
				}

				// 8-3. 移動作業目錄至指定的裝置
				ftpUtils.changeDir(remoteFileDirPath, true);

				String configFileName = new String(ciVO.getConfigFileName().getBytes("UTF-8"),"iso-8859-1");

				ftpUtils.uploadFiles(
						configFileName,
						IOUtils.toInputStream(ciVO.getConfigContent(), Constants.CHARSET_UTF8)
						);
			}
		}
	}

	/**
	 * [Step] 寫入DB資料
	 * @param ciVOList
	 * @param jobTrigger
	 */
	private void record2DB4ConfigVersionInfo(List<ConfigInfoVO> ciVOList, boolean jobTrigger) {
		for (ConfigInfoVO ciVO : ciVOList) {
			configDAO.insertConfigVersionInfo(CommonUtils.composeModelEntityByConfigInfoVO(ciVO, jobTrigger));
		}
	}

	/**
	 * [Step] 從FTP/TFTP下載資料
	 * @param fileUtils
	 * @param vsVOs
	 * @param ciVO
	 * @param returnFileString
	 * @return
	 * @throws Exception
	 */
	private List<ConfigInfoVO> downloadFile(String actionType, FileUtils fileUtils, List<VersionServiceVO> vsVOs, ConfigInfoVO ciVO, boolean returnFileString) throws Exception {
		List<ConfigInfoVO> ciVOList = new ArrayList<>();

		ConfigInfoVO tmpVO = null;
		for (VersionServiceVO vsVO : vsVOs) {
			tmpVO = (ConfigInfoVO)ciVO.clone();
			tmpVO.setConfigFileDirPath(vsVO.getConfigFileDirPath());

			String remoteFileDirPath = vsVO.getRemoteFileDirPath();

			if (StringUtils.equals(actionType, Constants.ACTION_TYPE_RESTORE)) {
			  if (Env.ENABLE_LOCAL_BACKUP_USE_TODAY_ROOT_DIR) {
                  String yyyyMMdd = vsVO.getCreateYyyyMMdd();

                  if (StringUtils.isBlank(yyyyMMdd)) {
                      SimpleDateFormat sdf = new SimpleDateFormat(Env.DIR_PATH_OF_CURRENT_DATE_FORMAT);
                      yyyyMMdd = sdf.format(new Date());
                  }

                  remoteFileDirPath = yyyyMMdd.concat(Env.FTP_DIR_SEPARATE_SYMBOL).concat(remoteFileDirPath);
              }
			}

			tmpVO.setRemoteFileDirPath(remoteFileDirPath);
			tmpVO.setFileFullName(vsVO.getFileFullName());

			if (returnFileString) {
				final String fileContent = fileUtils.downloadFilesString(tmpVO);
				tmpVO.setConfigContent(fileContent);

			} else {
				final List<String> fileContentList = fileUtils.downloadFiles(tmpVO);
				tmpVO.setConfigContentList(fileContentList);
			}

			tmpVO.setConfigFileName(vsVO.getFileFullName());

			ciVOList.add(tmpVO);
		}
		return ciVOList;
	}

	@Override
	public StepServiceVO doScript(ConnectionMode connectionMode, String deviceListId,
	        Map<String, String> deviceInfo, ScriptInfo scriptInfo, List<Map<String, String>> varMapList,
	        boolean sysTrigger, String triggerBy, String triggerRemark, String reason) {

		StepServiceVO processVO = new StepServiceVO();

		ProvisionServiceVO psMasterVO = new ProvisionServiceVO();
		ProvisionServiceVO psDetailVO = new ProvisionServiceVO();
		ProvisionServiceVO psStepVO = new ProvisionServiceVO();
		ProvisionServiceVO psRetryVO;
		ProvisionServiceVO psDeviceVO;

		// 定義retry次數，預設為1表示不retry
		final int RETRY_TIMES = StringUtils.isNotBlank(Env.RETRY_TIMES) ? Integer.parseInt(Env.RETRY_TIMES) : 1;
		// 紀錄當前執行回合數
		int round = 1;

		/*
		 * Provision_Log_Master & Step
		 */
		final String userName = sysTrigger ? triggerBy : SecurityUtil.getSecurityUser() != null
		                                                       ? SecurityUtil.getSecurityUser().getUsername() : Constants.SYS;
		final String userIp = sysTrigger ? Env.USER_IP_JOB : SecurityUtil.getSecurityUser() != null
		                                                        ? SecurityUtil.getSecurityUser().getUser().getIp() : Constants.UNKNOWN;

		psDetailVO.setUserName(userName);
		psDetailVO.setUserIp(userIp);
		psDetailVO.setBeginTime(new Date());
		psDetailVO.setRemark(sysTrigger ? triggerRemark : null);
		psStepVO.setBeginTime(new Date());

		processVO.setActionBy(userName);
		processVO.setActionFromIp(userIp);
		processVO.setBeginTime(new Date());

		ConnectUtils connectUtils = null;			// 連線裝置物件
		List<String> outputList = null;

		boolean stopRetry = false;
		boolean retryRound = false;
		while (round <= RETRY_TIMES && !stopRetry) {
			try {
				Step[] steps = null;
				ConnectionMode deviceMode = null;

				/*
				 * 此method是在執行指定的腳本，此處是在決定連線設備的方式
				 * 先看呼叫端是否有給定connectionMode；沒有的話則依照[組態備份]所設定的連線方式
				 */
				if (connectionMode != null) {
					deviceMode = connectionMode;

				} else {
					switch (Env.DEFAULT_DEVICE_CONFIG_BACKUP_MODE) {
    					case Constants.DEVICE_CONFIG_BACKUP_MODE_TELNET_SSH_FTP:
    					case Constants.DEVICE_CONFIG_BACKUP_MODE_TFTP_SSH_TFTP:
    					case Constants.DEVICE_CONFIG_BACKUP_MODE_FTP_SSH_FTP:
    						deviceMode = ConnectionMode.SSH;
    						break;

    					case Constants.DEVICE_CONFIG_BACKUP_MODE_TFTP_TELNET_TFTP:
    					case Constants.DEVICE_CONFIG_BACKUP_MODE_FTP_TELNET_FTP:
    						deviceMode = ConnectionMode.TELNET;
    						break;
    				}
				}

				steps = Env.SEND_SCRIPT;

				String scriptInfoId = scriptInfo.getScriptInfoId();
                String scriptCode = scriptInfo.getScriptCode();
                
                boolean doAlternativeProcess = false;   // 決定是否要跑替代方案腳本(目前 for IP封鎖 > MAC封鎖<替代>)
                Map<String, Object> alternativeProcessParaMap = new HashMap<>();    // 紀錄替代方案腳本流程所需參數

				List<ScriptServiceVO> scripts = null;       // 存放 Action 腳本指令
				List<ScriptServiceVO> checkScripts = null;  // 存放 Check 腳本指令
				ConfigInfoVO ciVO = null;					// 裝置相關設定資訊VO

				for (Step _step : steps) {
					switch (_step) {
						case LOAD_SPECIFIED_SCRIPT:
							try {
								psStepVO.setScriptCode(scriptCode);
								processVO.setScriptCode(scriptCode);

								scripts = loadSpecifiedScript(scriptInfoId, scriptCode, varMapList, scripts, Constants.SCRIPT_MODE_ACTION);

								/*
								 * Provision_Log_Step => for 後續寫入供裝紀錄table使用
								 */
								final String scriptName = (scripts != null && !scripts.isEmpty()) ? scripts.get(0).getScriptName() : null;
								psStepVO.setRemark(scriptName);

								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("讀取腳本資料時失敗 [ 錯誤代碼: LOAD_SPECIFIED_SCRIPT ]");
							}

						case LOAD_SPECIFIED_CHECK_SCRIPT:
						    try {
						        checkScripts = loadSpecifiedScript(scriptInfoId, scriptCode, varMapList, scripts, Constants.SCRIPT_MODE_CHECK);
						        break;

						    } catch (Exception e) {
                                log.error(e.toString(), e);
                                throw new ServiceLayerException("讀取檢核腳本資料時失敗 [ 錯誤代碼: LOAD_SPECIFIED_CHECK_SCRIPT ]");
                            }

						case FIND_DEVICE_CONNECT_INFO:
							try {
								ciVO = findDeviceConfigInfo(ciVO, deviceListId, deviceInfo, deviceMode);
								ciVO.setTimes(String.valueOf(round));

								/*
								 * Provision_Log_Device => for 後續寫入供裝紀錄table使用
								 */
								if (!retryRound) {
									psDeviceVO = new ProvisionServiceVO();
									psDeviceVO.setDeviceListId(deviceListId);

									if (deviceInfo != null) {
										String deviceInfoStr = deviceInfo.get(Constants.DEVICE_IP) + Env.COMM_SEPARATE_SYMBOL + Constants.DEVICE_NAME;
										psDeviceVO.setDeviceInfoStr(deviceInfoStr);
									}

									psDeviceVO.setOrderNum(1);
									psStepVO.getDeviceVO().add(psDeviceVO); // add DeviceVO to StepVO

									processVO.setDeviceName(ciVO.getDeviceName());
									processVO.setDeviceIp(ciVO.getDeviceIp());
								}

								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("取得設備資訊時失敗 [ 錯誤代碼: FIND_DEVICE_CONNECT_INFO ]");
							}

						case FIND_DEVICE_LOGIN_INFO:
							try {
								findDeviceLoginInfo(ciVO, deviceListId, ciVO.getGroupId(), ciVO.getDeviceId());
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("取得設備登入帳密設定時失敗 [ 錯誤代碼: FIND_DEVICE_LOGIN_INFO]");
							}

						case CONNECT_DEVICE:
							try {
								connectUtils = connect2Device(connectUtils, ciVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("設備連線失敗 [ 錯誤內容: " + e.getMessage() + " ]");
							}

						case LOGIN_DEVICE:
							try {
								login2Device(connectUtils, ciVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("登入設備失敗 [ 錯誤代碼: LOGIN_DEVICE ]");
							}

						case SEND_COMMANDS:
							try {
								outputList = sendCmds(connectUtils, scripts, ciVO, processVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("派送設備命令失敗 [ 錯誤內容: " + e.getMessage() + " ]");
							}
							
							
						case CHECK_PROVISION_RESULT:
						    /*
						     * Y191115, Ken
						     * 目前針對[苗栗教網]IP封鎖流程，可能發生 by IP封鎖無效，必須改採 by MAC封鎖
						     * 因此需檢核 IP封鎖 的結果正不正常 >> 透過 Ping IP 來檢查
						     */
							try {
							    // 判斷當前供裝是否為IP封鎖，是的話才做檢核
							    // TODO:未來應該應用在所有供裝
								//20200113, Owen 苗栗 Issue#60, #61 取消學校端(非admin)功能, 中心端(admin)皆封鎖MAC
								List<String> scriptList = new ArrayList<>();
								// 若使用者為管理者，多查出中心端的IP控制腳本
								if (Env.DELIVERY_IP_OPEN_BLOCK_SCRIPT_CODE_4_ADMIN != null) {
									scriptList.addAll(Env.DELIVERY_IP_OPEN_BLOCK_SCRIPT_CODE_4_ADMIN);
								}
							    if (scriptList.contains(scriptCode) && scriptInfo.getDeviceModel().equalsIgnoreCase(Env.DELIVERY_MAC_BLOCK_WITH_IP_DEVICE_MODEL))  {

							        // 初始化檢核工具 >> IP供裝檢核採用 PingUtils
//							        ProvisionUtils pingUtils = new PingUtils();

							        // 定義IP封鎖腳本中「IP_Address」的變數名稱 for 待會取得此次封鎖的IP
							        String ipAddressVarKey = Env.KEY_VAL_OF_IP_ADDR_WITH_IP_OPEN_BLOCK;

							        // 要檢核的 IP
							        String pingIp = null;

							        /*
							         * 迴圈跑供裝參數List
							         * 目前[IP封鎖]一次只能封鎖1個IP，但還是先保留彈性 for 未來如果有要強化一次可封鎖多筆
							         */
//							        int totalPingTimes = 10;                     // 最大Ping嘗試次數(預設值)
//							        long intervalOfPing = 1000;                  // Ping間隔時間(預設值)
//							        int timesOfPing = 1;                         // Ping次數
//							        int timesOfPingFailedContinuous = 0;         // 連續Ping失敗次數
//							        int targetTimesOfPingFailedContinuous = 3;   // 連續Ping失敗次數目標值(預設值)
//
//							        // 若參數檔(sys_config_setting)有設定值，則以參數檔的數值為準
//							        if (Env.PROVISION_CHECK_PARA_4_TOTAL_PING_TIMES != null) {
//							            totalPingTimes = Env.PROVISION_CHECK_PARA_4_TOTAL_PING_TIMES;
//							        }
//							        if (Env.PROVISION_CHECK_PARA_4_INTERVAL_OF_PING != null) {
//							            intervalOfPing = new Long(Env.PROVISION_CHECK_PARA_4_INTERVAL_OF_PING);
//                                    }
//							        if (Env.PROVISION_CHECK_PARA_4_TARGET_TIMES_OF_PING_FAILED_CONTINUOUS != null) {
//							            targetTimesOfPingFailedContinuous = Env.PROVISION_CHECK_PARA_4_TARGET_TIMES_OF_PING_FAILED_CONTINUOUS;
//                                    }

//							        boolean stopPing = false;

//							        Map<String, String> paraMap = null;
							        List<String> ipList = new ArrayList<>();     // 紀錄 Ping 可通的 IP 清單，for 後續走替代方案時使用

							        for (Map<String, String> varMap : varMapList) {
							            pingIp = varMap.get(ipAddressVarKey);

							            if (StringUtils.isBlank(pingIp)) {
							                continue;
							            }

							            // 準備呼叫 PingUtils 所需參數
//							            paraMap = new HashMap<>();
//							            paraMap.put(Constants.PARA_IP_ADDRESS, pingIp);
//
//							            boolean pingResult = false;  // 每一次 Ping 的結果
//							            boolean checkResult = false; // 紀錄此 IP 最終 Ping 結果 (true=不通，供裝成功；false=可通，須走替代方案)

							            /*
							             * 預設 Ping 失敗判斷邏輯 = 連續 Ping 不通達三次以上
							             * 若其中發生 Ping 可通狀況，則歸零重新計算，必須「連續」Ping 不通達到目標值才認定真的不通
							             * 最大嘗試 Ping 次數 = totalPingTimes
							             * 兩次 Ping 間格時間  = intervalOfPing
							             */
//							            while (timesOfPing <= totalPingTimes && !stopPing) {
//							                // 檢核此 IP 是否還 ping 的通
//	                                        pingResult = pingUtils.doCheck(paraMap);
//
//	                                        if (!pingResult) {
//	                                            timesOfPingFailedContinuous++; // Ping 失敗 >> 次數+1
//
//	                                            if (timesOfPingFailedContinuous >= targetTimesOfPingFailedContinuous) {
//	                                                // 連續 Ping 失敗達目標值 >> 中止 Ping
//	                                                stopPing = true;
//	                                                checkResult = true;
//	                                            }
//
//	                                        } else {
//	                                            // Ping 成功 >> 次數歸零
//	                                            timesOfPingFailedContinuous = 0;
//	                                        }
//
//	                                        try {
//	                                            Thread.sleep(intervalOfPing);
//	                                        } catch (InterruptedException ie) {
//                                                log.error(ie.toString(), ie);
//                                            }
//
//	                                        timesOfPing++;
//							            }

//							            if (!checkResult) {
							                // IP 最終還是 Ping 的通，記錄下 IP for 後續替代方案使用
							                ipList.add(pingIp);
							                doAlternativeProcess = true;   // 檢核多筆 IP Ping 結果，只要有其中1筆 Ping 可通就必須走替代方案
//							            }
							        }

							        if (doAlternativeProcess && ipList != null && !ipList.isEmpty()) {
							            // 將需要走替代方案的 IP 清單記錄到 MAP for 後續流程使用
							            alternativeProcessParaMap.put(Constants.PARA_IP_ADDRESS, ipList);
							        }
							    }

								break;

							} catch (Exception e) {
								// 執行到此步驟時，前面對設備的供裝流程已經跑完，因此到此執行失敗時就不再進行retry，否則會對設備做多次供裝
								stopRetry = true;
								log.error(e.toString(), e);
								throw new ServiceLayerException("檢核供裝派送結果時失敗 [ 錯誤代碼: CHECK_PROVISION_RESULT ]");
							}
							
						case CLOSE_DEVICE_CONNECTION:
							try {
								closeDeviceConnection(connectUtils);
								break;

							} catch (Exception e) {
								// 執行到此步驟時，前面對設備的供裝流程已經跑完，因此到此執行失敗時就不再進行retry，否則會對設備做多次供裝
								stopRetry = true;
								log.error(e.toString(), e);
								throw new ServiceLayerException("關閉與設備間連線時失敗 [ 錯誤代碼: CLOSE_DEVICE_CONNECTION ]");
							}

						case WRITE_SPECIFY_LOG:
                            try {
                            	String scriptType = scriptInfo.getScriptType().getScriptTypeCode();
                            	if(StringUtils.equalsAnyIgnoreCase(scriptType, ScriptType.PORT_.toString())
                            			||StringUtils.equalsAnyIgnoreCase(scriptType, ScriptType.IP_.toString())
                            			||StringUtils.equalsAnyIgnoreCase(scriptType, ScriptType.IP_CTR_.toString())
                            			||StringUtils.equalsAnyIgnoreCase(scriptType, ScriptType.MAC_.toString())
                            			||StringUtils.equalsAnyIgnoreCase(scriptType, ScriptType.BIND_.toString())) {
                            		
                            		//針對[苗栗教網]IP封鎖流程 如果是特定model封鎖則封鎖原因待預設值
                                	List<String> scriptList = new ArrayList<>();
                                	// 若使用者為管理者，多查出中心端的IP控制腳本
    								if (Env.DELIVERY_IP_OPEN_BLOCK_SCRIPT_CODE_4_ADMIN != null) {
    									scriptList.addAll(Env.DELIVERY_IP_OPEN_BLOCK_SCRIPT_CODE_4_ADMIN);
    								}
    					            // 判斷當前執行的供裝是否為IP封鎖腳本
                                    if (scriptList.contains(scriptCode) && scriptInfo.getDeviceModel().equalsIgnoreCase(Env.DELIVERY_MAC_BLOCK_WITH_IP_DEVICE_MODEL)
                                    		&& StringUtils.isBlank(reason)) {
                                    	reason = "資安通報";
                                    }
                                    writeSpecifyLog(ciVO, scriptCode, varMapList, reason);
                            	}
                            	
                                break;

                            } catch (Exception e) {
                            	// 執行到此步驟時，前面對設備的供裝流程已經跑完，因此到此執行失敗時就不再進行retry，否則會對設備做多次供裝
								stopRetry = true;
                                log.error(e.toString(), e);
                                throw new ServiceLayerException("寫入指定LOG table時失敗 [ 錯誤代碼: WRITE_SPECIFY_LOG ]");
                            }

						case DO_SPECIFIED_ALTERNATIVE_ACTION:
						    //TODO
						    try {
						        // 先判斷是否有要走替代方案
						        if (doAlternativeProcess) {
						        	//20200113, Owen 苗栗 Issue#60, #61 取消學校端(非admin)功能, 中心端(admin)皆封鎖MAC
									List<String> scriptList = new ArrayList<>();
									// 若使用者為管理者，多查出中心端的IP控制腳本
									if (Env.DELIVERY_IP_OPEN_BLOCK_SCRIPT_CODE_4_ADMIN != null) {
										scriptList.addAll(Env.DELIVERY_IP_OPEN_BLOCK_SCRIPT_CODE_4_ADMIN);
									}
						            // 判斷當前執行的供裝是否為IP封鎖腳本
	                                if (scriptList.contains(scriptCode) && scriptInfo.getDeviceModel().equalsIgnoreCase(Env.DELIVERY_MAC_BLOCK_WITH_IP_DEVICE_MODEL)) {

	                                	//scriptType == 1 封鎖， 2 開通
	                                	String scriptTypeName = scriptInfo.getUndoScriptCode() == null ?"開通":"封鎖";
	                                	
	                                    // 取出需要走替代方案的 IP 清單
	                                    List<String> ipList = (List<String>)alternativeProcessParaMap.get(Constants.PARA_IP_ADDRESS);

	                                    // 查找此次供裝設備是哪個GroupID
	                                    String groupId = null;
	                                    DeviceList dlEntity = deviceDAO.findDeviceListByDeviceListId(deviceListId);

	                                    if (dlEntity != null) {
	                                    	groupId = dlEntity.getGroupId();
	
		                                    // 查找[MAC]腳本
	                                        ScriptInfo macBlockScriptInfo = null;
	                                        try {
	                                            macBlockScriptInfo = scriptService.loadDefaultScriptInfo(dlEntity.getDeviceModel(), ScriptType.MAC_.toString(), scriptInfo.getUndoScriptCode());
	
	                                        } catch (Exception e) {
	                                            log.error(e.toString(), e);
	                                        }
	
	                                        if (macBlockScriptInfo == null) {
	                                            // 查不到預設[MAC]腳本無法執行後續流程
	                                        	log.error("[IP"+scriptTypeName+"]欲一併執行[MAC"+scriptTypeName+"]時失敗 >> 查無預設[MAC"+scriptTypeName+"]腳本");
	                                            throw new ServiceLayerException("[IP"+scriptTypeName+"]欲一併執行[MAC"+scriptTypeName+"]時失敗 >> 查無預設[MAC"+scriptTypeName+"]腳本");
	                                        }
	
		                                    List<ModuleArpTable> arpTableList = null;
		                                    Map<String, String> macBlockVarMap = null;
		                                    List<Map<String, String>> macBlockVarMapList = null;
		                                    String macAddr = null;
	
		                                    List<String> ipNotExistInArpTableList = null;      // 紀錄 IP 不存在於 ARP_TABLE 的清單
		                                    List<String> ipExecuteMacBlockFailedList = null;   // 紀錄 IP 在執行 MAC 封鎖時失敗的清單
	
		                                    // 迴圈跑 IP 清單，執行替代方案流程
		                                    for (String ip : ipList) {
		                                        /*
		                                         * Step 1. 查找要封裝的IP是否存在於ARP_TABLE，不存在則結束並提示訊息
		                                         * [資料表: module_arp_table]
		                                         */
		                                        arpTableList = ipMappingDAO.findModuleArpTable(groupId, null, ip, 1);
	
		                                        // 若 IP 不存在於 ARP_TABLE 內，則註記此 IP 供裝失敗
		                                        if (arpTableList == null || (arpTableList != null && arpTableList.isEmpty())) {
		                                            // 記錄下哪一筆IP不存在於ARP_TABLE
		                                            log.error("[IP"+scriptTypeName+"]欲一併執行[MAC"+scriptTypeName+"]時失敗 >> IP不存在於ARP_TABLE (GroupId: " + groupId + ", IP: " + ip + ")");
	
		                                            if (ipNotExistInArpTableList == null) {
		                                                ipNotExistInArpTableList = new ArrayList<>();
		                                            }
	
		                                            ipNotExistInArpTableList.add(ip);
	
		                                            // 繼續執行下一筆
		                                            continue;
		                                        }
	
		                                        /*
		                                         * Step 2. IP存在，取得對應的MAC並執行MAC封鎖供裝
		                                         */
		                                        macAddr = arpTableList.get(0).getMacAddr();
	
	                                            //TODO
		                                        // 準備[MAC]所需參數
		                                        String paraNameOfMac = Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK;
	
		                                        macBlockVarMap = new HashMap<>();
		                                        macBlockVarMap.put(paraNameOfMac, macAddr);
	
		                                        macBlockVarMapList = new ArrayList<>();
		                                        macBlockVarMapList.add(macBlockVarMap);
	
		                                        ProvisionServiceVO subMasterVO = new ProvisionServiceVO();
		                                        StepServiceVO subProcessVO = null;
		                                        boolean macBlockSuccess = true;
		                                        String subReason = "[IP"+scriptTypeName+"]後一併執行[MAC"+scriptTypeName+"] [IP: " + ip + "]";
		                                        try {
		                                        	subMasterVO.setLogMasterId(UUID.randomUUID().toString());
		                                        	subMasterVO.setBeginTime(new Date());
		                                        	subMasterVO.setUserName(sysTrigger ? triggerBy : SecurityUtil.getSecurityUser().getUsername());
		                                            
		                                            // 呼叫執行[MAC]腳本
		                                            subProcessVO = doScript(
					                                                    connectionMode,
					                                                    deviceListId,
					                                                    deviceInfo,
					                                                    macBlockScriptInfo,    // 替換成[MAC]的Script_Info
					                                                    macBlockVarMapList,    // 替換成[MAC]的腳本參數
					                                                    sysTrigger,
					                                                    triggerBy,
					                                                    triggerRemark,
					                                                    subReason);
		                                            
		                                            subMasterVO.getDetailVO().addAll(subProcessVO.getPsVO().getDetailVO());
	
		                                        } catch (Exception e) {
		                                            log.error("[IP"+scriptTypeName+"]後一併執行[MAC"+scriptTypeName+"] 時失敗 >> " + e.toString());
		                                            log.error(e.toString(), e);
	
	                                                if (ipExecuteMacBlockFailedList == null) {
	                                                    ipExecuteMacBlockFailedList = new ArrayList<>();
	                                                }
	
	                                                ipExecuteMacBlockFailedList.add(ip);
	                                                macBlockSuccess = false;
		                                        }
		                                        
		                                        if (subProcessVO == null || (subProcessVO != null && !subProcessVO.isSuccess())) {
		                                        	macBlockSuccess = false;
		                                        }
		                                        
		                                        String msg = "";
		                                        String[] args = null;
	                                            if (macBlockSuccess) {
	                                                msg = "供裝成功";
	                                            } else {
	                                                msg = "供裝失敗";
	                                            }
		                                        
		                                        subMasterVO.setEndTime(new Date());
		                                        subMasterVO.setResult(CommonUtils.converMsg(msg, args));
		                                        subMasterVO.setReason(subReason);
		                                        provisionService.insertProvisionLog(subMasterVO);
		                                    }
	
		                                    // 若有紀錄執行失敗的 IP 清單，則於此組合錯誤訊息
		                                    StringBuffer errorMsg = null;
		                                    if (ipNotExistInArpTableList != null || ipExecuteMacBlockFailedList != null) {
		                                        errorMsg = new StringBuffer();
		                                        int idx = 1;
	
		                                        if (ipNotExistInArpTableList != null && !ipNotExistInArpTableList.isEmpty()) {
		                                            errorMsg.append("[IP"+scriptTypeName+"]後一併執行[MAC"+scriptTypeName+"]時失敗 >> IP不存在於ARP_TABLE")
		                                                    .append("<br>");
	
		                                            for (String ip : ipNotExistInArpTableList) {
		                                                errorMsg.append("IP<").append(idx).append(">: ").append(ip).append("<br>");
		                                                idx++;
		                                            }
		                                        }
	
		                                        if (ipExecuteMacBlockFailedList != null && !ipExecuteMacBlockFailedList.isEmpty()) {
	                                                errorMsg.append("[IP"+scriptTypeName+"]後一併執行[MAC"+scriptTypeName+"] >> [MAC"+scriptTypeName+"]供裝失敗")
	                                                        .append("<br>");
	
	                                                for (String ip : ipExecuteMacBlockFailedList) {
	                                                    errorMsg.append("IP<").append(idx).append(">: ").append(ip).append("<br>");
	                                                    idx++;
	                                                }
	                                            }
		                                    }

	                                    // 若錯誤訊息不為空，則拋出Exception
//	                                    if (errorMsg != null) {
//	                                        throw new ServiceLayerException(errorMsg.toString());
//	                                    }
	                                    }
	                                }
						        }

                                break;

                            } catch (ServiceLayerException sle) {
                            	// 執行到此步驟時，前面對設備的供裝流程已經跑完，因此到此執行失敗時就不再進行retry，否則會對設備做多次供裝
								stopRetry = true;
                                log.error(sle.toString(), sle);
                                throw sle;
                            	
                            } catch (Exception e) {
                            	// 執行到此步驟時，前面對設備的供裝流程已經跑完，因此到此執行失敗時就不再進行retry，否則會對設備做多次供裝
								stopRetry = true;
                                log.error(e.toString(), e);
                                throw new ServiceLayerException("執行替代方案流程時失敗 [ 錯誤代碼: DO_SPECIFIED_ALTERNATIVE_ACTION ]");
                            }

						default:
							break;
					}
				}

				processVO.setCmdOutputList(outputList);
				processVO.setSuccess(true);
				processVO.setResult(Result.SUCCESS);
				break;

			} catch (ServiceLayerException sle) {
				/*
				 * Provision_Log_Retry => for 後續寫入供裝紀錄table使用(執行失敗，紀錄歷程)
				 */
				psRetryVO = new ProvisionServiceVO();
				psRetryVO.setResult(Result.ERROR.toString());
				psRetryVO.setMessage(sle.getMessage());
				psRetryVO.setRetryOrder(round);
				psStepVO.getRetryVO().add(psRetryVO); // add RetryVO to StepVO

				processVO.setSuccess(false);
				processVO.setResult(Result.ERROR);
				processVO.setMessage(sle.getMessage());
				processVO.setCmdProcessLog(sle.getMessage());

				retryRound = true;
				round++;

				if (connectUtils != null) {
					try {
						connectUtils.disconnect();
					} catch (Exception e1) {
						log.error(e1.toString(), e1);
					}
				}
			} catch (Exception e) {
				log.error(e.toString(), e);

				/*
				 * Provision_Log_Retry => for 後續寫入供裝紀錄table使用(執行失敗，紀錄歷程)
				 */
				psRetryVO = new ProvisionServiceVO();
				psRetryVO.setResult(Result.ERROR.toString());
				psRetryVO.setMessage(e.getMessage());
				psRetryVO.setRetryOrder(round);
				psStepVO.getRetryVO().add(psRetryVO); // add RetryVO to StepVO

				processVO.setSuccess(false);
				processVO.setResult(Result.ERROR);
				processVO.setMessage(e.getMessage());
				processVO.setCmdProcessLog(e.getMessage());

				retryRound = true;
				round++;

				if (connectUtils != null) {
					try {
						connectUtils.disconnect();
					} catch (Exception e1) {
						log.error(e1.toString(), e1);
					}
				}
			}
		}

		/*
		 * Provision_Log_Step
		 */
		psStepVO.setEndTime(new Date());
		psStepVO.setResult(processVO.getResult().toString());
		psStepVO.setMessage(processVO.getMessage());
		psStepVO.setRetryTimes(round-1);
		psStepVO.setProcessLog(processVO.getCmdProcessLog());

		/*
		 * Provision_Log_Detail
		 */
		psDetailVO.setEndTime(new Date());
		psDetailVO.setResult(processVO.getResult().toString());
		psDetailVO.setMessage(processVO.getMessage());
		psDetailVO.getStepVO().add(psStepVO); // add StepVO to DetailVO

		psMasterVO.getDetailVO().add(psDetailVO); // add DetailVO to MasterVO
		processVO.setPsVO(psMasterVO);

		processVO.setEndTime(new Date());
		processVO.setRetryTimes(round);

		return processVO;
	}

	@Override
    public StepServiceVO doCommands(ConnectionMode connectionMode, String deviceListId,
            Map<String, String> deviceInfo, List<ScriptServiceVO> cmdList, boolean sysTrigger,
            String triggerBy, String triggerRemark) {
	    /*
	     * 此Method不讀取腳本資料，由前端呼叫功能準備好要執行的指令(cmdList)依序執行
	     */
	    StepServiceVO processVO = new StepServiceVO();

        ProvisionServiceVO psMasterVO = new ProvisionServiceVO();
        ProvisionServiceVO psDetailVO = new ProvisionServiceVO();
        ProvisionServiceVO psStepVO = new ProvisionServiceVO();
        ProvisionServiceVO psRetryVO;
        ProvisionServiceVO psDeviceVO;

        // 定義retry次數，預設為1表示不retry
        final int RETRY_TIMES = StringUtils.isNotBlank(Env.RETRY_TIMES) ? Integer.parseInt(Env.RETRY_TIMES) : 1;
        // 紀錄當前執行回合數
        int round = 1;

        /*
         * Provision_Log_Master & Step => for 後續寫入供裝紀錄table使用
         */
        final String userName = sysTrigger ? triggerBy : SecurityUtil.getSecurityUser() != null
                                                            ? SecurityUtil.getSecurityUser().getUsername() : Constants.SYS;
        final String userIp = sysTrigger ? Env.USER_IP_JOB : SecurityUtil.getSecurityUser() != null
                                                                ? SecurityUtil.getSecurityUser().getUser().getIp() : Constants.UNKNOWN;

        psDetailVO.setUserName(userName);
        psDetailVO.setUserIp(userIp);
        psDetailVO.setBeginTime(new Date());
        psDetailVO.setRemark(sysTrigger ? triggerRemark : null);
        psStepVO.setBeginTime(new Date());

        processVO.setActionBy(userName);
        processVO.setActionFromIp(userIp);
        processVO.setBeginTime(new Date());

        ConnectUtils connectUtils = null;           // 連線裝置物件
        List<String> outputList = null;

        boolean retryRound = false;
        while (round <= RETRY_TIMES) {
            try {
                Step[] steps = null;
                ConnectionMode deviceMode = connectionMode;

                steps = Env.SEND_COMMANDS;

                List<ScriptServiceVO> scripts = cmdList;
                ConfigInfoVO ciVO = null;                   // 裝置相關設定資訊VO

                for (Step _step : steps) {
                    switch (_step) {
                        case FIND_DEVICE_CONNECT_INFO:
                            try {
                                ciVO = findDeviceConfigInfo(ciVO, deviceListId, deviceInfo, deviceMode);
                                ciVO.setTimes(String.valueOf(round));

                                /*
                                 * Provision_Log_Device => for 後續寫入供裝紀錄table使用
                                 */
                                if (!retryRound) {
                                    psDeviceVO = new ProvisionServiceVO();
                                    psDeviceVO.setDeviceListId(deviceListId);

                                    if (deviceInfo != null) {
                                        String deviceInfoStr = deviceInfo.get(Constants.DEVICE_IP) + Env.COMM_SEPARATE_SYMBOL + Constants.DEVICE_NAME;
                                        psDeviceVO.setDeviceInfoStr(deviceInfoStr);
                                    }

                                    psDeviceVO.setOrderNum(1);
                                    psStepVO.getDeviceVO().add(psDeviceVO); // add DeviceVO to StepVO

                                    processVO.setDeviceName(ciVO.getDeviceName());
                                    processVO.setDeviceIp(ciVO.getDeviceIp());
                                }

                                break;

                            } catch (Exception e) {
                                log.error(e.toString(), e);
                                throw new ServiceLayerException("取得設備資訊時失敗 [ 錯誤代碼: FIND_DEVICE_CONNECT_INFO ]");
                            }

                        case FIND_DEVICE_LOGIN_INFO:
                            try {
                                findDeviceLoginInfo(ciVO, deviceListId, ciVO.getGroupId(), ciVO.getDeviceId());
                                break;

                            } catch (Exception e) {
                                log.error(e.toString(), e);
                                throw new ServiceLayerException("取得設備登入帳密設定時失敗 [ 錯誤代碼: FIND_DEVICE_LOGIN_INFO]");
                            }

                        case CONNECT_DEVICE:
                            try {
                                connectUtils = connect2Device(connectUtils, ciVO);
                                break;

                            } catch (Exception e) {
                                log.error(e.toString(), e);
                                throw new ServiceLayerException("設備連線失敗 [ 錯誤內容: " + e.getMessage() + " ]");
                            }

                        case LOGIN_DEVICE:
                            try {
                                login2Device(connectUtils, ciVO);
                                break;

                            } catch (Exception e) {
                                log.error(e.toString(), e);
                                throw new ServiceLayerException("登入設備失敗 [ 錯誤代碼: LOGIN_DEVICE ]");
                            }

                        case SEND_COMMANDS:
                            try {
                                outputList = sendCmds(connectUtils, scripts, ciVO, processVO);
                                break;

                            } catch (Exception e) {
                                log.error(e.toString(), e);
                                throw new ServiceLayerException("派送設備命令失敗 [ 錯誤內容: " + e.getMessage() + " ]");
                            }

                        case CHECK_PROVISION_RESULT:
                            try {
                                break;

                            } catch (Exception e) {
                                log.error(e.toString(), e);
                                throw new ServiceLayerException("檢核供裝派送結果時失敗 [ 錯誤代碼: CHECK_PROVISION_RESULT ]");
                            }

                        case CLOSE_DEVICE_CONNECTION:
                            try {
                                closeDeviceConnection(connectUtils);
                                break;

                            } catch (Exception e) {
                                log.error(e.toString(), e);
                                throw new ServiceLayerException("關閉與設備間連線時失敗 [ 錯誤代碼: CLOSE_DEVICE_CONNECTION ]");
                            }

                        default:
                            break;
                    }
                }

                processVO.setCmdOutputList(outputList);
                processVO.setSuccess(true);
                processVO.setResult(Result.SUCCESS);
                break;

            } catch (ServiceLayerException sle) {
                /*
                 * Provision_Log_Retry => for 後續寫入供裝紀錄table使用(執行失敗，紀錄歷程)
                 */
                psRetryVO = new ProvisionServiceVO();
                psRetryVO.setResult(Result.ERROR.toString());
                psRetryVO.setMessage(sle.toString());
                psRetryVO.setRetryOrder(round);
                psStepVO.getRetryVO().add(psRetryVO); // add RetryVO to StepVO

                processVO.setSuccess(false);
                processVO.setResult(Result.ERROR);
                processVO.setMessage(sle.toString());
                processVO.setCmdProcessLog(sle.getMessage());

                retryRound = true;
                round++;

                if (connectUtils != null) {
                    try {
                        connectUtils.disconnect();
                    } catch (Exception e1) {
                        log.error(e1.toString(), e1);
                    }
                }
            } catch (Exception e) {
                log.error(e.toString(), e);

                /*
                 * Provision_Log_Retry => for 後續寫入供裝紀錄table使用(執行失敗，紀錄歷程)
                 */
                psRetryVO = new ProvisionServiceVO();
                psRetryVO.setResult(Result.ERROR.toString());
                psRetryVO.setMessage(e.toString());
                psRetryVO.setRetryOrder(round);
                psStepVO.getRetryVO().add(psRetryVO); // add RetryVO to StepVO

                processVO.setSuccess(false);
                processVO.setResult(Result.ERROR);
                processVO.setMessage(e.toString());
                processVO.setCmdProcessLog(e.getMessage());

                retryRound = true;
                round++;

                if (connectUtils != null) {
                    try {
                        connectUtils.disconnect();
                    } catch (Exception e1) {
                        log.error(e1.toString(), e1);
                    }
                }
            }
        }

        /*
         * Provision_Log_Step
         */
        psStepVO.setEndTime(new Date());
        psStepVO.setResult(processVO.getResult().toString());
        psStepVO.setMessage(processVO.getMessage());
        psStepVO.setRetryTimes(round-1);
        psStepVO.setProcessLog(processVO.getCmdProcessLog());

        /*
         * Provision_Log_Detail
         */
        psDetailVO.setEndTime(new Date());
        psDetailVO.setResult(processVO.getResult().toString());
        psDetailVO.setMessage(processVO.getMessage());
        psDetailVO.getStepVO().add(psStepVO); // add StepVO to DetailVO

        psMasterVO.getDetailVO().add(psDetailVO); // add DetailVO to MasterVO
        processVO.setPsVO(psMasterVO);

        processVO.setEndTime(new Date());
        processVO.setRetryTimes(round);

        return processVO;
    }

	/**
	 * 組態還原流程
	 */
	@Override
	public StepServiceVO doRestoreStep(RestoreMethod restoreMethod, String restoreType, StepServiceVO stepServiceVO, String triggerBy, String reason) {
		StepServiceVO retVO = new StepServiceVO();

		/*
		 * for 供裝紀錄使用
		 */
		ProvisionServiceVO psMasterVO = new ProvisionServiceVO();
		ProvisionServiceVO psDetailVO = new ProvisionServiceVO();
		ProvisionServiceVO psStepVO = new ProvisionServiceVO();
		ProvisionServiceVO psDeviceVO;

		// 定義retry次數，預設為1表示不retry
		final int RETRY_TIMES = StringUtils.isNotBlank(Env.RETRY_TIMES) ? Integer.parseInt(Env.RETRY_TIMES) : 1;
		// 紀錄當前執行回合數
		int round = 1;

		/*
		 * Provision_Log_Master & Step => for 後續寫入供裝紀錄table使用
		 */
		final String userName = triggerBy;
		final String userIp = SecurityUtil.getSecurityUser() != null ? SecurityUtil.getSecurityUser().getUser().getIp() : Constants.UNKNOWN;

		psDetailVO.setUserName(userName);
		psDetailVO.setUserIp(userIp);
		psDetailVO.setBeginTime(new Date());
		psDetailVO.setRemark(reason);
		psStepVO.setBeginTime(new Date());

		retVO.setActionBy(userName);
		retVO.setActionFromIp(userIp);
		retVO.setBeginTime(new Date());

		ConnectUtils connectUtils = null;													// 連線裝置物件
		final String deviceListId = stepServiceVO.getDeviceListId();						// 要還原的設備 Device_List.device_list_id
		final String restoreVersionId = stepServiceVO.getRestoreVersionId();				// 要還原的版本號 Config_Version_Info.version_id
		List<String> restoreContentList = stepServiceVO.getRestoreContentList();		    // 要還原的腳本內容，若此參數有給值則只還原給定的內容部分；否則則依照給定的版本號內容做還原
		final boolean __NEED_DOWNLOAD_RESTORE_FILE__ = restoreContentList != null ? true : false;	// 依照是否有傳入要還原的內容(restoreContentList)，決定是否需要下載要還原的組態備份檔

		boolean retryRound = false;
		while (round <= RETRY_TIMES) {
			try {
				Step[] steps = null;					// 指定還原的步驟
				ConnectionMode deviceMode = null;		// 指定連線裝置的模式
				ConnectionMode fileServerMode = null;	// 指定連線FileServer模式 (for 下載組態備份檔)

				switch (Env.DEFAULT_DEVICE_CONFIG_RESTORE_MODE) {
					case Constants.DEVICE_CONFIG_RESTORE_MODE_SSH_FTP:
						deviceMode = ConnectionMode.SSH;
						fileServerMode = ConnectionMode.FTP;
						break;

					case Constants.DEVICE_CONFIG_RESTORE_MODE_SSH_TFTP:
						deviceMode = ConnectionMode.SSH;
						fileServerMode = ConnectionMode.TFTP;
						break;

					case Constants.DEVICE_CONFIG_RESTORE_MODE_TELNET_FTP:
						deviceMode = ConnectionMode.TELNET;
						fileServerMode = ConnectionMode.FTP;
						break;

					case Constants.DEVICE_CONFIG_RESTORE_MODE_TELNET_TFTP:
						deviceMode = ConnectionMode.TELNET;
						fileServerMode = ConnectionMode.TFTP;
						break;
				}

				/*
				 * 還原方式:
				 * (1) CLI >> by 命令逐行派送
				 * (2) FTP >> 在設備上下命令透過FTP上抓組態檔還原到設備 (copy ftp:<帳號>:<密碼>@<要還原的組態檔路徑>)
				 * (3) TFTP >> 在設備上下命令透過TFTP上抓組態檔還原到設備 (copy tftp:<帳號>:<密碼>@<要還原的組態檔路徑>)
				 */
				switch (restoreMethod) {
					case CLI:
						steps = Env.RESTORE_BY_CLI;
						break;

					case FTP:
						steps = Env.RESTORE_BY_FTP;
						break;

					case TFTP:
						steps = Env.RESTORE_BY_TFTP;
						break;

					case LOCAL:
					    steps = Env.RESTORE_BY_LOCAL;
					    break;
				}

				List<ScriptServiceVO> scripts = null;

				ConfigInfoVO ciVO = null;					// 裝置相關設定資訊VO
				List<String> outputList = null;				// 命令Output內容List
				List<ConfigInfoVO> configInfoList = null;	// 組態檔內容List
				FileUtils fileUtils = null;					// 連線FileServer物件
				List<VersionServiceVO> vsVOs = null;		// 要還原的版本資訊 for 檔案下載

				for (Step _step : steps) {

					switch (_step) {
						// 取得要還原的版本號相關資訊
						case GET_VERSION_INFO:
							try {
								if (!__NEED_DOWNLOAD_RESTORE_FILE__) {
									break;
								}

								vsVOs = getVersionInfo(
											new String[]{restoreVersionId}
										);

								if (vsVOs != null && !vsVOs.isEmpty() && ciVO != null) {
									VersionServiceVO vsVO = vsVOs.get(0);
									String remoteFileDirPath = vsVO.getRemoteFileDirPath();
									String configFileName = vsVO.getFileFullName();

									String ftpConfigPath = remoteFileDirPath.concat(Env.FTP_DIR_SEPARATE_SYMBOL).concat(configFileName);
									String deviceFlashConfigPath = Env.DEFAULT_DEVICE_FLASH_DIR_PATH.concat(Env.FTP_DIR_SEPARATE_SYMBOL).concat(configFileName);
									
									ciVO.setFtpFilePath(ftpConfigPath);						// 要還原的組態檔案在FTP上的完整路徑
									ciVO.settFtpFilePath(ftpConfigPath);						// 要還原的組態檔案在FTP上的完整路徑
									ciVO.setDeviceFlashConfigPath(deviceFlashConfigPath);	// 要還原的組態檔案傳到設備後的儲存路徑
								}

								break;

							} catch (ServiceLayerException sle) {
								throw new ServiceLayerException("取得要還原的組態版本資訊時失敗 [ 錯誤代碼: GET_VERSION_INFO ]");

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("取得要還原的組態版本資訊時失敗 [ 錯誤代碼: GET_VERSION_INFO ]");
							}

						// 依照前面傳入的資訊設定要還原的版本號
						case SET_LOCAL_VERSION_INFO:
						    try {
						        final String configPath = stepServiceVO.getRestoreVersionConfigPath();
						        final String imagePath = stepServiceVO.getRestoreVersionImagePath();

                                ciVO.setDeviceFlashConfigPath(configPath);   // 要還原的組態檔案在設備的儲存路徑
                                ciVO.setDeviceFlashImagePath(imagePath);     // 要還原的Image檔案在設備的儲存路徑

						    } catch (Exception e) {
						        log.error(e.toString(), e);
                                throw new ServiceLayerException("設定要還原的組態版本資訊時失敗 [ 錯誤代碼: SET_LOCAL_VERSION_INFO ]");
						    }

						    break;

						// 連線至組態檔放置的 FTP / TFTP
						case CONNECT_FILE_SERVER_4_DOWNLOAD:
							try {
								if (!__NEED_DOWNLOAD_RESTORE_FILE__) {
									break;
								}

								fileUtils = connect2FileServer(fileUtils, fileServerMode, ciVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("連線至 File Server 時失敗 [ 錯誤代碼: CONNECT_FILE_SERVER_4_DOWNLOAD ]");
							}

						// 登入 FTP
						case LOGIN_FILE_SERVER_4_DOWNLOAD:
							try {
								if (!__NEED_DOWNLOAD_RESTORE_FILE__) {
									break;
								}
								login2FileServer(fileUtils, ciVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("登入 File Server 時失敗 [ 錯誤代碼: LOGIN_FILE_SERVER_4_DOWNLOAD ]");
							}

						// 從 FTP / TFTP 上下載要還原的版本檔案內容
						case DOWNLOAD_FILE:
							try {
								if (!__NEED_DOWNLOAD_RESTORE_FILE__) {
									break;
								}

								configInfoList = downloadFile(Constants.ACTION_TYPE_RESTORE, fileUtils, vsVOs, ciVO, false);
								ciVO.setConfigContentList(
										configInfoList.get(0).getConfigContentList());
								break;

							} catch (ServiceLayerException sle) {
								throw new ServiceLayerException("從 File Server 下載檔案時失敗 [ 錯誤代碼: DOWNLOAD_FILE ]");

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("從 File Server 下載檔案時失敗 [ 錯誤代碼: DOWNLOAD_FILE ]");
							}

						// 關閉與 FTP / TFTP 連線
						case CLOSE_FILE_SERVER_CONNECTION:
							try {
								closeFileServerConnection(fileUtils);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("關閉與 File Server 間連線時失敗 [ 錯誤代碼: CLOSE_FILE_SERVER_CONNECTION ]");
							}

						// 依組態內容設定(Config_Content_Setting)處理要還原的版本內容
						case PROCESS_CONFIG_CONTENT_SETTING:
							try {
								restoreContentList = processConfigContentSetting(null, restoreType, ciVO);
								ciVO.setConfigContentList(restoreContentList);
								break;

							} catch (ServiceLayerException sle) {
								throw new ServiceLayerException("處理要還原的組態檔內容時失敗 [ 錯誤代碼: PROCESS_CONFIG_CONTENT_SETTING ]");

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("處理要還原的組態檔內容時失敗 [ 錯誤代碼: PROCESS_CONFIG_CONTENT_SETTING ]");
							}

						// 取得組態還原預設腳本
						case LOAD_DEFAULT_SCRIPT:
							try {
								scripts = loadDefaultScript(deviceListId, ScriptType.RES_);
								/*
								 * Provision_Log_Step
								 */
								final String scriptName = (scripts != null && !scripts.isEmpty()) ? scripts.get(0).getScriptName() : null;
								final String scriptCode = (scripts != null && !scripts.isEmpty()) ? scripts.get(0).getScriptCode() : null;
								
								psStepVO.setScriptCode(scriptCode);
								psStepVO.setRemark(scriptName);

								retVO.setScriptCode(scriptCode);

								break;

							} catch (ServiceLayerException sle) {
								throw new ServiceLayerException("讀取腳本資料時失敗 [ 錯誤代碼: LOAD_DEFAULT_SCRIPT ]");

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("讀取腳本資料時失敗 [ 錯誤代碼: LOAD_DEFAULT_SCRIPT ]");
							}

						// 取得要還原的目標設備相關連線資訊
						case FIND_DEVICE_CONNECT_INFO:
							try {
								ciVO = findDeviceConfigInfo(ciVO, deviceListId, null, deviceMode);
								ciVO.setTimes(String.valueOf(round));

								/*
								 * Provision_Log_Device
								 */
								if (!retryRound) {
									psDeviceVO = new ProvisionServiceVO();
									psDeviceVO.setDeviceListId(deviceListId);
									psDeviceVO.setOrderNum(1);
									psStepVO.getDeviceVO().add(psDeviceVO); // add DeviceVO to StepVO

									retVO.setDeviceName(ciVO.getDeviceName());
									retVO.setDeviceIp(ciVO.getDeviceIp());
								}

								break;

							} catch (ServiceLayerException sle) {
								throw new ServiceLayerException("取得設備資訊時失敗 [ 錯誤代碼: FIND_DEVICE_CONNECT_INFO ]");

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("取得設備資訊時失敗 [ 錯誤代碼: FIND_DEVICE_CONNECT_INFO ]");
							}

						// 取得要還原的目標設備登入資訊
						case FIND_DEVICE_LOGIN_INFO:
							try {
								findDeviceLoginInfo(ciVO, deviceListId, ciVO.getGroupId(), ciVO.getDeviceId());
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("取得設備登入帳密設定時失敗 [ 錯誤代碼: FIND_DEVICE_LOGIN_INFO ]");
							}

						// 連線至要還原的目標設備
						case CONNECT_DEVICE:
							try {
								connectUtils = connect2Device(connectUtils, ciVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("設備連線失敗 [ 錯誤內容: " + e.getMessage() + " ]");
							}

						// 登入要還原的目標設備
						case LOGIN_DEVICE:
							try {
								login2Device(connectUtils, ciVO);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("登入設備失敗 [ 錯誤代碼: LOGIN_DEVICE ]");
							}

						// 依腳本內容進行命令派送
						case SEND_COMMANDS:
							try {
								outputList = sendCmds(connectUtils, scripts, ciVO, retVO);
								break;

							} catch (ServiceLayerException sle) {
							    throw new ServiceLayerException("派送設備命令失敗 [ 錯誤內容: " + sle.getMessage() + " ]");

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("派送設備命令失敗 [ 錯誤內容: " + e.getMessage() + " ]");
							}

						// 關閉與設備的連線
						case CLOSE_DEVICE_CONNECTION:
							try {
								closeDeviceConnection(connectUtils);
								break;

							} catch (Exception e) {
								log.error(e.toString(), e);
								throw new ServiceLayerException("關閉與設備間連線時失敗 [ 錯誤代碼: CLOSE_DEVICE_CONNECTION ]");
							}

						default:
							break;
					}
				}


				retVO.setSuccess(true);
				retVO.setResult(Result.SUCCESS);
				break;

			} catch (Exception e) {
				log.error(e.toString(), e);
				round++;
			}
		}

		/*
		 * Provision_Log_Step
		 */
		psStepVO.setEndTime(new Date());
		psStepVO.setResult(retVO.getResult().toString());
		psStepVO.setMessage(retVO.getMessage());
		psStepVO.setRetryTimes(round-1);
		psStepVO.setProcessLog(retVO.getCmdProcessLog());

		/*
		 * Provision_Log_Detail
		 */
		psDetailVO.setEndTime(new Date());
		psDetailVO.setResult(retVO.getResult().toString());
		psDetailVO.setMessage(retVO.getMessage());
		psDetailVO.getStepVO().add(psStepVO); // add StepVO to DetailVO

		psMasterVO.getDetailVO().add(psDetailVO); // add DetailVO to MasterVO
		retVO.setPsVO(psMasterVO);

		retVO.setEndTime(new Date());
		retVO.setRetryTimes(round);

		return retVO;
	}

	/**
	 * 取得組態檔版本相關資訊
	 * @param versionIds
	 * @return
	 * @throws ServiceLayerException
	 */
	private List<VersionServiceVO> getVersionInfo(String[] versionIds) throws ServiceLayerException {
		List<VersionServiceVO> retList = new ArrayList<>();

		VersionServiceVO vsVO;
		for (String versionId : versionIds) {
			vsVO = new VersionServiceVO();
			vsVO.setQueryVersionId(versionId);
			List<VersionServiceVO> entities = null;

			try {
				entities = versionService.findVersionInfo(vsVO, null, null);

			} catch (ServiceLayerException e) {
				log.error(e.toString(), e);
				throw new ServiceLayerException("取得要還原的版本號資訊時發生異常 >> versionId: [" + versionId + "] [" + e.toString() + "]");
			}

			if (entities != null && !entities.isEmpty()) {
				retList.addAll(entities);
			}
		}

		return retList;
	}

	/**
	 * 處理實際要還原的組態內容，依照 Config_Content_Setting設定的條件做處理
	 * 若設定內有 【action = 「+」】
	 * @param settingType
	 * @param configInfoVO
	 * @return
	 */
	@Override
    public List<String> processConfigContentSetting(ConfigVO configVO, String settingType, ConfigInfoVO configInfoVO) throws ServiceLayerException {
		final String systemVersion = configInfoVO.getSystemVersion();
		final String deviceName = configInfoVO.getDeviceEngName();
		final String deviceListId = configInfoVO.getDeviceListId();

		final List<String> configContentList = configInfoVO.getConfigContentList();

		boolean hasPositiveSetting = false;
		List<ConfigVO> settings = null;
		try {
			// Step 1. 取得設定
		    if (configVO == null) {
		        configVO = configService.findConfigContentSetting(null, settingType, systemVersion, deviceName, deviceListId);
		    }

			settings = configVO.getConfigVOList();

			// 確認有無正向設定
			if (settings != null && !settings.isEmpty()) {
				for (ConfigVO cVO : settings) {
					if (StringUtils.equals(cVO.getAction(), Constants.CONTENT_SETTING_ACTION_ADD)) {
						configVO.setHasPositiveSetting(true);
						break;
					}
				}
			}
			hasPositiveSetting = configVO.isHasPositiveSetting();

			// Step 2. 依設定逐行處理組態檔內容，取得最終實際要還原的

			if (settings != null && !settings.isEmpty()) {
				return runConfigAndSettingCheck(configContentList, settings, hasPositiveSetting);

			} else {
				return configContentList;
			}

		} catch (ServiceLayerException sle) {
			throw sle;

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException(e.toString());
		}
	}

	/**
	 * 此方法在逐行讀取組態檔內容，並參照table設定[config_content_setting]決定哪些內容要取出(+)或刪除(-)
	 * 目前主要for[亞太HeNBGW/ePDG]的組態還原，因為不能將整份檔案copy覆蓋到運行的組態，只能將需要還原的片段取出並透過供裝的方式修改運行中組態的內容
	 *
	 * table設定的方式說明:
	 * (1) content_start_regex = 定義起始的組態內容正則表示式，當比對符合時便開始進行判斷此區段是否要取出or刪除流程
	 * (2) content_layer = 定義上述表示式，必須要符合哪個層級
	 *     [ex]:
	 *          「interface]←這word可能出現在組態檔內容不同位置
	 *          config
	 *            interface ....<a>
	 *            exit
	 *
	 *            some section
	 *              interface ....<b>
	 *              exit
	 *            exit
	 *          exit
	 *
	 *          => interface出現在<a>這行時，前面有[兩個]空白並依照參數設定的[1個層級是由幾個空白組成](假設設定值為2)得出此段是第1個layer (2/2=1)
	 *          => interface出現在<b>這行時，前面有[四個]空白，得出此段是第2個layer (4/2=2)
	 *          [1個層級是由幾個空白組成] = Env.CONFIG_CONTENT_ONE_LAYER_EQUAL_TO_WHITE_SPACE_COUNT
	 *
	 *      ※ 會有此設定是因為相同表示式可能出現在不同區段內，必須更明確指定我們要的是哪一塊
	 * (3) content_end_regex = 定義該區段結尾的指令文字為何
	 * (4) action = 定義是要取出(+) or 刪除(-)
	 * @param configContentList
	 * @param settings
	 * @param hasPositiveSetting
	 * @return
	 * @throws ServiceLayerException
	 */
	private List<String> runConfigAndSettingCheck(List<String> configContentList, List<ConfigVO> settings, boolean hasPositiveSetting) throws ServiceLayerException {
		List<String> retList = new ArrayList<>();

		try {
			Pattern pattern = null;
			Matcher matcher = null;

			Map<Integer, MatchVO> layerMatchMap = new HashMap<>();

			boolean doLayerCheck = false;
			for (String content : configContentList) {
			//------------------- Config內容逐行比對迴圈 START -------------------//

				content = new String(content.getBytes("UTF-8"));
				int currentLayer = chkCurrentContentLineLayer(content);	// 當前行的層級

				if (content.startsWith("interface")) {
				    System.out.println("COME ON, LET's GO !!");
				}

				/*
				 * Step 1. 判斷此行層級，確認是否為同一區塊內容&是否納入 OR 是新的區塊開始
				 */
				if (layerMatchMap.containsKey(currentLayer)) {
					/*
					 * 如果MAP內已存在相同層級紀錄:
					 * (1) 若當前行內容為 exit，表示是區塊結束 >> 判斷該區塊是否要納入(skip)，不納入則直接跳至下一行
					 * (2) 若當前行內容不是 exit，表示是新的內容 >> 需做後續比對
					 */
					String trimContent = content.trim();
					if (trimContent.equalsIgnoreCase("#exit") || trimContent.equalsIgnoreCase("exit") || trimContent.equals("!")) {
						MatchVO currentLayerMatchVO = layerMatchMap.get(currentLayer);

						if (currentLayerMatchVO.isSkip()) {
							continue;

						} else {
						    if (!trimContent.equals("!")) {
						        retList.add("exit"); // 區塊結束需下exit指令
						    }
							layerMatchMap.remove(currentLayer);	// 區塊已結束，將MAP紀錄清除
							continue;
						}

					} else {
						layerMatchMap.remove(currentLayer);	// 區塊已結束，將MAP紀錄清除
						doLayerCheck = true;
					}

				} else {
					/*
					 * MAP不存在相同層級紀錄，表示是第一次跑到此層級的行數 >> 需做後續比對
					 */
					doLayerCheck = true;
				}

				boolean needDoMatch = true;

				/*
				 * Step 2. 確認此行內容是否需要做設定比對
				 */
				if (doLayerCheck) {
					/*
					 * 如果當前層級不是第一層，則往前看上一層的比對結果: (preLayerMatchVO)
					 * (1) 若前一層已標記要跳過 (skip)，則同區塊內的內層都一律跳過，並且標記當前階層也需跳過 for後續內層判斷
					 */
					MatchVO preLayerMatchVO = null;
					if (currentLayer > Env.CONFIG_CONTENT_TOP_LAYER_NUM) {
						preLayerMatchVO = layerMatchMap.get(currentLayer - 1);

						if (preLayerMatchVO.isSkip()) {
							needDoMatch = false;

							MatchVO mVO = new MatchVO();
							mVO.setSkip(true);	// 標記當前層需跳過
							layerMatchMap.put(currentLayer, mVO);

							continue;
						}
					}

					/*
					 * Step 3. 針對此行內容進行設定比對，確認是否納入或跳過
					 */
					if (needDoMatch) {
						boolean match = false;	//是否有符合的設定

						String sAction = "";
						String remark = "";
						for (ConfigVO setting : settings) {
						//------------------- 內容篩選設定迴圈 START -------------------//

							int sLayer = setting.getContentLayer();
							String sStartRegex = setting.getContentStartRegex();
							sAction = setting.getAction();
							remark = setting.getRemark();

							/*
							 * Step 1. 先比對此行的階層(layer)是否符合設定
							 * Step 2. 階層符合後再比對內容表示式是否吻合(regex) >> 符合設定後則跳至下一行，符合的設定只跑一次 (前面查詢設定時已從範圍小到大排序)
							 */
							Integer noLimit = Env.CONFIG_CONTENT_NO_LIMIT_LAYER_NUM;
							if (sLayer == noLimit || (sLayer != noLimit && sLayer == currentLayer)) {
								pattern = Pattern.compile(sStartRegex);
						        matcher = pattern.matcher(content);

								matcher.reset();
								match = matcher.find();

								matcher.reset();

						        if (match) {
						        	break;

						        } else {
						        	continue;
						        }

							} else {
								// 階層不符則跳至下一筆設定比對
								continue;
							}

						//------------------- 內容篩選設定迴圈 END -------------------//
						}

						MatchVO mVO = new MatchVO();
						if (match) {
							if (sAction.equals(Constants.CONTENT_SETTING_ACTION_ADD)) {
								/*
								 * 判斷有無設定特殊處理(remark):
								 * (1)若有設定「no」，表示在執行這一行命令前須先下「no ....」將先前的設定刪除 (目前 for "content"使用)
								 */
								if (StringUtils.isNotBlank(remark)) {
									switch (remark) {
										case "no":
											retList.add("no " + content);
											break;
									}
								}

								retList.add(content);

							} else if (sAction.equals(Constants.CONTENT_SETTING_ACTION_SUBSTRACT)) {
								mVO.setSkip(true);	// 如果此行比對成功的設定是「-」(去除)，則將此階層標記為跳過(skip)
							}

						} else {
							if (preLayerMatchVO != null && !preLayerMatchVO.isSkip()) {
								// 如果有上一階層且上一層沒有標記要跳過，則底層的內容除非有比對到設定且是設定要跳過之外，其他則應納入
								retList.add(content);

							} else {
								if (!hasPositiveSetting) {
									// 如果設定皆比對不成功 且 沒有正向設定，則納入此行
									retList.add(content);

								} else {
									// 如果設定皆比對不成功 但 有正向設定，則將此階層標記為跳過(skip)
									mVO.setSkip(true);
								}
							}
						}

						mVO.setMatch(match);
						mVO.setAction(sAction);

						layerMatchMap.put(currentLayer, mVO);
					}
				}

			//------------------- Config內容逐行比對迴圈 END -------------------//
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException(e.toString());
		}

		return retList;
	}

	/**
	 * 計算此行組態內容是第幾個層級(layer)
	 * @param content
	 * @return
	 */
	private int chkCurrentContentLineLayer(String content) {
		int whiteSpaceCount = 0;
		for (int i=0; i<content.length(); i++) {
			char ch = content.charAt(i);

			if (Character.isWhitespace(ch)) {
				whiteSpaceCount++;

			} else {
				break;
			}
		}

		int oneLayerWhiteSpaceCount =
				Env.CONFIG_CONTENT_ONE_LAYER_EQUAL_TO_WHITE_SPACE_COUNT != null
						? Env.CONFIG_CONTENT_ONE_LAYER_EQUAL_TO_WHITE_SPACE_COUNT
						: 1;
		int layer = whiteSpaceCount / oneLayerWhiteSpaceCount;
		return layer;
	}

	/**
	 * 檢核設備SSH連線是否enable
	 */
    @Override
    public boolean chkSSHIsEnable(ConfigInfoVO ciVO) {
        boolean sshEnable = false;

        ConnectUtils connectUtils = null; // 連線裝置物件
        try {
            ciVO.setConnectionMode(ConnectionMode.SSH);
            connectUtils = connect2Device(connectUtils, ciVO);
            sshEnable = true;

        } catch (Exception e) {
            log.error(e.toString(), e);

            sshEnable = false;

        } finally {
            try {
                if (connectUtils != null) {
                    connectUtils.disconnect();
                }

            } catch (Exception e) {
                log.error(e.toString(), e);
            }

            connectUtils = null;
        }
        return sshEnable;
    }
}
