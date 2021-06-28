package com.cmap.plugin.module.report;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.vo.DeviceDAOVO;
import com.cmap.service.DeliveryService;
import com.cmap.service.DeviceService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleDocxExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Controller
@RequestMapping("/report")
public class ReportController extends BaseController {
	@Log
	private static Logger log;
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DeviceDAO deviceDAO;
		
	private void initMenu(Model model, HttpServletRequest request) {
		try {
			
		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
		}
	}
	
	@RequestMapping("/report2")
    public void createtopdf(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = new ClassPathResource("jasper/report2.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        ServletOutputStream os = response.getOutputStream();
        try {
            Map parameters = new HashMap<>();
            Connection conn = getConnection();
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, conn);
            JasperExportManager.exportReportToPdfStream(print, os);
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
	
	/**
     * 返回一个mysql的数据连接对象
     *
     * @return
     * @throws Exception
     */
    public Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/cmap?serverTimezone=UTC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, "root", "changeme");
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 可以带有参数筛选的JDBC连接源填充
     *
     * @param request
     * @param response
     * @param params
     * @throws Exception
     */
    @RequestMapping("/params")
    public void createpdf33(HttpServletRequest request, HttpServletResponse response, String params) throws Exception {
        Resource resource = new ClassPathResource("jasper/hasparams.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        ServletOutputStream os = response.getOutputStream();
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("id", params);
            Connection conn = getConnection();
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, conn);
            JasperExportManager.exportReportToPdfStream(print, os);
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
    
    /**
     * 使用javabean填充数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/bean2")
    public void javabeantest222(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = new ClassPathResource("jasper/bean2.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        ServletOutputStream os = response.getOutputStream();
        try {
            Map<String, Object> parameters = new HashMap<>();
            List<Map<String, Object>> list = getMapmodel(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);

//            Connection conn = getConnection();
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, ds);
            JasperExportManager.exportReportToPdfStream(print, os);
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
    
    /**
     * 使用List<Map<String，Object>>集合填充数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/testmap")
    public void testmapcollection(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = new ClassPathResource("jasper/Coffee2.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        ServletOutputStream os = response.getOutputStream();
        try {
            Map<String, Object> parameters = new HashMap<>();
            List<Map<String, Object>> list = getMapmodel(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
            System.out.println(list.toString());
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
//            Connection conn = getConnection();
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, ds);
            String filename = "javabean";
            response.setContentType("application/pdf");
//            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8") + ".pdf");
            JasperExportManager.exportReportToPdfStream(print, os);
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
    
    /**
     * 预览html格式
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/html")
    public void exportHtml(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = new ClassPathResource("jasper/Coffee2.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        ServletOutputStream os = response.getOutputStream();
        try {
            Map<String, Object> parameters = new HashMap<>();
            List<Map<String, Object>> list = getMapmodel(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
//            Connection conn = getConnection();
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, ds);
//            JasperExportManager.exportReportToPdfStream(print,os);
            JasperExportManager.exportReportToHtmlFile(print, request.getServletContext().getRealPath("/") + "test.html");
            response.sendRedirect("test.html");
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
    
    /**
     * 在服务器端批量导出生成word文件，可以一次性导出多个JasperPrint
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/report.doc")
    public void listJasperPrint(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = new ClassPathResource("jasper/Coffee2.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        ServletOutputStream os = response.getOutputStream();
        try {
            Map<String, Object> parameters = new HashMap<>();
            List<Map<String, Object>> list = getMapmodel(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
//            Connection conn = getConnection();
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, ds);
//            JasperExportManager.exportReportToPdfStream(print,os);
//            JasperExportManager.exportReportToHtmlFile(print, request.getServletContext().getRealPath("/") + "test.html");
//            response.sendRedirect("test.html");
            JRDocxExporter exporter = new JRDocxExporter();
            List<JasperPrint> plist = new ArrayList<>();
            plist.add(print);
            exporter.setExporterInput(SimpleExporterInput.getInstance(plist));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new File("D:/javabean.doc")));
            exporter.exportReport();

            System.out.println("批量导出word文件成功");

        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
    
    /**
     * 把jrxml文件编译成 jrasper文件，之后填充数据
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping("/compile")
    public void compilejrxml(HttpServletRequest request,HttpServletResponse response)throws IOException,SQLException {
        String path = "D:\\Idea Projects\\jasper\\src\\main\\resources\\templates\\report6.jrxml";
        File  file = new File(path);
        String parentPath = file.getParent();
        String jrxmlDestSourcePath = parentPath+"/report9.jasper";
//        String jrXMLPath = this.getServletContext().getRealPath("/")+ "WEB-INF\\jrxml\\"+JRXML+".jrxml";
//		log.debug(jrXMLPath);
        try {
            JasperCompileManager.compileReportToFile(path,
                    jrxmlDestSourcePath);
        } catch (JRException e) {
            e.printStackTrace();
        }
        File jfile = new File(parentPath+"\\report9.jasper");

        FileInputStream fis = new FileInputStream(jfile);
        ServletOutputStream os = response.getOutputStream();
        try {
            Map<String, Object> parameters = new HashMap<>();
            List<Map<String, Object>> list = getMapmodel(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
            System.out.println(list.toString());
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, ds);
            JasperExportManager.exportReportToPdfStream(print, os);
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
    
    /**
     * 下載報表pdf格式
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/downloadpdf")
    public void downloadPdf(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = new ClassPathResource("jasper/Coffee2.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        ServletOutputStream os = response.getOutputStream();
        try {
            Map<String, Object> parameters = new HashMap<>();
            List<Map<String, Object>> list = getMapmodel(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
            System.out.println(list.toString());
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, ds);
            response.setContentType("application/pdf");
            response.setCharacterEncoding("UTF-8");
            String filename = "javabean";
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8") + ".pdf");
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
    
    /**
     * 報表下載xls格式
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/downloadxls")
    public void downloadXls(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = new ClassPathResource("jasper/Coffee2.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        ServletOutputStream os = response.getOutputStream();
        try {
            Map<String, Object> parameters = new HashMap<>();
            List<Map<String, Object>> list = getMapmodel(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
            System.out.println(list.toString());
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, ds);
            response.setContentType("application/xls");//application/vnd.ms-excel
            response.setCharacterEncoding("UTF-8");
            String filename = "javabean";
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8") + ".xls");
            JRXlsExporter exporter = new JRXlsExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
            SimpleXlsExporterConfiguration configuration = new SimpleXlsExporterConfiguration();
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
    
    /**
     * 報表下載xls格式
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/downloadxlsx")
    public void downloadXlsx(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = new ClassPathResource("jasper/Coffee2.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        ServletOutputStream os = response.getOutputStream();
        try {
        	Map<String, Object> parameters = new HashMap<>();
            List<Map<String, Object>> list = getMapmodel(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
            System.out.println(list.toString());
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, ds);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=jasperReport.xlsx");
            response.setContentType("application/octet-stream");
            JRXlsxExporter exporter = new JRXlsxExporter();
            SimpleXlsxReportConfiguration reportConfigXLS = new SimpleXlsxReportConfiguration();
            reportConfigXLS.setSheetNames(new String[] { "sheet1" });
            exporter.setConfiguration(reportConfigXLS);
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
    
    /**
     * 報表下載word（DOC）格式
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/downloadword")
    public void downloadWord(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = new ClassPathResource("jasper/Coffee2.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        ServletOutputStream os = response.getOutputStream();
        try {
            Map<String, Object> parameters = new HashMap<>();
            List<Map<String, Object>> list = getMapmodel(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
            System.out.println(list.toString());
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
            JasperPrint print = JasperFillManager.fillReport(fis, parameters, ds);
            response.setContentType("application/ms-word");
            response.setCharacterEncoding("UTF-8");
            String filename = "javabean";
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8") + ".doc");
            JRDocxExporter exporter = new JRDocxExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
            SimpleDocxExporterConfiguration configuration = new SimpleDocxExporterConfiguration();
            exporter.setConfiguration(configuration);
            exporter.exportReport();

        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            behaviorLog(request);
        }
    }
        
    /**
     * send 報表
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/reportsendmail", method = RequestMethod.GET)
    public String reportsendmail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = new ClassPathResource("jasper/Coffee.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        
        try {
        	String type = request.getParameter("type");
        	Map<String, Object> parameters = new HashMap<>();
        	parameters.put("title", "testTitle");
            List<Map<String, Object>> list = getMapmodel(deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO()));
            
            System.out.println(list.toString());
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);
            JasperPrint jasperPrint = JasperFillManager.fillReport(fis, parameters, ds);
            
			reportService.createReportAndSendMail(type, jasperPrint);
			
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            behaviorLog(request);
        }
        return "admin/admin_env";
    }
    
    private List getMapmodel(List retList) {
//    	List<DeviceLoginInfo> list = deviceService.findDeviceLoginInfoList(new DeviceLoginInfoServiceVO());
//    	List<DeviceList> retList = deviceDAO.findDeviceListByDAOVO(new DeviceDAOVO());
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

}
