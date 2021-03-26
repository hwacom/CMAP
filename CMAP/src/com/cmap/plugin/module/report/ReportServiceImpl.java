package com.cmap.plugin.module.report;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.vo.DeviceDAOVO;
import com.cmap.service.DeviceService;
import com.cmap.service.MibService;
import com.cmap.service.impl.CommonServiceImpl;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleCsvReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service("ReportService")
public class ReportServiceImpl extends CommonServiceImpl implements ReportService {
	@Log
    private static Logger log;
	
	@Autowired
    private DeviceDAO deviceDAO;
	
	@Autowired
    private MibService mibService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private JavaMailSenderImpl mailSender;

	@Override
	public void createReportAndSendMailBatch(String reportType, String reportName, List<String> mailtoList) {
        try {
        	Resource resource = new ClassPathResource("jasper/"+reportName+".jasper");
            FileInputStream fis = new FileInputStream(resource.getFile());
            
        	Map<String, Object> parameters = new HashMap<>();
        	parameters.put("title", reportName);
            List<Map<String, Object>> list = getMapmodel(reportName);
            
            System.out.println(list.toString());
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
            JasperPrint jasperPrint = JasperFillManager.fillReport(fis, parameters, ds);
            
			createReportAndSendMail(reportType, jasperPrint);
			
        } catch (JRException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	private List getMapmodel(String reportName){
		List<Map<String, Object>> mapList = new ArrayList();
		switch (reportName) {
		case "sample":
			mapList = beanTransfer2Map(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
			break;

		default:
			break;
		}
		
		return mapList;
	}
	
	private List beanTransfer2Map(List retList) {
    	List<Map<String, Object>> mapList = new ArrayList();
		Map<String, Object> map = null;
		try {
			for (Object obj : retList) {
				BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
				map = new HashMap<String, Object>();
				for (PropertyDescriptor property : propertyDescriptors) {
					String key = property.getName();
					// 過濾class屬性
					if (!key.equals("class")) {
						// 得到property對應的getter方法
						Method getter = property.getReadMethod();
						Object value = getter.invoke(obj);

						map.put(key, value);
					}

				}
				mapList.add(map);
			}

		} catch (Exception e) {
			System.out.println("transBean2Map Error " + e);
		}
		
    	return mapList;
    }
	
	@Override
	public void createReportAndSendMail(String reportType, JasperPrint jasperPrint) {
		DataSource aAttachment;
		try {
			aAttachment = reportTransfer2DataSource(reportType, jasperPrint);
		
			if(aAttachment != null) {
				sendMail(reportType, new String[] {"owen.chang@hwacom.com"}, null, null, "Testing report Email", "Testing Email", aAttachment, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public DataSource reportTransfer2DataSource(String reportType, JasperPrint jasperPrint )  throws Exception {

		DataSource aAttachment = null;
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		switch (reportType) {
		case "pdf":
			JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
            aAttachment =  new ByteArrayDataSource(byteArrayOutputStream.toByteArray(), "application/pdf");
            break;
		case "html":
	        JasperExportManager.exportReportToHtmlFile(jasperPrint, byteArrayOutputStream.toString());
	        aAttachment =  new ByteArrayDataSource(byteArrayOutputStream.toByteArray(), "text/html");
	        break;
	    case "xml":
	        JasperExportManager.exportReportToXmlFile(jasperPrint, byteArrayOutputStream.toString(), true);
	        aAttachment =  new ByteArrayDataSource(byteArrayOutputStream.toByteArray(), "text/xml");
	        break;
		case "csv":
			SimpleCsvReportConfiguration reportConfigCSV = new SimpleCsvReportConfiguration();
            JRCsvExporter csvExporter = new JRCsvExporter();
            csvExporter.setConfiguration(reportConfigCSV);
            csvExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            csvExporter.setExporterOutput(new SimpleWriterExporterOutput(byteArrayOutputStream));
            csvExporter.exportReport();
            
            aAttachment =  new ByteArrayDataSource(byteArrayOutputStream.toByteArray(), "application/octet-stream");
            
			break;
		case "xlsx":
			SimpleXlsxReportConfiguration reportConfigXLSX = new SimpleXlsxReportConfiguration();
			reportConfigXLSX.setOnePagePerSheet(true);
			reportConfigXLSX.setIgnoreGraphics(false);
            JRXlsxExporter xlsxExporter = new JRXlsxExporter();
            xlsxExporter.setConfiguration(reportConfigXLSX);
            xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
            xlsxExporter.exportReport();
            
            aAttachment =  new ByteArrayDataSource(byteArrayOutputStream.toByteArray(), "application/octet-stream");
            
			break;
		case "xls":
			SimpleXlsReportConfiguration reportConfigXLS = new SimpleXlsReportConfiguration();
			reportConfigXLS.setOnePagePerSheet(true);
			reportConfigXLS.setIgnoreGraphics(false);
			JRXlsExporter xlsExporter = new JRXlsExporter();
			xlsExporter.setConfiguration(reportConfigXLS);
			xlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
			xlsExporter.exportReport();
            
            aAttachment =  new ByteArrayDataSource(byteArrayOutputStream.toByteArray(), "application/xls");
			break;
		default:
			return null;
		}
		
		return aAttachment;
    }
	
	@Override
	public void sendMail(String reportType, String[] toAddress, String[] ccAddress, String[] bccAddress, String subject,
			String mailContent, DataSource aAttachment, ArrayList<String> filePathList) throws Exception {

		if(filePathList != null && filePathList.size() > 0) {
			sendMail(toAddress, ccAddress, bccAddress, subject, mailContent, filePathList);
			
		}else if(aAttachment != null){
			
			MimeMessage mimeMessage = javaMailSenderImpl().createMimeMessage();
			MimeMessageHelper mailMsg = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mailMsg.setFrom(Env.MAIL_FROM_ADDRESS);
			mailMsg.setTo(toAddress);
			if(ccAddress != null && ccAddress.length > 0)mailMsg.setCc(ccAddress);
			if(bccAddress != null && bccAddress.length > 0)mailMsg.setBcc(bccAddress);
			mailMsg.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
			mailMsg.setText(mailContent, false);
			mailMsg.addAttachment("report."+reportType,aAttachment);
	
			mailSender.send(mimeMessage);
		}
	}
	
}
