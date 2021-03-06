package com.cmap.utils.impl;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.VariableBinding;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.exception.CommandExecuteException;
import com.cmap.service.vo.CommonServiceVO;
import com.cmap.service.vo.ConfigInfoVO;
import com.cmap.service.vo.ScriptServiceVO;
import com.cmap.service.vo.StepServiceVO;
import com.cmap.utils.ConnectUtils;

public class TelnetUtils extends CommonUtils implements ConnectUtils {
	private static Logger log = LoggerFactory.getLogger(TelnetUtils.class);

	private TelnetClient telnet = null;
	private InputStream in;
	private PrintStream out;
	private final String prompt = "$";
	private StringBuilder processLog = new StringBuilder();

	public TelnetUtils() throws Exception {
		if (telnet == null) {
			telnet = new TelnetClient();
			telnet.setConnectTimeout(Env.TELNET_CONNECT_TIME_OUT);
			telnet.setDefaultTimeout(Env.TELNET_DEFAULT_TIME_OUT);
		}
	}

	private void checkTelnetStatus() throws Exception {
		boolean conError = false;

		if (telnet == null) {
			conError = true;

		} else {
			if (!telnet.isConnected() || !telnet.isAvailable()) {
				conError = true;
			}
		}

		if (conError) {
			throw new Exception("Telnet connect interrupted!");
		}
	}

	@Override
	public boolean connect(final String ipAddress, final Integer port) throws Exception {
		boolean result = false;
		if (telnet != null) {
			telnet.connect(ipAddress, port == null ? Env.TELNET_DEFAULT_PORT : port);
			log.info("Telnet connect success! >>> [ " + ipAddress + ":" + port + " ]");
			// telnet.setSoTimeout(Env.TELNET_DEFAULT_TIME_OUT);

			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());
		}

