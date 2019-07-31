package com.cmap.plugin.module.ip.mapping;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import com.cmap.annotation.Log;
import com.cmap.service.impl.CommonServiceImpl;

@Service("ipMappingService")
public class IpMappingServiceImpl extends CommonServiceImpl implements IpMappingService {
    @Log
    private static Logger log;

}
