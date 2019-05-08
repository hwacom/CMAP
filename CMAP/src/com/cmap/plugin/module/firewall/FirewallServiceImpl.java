package com.cmap.plugin.module.firewall;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.annotation.Log;
import com.cmap.service.impl.CommonServiceImpl;

@Service("firewallService")
@Transactional
public class FirewallServiceImpl extends CommonServiceImpl implements FirewallService {
    @Log
    private static Logger log;

    @Autowired
    private FirewallDAO firewallDAO;
}