		return result;
	}

	@Override
	public boolean login(final String account, final String password, final String enable, ConfigInfoVO ciVO)
			throws Exception {
		String output = "";
		String enableString = "enable";
		
		output = readUntil(Arrays.asList(":"), 0);

		if (StringUtils.isNotBlank(output)) {
			if (output.toLowerCase().indexOf(Env.TELNET_LOGIN_PASSWORD_TEXT) > -1) {// 直接輸入密碼
				write(password);
			} else {
				log.debug("for debug login account & password!");
				write(account);
				Thread.sleep(500);
				write(password);
			}
			processLog.append(output);

			output = readUntil(Env.TELNET_LOGIN_SUCCESS_TEXT, 1000);

			if (StringUtils.isNotBlank(output)) {
				boolean actionFlag = false;

				for (String text : Env.TELNET_LOGIN_SUCCESS_TEXT) {
					if (output.toLowerCase().endsWith(text)) {
						actionFlag = true;
					}
				}

				if (!actionFlag) {
					log.info("for debug output:" + output);
					throw new Exception("Login info incorrect!!");
				}

				actionFlag = false;
				for (String text : Env.TELNET_LOGIN_ENABLE_TEXT) { // 判斷是否包含需要enable字元
					if (output.toLowerCase().endsWith(text)) {
						actionFlag = true;
						if(!StringUtils.equals(text, ">")) {//DLINK 等其他設備需下enable admin指令
							enableString = "enable admin";
						}
						log.debug("telnet login need enable");
						break;
					}
				}

				if (actionFlag) {
					write(enableString);
					output = readUntil(Arrays.asList(":"), 1000);

					if (StringUtils.isNotBlank(output)) {
						if (output.toLowerCase().indexOf(Env.TELNET_LOGIN_PASSWORD_TEXT) > -1) {// enable 直接輸入密碼
							write(enable);
						} else { // enable需要輸入帳號
							log.debug("for debug enable account & password!");
							write(account);
							Thread.sleep(500);
							write(enable);
						}

						processLog.append(output);
					}
				}
			} else {
				throw new Exception(" Login incorrect!! >>> [ " + ciVO.getDeviceIp() + " ] telnet read login sucess text is empty!!");
			}
		} else {
			throw new Exception(" Login incorrect!! >>> [ " + ciVO.getDeviceIp() + " ] readUntil is empty!!");
		}

		return false;
	}

	private void write(String cmd) {
		try {
			out.println(cmd);
			out.flush();
			log.debug("cmd = " + cmd);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	private String readUntil(List<String> pattern, long waitTime) throws Exception {
		StringBuffer sb = new StringBuffer();
		List<String> lastChars = new ArrayList<>();
		try {

			for (String text : pattern) {
				if (!lastChars.contains(text.substring(text.length() - 1))) {
					lastChars.add(text.substring(text.length() - 1));
				}
			}
			if (waitTime > 0) {
				Thread.sleep(waitTime);
			}
			byte[] bytes = new byte[1024];
//			char ch = (char) in.read(bytes);

			int runTime = 0;
			int len = in.read(bytes);
			
			while (true) {
//				sb.append(ch);
				if(len == -1){
					log.debug("for debug lastChars = " + lastChars.toString() + ";sb = " + sb.toString());
					return sb.toString().trim();
				}
				
				sb.append(new String(bytes, 0, len, "UTF-8"));
//				if (lastChars.contains(String.valueOf(ch))) {
//					log.debug("for debug lastChars = " + lastChars.toString() + ";sb = " + sb.toString());
					for (String text : pattern) {
//						if (Strings.toUpperCase(sb.toString().trim()).endsWith(Strings.toUpperCase(text))) {
						if (Strings.toUpperCase(sb.toString()).lastIndexOf(Strings.toUpperCase(text)) > -1) {
							log.debug("for debug lastChars = " + lastChars.toString() + ";sb = " + sb.toString());
							return sb.toString().trim();
						}
					}
//				}

				if (runTime > Env.TELNET_READ_UNTIL_MAX_RUNTIME) {
					log.info("for debug lastChars = " + lastChars.toString() + ";sb = " + sb.toString());
					return sb.toString().trim();
				}

//				ch = (char) in.read(bytes);
				len = in.read(bytes);
				runTime++;
			}
		} catch (SocketTimeoutException ste) {
			return sb.toString();
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw e;
		}
	}

	@Override
	public List<String> sendCommands(List<ScriptServiceVO> scriptList, ConfigInfoVO configInfoVO, StepServiceVO ssVO)
			throws Exception {
		List<String> cmdOutputs = new ArrayList<String>();
		try {
			checkTelnetStatus();

			try {
				long sleepTime = Env.SEND_COMMAND_SLEEP_TIME != null ? Env.SEND_COMMAND_SLEEP_TIME : 1000;
				String cmd;
				String output;
				CommonServiceVO csVO = new CommonServiceVO();
				for (ScriptServiceVO scriptVO : scriptList) {
					output = "\n";

					/*
					 * 預期命令送出後結束符號，針對VM設備的config檔因為內含有「#」符號，判斷會有問題 e.g. 「#」 > 「NK-HeNBGW-04#」
					 */
					List<String> expectedTerminalSymbols = new ArrayList<>();

					for (String word : scriptVO.getExpectedTerminalSymbol().split("\\^")) {
						expectedTerminalSymbols.add(replaceExpectedTerminalSymbol(word, configInfoVO));
					}

					String[] errorSymbols = StringUtils.isNotBlank(scriptVO.getErrorSymbol())
							? scriptVO.getErrorSymbol().split(Env.COMM_SEPARATE_SYMBOL)
							: null;

					// 送出命令
					cmd = replaceContentSign(csVO, scriptVO, configInfoVO, null);
					write(cmd);

					Thread.sleep(StringUtils.isNotBlank(scriptVO.getScriptSleepTime())
							? Long.parseLong(scriptVO.getScriptSleepTime())
							: sleepTime); // 執行命令間格時間

					output = readUntil(expectedTerminalSymbols, 0);

					if (StringUtils.isBlank(output)) {
						throw new SocketTimeoutException("readUntil return is blank then no expectedTerminalSymbols!!");
					}

					processLog.append(output);

					boolean success = true;

					if (errorSymbols != null) {
						for (String errSymbol : errorSymbols) {
							success = output.toUpperCase().contains(errSymbol) ? false : true;

							if (!success) {
								throw new CommandExecuteException("[Command execute failed!] >> output: " + output);
							}
						}
					}

					if (success) {
						if (scriptVO.getOutput() != null && scriptVO.getOutput().equals(Constants.DATA_Y)) {
							csVO = processOutput(csVO, scriptVO, output, cmdOutputs);
						}
					}
				}

				/*
				 * expect.sendLine("cisco").expect(contains("#"));
				 * expect.sendLine("terminal length 0").expect(contains("#")); String fullStr =
				 * expect.sendLine("sh run").expect(contains("#")).getBefore(); cmdResult =
				 * cutContent(fullStr, 3, 2, System.lineSeparator());
				 * log.info("*****************************************************************")
				 * ; log.info(cmdResult);
				 * log.info("*****************************************************************")
				 * ;
				 */

			} catch (Exception e) {
				log.error(e.toString(), e);
				throw e;

			} finally {
				// log.info(processLog.toString());
				ssVO.setCmdProcessLog(processLog.toString());
			}

		} catch (CommandExecuteException cee) {
			throw cee;
		} catch (Exception e) {
			throw new Exception("[TELNET send command failed] >> " + e.getMessage());
		}

		return cmdOutputs;
	}

	@Override
	public boolean logout() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disconnect() throws Exception {
		boolean result = false;
		try {

			if (telnet != null) {
				telnet.disconnect();
				telnet = null;
				result = true;
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return result;
	}

	@Override
	public boolean connect(String udpAddress, String community) throws Exception {
		// TODO 自動產生的方法 Stub
		return false;
	}

	@Override
	public Map<String, List<VariableBinding>> pollData(List<String> oids, SNMP pollMethod) throws Exception {
		// TODO 自動產生的方法 Stub
		return null;
	}

	@Override
	public Map<String, Map<String, String>> pollTableView(String oid, Map<String, String> entryMap) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
