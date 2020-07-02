package com.cmap.plugin.module.netflowpoller;

import java.util.ArrayList;
import java.util.List;
import com.cmap.service.vo.CommonServiceVO;

public class NetFlowTraceVO extends CommonServiceVO {

	private String queryDataId;
	private String queryGroupId;
	private String querySchoolId;
	private String queryIp;
	private String queryPort;
	private String querySourceIp;
	private String querySourcePort;
	private String queryDestinationIp;
	private String queryDestinationPort;
	private String querySenderIp;
	private String queryMac;
	private String queryDate;
	private String queryDateBegin;
	private String queryDateEnd;
	private String queryTimeBegin;
	private String queryTimeEnd;
	private String queryValue;
	private String queryCondition;
	private String queryDateStr;
	private String queryTimeBeginStr;
	private String queryTimeEndStr;
	private String querySensorId;

	private List<NetFlowTraceVO> matchedList = new ArrayList<>();
	private int totalCount = 0;

	private String dataId;
	private String groupId;
	private String groupName;
	private String fromDateTime;
	private String sourceIP;
	private String sourcePort;
	private String sourceMAC;
	private String destinationIP;
	private String destinationPort;
	private String destinationMAC;
	private String size;
	private String session;
	private String inboundInterface;
	private String outboundInterface;
	private String nextHop;
	private String sourceIPInGroup;
	private String destinationIPInGroup;
	
	private String totalFlow;

	public String getQueryGroupId() {
		return queryGroupId;
	}

	public void setQueryGroupId(String queryGroupId) {
		this.queryGroupId = queryGroupId;
	}

	public String getQueryDateBegin() {
		return queryDateBegin;
	}

	public void setQueryDateBegin(String queryDateBegin) {
		this.queryDateBegin = queryDateBegin;
	}

	public String getQueryDateEnd() {
		return queryDateEnd;
	}

	public void setQueryDateEnd(String queryDateEnd) {
		this.queryDateEnd = queryDateEnd;
	}

	public String getQueryIp() {
		return queryIp;
	}

	public void setQueryIp(String queryIp) {
		this.queryIp = queryIp;
	}

	public String getQueryPort() {
		return queryPort;
	}

	public void setQueryPort(String queryPort) {
		this.queryPort = queryPort;
	}

	public String getQueryMac() {
		return queryMac;
	}

	public void setQueryMac(String queryMac) {
		this.queryMac = queryMac;
	}

	public String getFromDateTime() {
		return fromDateTime;
	}

	public void setFromDateTime(String fromDateTime) {
		this.fromDateTime = fromDateTime;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getSourceMAC() {
		return sourceMAC;
	}

	public void setSourceMAC(String sourceMAC) {
		this.sourceMAC = sourceMAC;
	}

	public String getDestinationIP() {
		return destinationIP;
	}

	public void setDestinationIP(String destinationIP) {
		this.destinationIP = destinationIP;
	}

	public String getDestinationPort() {
		return destinationPort;
	}

	public void setDestinationPort(String destinationPort) {
		this.destinationPort = destinationPort;
	}

	public String getDestinationMAC() {
		return destinationMAC;
	}

	public void setDestinationMAC(String destinationMAC) {
		this.destinationMAC = destinationMAC;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getInboundInterface() {
		return inboundInterface;
	}

	public void setInboundInterface(String inboundInterface) {
		this.inboundInterface = inboundInterface;
	}

	public String getOutboundInterface() {
		return outboundInterface;
	}

	public void setOutboundInterface(String outboundInterface) {
		this.outboundInterface = outboundInterface;
	}

	public String getNextHop() {
		return nextHop;
	}

	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getQuerySourceIp() {
		return querySourceIp;
	}

	public void setQuerySourceIp(String querySourceIp) {
		this.querySourceIp = querySourceIp;
	}

	public String getQuerySourcePort() {
		return querySourcePort;
	}

	public void setQuerySourcePort(String querySourcePort) {
		this.querySourcePort = querySourcePort;
	}

	public String getQueryDestinationIp() {
		return queryDestinationIp;
	}

	public void setQueryDestinationIp(String queryDestinationIp) {
		this.queryDestinationIp = queryDestinationIp;
	}

	public String getQueryDestinationPort() {
		return queryDestinationPort;
	}

	public void setQueryDestinationPort(String queryDestinationPort) {
		this.queryDestinationPort = queryDestinationPort;
	}

	public String getQuerySenderIp() {
		return querySenderIp;
	}

	public void setQuerySenderIp(String querySenderIp) {
		this.querySenderIp = querySenderIp;
	}

	public String getQueryDate() {
		return queryDate;
	}

	public void setQueryDate(String queryDate) {
		this.queryDate = queryDate;
	}

	public String getQueryValue() {
		return queryValue;
	}

	public void setQueryValue(String queryValue) {
		this.queryValue = queryValue;
	}

	public String getQueryCondition() {
		return queryCondition;
	}

	public void setQueryCondition(String queryCondition) {
		this.queryCondition = queryCondition;
	}

	public List<NetFlowTraceVO> getMatchedList() {
		return matchedList;
	}

	public void setMatchedList(List<NetFlowTraceVO> matchedList) {
		this.matchedList = matchedList;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getQuerySchoolId() {
		return querySchoolId;
	}

	public void setQuerySchoolId(String querySchoolId) {
		this.querySchoolId = querySchoolId;
	}

	public String getQueryTimeBegin() {
		return queryTimeBegin;
	}

	public void setQueryTimeBegin(String queryTimeBegin) {
		this.queryTimeBegin = queryTimeBegin;
	}

	public String getQueryTimeEnd() {
		return queryTimeEnd;
	}

	public void setQueryTimeEnd(String queryTimeEnd) {
		this.queryTimeEnd = queryTimeEnd;
	}

	public String getQueryDateStr() {
		return queryDateStr;
	}

	public void setQueryDateStr(String queryDateStr) {
		this.queryDateStr = queryDateStr;
	}

	public String getQueryTimeBeginStr() {
		return queryTimeBeginStr;
	}

	public void setQueryTimeBeginStr(String queryTimeBeginStr) {
		this.queryTimeBeginStr = queryTimeBeginStr;
	}

	public String getQueryTimeEndStr() {
		return queryTimeEndStr;
	}

	public void setQueryTimeEndStr(String queryTimeEndStr) {
		this.queryTimeEndStr = queryTimeEndStr;
	}

	public String getQuerySensorId() {
		return querySensorId;
	}

	public void setQuerySensorId(String querySensorId) {
		this.querySensorId = querySensorId;
	}

	public String getTotalFlow() {
		return totalFlow;
	}

	public void setTotalFlow(String totalFlow) {
		this.totalFlow = totalFlow;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getQueryDataId() {
		return queryDataId;
	}

	public void setQueryDataId(String queryDataId) {
		this.queryDataId = queryDataId;
	}

    public String getSourceIPInGroup() {
        return sourceIPInGroup;
    }

    public void setSourceIPInGroup(String sourceIPInGroup) {
        this.sourceIPInGroup = sourceIPInGroup;
    }

    public String getDestinationIPInGroup() {
        return destinationIPInGroup;
    }

    public void setDestinationIPInGroup(String destinationIPInGroup) {
        this.destinationIPInGroup = destinationIPInGroup;
    }
}
