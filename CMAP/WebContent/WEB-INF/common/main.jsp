<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="com.cmap.Env" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="taglib.jsp" %>
<!DOCTYPE html>

<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="cache-control" content="no-cache" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title><spring:message code="cmap.title" /></title>
	<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
	<meta name="description" content="">
    <meta name="author" content="">
    <meta name="ctx" content="${pageContext.request.contextPath}" />
    <meta name="timeout" content="${timeout}" />
    <!-- Favicon icon -->
    <!-- Hwacom -->
    <link rel="icon" sizes="16x16" href="${pageContext.request.contextPath}/resources/images/hwacom_icon.ico">
    
    <!-- 台灣大哥大 -->
    <!--
    <link rel="icon" sizes="16x16" href="${pageContext.request.contextPath}/resources/images/icon_taiwan_mobile.ico">
	 -->
	 
	<!-- 桃機 -->
	<!--
	<link rel="icon" sizes="16x16" href="${pageContext.request.contextPath}/resources/images/icon_t3.ico">
	 -->
	  
	<!-- 亞太 -->
	<!-- 
	<link rel="icon" sizes="16x16" href="${pageContext.request.contextPath}/resources/images/icon_apt.ico">
	 -->
	 
	<!-- 苗栗 -->
	<!-- 
	<link rel="icon" href="${pageContext.request.contextPath}/resources/images/icon_maoli.ico">
	 -->
	 
	<!-- 新北市 -->
	<!-- 
	<link rel="icon" href="${pageContext.request.contextPath}/resources/images/icon_new_taipei_city.ico">
	-->
	   
    <!-- Bootstrap Core CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap-Select-v1.13.9 CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap-select/bootstrap-select.min.css" rel="stylesheet">
    <!-- JQuery-UI -->
    <!-- <link href="${pageContext.request.contextPath}/resources/css/jquery-ui/jquery-ui.min.css" rel="stylesheet"> -->
    <link href="${pageContext.request.contextPath}/resources/css/jquery-ui/jquery-ui.structure.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/jquery-ui/jquery-ui.theme.min.css" rel="stylesheet">
    <!-- dataTable -->
	<link href="${pageContext.request.contextPath}/resources/DataTables/datatables.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/main.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/blog.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/fontawesome/all.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/jquery-scrollbar/jquery.scrollbar.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/flag-icon-css-master/flag-icon.min.css" rel="stylesheet">
	
	<!-- Core Javascript -->
	<script src="${pageContext.request.contextPath}/resources/js/jquery/jquery-3.3.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/dist/jquery.validate.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/dist/additional-methods.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/dist/localization/messages_zh_TW.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/popper/popper.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap/bootstrap.min.js"></script>
    <!-- Bootstrap-Select-v1.13.9 JS -->
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap-select/bootstrap-select.min.js"></script>
    <!-- Bootstrap-Select-v1.13.9 (i18n) JS -->
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap-select/i18n/defaults-zh_TW.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/jquery-ui/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/jquery-scrollbar/jquery.scrollbar.min.js"></script>
    <!-- Icons -->
    <script src="${pageContext.request.contextPath}/resources/js/feather-icons/feather.min.js"></script>
	<!-- dataTable -->
	<!-- <script src="${pageContext.request.contextPath}/resources/js/dataTable/jquery.dataTables.min.js"></script> -->
	<script src="${pageContext.request.contextPath}/resources/DataTables/datatables.min.js"></script>
	<!-- Underscore -->
	<script src="${pageContext.request.contextPath}/resources/js/underscore/underscore-min.js"></script>
	<!-- blockUI -->
	<script src="${pageContext.request.contextPath}/resources/js/blockUI/jquery.blockUI.js"></script>
	<!-- cleave -->
	<script src="${pageContext.request.contextPath}/resources/js/cleave/cleave.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/modernizr/modernizr-custom.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/FileSaver/FileSaver.min.js"></script>
	<!-- D3 -->
	<script charset="utf-8" type="text/javascript" src="${pageContext.request.contextPath}/resources/D3/d3.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/resources/js/custom/min/common.min.js"></script>
	
	<script>
		$(function () {
		  $('[data-toggle="popover"]').popover()
		})
	</script>
</head>

<c:set var="__SHOW__" value="Y" scope="request"/>

