package com.cmap.service;

import com.cmap.exception.ServiceLayerException;
import com.cmap.model.PrtgAccountMapping;

public interface PrtgService {

	public PrtgAccountMapping getMappingByAccount(String account) throws ServiceLayerException;
}
