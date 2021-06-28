package com.cmap.plugin.module.circuit;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmap.AppResponse;
import com.cmap.Constants;
import com.cmap.DatatableResponse;
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
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

	private void initMenu(Model model, HttpServletRequest request) {
		try {
			
//			CircuitVO cVO = new CircuitVO();
//			infoList = circuitService.findModuleCircuitDiagramInfo(cVO);

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			behaviorLog(request);
		}
	}
	
	@RequestMapping(value = "/circuitE1OpenRecord", method = RequestMethod.GET)
	public String main(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}
		return "plugin/module_circuit_e1_open_record";
	}


	@RequestMapping(value = "getTotalFilteredCount.json", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody AppResponse getTotalFilteredCount(
            Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "queryStatus", required = false, defaultValue = "") String queryStatus){

	    String retVal = "N/A";
	    long filteredTotal = 0;
	    
        try {
			CircuitOpenListVO clVO = new CircuitOpenListVO();

//			filteredTotal = circuitService.countModuleAlarmSummary(clVO);
			
            retVal = Constants.NUMBER_FORMAT_THOUSAND_SIGN.format(filteredTotal);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "SUCCESS");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;

//        } catch (ServiceLayerException sle) {
//        	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, sle.getMessage());
        } catch (Exception e) {
            log.error(e.toString(), e);

            AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;
        } finally {
			initMenu(model, request);
		}
	}
	
	@RequestMapping(value = "getE1OpenListData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getE1OpenListData(Model model, HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(name = "queryStatus", required = false, defaultValue = "") String queryStatus,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer startNum,
			@RequestParam(name = "length", required = false, defaultValue = "100") Integer pageLength,
			@RequestParam(name = "order[0][column]", required = false, defaultValue = "0") Integer orderColIdx,
			@RequestParam(name = "order[0][dir]", required = false, defaultValue = "desc") String orderDirection) {

		long total = 0;
		long filteredTotal = 0;
		List<CircuitOpenListVO> dataList = new ArrayList<>();
		try {
			String[] orderCol = new String[]{"", "deviceId", "circleame", "stationName", "stationEngName", "neName", "srcSid", "dstSid", "localCsrIp", "remoteCsrIp", "srcE1gwIp", "dstE1gwIp", "scrPortNumber", "dstPortNumber", "remark", "updateTime"};
			//ALARM_ID '9', GROUP_ID '10', DEVICE_ID '11', SENSOR_ID '12'
			CircuitOpenListVO clVO = new CircuitOpenListVO();
//			clVO.setQueryDstE1gwSID(queryStatus);
			clVO.setStartNum(startNum);
			clVO.setPageLength(pageLength);
			clVO.setOrderColumn(orderCol[orderColIdx]);
			clVO.setOrderDirection(orderDirection);

			dataList =  circuitService.findModuleE1OpenList(clVO);
			
			filteredTotal = dataList.size();
			total = dataList.size();

		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			initMenu(model, request);
		}

		return new DatatableResponse(total, dataList, filteredTotal, null, "");
	}
	
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "getCircuitSetting.json", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String getCircuitSetting(Model model, HttpServletRequest request, @RequestBody JsonNode jsonData) {

		String jsonString = "";
		// 宣告container並填充內容，最後會以JSON格式回傳至前端
		Map<String, Object> data = new HashMap<String, Object>();
//		List<ModuleCircuitDiagramInfo> voList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();		
		String infoMsg = "success";
		String errMsg = "";
		
		try {
			
			// Step 1.
			if (jsonData == null || (!jsonData.has("sn") && !jsonData.has("neN") && !jsonData.has("gi") && !jsonData.has("id"))) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				jsonString = mapper.writeValueAsString(data);
				return jsonString;
			}

			CircuitVO cVO = new CircuitVO();
			if(jsonData.has("id")) {
				cVO.setQueryCircleId(jsonData.findValue("id").asText());
			}
			if(jsonData.has("sn")) {
				cVO.setQueryStationEngName(jsonData.findValue("sn").asText());
			}
			if(jsonData.has("neN")) {
				cVO.setQueryNeName(jsonData.findValue("neN").asText());
			}
			if(jsonData.has("gi")) {
				cVO.setQueryE1gwIpList(Arrays.asList(jsonData.findValue("gi").asText()));
			}			
			
			List<Map<String, Object>> infoList = circuitService.findModuleCircuitDiagramInfo(cVO);
						
			data.put("dataList", infoList);
			data.put("infoMsg", infoMsg);
			data.put("errMsg", errMsg);

	        jsonString = mapper.writeValueAsString(data);
//	        System.out.println(new Date() + " >>> " + jsonString);
	        
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
	    } finally {
	    	initMenu(model, request);
		}

		return jsonString;
	}
	
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "doE1Open.json", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String doE1Open(HttpServletRequest request, @RequestBody JsonNode jsonData) {

		String jsonString = "";		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = new HashMap<String, Object>();
		/**
		 * SrcNode_to_DstNode_Path_1th 
		 * SrcNode_to_DstNode_Path_2th
		 * SrcNode_to_DstNode_Path_3th
		 *  Local Area CSR IP
		 *  Remote Area CSR IP
		 *  Dst E1GW IP
		 *  Src E1GW SID
		 *  Dst E1GW SID
		 *  Port Number
		 *  PseudowireID = "10"+ port
		 */
		String[] columns = new String[] {"lci", "rci", "sei", "dei", "ses", "des", "sp", "dp"};
		try {
			if (jsonData == null) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				jsonString = mapper.writeValueAsString(data);
	            return jsonString;
			}else {
				for (String column : columns) {
					if(!jsonData.has(column)) {
						data.put("infoMsg", "error");
						data.put("errMsg", "參數不正確!!");
						jsonString = mapper.writeValueAsString(data);
			            return jsonString;
					}
				}
			}
				                	
	        data = circuitService.doE1OpenProvision(jsonData);
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
	    } finally {
	    	behaviorLog(request);
		}

	    return jsonString;
	}
	
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "doL3BGPCircuitOpen.json", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String doL3BGPCircuitOpen(HttpServletRequest request, @RequestBody JsonNode jsonData) {

		String jsonString = "";		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = new HashMap<String, Object>();
		/**
		 * "lci", "rci", "sei", "dei", "sp", "dp"
		 */
		String[] columns = new String[] {"lci", "rci", "sei", "dei", "sp", "dp"};
		try {
			if (jsonData == null) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				jsonString = mapper.writeValueAsString(data);
	            return jsonString;
			}else {
				for (String column : columns) {
					if(!jsonData.has(column)) {
						data.put("infoMsg", "error");
						data.put("errMsg", "參數不正確!!");
						jsonString = mapper.writeValueAsString(data);
			            return jsonString;
					}
				}
			}
				                	
	        data = circuitService.doL3BGPCircuitOpenProvision(jsonData);
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
	    } finally {
	    	behaviorLog(request);
		}

	    return jsonString;
	}
}
