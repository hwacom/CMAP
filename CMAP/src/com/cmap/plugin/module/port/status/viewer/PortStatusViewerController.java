package com.cmap.plugin.module.port.status.viewer;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmap.DatatableResponse;
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.security.SecurityUtil;

@Controller
@RequestMapping("/plugin/module/portStatusViewer")
public class PortStatusViewerController extends BaseController {
	@Log
    private static Logger log;

    @Autowired
    private PortStatusViewerService portStatusViewerService;
    
    @Autowired
	private DatabaseMessageSourceBase messageSource;
    
    /**
     * 初始化選單
     * @param model
     * @param request
     */
    private void initMenu(Model model, HttpServletRequest request) {
        Map<String, String> groupListMap = null;
        Map<String, String> deviceListMap = null;
        try {
            groupListMap = getGroupList(request);

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            model.addAttribute("queryGroup", "");
            model.addAttribute("groupList", groupListMap);
            model.addAttribute("queryDevice", "");
			model.addAttribute("device1List", deviceListMap);

            model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
        }
    }
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String portRecord(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            initMenu(model, request);
        }
        return "plugin/module_port_status_viewer";
    }
    
    @RequestMapping(value = "getInterfaceStatusList.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getInterfaceStatusList(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="queryGroup", required=true, defaultValue="") String queryGroup,
			@RequestParam(name="queryDevice", required=true, defaultValue="") String queryDevice) {

		long total = 0;
		long filterdTotal = 0;
		List<PortStatusViewerVO> dataList = new ArrayList<>();
		PortStatusViewerVO psvVO;
		try {
			if (StringUtils.isBlank(queryGroup)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("group.name", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<PortStatusViewerVO>(), new Long(0), msg);
	        }
			if (StringUtils.isBlank(queryDevice)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("device.name", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<PortStatusViewerVO>(), new Long(0), msg);
	        }
			
			psvVO = new PortStatusViewerVO();
			psvVO.setQueryGroupId(queryGroup);
			psvVO.setQueryDeviceId(queryDevice);

			dataList = portStatusViewerService.getPortStatusList(psvVO);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new DatatableResponse(total, dataList, filterdTotal, e.getMessage());
		}

		return new DatatableResponse(total, dataList, filterdTotal);
	}
}
