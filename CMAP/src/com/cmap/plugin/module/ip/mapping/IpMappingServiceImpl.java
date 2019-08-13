package com.cmap.plugin.module.ip.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.vo.DeviceDAOVO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DeviceList;
import com.cmap.service.impl.CommonServiceImpl;

@Service("ipMappingService")
public class IpMappingServiceImpl extends CommonServiceImpl implements IpMappingService {
    @Log
    private static Logger log;

    @Autowired
    private DeviceDAO deviceDAO;

    @Autowired
    private IpMappingDAO ipMappingDAO;

    /**
     * 逐筆Device撈取ArpTable資料
     * @param deviceL3
     * @return Map<String, Map<String, IpMappingServiceVO>>
     * 結構:
     * Map<String, Map<String, IpMappingServiceVO>>
     *   - [Key]    String: DeviceId值
     *   - [Value]  Map<String, IpMappingServiceVO>
     *       - [Key]    String: MAC值
     *       - [Value]  IpMappingServiceVO: InterfaceId、IpAddress
     * @throws ServiceLayerException
     */
    private Map<String, Map<String, IpMappingServiceVO>> pollingArpTable(List<DeviceList> deviceL3) throws ServiceLayerException {
        Map<String, Map<String, IpMappingServiceVO>> retMap = new HashMap<>();
        try {



        } catch (Exception e) {

        }
        return retMap;
    }

    /**
     * 逐筆Device撈取MacTable資料
     * @param deviceL2
     * @return Map<String, Map<String, IpMappingServiceVO>>
     * 結構:
     * Map<String, Map<String, IpMappingServiceVO>>
     *   - [Key]    String: DeviceId值
     *   - [Value]  Map<String, IpMappingServiceVO>
     *       - [Key]    String: MAC值
     *       - [Value]  IpMappingServiceVO: PortId
     * @throws ServiceLayerException
     */
    private Map<String, Map<String, IpMappingServiceVO>> pollingMacTable(List<DeviceList> deviceL2) throws ServiceLayerException {
        Map<String, Map<String, IpMappingServiceVO>> retMap = new HashMap<>();
        try {
            for (DeviceList device : deviceL2) {
                String groupId = device.getGroupId();
                String deviceId = device.getDeviceId();

                // 查找要排除的PORT清單
                List<ModuleMacTableExcludePort> excludePortList = ipMappingDAO.findModuleMacTableExcludePort(groupId, deviceId);



            }


        } catch (Exception e) {

        }
        return retMap;
    }

    @Override
    public IpMappingServiceVO executeIpMappingPolling(String groupId, Date executeDate) throws ServiceLayerException {
        IpMappingServiceVO retVO = new IpMappingServiceVO();
        Map<String, Map<String, IpMappingServiceVO>> L3ArpTableMap = null;
        Map<String, Map<String, IpMappingServiceVO>> L2MacTableMap = null;
        List<IpMappingServiceVO> ipMacPortMappingList = new ArrayList<>();

        try {
            DeviceDAOVO daovo = new DeviceDAOVO();
            daovo.setGroupId(groupId);

            // Step 1. 撈取ArpTable資料 (僅針對L3 switch撈取)
            daovo.setDeviceLayer(Env.DEVICE_LAYER_L3);

            List<DeviceList> deviceL3 = deviceDAO.findDeviceListByDAOVO(daovo);
            if (deviceL3 != null && !deviceL3.isEmpty()) {
                L3ArpTableMap = pollingArpTable(deviceL3);
            }

            // Step 2. 撈取MacTable資料 (僅針對L2 switch撈取)
            daovo.setDeviceLayer(Env.DEVICE_LAYER_L2);
            List<DeviceList> deviceL2 = deviceDAO.findDeviceListByDAOVO(daovo);
            if (deviceL2 != null && !deviceL2.isEmpty()) {
                L2MacTableMap = pollingMacTable(deviceL2);

            } else {
                throw new ServiceLayerException("查無L2 Switch MAC table資料!!");
            }

            // Step 3. ArpTable & MacTable mapping處理
            IpMappingServiceVO mappingVO = null;
            // 以L3 ArpTable為主
            for (Map.Entry<String, Map<String, IpMappingServiceVO>> L3DeviceEntry : L3ArpTableMap.entrySet()) {
                Map<String, IpMappingServiceVO> L3DeviceArpTable = L3DeviceEntry.getValue();

                // 跑L3 device ArpTable 資料
                for (Map.Entry<String, IpMappingServiceVO> L3ArpTableEntry : L3DeviceArpTable.entrySet()) {
                    String macAddress = L3ArpTableEntry.getKey();
                    IpMappingServiceVO atEntryVO = L3ArpTableEntry.getValue();

                    String ipAddress = atEntryVO.getIpAddress();
                    String portId = null;

                    for (Map.Entry<String, Map<String, IpMappingServiceVO>> L2MacTableEntry : L2MacTableMap.entrySet()) {
                        String L2DeviceId = L2MacTableEntry.getKey();
                        Map<String, IpMappingServiceVO> L2DeviceMacTable = L2MacTableEntry.getValue();

                        // 若該Group底下L2 switch的MAC table不為空，且存在該MAC address資料才往下取得該MAC連接的PortID
                        if (L2DeviceMacTable != null && !L2DeviceMacTable.isEmpty()) {
                            if (L2DeviceMacTable.containsKey(macAddress)) {
                                IpMappingServiceVO mtEntryVO = L2DeviceMacTable.get(macAddress);
                                portId = mtEntryVO.getPortId();

                                mappingVO = new IpMappingServiceVO();
                                mappingVO.setExecuteDate(executeDate);
                                mappingVO.setGroupId(groupId);
                                mappingVO.setDeviceId(L2DeviceId);
                                mappingVO.setIpAddr(ipAddress);
                                mappingVO.setMacAddress(macAddress);
                                mappingVO.setPortId(portId);

                                ipMacPortMappingList.add(mappingVO);
                            }
                        }
                    }
                }
            }

            // Step 4. 寫入Module_Arp_Table / Module_Mac_Table / Module_Ip_Mac_Port_Mapping資料


            // Step 5. 比對IP前一次Mapping紀錄，判斷是否有異動 & 寫入Module_Ip_Mac_Port_Mapping_Change資料


            // Step 6. 寫入私接分享器資料

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retVO;
    }

    @Override
    public long countModuleIpMacPortMappingChange(IpMappingServiceVO imsVO)
            throws ServiceLayerException {
        // TODO 自動產生的方法 Stub
        return 0;
    }

    @Override
    public List<IpMappingServiceVO> findModuleIpMacPortMappingChange(IpMappingServiceVO imsVO)
            throws ServiceLayerException {
        // TODO 自動產生的方法 Stub
        return null;
    }
}
