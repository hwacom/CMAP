package com.cmap.utils.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.service.vo.ConfigInfoVO;
import com.cmap.utils.FileUtils;

public class FtpFileUtils implements FileUtils {
	private static Logger log = LoggerFactory.getLogger(FtpFileUtils.class);
	
	FTPClient ftp = null;
	
	public FtpFileUtils() throws Exception {
		try {
			ftp = new FTPClient();
			ftp.setDefaultTimeout(Env.FTP_DEFAULT_TIME_OUT);
			ftp.setConnectTimeout(Env.FTP_CONNECT_TIME_OUT);
			
		} catch (Exception e) {
			log.error(e.toString(), e);
			e.printStackTrace();
			
			ftp = null;
			throw new Exception("[FTP initial failed] >> " + e.toString());
		}
	}
	
	private void checkFtpStatus() throws Exception {
		boolean conError = false;
		
		if (ftp == null) {
			conError = true;
			
		} else {
			if (!ftp.isConnected() || !ftp.isAvailable()) {
				conError = true;
			}
		}
		
		if (conError) {
			disconnect();
			throw new Exception("FTP connect interrupted!");
		}
	}

	@Override
	public boolean connect(final String hostIp, final Integer hostPort) throws Exception {
		try {
			if (ftp != null) {
				ftp.connect(
						StringUtils.isBlank(hostIp) ? Env.FTP_HOST_IP : hostIp,
						hostPort == null ? Env.FTP_HOST_PORT : hostPort);
				
				ftp.setSoTimeout(Env.FTP_CONNECT_TIME_OUT);
				
				int replyCode = ftp.getReplyCode();
				
				System.out.println("FTP connect success: "+FTPReply.isPositiveCompletion(replyCode));
				if (!FTPReply.isPositiveCompletion(replyCode)) {
					throw new Exception("FTP init error!");
				}
			}
			
		} catch (Exception e) {
			throw new Exception("[FTP connect failed] >> " + e.toString());
		}
		
		return true;
	}
	
	@Override
	public boolean login(String account, String password) throws Exception {
		try {
			if (ftp != null) {
		        ftp.login(account, password);
		        System.out.println("FTP login success");
			}
			
		} catch (Exception e) {
			throw new Exception("[FTP connect failed] >> " + e.toString());
		}
		
		return true;
	}

	@Override
	public boolean changeDir(String targetDirPath, boolean createFolder) throws Exception {
		int returnCode = 0;
		try {
			checkFtpStatus();
			
			ftp.changeWorkingDirectory("/"); //先切換至根目錄
			
			returnCode = ftp.getReplyCode();
		    if (returnCode == FTPReply.FILE_UNAVAILABLE) {
		        throw new Exception("[FTP]切換目錄異常 >> 「/」目錄不存在");
		    }
			
			ftp.changeWorkingDirectory(targetDirPath);
			returnCode = ftp.getReplyCode();
		    if (returnCode == FTPReply.FILE_UNAVAILABLE) {
		    	//若目標路徑目錄不存在，則依循路徑建立資料夾
		    	if (createFolder) {
		    		String stepDirPath = "";
		    		boolean actionSuccess = false;
		    		for (String dir : targetDirPath.split(Constants.FTP_FILE_SEPARATOR)) {
		    			if (StringUtils.isNotBlank(dir)) {
		    				stepDirPath = stepDirPath.concat(Constants.FTP_FILE_SEPARATOR).concat(dir);
		    				
		    				if (ftp.cwd(stepDirPath) == FTPReply.FILE_UNAVAILABLE) {
		    					actionSuccess = ftp.makeDirectory(stepDirPath);
		    				}
		    				
		    				if (ftp.cwd(stepDirPath) == FTPReply.FILE_ACTION_OK) {
		    					actionSuccess = ftp.changeWorkingDirectory(stepDirPath);
		    				}
		    				
		    				if (!actionSuccess) {
			    				throw new Exception("[FTP]切換目錄異常 >> 「" + targetDirPath + "」創建目錄失敗");
			    			}
		    			}
		    		}
		    		
		    	} else {
		    		throw new Exception("[FTP]切換目錄異常 >> 「" + targetDirPath + "」目錄不存在");
		    	}
		    }
			
		} catch (Exception e) {
			throw new Exception("[FTP change direction failed] >> " + e.toString());
		}
		
		return true;
	}
	

	@Override
	public FTPFile[] listFiles() throws Exception {
		FTPFile[] retVal = null;
		try {
			checkFtpStatus();
			
			retVal = ftp.listFiles();
			for (FTPFile ff : retVal) {
				System.out.println("[Name]: "+ff.getName()+", [Type]: "+ff.getType()+", [Size]: "+ff.getSize());
			}
			
		} catch (Exception e) {
			throw new Exception("[FTP list files failed] >> " + e.toString());
		}
		
		return null;
	}

	@Override
	public boolean uploadFiles(String fileName, InputStream inputStream) throws Exception {
		boolean success = false;
		try {
			checkFtpStatus();
			
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			
			success = ftp.storeFile(fileName, inputStream);
			
			if (success) {
				System.out.println("檔案上傳完成!");
			} else {
				throw new Exception("[FTP]檔案上傳失敗");
			}
			
		} catch (Exception e) {
			throw new Exception("[FTP upload file failed] >> " + e.toString());
			
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		
		return success;
	}

	@Override
	public List<String> downloadFiles(ConfigInfoVO ciVO) throws Exception {
		List<String> fileContentList = null;
		
		int returnCode = 0;
		BufferedReader bufReader = null;
		InputStreamReader isReader = null;
		InputStream iStream = null;
		try {
			checkFtpStatus();
			
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			
			iStream = ftp.retrieveFileStream(ciVO.getFileFullName());

			if (iStream == null || returnCode == FTPReply.FILE_UNAVAILABLE) {
		        throw new Exception("[FTP]下載檔案異常 >> 「" + ciVO.getFileFullName() + "」檔案不存在");
		        
		    } else {
				fileContentList = new ArrayList<String>();
				
				isReader = new InputStreamReader(iStream, StandardCharsets.UTF_8);
				bufReader = new BufferedReader(isReader);
				
				String line;
				while ((line = bufReader.readLine()) != null) {
					fileContentList.add(line);
				}
				
				System.out.println("fileContentList.size: "+fileContentList.size());
			}
			
		} catch (Exception e) {
			throw new Exception("[FTP download file failed] >> " + e.toString());
			
		} finally {
			if (iStream != null) {
				iStream.close();
			}
			if (isReader != null) {
				isReader.close();
			}
			if (bufReader != null) {
				bufReader.close();
			}
			
			if (iStream != null) {
				boolean complete = ftp.completePendingCommand();
				System.out.println(">>> Complete: "+complete);
				
				if (!complete) {
					disconnect();
				}
			}
		}
		
		return fileContentList;
	}

	@Override
	public boolean disconnect() throws Exception {
		try {
			boolean logoutRet = false;
			
			if (ftp != null) {
				logoutRet = ftp.logout();
			}
			System.out.println(logoutRet); 
			
		} catch (Exception e) {
			throw new Exception("[FTP disconnect failed] >> " + e.toString());
			
		} finally {
			if (ftp != null) {
				ftp.disconnect();
			}
		}
		
		return true;
	}

	@Override
	public String downloadFilesString(ConfigInfoVO ciVO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
