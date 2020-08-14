package com.cmap.utils.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.ConfigVersionInfo;
import com.cmap.security.SecurityUtil;
import com.cmap.service.vo.CommonServiceVO;
import com.cmap.service.vo.ConfigInfoVO;
import com.cmap.service.vo.ScriptServiceVO;

public class CommonUtils {
	private static Logger log = LoggerFactory.getLogger(CommonUtils.class);

	public static String composeConfigFileName(ConfigInfoVO vo, int seqNo) {

		String fileName = StringUtils.isNotBlank(Env.BACKUP_FILENAME_FORMAT) ? Env.BACKUP_FILENAME_FORMAT : "";

		/*
		 * [dName]-[dIP]-[configType]-[date]-[seqNo].[extName]
		 * Ex: 1F_Center-192.168.1.1-RUNNING_CONFIG-201808210914-001.cfg
		 */
		// 裝置ID: [Device_Id]
		if (fileName.indexOf(Constants.DIR_PATH_DEVICE_ID) != -1) {
			fileName = StringUtils.replace(fileName, Constants.DIR_PATH_DEVICE_ID, vo.getDeviceId());
		}
		// 裝置名稱: [Device_Name]
		if (fileName.indexOf(Constants.DIR_PATH_DEVICE_NAME) != -1) {
			fileName = StringUtils.replace(fileName, Constants.DIR_PATH_DEVICE_NAME, vo.getDeviceEngName());
		}
		// 裝置IP: [Device_Ip]
		if (fileName.indexOf(Constants.DIR_PATH_DEVICE_IP) != -1) {
			fileName = StringUtils.replace(fileName, Constants.DIR_PATH_DEVICE_IP, vo.getDeviceIp());
		}
		// 裝置名稱: [Device_Name]
		if (fileName.indexOf(Constants.DIR_PATH_DEVICE_SYSTEM) != -1) {
			fileName = StringUtils.replace(fileName, Constants.DIR_PATH_DEVICE_SYSTEM, vo.getSystemVersion());
		}
		// 組態檔類別: running/startup
		if (fileName.indexOf(Constants.DIR_PATH_CONFIG_TYPE) != -1) {
			fileName = StringUtils.replace(fileName, Constants.DIR_PATH_CONFIG_TYPE, (StringUtils.isBlank(vo.getConfigType()) ? Env.COMM_SEPARATE_SYMBOL : vo.getConfigType()));
		}
		// 日期戳記: YYYYMMDDHH24MI
		if (fileName.indexOf(Constants.DIR_PATH_DATE) != -1) {
			fileName = StringUtils.replace(fileName, Constants.DIR_PATH_DATE, Env.BACKUP_FILENAME_DATE_FORMAT.format(new Date()));
		}
		// 流水號: [seqNo]
		if (fileName.indexOf(Constants.DIR_PATH_SEQ_NO) != -1) {
			fileName = StringUtils.replace(
					fileName, Constants.DIR_PATH_SEQ_NO,
					StringUtils.leftPad(String.valueOf(seqNo), StringUtils.isNotBlank(Env.BACKUP_FILENAME_SEQ_NO_LENGTH) ? Integer.valueOf(Env.BACKUP_FILENAME_SEQ_NO_LENGTH) : 3, "0"));
		}
		// 副檔名: [系統參數]
		if (fileName.indexOf(Constants.DIR_PATH_EXT_NAME) != -1) {
			fileName = StringUtils.replace(fileName, Constants.DIR_PATH_EXT_NAME, Env.CONFIG_FILE_EXTENSION_NAME);
		}

		return fileName;
	}

