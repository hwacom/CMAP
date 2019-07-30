package com.cmap.utils.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import com.cmap.Env;
import com.cmap.exception.ServiceLayerException;
import com.cmap.service.vo.ConfigInfoVO;
import com.cmap.service.vo.ScriptServiceVO;
import com.cmap.service.vo.StepServiceVO;
import com.cmap.utils.ConnectUtils;

public class SnmpV2Utils implements ConnectUtils {
	private static Logger log = LoggerFactory.getLogger(SnmpV2Utils.class);

	private static int version = SnmpConstants.version2c;
	private static String protocol = "udp";

	private static CommunityTarget target = null;
	private static DefaultUdpTransportMapping udpTransportMapping = null;
	private static Snmp snmp = null;

	@Override
	public boolean connect(final String udpAddress, final String community) throws Exception {
		try {
			target = new CommunityTarget();
			target.setCommunity(new OctetString(community));
			target.setAddress(GenericAddress.parse(udpAddress));
			target.setVersion(version);
			target.setTimeout(Env.SNMP_CONNECT_TIME_OUT); 	// milliseconds
			target.setRetries(Integer.valueOf(Env.RETRY_TIMES)); 			// retry 3次

			try {
				udpTransportMapping = new DefaultUdpTransportMapping();
				// 這裡一定要呼叫 listen, 才能收到結果
				udpTransportMapping.listen();
				snmp = new Snmp(udpTransportMapping);

			} catch (Exception e) {
				throw e;
			}

			return true;

		} catch (Exception e) {
			log.error(e.toString(), e);
			return false;
		}
	}

	@Override
	public boolean login(String account, String password) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, List<VariableBinding>> pollData(List<String> oids, SNMP pollMethod) throws Exception {
		Map<String, List<VariableBinding>> retMap;
		try {
		    switch (pollMethod) {
		        case GET:
		            return doSnmpGet(oids);
		        case WALK:
		            return doSnmpWalk(oids);
		        default:
		            return null;
		    }

		} catch (ServiceLayerException sle) {
		    throw sle;

		} catch (Exception e) {
		    log.error(e.toString(), e);
			throw e;
		}
	}

	private Map<String, List<VariableBinding>> doSnmpGet(List<String> oids) throws ServiceLayerException {
	    Map<String, List<VariableBinding>> retMap = new HashMap<String, List<VariableBinding>>();
        List<VariableBinding> vbList = null;

        for (String oid : oids) {
            vbList = snmpGet(oid);
            // System.out.println("OID: "+oid+" >> count: "+vbList.size());

            retMap.put(oid, vbList);
        }
        return retMap;
	}

	private List<VariableBinding> snmpGet(String targetOid) throws ServiceLayerException {
		try {
			PDU pdu = new PDU();
			pdu.setType(PDU.GETNEXT);

			System.out.println("[oid]: "+targetOid);

			if (StringUtils.isBlank(targetOid)) {
				return null;
			}

			pdu.add(new VariableBinding(new OID(targetOid)));

			// 以同步的方式發送 snmp get, 會等待target 設定的 timeout 時間結束後
			// 就會以 Request time out 的方式 return 回來
			ResponseEvent response = snmp.send(pdu, target);
			// System.out.println("PeerAddress:" + response.getPeerAddress());
			PDU responsePdu = response.getResponse();

			if (responsePdu == null) {
				throw new Exception("Request time out");
			} else {
				System.out.println(" response pdu vb size is " + responsePdu.size());

				List<VariableBinding> datalist = new ArrayList<VariableBinding>();
				for (int i = 0; i < responsePdu.size(); i++) {
					VariableBinding vb = responsePdu.get(i);
					System.out.println(vb.getOid().toString()+": "+vb.getVariable().toString());
					datalist.add(vb);
				}
				return datalist;
			}

		} catch (Exception e) {
		    log.error(e.toString(), e);
            throw new ServiceLayerException("snmpGet error!");
		}
	}

	private Map<String, List<VariableBinding>> doSnmpWalk(List<String> oids) throws ServiceLayerException {
        Map<String, List<VariableBinding>> retMap = new HashMap<String, List<VariableBinding>>();
        List<VariableBinding> vbList = null;

        for (String oid : oids) {
            vbList = snmpWalk(oid);
            // System.out.println("OID: "+oid+" >> count: "+vbList.size());

            retMap.put(oid, vbList);
        }
        return retMap;
    }

