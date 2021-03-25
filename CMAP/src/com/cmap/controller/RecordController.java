package com.cmap.controller;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cmap.annotation.Log;

@Controller
@RequestMapping("/record")
public class RecordController extends BaseController {
	@Log
	private static Logger log;

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
			model.addAttribute("deviceList", deviceListMap);
			
			behaviorLog(request.getRequestURI(), request.getQueryString());
		}
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String main(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "record/record_main";
	}

	@RequestMapping(value = "ipBlocked", method = RequestMethod.GET)
    public String ipBlocked(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {


        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            initMenu(model, request);
        }

        return "plugin/module_ip_open_block_record";
    }

	@RequestMapping(value = "portBlocked", method = RequestMethod.GET)
	public String portBlocked(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "plugin/module_switch_port_block_record";
	}

	@RequestMapping(value = "macBlocked", method = RequestMethod.GET)
	public String macBlocked(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "plugin/module_mac_open_block_record";
	}
	

	@RequestMapping(value = "blockedListRecord", method = RequestMethod.GET)
	public String blockListRecord(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "plugin/module_block_list_record";
	}
}