	public static String composeConfigDirPath(ConfigInfoVO vo, boolean isTFTP) {

		String dirPath = "";
		if(isTFTP) {
			dirPath.concat(Env.TFTP_SERVER_AT_LOCAL ? "" :  Env.TFTP_TEMP_DIR_PATH);
		}else {
			dirPath.concat(Env.FTP_SERVER_AT_LOCAL ? "" : Env.FTP_TEMP_DIR_PATH);
		}
		dirPath.concat(StringUtils.isNotBlank(Env.BACKUP_DIR_FORMAT) ? Env.BACKUP_DIR_FORMAT : "");

		/*
		 *[dName]-[dIP]-[date]
		 * Ex: 1F_Center-192.168.1.1-RUNNING_CONFIG-201808210914
		 */
		// 裝置ID: [Device_Id]
		if (dirPath.indexOf(Constants.DIR_PATH_DEVICE_ID) != -1) {
			dirPath = StringUtils.replace(dirPath, Constants.DIR_PATH_DEVICE_ID, vo.getDeviceId());
		}
		// 裝置名稱: [Device_Name]
		if (dirPath.indexOf(Constants.DIR_PATH_DEVICE_NAME) != -1) {
			dirPath = StringUtils.replace(dirPath, Constants.DIR_PATH_DEVICE_NAME, vo.getDeviceEngName());
		}
		// 裝置IP: [Device_Ip]
		if (dirPath.indexOf(Constants.DIR_PATH_DEVICE_IP) != -1) {
			dirPath = StringUtils.replace(dirPath, Constants.DIR_PATH_DEVICE_IP, vo.getDeviceIp());
		}
		// 日期戳記: YYYYMMDD
		if (dirPath.indexOf(Constants.DIR_PATH_DATE) != -1) {
			dirPath = StringUtils.replace(dirPath, Constants.DIR_PATH_DATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		}

		return dirPath;
	}
	
	public static ConfigVersionInfo composeModelEntityByConfigInfoVO(ConfigInfoVO ciVO, boolean jobTrigger) {
		String _user = "";

		try {
			_user = jobTrigger ? Constants.SYS : SecurityUtil.getSecurityUser().getUsername();

		} catch (NullPointerException npe) {
			_user = Constants.SYS;
		}

		ConfigVersionInfo entity = new ConfigVersionInfo(
				null
				,ciVO.getGroupId()
				,ciVO.getGroupName()
				,ciVO.getDeviceId()
				,ciVO.getDeviceName()
				,ciVO.getDeviceModel()
				,ciVO.getConfigType()
				,StringUtils.substring(ciVO.getConfigFileName(), 0, ciVO.getConfigFileName().lastIndexOf("."))
				,ciVO.getConfigFileDirPath()
				,ciVO.getConfigFileName()
				,Constants.DATA_MARK_NOT_DELETE
				,null
				,null
				,new Timestamp((new Date()).getTime())
				,_user
				,new Timestamp((new Date()).getTime())
				,_user
				);
		return entity;
	}

	public static String converMsg(String msg, Object... args) {
		if (args != null) {
			for (int i=0; i<args.length; i++) {
				String symbol = "{".concat(String.valueOf(i)).concat("}");
				msg = msg.replace(symbol, args[i].toString());
			}
		}

		return msg;
	}

	public static int calculateSpendTime(Date beginDate, Date endDate) {
		final long beginTime = beginDate.getTime();
		final long endTime = endDate.getTime();
		final long spendTimeInSeconds =
				TimeUnit.MILLISECONDS.toSeconds(endTime-beginTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime-beginTime));

		int spendTime = 0;

		try {
			spendTime = ((Long)spendTimeInSeconds).intValue();

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return spendTime;
	}

	public static int calculateSpendTime(Timestamp beginDate, Timestamp endDate) {
		final long beginTime = beginDate.getTime();
		final long endTime = endDate.getTime();
//		final long spendTimeInSeconds =
//				TimeUnit.MILLISECONDS.toSeconds(endTime-beginTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime-beginTime));
		final long spendTimeInSeconds =
				TimeUnit.MILLISECONDS.toSeconds(endTime-beginTime);

		int spendTime = 0;

		try {
			spendTime = ((Long)spendTimeInSeconds).intValue();

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return spendTime;
	}

	protected String replaceContentSign(CommonServiceVO csVO, ScriptServiceVO scriptVO, ConfigInfoVO configInfoVO, String cli) {
		String cmd = scriptVO.getScriptContent();

		if (StringUtils.isBlank(cmd)) {
			return cmd;
		}

		String remark = scriptVO.getRemark();

		if (cmd.contains(Env.CLI_VAR_ACT)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_ACT, configInfoVO.getAccount());
		}
		if (cmd.contains(Env.CLI_VAR_PWD)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_PWD, configInfoVO.getPassword());
		}
		if (cmd.contains(Env.CLI_VAR_ENABLE_PWD)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_ENABLE_PWD, configInfoVO.getEnablePassword());
		}
		if (cmd.contains(Env.CLI_VAR_TFTP_IP)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_TFTP_IP, configInfoVO.gettFtpIP());
		}
		
		if (cmd.contains(Env.CLI_VAR_TFTP_OUTPUT_FILE_PATH)) {
			String tFtpFilePath = "";
			if(StringUtils.equals(configInfoVO.getConfigFileDirPath(), File.separator) || StringUtils.equals(configInfoVO.getConfigFileDirPath(), Env.FTP_DIR_SEPARATE_SYMBOL)) {
				tFtpFilePath.concat(configInfoVO.getConfigFileName());
			}else {
				tFtpFilePath = configInfoVO.getConfigFileDirPath().concat(configInfoVO.getConfigFileName());
			}

			if (!Env.TFTP_SERVER_AT_LOCAL) {
				/*
				 * 若 TFTP Server 與 CMAP系統 不是架設在同一台主機上 (因TFTP Client端無法刪除 Server端檔案)
				 * 此時Config file上傳時會先上傳到 TFTP 的 temp 資料夾，待版本內容比對後才決定是否要將 temp 檔案移動到正確的 device 目錄下
				 * 因此，放置在 temp 資料夾內的檔案名稱須加上時間細數碼
				 */
				//				tFtpFilePath = tFtpFilePath.concat("-").concat(configInfoVO.getTempFileRandomCode());
				tFtpFilePath = configInfoVO.getTempFilePath().concat(configInfoVO.getConfigFileName());
			}

			if (StringUtils.isNotBlank(remark)) {
				tFtpFilePath = StringUtils.replace(tFtpFilePath, Env.COMM_SEPARATE_SYMBOL, remark);
			}
			
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_TFTP_OUTPUT_FILE_PATH, tFtpFilePath);
		}
		if (cmd.contains(Env.CLI_VAR_FTP_IP)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_FTP_IP, configInfoVO.getFtpIP());
		}
		if (cmd.contains(Env.CLI_VAR_FTP_URL)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_FTP_URL, configInfoVO.getFtpUrl());
		}
		if (cmd.contains(Env.CLI_VAR_FTP_LOGIN_ACT)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_FTP_LOGIN_ACT, configInfoVO.getFtpAccount());
		}
		if (cmd.contains(Env.CLI_VAR_FTP_LOGIN_PWD)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_FTP_LOGIN_PWD, configInfoVO.getFtpPassword());
		}
		if (cmd.contains(Env.CLI_VAR_FTP_OUTPUT_FILE_PATH)) {
			String ftpFilePath = "";
			if(StringUtils.equals(configInfoVO.getConfigFileDirPath(), File.separator) || StringUtils.equals(configInfoVO.getConfigFileDirPath(), Env.FTP_DIR_SEPARATE_SYMBOL)) {
				ftpFilePath.concat(configInfoVO.getConfigFileName());
			}else {
				ftpFilePath = configInfoVO.getConfigFileDirPath().concat(configInfoVO.getConfigFileName());
			}

			if (!Env.FTP_SERVER_AT_LOCAL) {
				/*
				 * 目前預設從設備copy檔案到FTP時都先放在temp目錄下
				 */
				ftpFilePath = configInfoVO.getTempFilePath();
			}

			if (StringUtils.isNotBlank(remark)) {
				ftpFilePath = StringUtils.replace(ftpFilePath, Env.COMM_SEPARATE_SYMBOL, remark);
			}

			cmd = StringUtils.replace(cmd, Env.CLI_VAR_FTP_OUTPUT_FILE_PATH, ftpFilePath.concat(configInfoVO.getConfigFileName()));
		}
		if (cmd.contains(Env.CLI_VAR_CMD_LIST)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_CMD_LIST, cli);
		}
		/*
		 * Y190318, APT ePDG & HeNBGW
		 * 組態還原指令應用
		 * copy ftp://[FTP_LOGIN_ACT]:[FTP_LOGIN_PWD]@[FTP_URL]/[FTP_FILE_PATH] [DEVICE_FLASH_PATH]
		 * boot system priority [PRIORITY] image [IMAGE_BIN] config [CONFIG_FILE]
		 */
		if (cmd.contains(Env.CLI_VAR_FTP_CONFIG_FILE_PATH)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_FTP_CONFIG_FILE_PATH, configInfoVO.getFtpFilePath());
		}
		if (cmd.contains(Env.CLI_VAR_DEVICE_FLASH_PATH)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_DEVICE_FLASH_PATH, configInfoVO.getDeviceFlashConfigPath());
		}
		if (cmd.contains(Env.CLI_VAR_DEVICE_IMAGE_PATH)) {
            cmd = StringUtils.replace(cmd, Env.CLI_VAR_DEVICE_IMAGE_PATH, configInfoVO.getDeviceFlashImagePath());
        }
		if (cmd.contains(Env.CLI_VAR_PRIORITY)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_PRIORITY, csVO.getBootInfoPriority());
		}
		if (cmd.contains(Env.CLI_VAR_IMAGE_BIN)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_IMAGE_BIN, csVO.getBootInfoImage());
		}
		if (cmd.contains(Env.CLI_VAR_CONFIG_FILE)) {
			cmd = StringUtils.replace(cmd, Env.CLI_VAR_CONFIG_FILE, csVO.getBootInfoConfig());
		}

		if (StringUtils.equals(Env.ENABLE_CMD_LOG, Constants.DATA_Y)) {
			log.info("cmd: " + cmd);
		}

		return cmd;
	}

	protected String replaceExpectedTerminalSymbol(String expectedTerminalSymbol, ConfigInfoVO configInfoVO) {
		if (StringUtils.isBlank(expectedTerminalSymbol)) {
			return "";
		}

		if (StringUtils.contains(expectedTerminalSymbol, Constants.EXPECTED_TERMINAL_SYMBOL_OF_DEVICE_NAME)) {
			String name = configInfoVO.getDeviceEngName();
			if(name.indexOf(".") > 1) {
				name = name.substring(0, name.indexOf("."));
			}
			expectedTerminalSymbol = StringUtils.replace(expectedTerminalSymbol, Constants.DIR_PATH_DEVICE_NAME, StringUtils.upperCase(name));
		}
		if (StringUtils.contains(expectedTerminalSymbol, Constants.EXPECTED_TERMINAL_SYMBOL_OF_CURRENT_YEAR)) {
			String currentYear = Constants.FORMAT_YYYY.format(new Date());
			expectedTerminalSymbol = StringUtils.replace(expectedTerminalSymbol, Constants.EXPECTED_TERMINAL_SYMBOL_OF_CURRENT_YEAR, currentYear);
		}

		return expectedTerminalSymbol;
	}

	/**
	 * 命令回傳結果內容處理
	 * @param content 原始回傳內容
	 * @param headCutCount 開頭往後要去除的行數
	 * @param tailCutCount 結尾往前要去除的行數
	 * @param splitBy 行段落用什麼符號分行
	 * @return
	 */
	protected String cutContent(String content, int headCutCount, int tailCutCount, String splitBy) {
		String retString = "";
		StringBuffer sb = null;
		try {
			final String[] contentArray = content.split(splitBy);

			if (contentArray != null && contentArray.length != 0) {
				int startAt = (headCutCount > contentArray.length) ? contentArray.length-1 : headCutCount;
				int endAt = ((contentArray.length-tailCutCount) < 0) ? contentArray.length-1 : (contentArray.length-tailCutCount);

				sb = new StringBuffer();
				for (int i=startAt; i<endAt; i++) {
					sb.append(contentArray[i])
					.append(System.lineSeparator());
				}

				retString = sb != null ? sb.toString() : retString;
			}

		} catch (Exception e) {
			throw e;
		}

		System.out.println("====================================================================");
		System.out.println(retString);
		System.out.println("====================================================================");
		return retString;
	}

	protected CommonServiceVO processOutput(CommonServiceVO csVO, ScriptServiceVO scriptVO, String cmdOutput, Object otherObj) throws Exception {
		final String scriptRemark = scriptVO.getRemark();

		if (StringUtils.equals(scriptRemark, Constants.SCRIPT_REMARK_OF_GET_BOOT_INFO)) {
			/*
			 * Case. ePDG / HeNBGW 備份還原
			 * 取得 show boot 中，目前 priority 最高的 entry 設定
			 * 目標取得目前的 prioriry 數值 & image 版號
			 * e.g.:
			 * 			boot system priority 80 \
    		 *			image /flash/qvpc-di-21.7.12.bin \
    		 *			config /flash/20190221_21712.cfg
			 */
			cmdOutput = cmdOutput.replace(System.lineSeparator(), " ").replace("\\", "");

			if (cmdOutput.indexOf(Env.BOOT_INFO_PARA_TITLE_OF_PRIORITY) == -1
					|| cmdOutput.indexOf(Env.BOOT_INFO_PARA_TITLE_OF_IMAGE) == -1
					|| cmdOutput.indexOf(Env.BOOT_INFO_PARA_TITLE_OF_CONFIG) == -1) {

				throw new ServiceLayerException("命令回傳結果查無 BOOT 相關資訊!! cmdOutput: [" + cmdOutput + "]");
			}

			String priority = cmdOutput.substring(cmdOutput.indexOf(Env.BOOT_INFO_PARA_TITLE_OF_PRIORITY),
												  cmdOutput.indexOf(Env.BOOT_INFO_PARA_TITLE_OF_IMAGE))
									   .replace(Env.BOOT_INFO_PARA_TITLE_OF_PRIORITY, "")
									   .trim();

			String image = cmdOutput.substring(cmdOutput.indexOf(Env.BOOT_INFO_PARA_TITLE_OF_IMAGE),
											   cmdOutput.indexOf(Env.BOOT_INFO_PARA_TITLE_OF_CONFIG))
									.replace(Env.BOOT_INFO_PARA_TITLE_OF_IMAGE, "")
									.trim();

			Integer currentPriority = StringUtils.isNotBlank(priority) ? Integer.valueOf(priority) : 2;
			Integer newPriority = (currentPriority > 1) ? (currentPriority - 1) : 1;
			csVO.setBootInfoPriority(String.valueOf(newPriority));
			csVO.setBootInfoImage(image);

		} else {
			if (otherObj instanceof List) {
				((List<String>)otherObj)
					.add(
						scriptVO.getRemark()
							.concat(Env.COMM_SEPARATE_SYMBOL)
							.concat(
								cutContent(
									cmdOutput,
									StringUtils.isNotBlank(scriptVO.getHeadCuttingLines()) ? Integer.valueOf(scriptVO.getHeadCuttingLines()) : 0,
										StringUtils.isNotBlank(scriptVO.getTailCuttingLines()) ? Integer.valueOf(scriptVO.getTailCuttingLines()) : 0,
											System.lineSeparator()
								)
							)
					);
			}
		}

		return csVO;
	}

	public static void zipFiles(List<File> srcfileList, File targetZipfile) throws ServiceLayerException {
        byte[] buf = new byte[1024];
        try {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetZipfile));
            // Compress the files
            for (File file : srcfileList) {
                FileInputStream in = new FileInputStream(file);
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(file.getName()));
                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                // Complete the entry
                out.closeEntry();
                in.close();
            }
            // Complete the ZIP file
            out.close();

        } catch (Exception e) {
           log.error(e.toString(), e);
           throw new ServiceLayerException("壓縮ZIP檔異常! >> " + e.toString());
        }
    }

	public static void downFile(HttpServletResponse response, String downloadFilePath, String fileName) throws ServiceLayerException {
        try {
            String path = downloadFilePath;
            File file = new File(path);
            if (file.exists()) {
                InputStream ins = new FileInputStream(path);
                BufferedInputStream bins = new BufferedInputStream(ins); // 放到緩衝流裡面
                OutputStream outs = response.getOutputStream(); // 獲取檔案輸出IO流
                BufferedOutputStream bouts = new BufferedOutputStream(outs);
                response.setContentType("application/x-download"); // 設定response內容的型別
                response.setHeader(
                        "Content-disposition",
                        "attachment;filename=" + URLEncoder.encode(fileName, "UTF8")); // 設定頭部資訊
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                // 開始向網路傳輸檔案流
                while ((bytesRead = bins.read(buffer, 0, 8192)) != -1) {
                   bouts.write(buffer, 0, bytesRead);
               }
               bouts.flush(); // 這裡一定要呼叫flush()方法
               ins.close();
               bins.close();
               outs.close();
               bouts.close();

            } else {
                throw new ServiceLayerException("找不到要下載的檔案! [" + downloadFilePath + " , " + fileName);
            }
        } catch (ServiceLayerException sle) {
            log.error(sle.toString(), sle);
            throw sle;

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("下載異常! >> " + e.toString());
        }
    }
}