	/**
	 * 1)responsePDU == null<br>
	 * 2)responsePDU.getErrorStatus() != 0<br>
	 * 3)responsePDU.get(0).getOid() == null<br>
	 * 4)responsePDU.get(0).getOid().size() < targetOID.size()<br>
	 * 5)targetOID.leftMostCompare(targetOID.size(),responsePDU.get(0).getOid())
	 * !=0<br>
	 * 6)Null.isExceptionSyntax(responsePDU.get(0).getVariable().getSyntax())<br>
	 * 7)responsePDU.get(0).getOid().compareTo(targetOID) <= 0<br>
	 */
	private List<VariableBinding> snmpWalk(String targetOid) throws ServiceLayerException {
		OID targetOID = new OID(targetOid);

		PDU requestPDU = new PDU();
		requestPDU.setType(PDU.GETNEXT);
		requestPDU.add(new VariableBinding(targetOID));

		try {
			List<VariableBinding> vblist = new ArrayList<VariableBinding>();
			boolean finished = false;
			while (!finished) {
				VariableBinding vb = null;
				ResponseEvent response = snmp.send(requestPDU, target);
				PDU responsePDU = response.getResponse();

				if (null == responsePDU) {
					System.out.println("responsePDU == null");
					finished = true;
					break;
				} else {
					vb = responsePDU.get(0);
				}
				// check finish
				finished = checkWalkFinished(targetOID, responsePDU, vb);
				if (!finished) {
					// System.out.println("vb:" + vb.toString());
					vblist.add(vb);
					// Set up the variable binding for the next entry.
					requestPDU.setRequestID(new Integer32(0));
					requestPDU.set(0, vb);
				}
			}
			// System.out.println("success finish snmp walk!");
			return vblist;

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("snmpWalk error!");
		}
	}

	/**
	 * check snmp walk finish
	 * @param resquestPDU
	 * @param targetOID
	 * @param responsePDU
	 * @param vb
	 * @return
	 */
	private boolean checkWalkFinished(OID targetOID, PDU responsePDU, VariableBinding vb) {
		boolean finished = false;
		if (responsePDU.getErrorStatus() != 0) {
			// System.out.println("responsePDU.getErrorStatus() != 0 ");
			// System.out.println(responsePDU.getErrorStatusText());
			finished = true;
		} else if (vb.getOid() == null) {
			// System.out.println("vb.getOid() == null");
			finished = true;
		} else if (vb.getOid().size() < targetOID.size()) {
			// System.out.println("vb.getOid().size() < targetOID.size()");
			finished = true;
		} else if (targetOID.leftMostCompare(targetOID.size(), vb.getOid()) != 0) {
			// System.out.print("["+CommonUtils.FORMAT_YYYYMMDDHHMISS.format(new Date())+"] ");
			// System.out.println("targetOID.leftMostCompare() != 0");
			finished = true;
		} else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
			// System.out.println("Null.isExceptionSyntax(vb.getVariable().getSyntax())");
			finished = true;
		} else if (vb.getOid().compareTo(targetOID) <= 0) {
			// System.out.println("Variable received is not "
			// 		+ "lexicographic successor of requested " + "one:");
			// System.out.println(vb.toString() + " <= " + targetOID);
			finished = true;
		}
		return finished;

	}

	@Override
	public boolean logout() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disconnect() throws Exception {
		if (snmp != null) {
			try {
				snmp.close();
			} catch (IOException ex1) {
				snmp = null;
			}
		}
		if (udpTransportMapping != null) {
			try {
				udpTransportMapping.close();
			} catch (IOException ex2) {
				udpTransportMapping = null;
			}
		}

		return true;
	}

    @Override
    public boolean connect(String ipAddress, Integer port) throws Exception {
        // TODO 自動產生的方法 Stub
        return false;
    }

    @Override
    public List<String> sendCommands(List<ScriptServiceVO> scriptList, ConfigInfoVO configInfoVO,
            StepServiceVO ssVO) throws Exception {
        // TODO 自動產生的方法 Stub
        return null;
    }
}
