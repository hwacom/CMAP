package com.cmap.utils;

import java.util.Map;
import com.cmap.exception.ServiceLayerException;

public interface ProvisionUtils {

    public boolean doCheck(Map<String, String> paraMap) throws ServiceLayerException;
}