<body>
	<div class="loader"></div>
	<div class="mask" style="display: none;"></div>
	<div class="processing" style="display: none;"></div>
	
    <nav class="navbar navbar-dark fixed-top flex-md-nowrap p-0 shadow navbar-bg">
      <a href="${pageContext.request.contextPath}/index">
      	<!-- Hwacom -->
		<img class="img" src="${pageContext.request.contextPath}/resources/images/hwacom.png" width="auto" height="40" style="padding-top: 3px" />
		  
		<!-- Innolux 群創 -->
		<!-- 
		<img class="img" src="${pageContext.request.contextPath}/resources/images/innolux_logo.png" width="auto" height="30" style="padding-top: 3px" />
		 -->
		 
		<!-- 新北 -->
		<!-- 
      	<img class="img" src="${pageContext.request.contextPath}/resources/images/logo_new_taipei_icon.png" width="auto" height="40" style="padding-top: 3px" />
  		<img class="img web-only" src="${pageContext.request.contextPath}/resources/images/logo_new_taipei_word.png" width="auto" height="40" style="padding-top: 3px" />
		-->
		
      	<!-- 苗栗 -->
      	<!-- 
      	<img class="img" src="${pageContext.request.contextPath}/resources/images/Logo_icon.png" width="auto" height="40" style="padding-top: 3px" />
  		<img class="img web-only" src="${pageContext.request.contextPath}/resources/images/Logo_word.png" width="auto" height="40" style="padding-top: 3px" />
 		 -->
 		  
 		<!-- 亞太 -->
 		<!-- 
 		<img class="img" src="${pageContext.request.contextPath}/resources/images/aptg_logo_icon.png" width="auto" height="30" style="padding-top: 3px" />
		<img class="img web-only" src="${pageContext.request.contextPath}/resources/images/aptg_logo_word.png" width="auto" height="23" style="padding-top: 3px" />
 		 -->
 		 
 		<!-- 桃機 -->
 		<!-- 
 		<img class="img" src="${pageContext.request.contextPath}/resources/images/logo_new_icon.png" width="auto" height="40" style="padding-top: 3px" />
		<img class="img web-only" src="${pageContext.request.contextPath}/resources/images/logo_new_word_short.png" width="auto" height="40" style="padding-top: 3px" />
 		 -->
 		 
 		<!-- 台灣大哥大 -->
 		<!-- 
      	<img class="img" src="${pageContext.request.contextPath}/resources/images/logo_taiwan_mobile_icon.png" width="auto" height="40" style="padding-top: 3px" />
  		<img class="img web-only" src="${pageContext.request.contextPath}/resources/images/logo_taiwan_mobile_word.png" width="auto" height="30" style="padding-top: 3px" />
 		 -->
 		 
 		 <span class="font-weight-bold title-font"
			style="color: rgb(82, 82, 82); border-bottom: 1px; border-bottom-color: black; border-bottom-style: dashed; font-family: 游ゴシック;"><spring:message
					code="cmap.title" /></span>	
					
		</a>
      <ul class="navbar-nav">
        <li class="nav-item text-nowrap">
          <a class="nav-link" href="${pageContext.request.contextPath}/logout"><span data-feather="log-out"></span></a>
        </li>
        <li class="nav-item text-nowrap">
          <a class="nav-link" href="#" data-container="body" data-toggle="popover" data-placement="bottom" title="<spring:message code="user.account" />" data-content="${userInfo }"><span data-feather="user"></span></a>
        </li>
        <li class="nav-item text-nowrap">
          <a class="nav-link" href="#" onclick="toggleMenu()"><span id="menu-icon" data-feather="menu"></span></a>
        </li>
      </ul>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <nav class="web-menu col-md-2 d-none d-md-block sidebar sidebar-bg">
          <div class="sidebar-sticky">
            <ul class="nav flex-column">
              <!-- [系統操作手冊下載] START -->
              <c:if test="${Env.SHOW_MENU_TREE_USER_GUIDE_DOWNLOAD eq __SHOW__}">
	              <li class="nav-item">
	                <a class="nav-link toggleMenuLink" id="toggleMenu_prtg" href="#" onclick="javascript:window.open('${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/${pageContext.request.contextPath}resource/download/userGuide','_blank')">
	                  <span data-feather="book-open"></span>
	                  	<span><spring:message code="func.user.guide.download" /></span>
	                </a>
	              </li>
	          </c:if>
	          <!-- [系統操作手冊下載] END -->
	          
              <!-- [間控平台] START -->
              <c:if test="${Env.SHOW_MENU_TREE_CONTROL_PLATFORM eq __SHOW__}">
	              <li class="nav-item">
	                <a class="nav-link toggleMenuLink" id="toggleMenu_prtg" href="#">
	                  <span data-feather="layout"></span>
	                  	<span><spring:message code="menu.monitor" />&nbsp;<span id="toggleMenu_prtg_icon" data-feather="chevron-down"></span></span>
	                </a>
	                <ul aria-expanded="false" id="toggleMenu_prtg_items" class="collapse">
	                	<!-- [首頁] START -->
	                	<c:if test="${Env.SHOW_MENU_ITEM_PRTG_INDEX eq __SHOW__}">
	                		<li class="subMenu-item">
		                    	<a id="mp_index" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/prtg/index')">
		                    	  <span data-feather="home"></span>
		                    		<span><spring:message code="func.prtg.index" /></span>
		                    	</a>
		                    </li>
	                	</c:if>
	                	<!-- [首頁] END -->
	                    
	                    <!-- [DASHBOARD] START -->
				        <c:if test="${Env.SHOW_MENU_ITEM_PRTG_DASHBOARD eq __SHOW__}">
		                    <li class="subMenu-item">
		                    	<a id="mp_dashboard" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/prtg/dashboard')">
		                    	  <span data-feather="grid"></span>
		                    	  	<span><spring:message code="func.prtg.dashboard" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [DASHBOARD] END -->
	                    
	                    <!-- [拓樸圖] START -->
				        <c:if test="${Env.SHOW_MENU_ITEM_PRTG_TOPOGRAPHY eq __SHOW__}">
				        	<li class="subMenu-item">
		                    	<a id="mp_topography" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/prtg/topography')">
		                    	  <span data-feather="git-merge"></span>
		                    	  	<span><spring:message code="func.prtg.topography" /></span>
		                    	</a>
		                    </li>
				        </c:if>
				        <!-- [拓樸圖] END -->
				        
				        <!-- [拓樸圖] START -->
			            <c:if test="${Env.SHOW_MENU_ITEM_PRTG_TOPOGRAPHY eq __SHOW__}">
	                		<li class="subMenu-item">
				                <a id="cm_topography" href="${pageContext.request.contextPath}/topography">
				                  <span data-feather="git-merge"></span>
	                    	  	<span><spring:message code="func.prtg.topography" /></span>
				                </a>
				            </li>
	                	</c:if>
		                <!-- [拓樸圖] END -->
		                
				        <!-- [流量統計] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_PRTG_NET_FLOW_STATICS eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="mp_netFlowSummary" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/prtg/netFlowSummary')">
		                    	  <span data-feather="activity"></span>
		                    	  	<span><spring:message code="func.prtg.net.flow.statistics" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [流量統計] END -->
	                    
	                    <!-- [核心路由器出口流量圖] START -->
	                    <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	                    	<c:if test="${Env.SHOW_MENU_ITEM_PRTG_CR_NET_FLOW_OUTPUT eq __SHOW__}">
					        	<li class="subMenu-item">
			                    	<a id="mp_netFlowOutputCore" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/prtg/netFlowOutput/core')">
			                    	  <span data-feather="activity"></span>
			                    	  	<span><spring:message code="func.prtg.core.router.net.flow.output" /></span>
			                    	</a>
			                    </li>
					        </c:if>
	                    </sec:authorize>
	                    <!-- [核心路由器出口流量圖] END -->
	                    
	                    <!-- [Firewall出口流量圖] START -->
	                    <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	                    	<c:if test="${Env.SHOW_MENU_ITEM_FIREWALL_OUTPUT eq __SHOW__}">
					        	<li class="subMenu-item">
			                    	<a id="mp_firewallOutput" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/prtg/firewallOutput')">
			                    	  <span data-feather="activity"></span>
			                    	  	<span><spring:message code="func.prtg.firewall.output" /></span>
			                    	</a>
			                    </li>
					        </c:if>
	                    </sec:authorize>
	                    <!-- [Firewall出口流量圖] END -->
	                    
	                    <!-- [各校出口端流量圖] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_PRTG_NET_FLOW_OUTPUT eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="mp_netFlowOutput" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/prtg/netFlowOutput')">
		                    	  <span data-feather="activity"></span>
		                    	  	<span><spring:message code="func.prtg.net.flow.output" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [各校出口端流量圖] END -->
	                    	                    
	                    <!-- [各校即時IP流量排行] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_NET_FLOW_CURRNET_RANKING_TRAFFIC eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="mp_netFlowCurrentRanking_traffic" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/plugin/module/netFlow/ranking/traffic')">
		                    	  <span data-feather="activity"></span>
		                    	  	<span><spring:message code="func.net.flow.current.ranking.traffic" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [各校即時IP流量排行] END -->
	                    
	                    <!-- [所有學校即時IP流量排行] START -->
	                    <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
					        <c:if test="${Env.SHOW_MENU_ITEM_NET_FLOW_ALL_CURRNET_RANKING_TRAFFIC eq __SHOW__}">
					        	<li class="subMenu-item">
			                    	<a id="mp_netFlowCurrentRanking_traffic_all" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/plugin/module/netFlow/ranking/traffic/all')">
			                    	  <span data-feather="activity"></span>
			                    	  	<span><spring:message code="func.net.flow.all.current.ranking.traffic" /></span>
			                    	</a>
			                    </li>
					        </c:if>
	                    </sec:authorize>
	                    <!-- [所有學校即時IP流量排行] END -->
	                    
	                    <!-- [各校即時連線數排行] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_NET_FLOW_CURRNET_RANKING_SESSION eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="mp_netFlowCurrentRanking_session" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/plugin/module/netFlow/ranking/session')">
		                    	  <span data-feather="link"></span>
		                    	  	<span><spring:message code="func.net.flow.current.ranking.session" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [各校即時連線數排行] END -->
	                    
	                    <!-- [所有學校即時連線數排行] START -->
	                    <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
					        <c:if test="${Env.SHOW_MENU_ITEM_NET_FLOW_ALL_CURRNET_RANKING_SESSION eq __SHOW__}">
					        	<li class="subMenu-item">
			                    	<a id="mp_netFlowCurrentRanking_session_all" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/plugin/module/netFlow/ranking/session/all')">
			                    	  <span data-feather="link"></span>
			                    	  	<span><spring:message code="func.net.flow.all.current.ranking.session" /></span>
			                    	</a>
			                    </li>
					        </c:if>
	                    </sec:authorize>
	                    <!-- [所有學校即時連線數排行] END -->
	                    
	                    <!-- [Interface狀態清單] START -->
				        <c:if test="${Env.SHOW_MENU_ITEM_INTERFACE_STATUS_LIST eq __SHOW__}">
				        	<li class="subMenu-item">
		                    	<a id="mp_interfaceStatusList" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/plugin/module/portStatusViewer')">
		                    	  <span data-feather="hard-drive"></span>
		                    	  	<span><spring:message code="func.interface.status.list" /></span>
		                    	</a>
		                    </li>
				        </c:if>
	                    <!-- [Interface狀態清單] END -->
	                    
	                    <!-- [IP衝突查詢(IP/MAC/Port異動查詢)] START -->
			            <c:if test="${Env.SHOW_MENU_ITEM_IP_CONFLICT eq __SHOW__}">
			            	<li class="subMenu-item">
		                    	<a id="ip_record" href="${pageContext.request.contextPath}/plugin/module/ipMapping/change">
		                    	  <span data-feather="minimize-2"></span>
		                    		<span><spring:message code="func.plugin.ip.record" /></span>
		                    	</a>
		                    </li>
			            </c:if>
			            <!-- [IP衝突查詢(IP/MAC/Port異動查詢)] END -->
			            
	                    <!-- [Net flow查詢] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_PLUGIN_NET_FLOW eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="cm_netflow" href="${pageContext.request.contextPath}/plugin/module/netFlow">
		                    	  <span data-feather="shuffle"></span>
		                    	  	<span><spring:message code="func.plugin.net.flow" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [Net flow查詢] END -->
	                    
	                    <!-- [Wifi查詢] START -->
			            <c:if test="${Env.SHOW_MENU_ITEM_PLUGIN_WIFI_POLLER eq __SHOW__}">
			            	<li class="subMenu-item">
		                    	<a id="cm_wifi" href="${pageContext.request.contextPath}/plugin/module/wifiPoller">
		                    	  <span data-feather="wifi"></span>
		                    		<span><spring:message code="func.plugin.wifi.manage" /></span>
		                    	</a>
		                    </li>
			            </c:if>
			            <!-- [Wifi查詢] END -->
			            
			            <!-- [IP異動紀錄查詢] START -->
			            <c:if test="${Env.SHOW_MENU_ITEM_PLUGIN_IP_TRACE_POLLER eq __SHOW__}">
			            	<li class="subMenu-item">
		                    	<a id="cm_iptrace" href="${pageContext.request.contextPath}/plugin/module/ipTracePoller">
		                    	  <span data-feather="crosshair"></span>
		                    		<span><spring:message code="func.plugin.ip.trace.manage" /></span>
		                    	</a>
		                    </li>
			            </c:if>
			            <!-- [IP異動紀錄查詢] END -->
	                    
	                    <!-- [防火牆LOG查詢] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_PLUGIN_FIREWALL eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="cm_firewallLog" href="${pageContext.request.contextPath}/plugin/module/firewall/log">
		                    	  <span data-feather="shield"></span>
		                    	  	<span><spring:message code="func.plugin.firewall" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [防火牆LOG查詢] END -->
	                    
	                    <!-- [設備roop查詢] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_LOOP_SEARCH eq __SHOW__}">
		                    <li class="subMenu-item">
		                    	<a id="mp_loopSearch" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/prtg/loopSearch')">
		                    	  <span data-feather="rotate-cw"></span>
		                    	  	<span><spring:message code="func.plugin.loopSearch" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [設備roop查詢] END -->
	                </ul>
		          </li>
	          </c:if>
	          <!-- [間控平台] END -->
	          
	          <!-- [組態管理] START -->
	          <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	          	<c:if test="${Env.SHOW_MENU_TREE_CONFIG_MANAGEMENT eq __SHOW__}">
	          		<li class="nav-item">
		            	<a class="nav-link toggleMenuLink" id="toggleMenu_cm" href="#">
		                  <span data-feather="file-text"></span>
		                  	<span><spring:message code="menu.cm.manage" />&nbsp;<span id="toggleMenu_cm_icon" data-feather="chevron-down"></span></span>
		                </a>
		                <ul aria-expanded="false" id="toggleMenu_cm_items" class="collapse">
		                	<!-- [版本管理] START -->
		                	<c:if test="${Env.SHOW_MENU_ITEM_CM_VERSION_MANAGEMENT eq __SHOW__}">
		                		<li class="subMenu-item">
			                    	<a id="cm_manage" href="${pageContext.request.contextPath}/version/manage">
			                    	  <span data-feather="file-text"></span>
			                    		<span><spring:message code="func.version.manage" /></span>
			                    	</a>
			                    </li> 
		                	</c:if>
		                	<!-- [版本管理] END -->
		                	
		                	<!-- [版本備份] START -->
		                    <c:if test="${Env.SHOW_MENU_ITEM_CM_VERSION_BACKUP eq __SHOW__}">
		                		<li class="subMenu-item">
					                <a id="cm_backup" href="${pageContext.request.contextPath}/version/backup">
					                  <span data-feather="download"></span>
					                  	<span><spring:message code="func.version.backup" /></span>
					                </a>
					            </li>
		                	</c:if>
		                	<!-- [版本備份] END -->
		                	
		                	<!-- [版本還原] START -->
				            <c:if test="${Env.SHOW_MENU_ITEM_CM_VERSION_RESTORE eq __SHOW__}">
		                		<li class="subMenu-item">
					                <a id="cm_restore" href="${pageContext.request.contextPath}/version/restore">
					                  <span data-feather="upload"></span>
					                  	<span><spring:message code="func.version.restore" /></span>
					                </a>
					            </li>
		                	</c:if>
		                	<!-- [版本還原] END -->
		                	
		                	<!-- [腳本管理] START -->
				            <c:if test="${Env.SHOW_MENU_ITEM_CM_SCRIPT eq __SHOW__}">
				            	<li class="subMenu-item">
				                <a id="cm_script" href="${pageContext.request.contextPath}/script">
				                  <span data-feather="code"></span>
				                  	<span><spring:message code="func.script.manage" /></span>
				                </a>
				            </li>
				            </c:if>
				            <!-- [腳本管理] END -->
				            
				            <!-- [供裝派送] START -->
				            <c:if test="${Env.SHOW_MENU_ITEM_CM_PROVISION_DELIVERY eq __SHOW__}">
		                		<li class="subMenu-item">
					                <a id="cm_delivery" href="${pageContext.request.contextPath}/delivery">
					                  <span data-feather="send"></span>
					                  	<span><spring:message code="func.provision.delivery" /></span>
					                </a>
					            </li>
		                	</c:if>
		                	<!-- [供裝派送] END -->
		                	
		                	<!-- [供裝紀錄] START -->
				            <c:if test="${Env.SHOW_MENU_ITEM_CM_PROVISION_RECORD eq __SHOW__}">
		                		<li class="subMenu-item">
					                <a id="cm_record" href="${pageContext.request.contextPath}/delivery/record">
					                  <span data-feather="search"></span>
					                  	<span><spring:message code="func.provision.record" /></span>
					                </a>
					            </li>
		                	</c:if>
		                	<!-- [供裝紀錄] END -->
		                </ul>
		            </li>
	          	</c:if>
	          </sec:authorize>
	          <!-- [組態管理] END -->
	          
	          <!-- [異常告警] START -->
              <c:if test="${Env.SHOW_MENU_TREE_ABNORMAL_ALARM eq __SHOW__}">
              	<li class="nav-item">
	                <a class="nav-link toggleMenuLink" id="toggleMenu_abnormalAlarm" href="#">
	                  <span data-feather="alert-triangle"></span>
	                  	<span><spring:message code="menu.abnormal.alarm" />&nbsp;<span id="toggleMenu_abnormalAlarm_icon" data-feather="chevron-down"></span></span>
	                </a>
	                <ul aria-expanded="false" id="toggleMenu_abnormalAlarm_items" class="collapse">
			            
				        <!-- [警報總覽] START -->
				        <c:if test="${Env.SHOW_MENU_ITEM_PRTG_ALARM_SUMMARY eq __SHOW__}">
				        	<li class="subMenu-item">
		                    	<a id="mp_alarmSummary" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/prtg/alarmSummary')">
		                    	  <span data-feather="alert-triangle"></span>
		                    	  	<span><spring:message code="func.prtg.alarm.summary" /></span>
		                    	</a>
		                    </li>
				        </c:if>
				        <!-- [警報總覽] END -->
				        	            
			            <!-- [未授權DHCP設備(私接分享器)] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_UNAUTHORIZED_DHCP eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="unauthroized_dhcp" href="${pageContext.request.contextPath}/plugin/module/unauthorizedDHCP">
		                    	  <span data-feather="user-x"></span>
		                    	  	<span><spring:message code="func.plugin.unauthorized.dhcp.device" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [未授權DHCP設備(私接分享器)] END -->
	                    
	                    <!-- [LOOP迴圈] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_LOOP_LOOP eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="loop_loop" href="${pageContext.request.contextPath}/plugin/module/loopLoop">
		                    	  <span data-feather="refresh-cw"></span>
		                    	  	<span><spring:message code="func.plugin.loop.loop" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [LOOP迴圈] END -->
	                    
	                    <!-- [設備故障] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_DEVICE_FAILURE eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="device_failure" href="${pageContext.request.contextPath}/prtg/deviceFailure">
		                    	  <span data-feather="help-circle"></span>
		                    	  	<span><spring:message code="func.prtg.device.failure" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [設備故障] END -->
	                    
	                    <!-- [流量異常] START -->
						<c:if test="${Env.SHOW_MENU_ITEM_ABNORMAL_TRAFFIC eq __SHOW__}">
							<li class="subMenu-item">
		                    	<a id="abnormal_traffic" href="${pageContext.request.contextPath}/prtg/abnormalTraffic">
		                    	  <span data-feather="trending-up"></span>
		                    	  	<span><spring:message code="func.prtg.abnormal.traffic" /></span>
		                    	</a>
		                    </li>
						</c:if>	 
						<!-- [流量異常] END -->
						
						<!-- [其他異常] START -->                   
						<c:if test="${Env.SHOW_MENU_ITEM_OTHER_EXCEPTION eq __SHOW__}">
							<li class="subMenu-item">
		                    	<a id="other_exception" href="${pageContext.request.contextPath}/prtg/otherException">
		                    	  <span data-feather="zap-off"></span>
		                    	  	<span><spring:message code="func.prtg.other.exception" /></span>
		                    	</a>
		                    </li>
						</c:if>	      
						<!-- [其他異常] END -->              
                	</ul>
		      	</li>
              </c:if>
              <!-- [異常告警] END -->
              
              <!-- [資安通報] START -->
              <c:if test="${Env.SHOW_MENU_TREE_PLUGIN eq __SHOW__}">
              	<li class="nav-item">
	                <a class="nav-link toggleMenuLink" id="toggleMenu_plugin" href="#">
	                  <span data-feather="alert-octagon"></span>
	                  	<span><spring:message code="menu.security" />&nbsp;<span id="toggleMenu_plugin_icon" data-feather="chevron-down"></span></span>
	                </a>
	                <ul aria-expanded="false" id="toggleMenu_plugin_items" class="collapse">		            
	                    <!-- [開關PORT] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_PLUGIN_SWITCH_PORT eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="cm_switchPort" href="${pageContext.request.contextPath}/delivery/switchPort">
		                    	  <span data-feather="shield-off"></span>
		                    	  	<span><spring:message code="func.plugin.switch.port" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [開關PORT] END -->
	                    
	                    <!-- [PORT封鎖紀錄查詢] START -->
	                    <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	                    	<c:if test="${Env.SHOW_MENU_ITEM_PORT_BLOCKED_RECORD eq __SHOW__}">
		                    	<li class="subMenu-item">
			                    	<a id="cm_portBlockedRecord" href="${pageContext.request.contextPath}/record/portBlocked">
			                    	  <span data-feather="shield-off"></span>
			                    	  	<span><spring:message code="func.port.open.block.record" /></span>
			                    	</a>
			                    </li>
		                    </c:if>
	                    </sec:authorize>
	                    <!-- [PORT封鎖紀錄查詢] END -->
	                    
	                    <!-- [IP開通/封鎖] START For Admin-->
	                    <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
		                    <c:if test="${Env.SHOW_MENU_ITEM_IP_OPEN_BLOCK eq __SHOW__}">
		                    	<li class="subMenu-item">
			                    	<a id="cm_ipOpenBlock" href="${pageContext.request.contextPath}/delivery/ipOpenBlock4Admin">
			                    	  <span data-feather="check-square"></span>
			                    	  	<span><spring:message code="func.ip.open.block" /></span>
			                    	</a>
			                    </li>
		                    </c:if>
	                    </sec:authorize>
	                    <!-- [IP開通/封鎖] END For Admin -->
	                    
	                    <!-- [IP開通/封鎖] START For User -->
	                    <sec:authorize access="!hasAnyRole('ROLE_ADMIN')">
	                    	<c:if test="${Env.SHOW_MENU_ITEM_IP_OPEN_BLOCK eq __SHOW__}">
		                    	<li class="subMenu-item">
			                    	<a id="cm_ipOpenBlock" href="${pageContext.request.contextPath}/delivery/ipOpenBlock">
			                    	  <span data-feather="check-square"></span>
			                    	  	<span><spring:message code="func.ip.open.block" /></span>
			                    	</a>
			                    </li>
		                    </c:if>
	                    </sec:authorize>
	                    <!-- [IP開通/封鎖] END For User -->
	                    
	                    <!-- [IP封鎖紀錄查詢] START -->
	                    <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	                    	<c:if test="${Env.SHOW_MENU_ITEM_IP_BLOCKED_RECORD eq __SHOW__}">
		                    	<li class="subMenu-item">
			                    	<a id="cm_ipBlockedRecord" href="${pageContext.request.contextPath}/record/ipBlocked">
			                    	  <span data-feather="check-square"></span>
			                    	  	<span><spring:message code="func.ip.open.block.record" /></span>
			                    	</a>
			                    </li>
		                    </c:if>
	                    </sec:authorize>
	                    <!-- [IP封鎖紀錄查詢] END -->
	                    
	                     <!-- [IP MAC 綁定 ] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_IP_MAC_BINDING eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="cm_ipOpenBlock" href="${pageContext.request.contextPath}/delivery/ipMacBinding">
		                    	  <span data-feather="link"></span>
		                    	  	<span><spring:message code="func.ip.mac.binding" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [IP MAC 綁定 ] END -->
	                    
	                    <!-- [網卡MAC開通/封鎖] START -->
	                    <c:if test="${Env.SHOW_MENU_ITEM_MAC_OPEN_BLOCK eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="cm_macOpenBlock" href="${pageContext.request.contextPath}/delivery/macOpenBlock">
		                    	  <span data-feather="at-sign"></span>
		                    	  	<span><spring:message code="func.mac.open.block" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [網卡MAC開通/封鎖] END -->
	                    
	                    <!-- [網卡MAC封鎖紀錄查詢] START -->
	                    <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	                    	<c:if test="${Env.SHOW_MENU_ITEM_MAC_BLOCKED_RECORD eq __SHOW__}">
		                    	<li class="subMenu-item">
			                    	<a id="cm_macBlockedRecord" href="${pageContext.request.contextPath}/record/macBlocked">
			                    	  <span data-feather="at-sign"></span>
			                    	  	<span><spring:message code="func.mac.open.block.record" /></span>
			                    	</a>
			                    </li>
		                    </c:if>
	                    </sec:authorize>
	                    <!-- [網卡MAC封鎖紀錄查詢] END -->
	                    
	                    <!-- [封鎖清單] START -->
                    	<c:if test="${Env.SHOW_MENU_ITEM_MAC_BLOCKED_RECORD eq __SHOW__}">
	                    	<li class="subMenu-item">
		                    	<a id="cm_blockedListRecord" href="${pageContext.request.contextPath}/record/blockedListRecord">
		                    	  <span data-feather="clipboard"></span>
		                    	  	<span><spring:message code="func.block.list.record" /></span>
		                    	</a>
		                    </li>
	                    </c:if>
	                    <!-- [封鎖清單] END -->
	                </ul>
		      	</li>
              </c:if>
              <!-- [資安通報] END -->
              
              <!-- [設定維護] START -->
              <c:if test="${Env.SHOW_MENU_TREE_SETTING_MANAGEMENT eq __SHOW__}">
	              <li class="nav-item">
	                <a class="nav-link toggleMenuLink" id="toggleMenu_setting" href="#">
	                  <span data-feather="settings"></span>
	                  	<span><spring:message code="menu.setting" />&nbsp;<span id="toggleMenu_setting_icon" data-feather="chevron-down"></span></span>
	                </a>
	                <ul aria-expanded="false" id="toggleMenu_setting_items" class="collapse">
	                	<!-- [IP備註維護] START -->
	                	<c:if test="${Env.SHOW_MENU_ITEM_IP_MAINTAIN eq __SHOW__}">
	                		<li class="subMenu-item">
		                    	<a id="st_ipMaintain" href="${pageContext.request.contextPath}/plugin/module/ipMaintain">
		                    	  <span data-feather="edit"></span>
		                    		<span><spring:message code="func.setting.ip.maintain" /></span>
		                    	</a>
		                    </li>
	                	</c:if>
	                	<!-- [IP備註維護] END -->
	                	
	                    <!-- [Email修改] START -->
				        <c:if test="${Env.SHOW_MENU_ITEM_EMAIL_UPDATE eq __SHOW__}">
				        	<li class="subMenu-item">
		                    	<a id="st_emailUpdate" href="#" onclick="closeTabAndGo('${pageContext.request.contextPath}/prtg/email/update')">
		                    	  <span data-feather="mail"></span>
		                    	  	<span><spring:message code="func.prtg.email.update" /></span>
		                    	</a>
		                    </li>
				        </c:if>
	                    <!-- [Email修改] END -->
	                </ul>
		          </li>
	          </c:if>
	          <!-- [設定維護] END -->
              
              <!-- [後台管理] START -->
              <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
              	<c:if test="${Env.SHOW_MENU_TREE_BACKEND eq __SHOW__}">
              		<li class="nav-item">
		                <a class="nav-link toggleMenuLink" id="toggleMenu_admin" href="#">
		                  <span data-feather="settings"></span>
		                  	<span><spring:message code="menu.backend" />&nbsp;<span id="toggleMenu_admin_icon" data-feather="chevron-down"></span></span>
		                </a>
		                <ul aria-expanded="false" id="toggleMenu_admin_items" class="collapse">
		                	<!-- [系統參數維護] START -->
		                	<c:if test="${Env.SHOW_MENU_ITEM_BK_SYS_ENV eq __SHOW__}">
		                		<li class="subMenu-item">
			                    	<a id="bk_env" href="${pageContext.request.contextPath}/admin/env/main">
			                    	  <span data-feather="command"></span> 
			                    		<span><spring:message code="func.sys.env.manage" /></span>
			                    	</a>
			                    </li>
		                	</c:if>
		                	<!-- [系統參數維護] END -->
		                	
		                	<!-- [預設腳本維護] START -->
		                    <c:if test="${Env.SHOW_MENU_ITEM_BK_DEFAULT_SCRIPT eq __SHOW__}">
		                		<li class="subMenu-item">
			                    	<a id="bk_script" href="${pageContext.request.contextPath}/admin/script/main">
			                    	  <span data-feather="hash"></span> 
			                    	  	<span><spring:message code="func.default.script.manage" /></span>
			                    	</a>
			                    </li>
		                	</c:if>
		                	<!-- [預設腳本維護] END -->
		                	
		                	<!-- [排程設定維護] START -->
		                    <c:if test="${Env.SHOW_MENU_ITEM_BK_SYS_JOB eq __SHOW__}">
		                		<li class="subMenu-item">
			                    	<a id="bk_job" href="${pageContext.request.contextPath}/admin/job/main">
			                    	  <span data-feather="check-square"></span> 
			                    	  	<span><spring:message code="func.job.manage" /></span>
			                    	</a>
			                    </li>
		                	</c:if>
		                	<!-- [排程設定維護] END -->
		                	
		                	<!-- [系統紀錄查詢] END -->
		                    <c:if test="${Env.SHOW_MENU_ITEM_BK_SYS_LOG eq __SHOW__}">
		                		<li class="subMenu-item">
			                    	<a id="bk_log" href="${pageContext.request.contextPath}/admin/log/main">
			                    	  <span data-feather="alert-triangle"></span> 
			                    	  	<span><spring:message code="func.sys.log.inquiry" /></span>
			                    	</a>
			                    </li>
		                	</c:if>
		                	<!-- [系統紀錄查詢] END -->
		                </ul>
		            </li>
              	</c:if>
              </sec:authorize>
              <!-- [後台管理] END -->
            </ul>
          </div>
        </nav>

        <!-- ============================================================== -->
        <!-- End Page wrapper  -->
        <!-- ============================================================== -->
        <div class="mobile-menu nav-scroller py-1 mb-2">
	        <nav class="nav d-flex justify-content-between">
	          <div>
	          	<span style="color: white;padding-top:9px;position: fixed;z-index: 999;background-color: #344e6a;width: 12px;height: 2.75rem;margin-top: -3px"></span>
	          </div>
	          	<span class="p-2"><a href="${pageContext.request.contextPath}/version/manage"><spring:message code="func.version.manage" /></a></span>
	            <span class="p-2" style="color:white">|</span>
	            <span class="p-2"><a class="p-2" href="${pageContext.request.contextPath}/version/backup"><spring:message code="func.version.backup" /></a></span>
	            <!-- 未完成版本先mark
	            <span class="p-2" style="color:white">|</span>
	            <span class="p-2"><a class="p-2" href="${pageContext.request.contextPath}/version/recover">版本還原</a></span>
	            <span class="p-2" style="color:white">|</span>
	            <span class="p-2"><a class="p-2" href="${pageContext.request.contextPath}/script">腳本管理</a></span>
	            <span class="p-2" style="color:white">|</span>
	            <span class="p-2"><a class="p-2" href="${pageContext.request.contextPath}/delivery">供裝派送</a></span>
	            <span class="p-2" style="color:white">|</span>
	            <span class="p-2"><a class="p-2" href="${pageContext.request.contextPath}/record">供裝紀錄</a></span>
	             -->
	          <div style="z-index: 9999;margin-right: 11px;margin-top: -3px;position: fixed;left: calc(100% - 15px);">
	          	<span style="color: white;padding-top:9px;background-color: #344e6a;width: 12px;height: 2.75rem;float: right">></span>
	          </div>
	        </nav>
	    </div>
		    
        <main role="main" class="ml-sm-auto col-md-10">
			<decorator:body />
        </main>
        
        <input type="hidden" id="queryFrom" name="queryFrom" />
        
        <!-- Modal [View 組態內容] start -->
		<div class="modal fade" id="viewModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
		  <div class="modal-dialog modal-xg" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title" id="exampleModalLabel"><span id="msgModal_title"><spring:message code="config.content.preview" /></span></h5>
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		     	<div class="form-group row">
		        	<label for="viewModal_group" class="col-md-1 col-sm-12 col-form-label"><spring:message code="group.name" /> :</label>
		    		<input type="text" class="form-control form-control-sm col-md-11 col-sm-12" id="viewModal_group" readonly>
		        </div>
		        <div class="form-group row">
		        	<label for="viewModal_device" class="col-md-1 col-sm-12 col-form-label"><spring:message code="device.name" /> :</label>
		    		<input type="text" class="form-control form-control-sm col-md-11 col-sm-12" id="viewModal_device" readonly>
		        </div>
		        <div class="form-group row">
		        	<label for="viewModal_version" class="col-md-1 col-sm-12 col-form-label"><spring:message code="config.version" /> :</label>
		    		<input type="text" class="form-control form-control-sm col-md-11 col-sm-12" id="viewModal_version" readonly>
		        </div>
		        <div class="form-group row">
		        	<label for="viewModal_content" class="col-md-1 col-sm-12 col-form-label"><spring:message code="config.content" /> :</label>
		        	<!-- <textarea class="form-control col-md-9 col-sm-12" id="viewModal_content" rows="10" readonly></textarea> -->
		        	<div class="form-control form-control-sm col-md-11 col-sm-12 script" id="viewModal_content"></div>
		        </div>
		        
		      </div>
		      <div class="modal-footer">
		      </div>
		    </div>
		  </div>
		</div>
		<!-- Modal [View 組態內容] end -->
		
		<!-- Modal [View 腳本內容] start -->
		<div class="modal fade" id="viewScriptModal" tabindex="-1" role="dialog" aria-labelledby="viewScriptModalLabel" aria-hidden="true">
		  <div class="modal-dialog modal-mid" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title" id="viewScriptModalLabel"><span id="msgModal_title"><spring:message code="script.content.preview" /></span></h5>
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		     	<div class="form-group row">
		        	<label for="viewScriptModal_scriptName" class="col-md-2 col-sm-12 col-form-label"><spring:message code="script.name" /> :</label>
		    		<input type="text" class="form-control form-control-sm col-md-10 col-sm-12" id="viewScriptModal_scriptName" readonly>
		        </div>
		        <div class="form-group row">
		        	<label for="viewScriptModal_scriptContent" class="col-md-2 col-sm-12 col-form-label"><spring:message code="script.content" /> :</label>
		        	<div class="form-control form-control-sm col-md-10 col-sm-12 font script" id="viewScriptModal_scriptContent"></div>
		        </div>
		      </div>
		      <div class="modal-footer">
		      </div>
		    </div>
		  </div>
		</div>
		<!-- Modal [View 腳本內容] end -->
		
		<!-- Modal [資料匯出] start -->
		<div class="modal fade" id="dataExportModal" tabindex="-1" role="dialog" aria-labelledby="dataExportLabel" aria-hidden="true">
		  <div class="modal-dialog modal-xs" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title" id="dataExportLabel"><span id="msgModal_title">資料匯出</span></h5>
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		        <div class="form-group row">
		        	<div class="form-control form-control-sm col-12 export-description">
			        	說明:<br>
			        	<ol style="padding-left: 15px;">
			        	  <li>按下確認後將以<font class="blue">當前</font>的<font class="blue">查詢條件</font>及<font class="blue">排序欄位</font>匯出指定的資料筆數</li>
			        	  <li>匯出檔案為<font class="blue">CSV格式</font>，並採<font class="blue">ZIP壓縮</font>；彈出下載視窗</li>
			        	  <li>選擇ALL匯出所有資料時，可能因資料量較大需要較多時間處理</li>
			        	  <li>Excel 2003僅支持編輯65536筆資料；Excel 2007以上支持編輯104萬筆資料</li>
			        	</ol>
		        	</div>
		        </div>
		     	<div class="form-group row">
		        	<label for="dataExportModal_recordCountSelect" class="col-md-3 col-sm-12 col-form-label"><spring:message code="export.record.count" /> :</label>
		    		<div class="form-control form-control-sm col-md-9 col-sm-12">
		    			<select id="dataExportModal_recordCountSelect" style="width: 100%">
	                        <option value="">=== 請選擇 ===</option>
	                        <option value="100">100</option>
	                        <option value="300">300</option>
	                        <option value="500">500</option>
	                        <option value="1000">1000</option>
	                        <option value="A">ALL</option>
	                        <option value="C">自行輸入</option>
	                    </select>
		    		</div>
		        </div>
		        <div class="form-group row">
		        	<label for="dataExportModal_recordCountInput" class="col-md-3 col-sm-12 col-form-label"><spring:message code="export.record.input" /> :</label>
		        	<div class="form-control form-control-sm col-md-9 col-sm-12">
		        		<input type="text" id="dataExportModal_recordCountInput" class="disable" style="width: 100%" disabled="disabled">
		        	</div>
		        </div>
		      </div>
		      <div class="modal-footer center">
	      		<div class="col-4">
	      			<button type="button" class="btn btn-secondary" id="btnClose" data-dismiss="modal" style="width: 100%;"><spring:message code="close" /></button>
	      		</div>
	      		<div class="col-4">
	      			<button type="button" class="btn btn-success" id="btnDataExportConfirm" style="width: 100%;"><spring:message code="confirm" /></button>
	      		</div>
			  </div>
			  <input type="hidden" id="dataExportModal_var1" name="dataExportModal_var1" value="" />
		    </div>
		  </div>
		</div>
		<!-- Modal [資料匯出] end -->
		
		<!-- Modal [解鎖輸入原因] start -->
		<div class="modal fade" id="openReasonModal" tabindex="-1" role="dialog" aria-labelledby="viewScriptModalLabel" aria-hidden="true">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title" id="viewScriptModalLabel"><span id="msgModal_title">解鎖確認</span></h5>
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		     	<div class="form-group row">
		        	<label for="openReasonModal_reason" class="col-md-3 col-sm-12 col-form-label">解鎖原因<br>(選填) :</label>
		    		<textarea class="form-control form-control-sm col-md-9 col-sm-12" id="openReasonModal_reason" rows="4" cols="30" style="resize: none;"></textarea>
		        </div>
		      </div>
		      <div class="modal-footer">
		      	<div class="col-12 row center">
		      		<div class="col-4">
			      		<button type="button" class="btn btn-success" id="btnDoOpen" style="width: 100%;">確認</button>
			      	</div>
			      	<div class="col-1"></div>
			      	<div class="col-4">
			      		<button type="button" class="btn btn-info" id="btnCancel" style="width: 100%;" data-dismiss="modal" aria-label="Close"><spring:message code="btn.cancel" /></button>
			      	</div>
		      	</div>
		      </div>
		    </div>
		  </div>
		</div>
		<!-- Modal [解鎖輸入原因] end -->
		
		<!-- setup details pane template -->
		<div id="details-pane" style="display: none;">
			<h3 class="title"></h3>
		  	<div class="content"></div>
		</div>
        
        <footer role="footer" class="ml-sm-auto col-md-10 footer">
        	<span class="copyright"><spring:message code="contact.us" /> | Copyright &copy; <spring:message code="copyright" /></span>	
        </footer>
        
      </div>
    </div>

    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
	<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.main.min.js"></script>
	
</body>
</html>