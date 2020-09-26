package com.cmap.plugin.module.circuit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.comm.enums.ConnectionMode;
import com.cmap.controller.BaseController;
import com.cmap.security.SecurityUtil;
import com.cmap.service.DeliveryService;
import com.cmap.service.StepService;
import com.cmap.service.vo.ScriptServiceVO;
import com.cmap.service.vo.StepServiceVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/circuit")
public class CircuitController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private CircuitService circuitService;
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private StepService stepService;
	
	private List<ModuleCircuitDiagramInfo> infoList = null;
	private Map<String, String> CSRMap = null;
	private Map<String, String> stationMap = null;

	private void initMenu(Model model, HttpServletRequest request) {
		try {
			
			CircuitVO cVO = new CircuitVO();
			infoList = circuitService.findModuleCircuitDiagramInfo(cVO);

			for(ModuleCircuitDiagramInfo info : infoList) {
				if(StringUtils.equalsAnyIgnoreCase(info.getType(), "CSR")) {
					if(!CSRMap.containsKey(info.getName())) {
						CSRMap.put(info.getName(), info.getName());
					}					
				}else {
					if(!stationMap.containsKey(info.getName())) {
						stationMap.put(info.getName(), info.getName());
					}
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("group", "");
			model.addAttribute("CSRMap", CSRMap);
			
			model.addAttribute("device", "");
			model.addAttribute("stationMap", stationMap);

			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
		}
	}
	
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "getCircuitInfo.json", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String getCircuitInfo(HttpServletRequest request, @RequestBody JsonNode jsonData) {

		String jsonString = "";
		// 宣告container並填充內容，最後會以JSON格式回傳至前端
		Map<String, Object> data = new HashMap<String, Object>();
		List<infoVO> voList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();		
		String infoMsg = "success";
		String errMsg = "";
		
		try {
			
			// Step 1.
			if (jsonData == null || (!jsonData.has("circleId") && !jsonData.has("type") && !jsonData.has("name"))) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				jsonString = mapper.writeValueAsString(data);
				return jsonString;
			}
			CircuitVO cVO = new CircuitVO();
			if(jsonData.has("circleId")) {
				cVO.setQueryCircleId(jsonData.get("circleId").textValue());
			}
			if(jsonData.has("type")) {
				cVO.setQueryType(jsonData.get("type").textValue());
			}
			if(jsonData.has("name")) {
				cVO.setQueryName(jsonData.get("name").textValue());
			}
			List<ModuleCircuitDiagramInfo> infoList = circuitService.findModuleCircuitDiagramInfo(cVO);
			
			for(ModuleCircuitDiagramInfo info :infoList) {
				infoVO circuitVO = new infoVO();
				BeanUtils.copyProperties(info, circuitVO);
				voList.add(circuitVO);
			}			
			
			data.put("voList", voList);
			data.put("infoMsg", infoMsg);
			data.put("errMsg", errMsg);

	        jsonString = mapper.writeValueAsString(data);
	        System.out.println(new Date() + " >>> " + jsonString);
	        
	    } catch (Exception e) {
	        log.error(e.toString(), e);
	        data.put("infoMsg", "error");
			data.put("errMsg", e.toString());
	        try {
				jsonString = mapper.writeValueAsString(data);
			} catch (JsonProcessingException e1) {
				//
			}
	        return jsonString;
	    }

		return jsonString;
	}
	
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "getCircuitSetting.json", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String getCircuitSetting(HttpServletRequest request, @RequestBody JsonNode jsonData) {

		String jsonString = "";
		// 宣告container並填充內容，最後會以JSON格式回傳至前端
		Map<String, Object> data = new HashMap<String, Object>();
		List<settingVO> voList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();		
		String infoMsg = "success";
		String errMsg = "";
		
		try {
			
			// Step 1.
			if (jsonData == null || !jsonData.has("ip")) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				jsonString = mapper.writeValueAsString(data);
				return jsonString;
			}

			List<ModuleCircuitDiagramSetting> settingList = circuitService.findModuleCircuitDiagramInfoSetting(jsonData.get("ip").textValue());
			
			for(ModuleCircuitDiagramSetting setting :settingList) {
				settingVO circuitVO = new settingVO();
				BeanUtils.copyProperties(setting, circuitVO);
				voList.add(circuitVO);
			}			
			
			data.put("voList", voList);
			data.put("infoMsg", infoMsg);
			data.put("errMsg", errMsg);

	        jsonString = mapper.writeValueAsString(data);
	        System.out.println(new Date() + " >>> " + jsonString);
	        
	    } catch (Exception e) {
	        log.error(e.toString(), e);
	        data.put("infoMsg", "error");
			data.put("errMsg", e.toString());
	        try {
				jsonString = mapper.writeValueAsString(data);
			} catch (JsonProcessingException e1) {
				//
			}
	        return jsonString;
	    }

		return jsonString;
	}
	
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "doE1Open.json", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String doE1Open(HttpServletRequest request, @RequestBody JsonNode jsonData) {

		String jsonString = "";		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			// Step 1.
			if(jsonData == null || (!jsonData.has("ip") && !jsonData.has("tunnel") && !jsonData.has("serial") && !jsonData.has("serial"))) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				jsonString = mapper.writeValueAsString(data);
	            return jsonString;
			}
			
			String ip = jsonData.get("ip").textValue();
			String tunnel = jsonData.get("tunnel").textValue();
			String serial = jsonData.get("serial").textValue();
			String username = jsonData.get("username").textValue();
			//TODO 確認查詢方式
			String deviceId = jsonData.get("id").textValue();
	        List<ScriptServiceVO> cmdList = new ArrayList<>();
	        StepServiceVO retVO;
	        
	        /**
	         * show interface | include pseudowire
	         */
	        ScriptServiceVO sVO = new ScriptServiceVO();
	        sVO.setScriptContent("show interface | include Vlan");
	        sVO.setExpectedTerminalSymbol("#");
	        cmdList.add(sVO);
	        
	        sVO = new ScriptServiceVO();
	        sVO.setScriptContent(" ");
	        sVO.setExpectedTerminalSymbol("#");
	        sVO.setOutput(Constants.DATA_Y);
	        sVO.setHeadCuttingLines("1");
	        sVO.setTailCuttingLines("1");
	        cmdList.add(sVO);
	        
        	retVO = stepService.doCommands(ConnectionMode.SSH, deviceId, null, cmdList, false, username, "");
        	log.info("pLog ==" +String.join("@~@~\r\n", retVO.getCmdProcessLog().trim().split("\r\n")));
        	String[] result = retVO.getCmdProcessLog().trim().split("\r\n");
        	long maxId = 0, currId;
        	for(String curLine : result) {
    			if(StringUtils.startsWithIgnoreCase(curLine, "Vlan")) {//Tunnel
    				currId = Long.parseLong(curLine.substring(0, curLine.indexOf(" ")).replaceAll("Vlan", ""));
    				if(maxId < currId) maxId = currId;
    			}
    		}
        	
	        /**
	         * interface pseudowire%id%
				 encapsulation mpls
				 neighbor %3.3.3.3% %id%
				 preferred-path interface %Tunnel10% disable-fallback
								
				l2vpn xconnect context %id%
				 member pseudowire%id%
				 member %Serial% service-instance %id%
	         */
        	cmdList.clear();
	        sVO = new ScriptServiceVO();
	        sVO.setScriptContent("interface pseudowire"+(maxId+1));
	        sVO.setExpectedTerminalSymbol("#");
	        cmdList.add(sVO);
	        sVO = new ScriptServiceVO();
	        sVO.setScriptContent("encapsulation mpls");
	        sVO.setExpectedTerminalSymbol("#");
	        cmdList.add(sVO);
	        sVO = new ScriptServiceVO();
	        sVO.setScriptContent("neighbor "+ip+" "+(maxId+1));
	        sVO.setExpectedTerminalSymbol("#");
	        cmdList.add(sVO);
	        sVO = new ScriptServiceVO();
	        sVO.setScriptContent("preferred-path interface "+tunnel+" disable-fallback");
	        sVO.setExpectedTerminalSymbol("#");
	        cmdList.add(sVO);
	        sVO = new ScriptServiceVO();
	        sVO.setScriptContent("l2vpn xconnect context "+(maxId+1));
	        sVO.setExpectedTerminalSymbol("#");
	        cmdList.add(sVO);
	        sVO = new ScriptServiceVO();
	        sVO.setScriptContent("member pseudowire"+(maxId+1));
	        sVO.setExpectedTerminalSymbol("#");
	        cmdList.add(sVO);
	        sVO = new ScriptServiceVO();
	        sVO.setScriptContent("member "+serial+" service-instance "+(maxId+1));
	        sVO.setExpectedTerminalSymbol("#");
	        cmdList.add(sVO);
	        
        	retVO = stepService.doCommands(ConnectionMode.SSH, deviceId, null, cmdList, false, username, "");
        	
        	data.put("infoMsg", retVO.getMessage());
    		data.put("errMsg", "");
    		data.put("r", retVO.getCmdProcessLog().trim());
    		
			jsonString = mapper.writeValueAsString(data);
			
	    } catch (Exception e) {
            log.error(e.toString(), e);
            data.put("infoMsg", "error");
			data.put("errMsg", e.toString());
            try {
				jsonString = mapper.writeValueAsString(data);
			} catch (JsonProcessingException e1) {
				//
			}
            return jsonString;
	    }

	    return jsonString;
	}
	
	private class infoVO{
		String circleId;
		String circleName;
		String type;
		String name;
		String e1Name;
		String e1Ip;
		public String getCircleId() {
			return circleId;
		}
		public void setCircleId(String circleId) {
			this.circleId = circleId;
		}
		public String getCircleName() {
			return circleName;
		}
		public void setCircleName(String circleName) {
			this.circleName = circleName;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getE1Name() {
			return e1Name;
		}
		public void setE1Name(String e1Name) {
			this.e1Name = e1Name;
		}
		public String getE1Ip() {
			return e1Ip;
		}
		public void setE1Ip(String e1Ip) {
			this.e1Ip = e1Ip;
		}
	}
	
	private class settingVO{
		String settingName;
		String settingValue;
		public String getSettingName() {
			return settingName;
		}
		public void setSettingName(String settingName) {
			this.settingName = settingName;
		}
		public String getSettingValue() {
			return settingValue;
		}
		public void setSettingValue(String settingValue) {
			this.settingValue = settingValue;
		}

	}
}
