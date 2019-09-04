package com.cmap.dao;

import com.cmap.model.ResourceInfo;

public interface ResourceDAO extends BaseDAO {

    public ResourceInfo getResourceInfoById(String id);
}
