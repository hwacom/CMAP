package com.cmap.plugin.module.report;

import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;

import net.sf.jasperreports.engine.JasperPrint;

public interface ReportService {

	public void createReportAndSendMailBatch(String reportType, String reportName, List<String> mailtoList);
	
	public void createReportAndSendMail(String reportType, JasperPrint jasperPrint);
	
	public DataSource reportTransfer2DataSource(String reportType, JasperPrint jasperPrint) throws Exception;

	/**
	 * 發送mail
	 * @param reportType
	 * @param toAddress
	 * @param ccAddress
	 * @param bccAddress
	 * @param subject
	 * @param mailContent
	 * @param aAttachment 不為空時為不落地檔發送
	 * @param filePathList 不為空時為取落地檔發送
	 * @throws Exception
	 */
	public void sendMail(String reportType, String[] toAddress, String[] ccAddress, String[] bccAddress, String subject,
			String mailContent, DataSource aAttachment, ArrayList<String> filePathList) throws Exception;

}
