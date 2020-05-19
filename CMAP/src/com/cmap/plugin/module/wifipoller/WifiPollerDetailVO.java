package com.cmap.plugin.module.wifipoller;

import com.cmap.service.vo.CommonServiceVO;

public class WifiPollerDetailVO extends CommonServiceVO {

    private String queryClientMac;
    private String queryGroupName;
    private String queryDateBegin;
    private String queryDateEnd;
    private String queryTimeBegin;
    private String queryTimeEnd;

    private String pollingTime;
    private String clientMac;
    private String uploadTraffic;
    private String downloadTraffic;
    private String totalTraffic;
    private String rssi;
    private String noise;
    private String snr;
    
	public String getQueryClientMac() {
		return queryClientMac;
	}
	public void setQueryClientMac(String queryClientMac) {
		this.queryClientMac = queryClientMac;
	}
	public String getQueryGroupName() {
		return queryGroupName;
	}
	public void setQueryGroupName(String queryGroupName) {
		this.queryGroupName = queryGroupName;
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
	public String getPollingTime() {
		return pollingTime;
	}
	public void setPollingTime(String pollingTime) {
		this.pollingTime = pollingTime;
	}
	public String getClientMac() {
		return clientMac;
	}
	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}
	public String getTotalTraffic() {
		return totalTraffic;
	}
	public void setTotalTraffic(String totalTraffic) {
		this.totalTraffic = totalTraffic;
	}
	public String getUploadTraffic() {
		return uploadTraffic;
	}
	public void setUploadTraffic(String uploadTraffic) {
		this.uploadTraffic = uploadTraffic;
	}
	public String getDownloadTraffic() {
		return downloadTraffic;
	}
	public void setDownloadTraffic(String downloadTraffic) {
		this.downloadTraffic = downloadTraffic;
	}
	public String getRssi() {
		return rssi;
	}
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}
	public String getNoise() {
		return noise;
	}
	public void setNoise(String noise) {
		this.noise = noise;
	}
	public String getSnr() {
		return snr;
	}
	public void setSnr(String snr) {
		this.snr = snr;
	}
	
}
