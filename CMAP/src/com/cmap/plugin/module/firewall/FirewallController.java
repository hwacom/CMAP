package com.cmap.plugin.module.firewall;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.cmap.annotation.Log;
import com.cmap.security.SecurityUtil;

@Controller
@RequestMapping("/plugin/module/firewall")
public class FirewallController {
    @Log
    private static Logger log;

    @Autowired
    private FirewallService firewallService;

    /**
     * 初始化選單
     * @param model
     * @param request
     */
    private void initMenu(Model model, HttpServletRequest request) {
        Map<String, String> typeListMap = null;
        Map<String, String> devNameListMap = null;
        try {
            typeListMap = new HashMap<>();
            devNameListMap = new HashMap<>();

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            model.addAttribute("queryType", "");
            model.addAttribute("queryTypeMobile", "");
            model.addAttribute("typeList", typeListMap);

            model.addAttribute("queryDevName", "");
            model.addAttribute("queryDevNameMobile", "");
            model.addAttribute("devNameList", devNameListMap);

            model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
            model.addAttribute("timeout", 3600);
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

        return "plugin/module_firewall";
    }
}
