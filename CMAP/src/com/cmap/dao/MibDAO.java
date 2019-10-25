package com.cmap.dao;

import java.util.List;

import com.cmap.model.MibOidMapping;
import com.cmap.model.MibValueMapping;

public interface MibDAO {

	public List<MibOidMapping> findMibOidMappingByNames(List<String> oidNames);
    
    public List<MibOidMapping> findMibOidMappingOfTableEntryByNameLike(String tableOidName);
   
    public List<MibValueMapping> findMibValueMappingByOidTable(String oidTable);
}
