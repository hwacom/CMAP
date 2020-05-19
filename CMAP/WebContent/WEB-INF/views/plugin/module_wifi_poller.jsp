<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<section>
  <input type="hidden" id="pageLength" name="pageLength" value="${pageLength }" />
  <div id="content" class="container-fluid">
    <!-- [START]查詢欄位&操作按鈕 for 大型解析度螢幕 -->
	<div id="search-bar-large" class="row search-bar-large">
	  <!-- [START]查詢欄位bar -->
      <div class="col-12 search-bar">
      	<form>
      		<div class="container-fluid">
	      	  <div class="form-group row">
	      	  <!-- 
	    	    <div class="col-lg-3 group-field-other">
	    	    	<label for="queryGroup" class="font-weight-bold must" style="width: 20%"><spring:message code="group.name" /></label>
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryGroup" id="queryGroup">    	    		 
                        <c:if test="${fn:length(groupList) gt 1}">
                        	<form:option value="" label="=== ALL ===" />
                        </c:if>              
                        <form:options items="${groupList}" />
                    </form:select>
	    	    </div>
	    	     -->
	    	    <!-- 
	    	    <div class="col-lg-3 group-field-other">
					<span class="font-weight-bold" style="width: 25%"><spring:message code="device.name" /></span>
					// TODO
				</div>
				 -->
				<div class="col-lg-3 group-field-other">
	    	    	<label for="query_Date" class="font-weight-bold must" style="width: 25%"><spring:message code="wifi.poller.date.conn" /></label>
	    	    	<input type="date" id="query_Date" style="width: 70%">
	    	    </div>
	    	     <div class="col-lg-3 group-field-other">
	    	    	<label for="query_TimeBegin" class="font-weight-bold must" style="width: 14%"><spring:message code="time" /></label>
	    	    	<input type="time" id="query_TimeBegin" style="width: 38%">
	    	    	<span class="font-weight-bold center" style="width: 5%">~</span>
	    	    	<input type="time" id="query_TimeEnd" style="width: 38%">
	    	    </div>
	    	    <div class="col-lg-2" style="padding-top: 5px;">
	   	    		<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_web"><spring:message code="inquiry" /></button>
	    	   	</div>
	    	   	<div class="col-lg-2 offset-lg-1 group-field-other">
	    	    	<input type="text" id="timeoutMsg" disabled="disabled" style="width: 100%">
	    	    </div>
	    	  </div>
	    	  <div class="form-group row">
	    	    <div class="col-lg-3 group-field-other">
					<label for="query_ClientMac" class="font-weight-bold" style="width: 25%"><spring:message code="wifi.poller.client.mac" /></label>
					<input type="text" id="query_ClientMac" class="" style="width: 70%">
				</div>
				<div class="col-lg-3 group-field-other">
					<label for="query_ClientIp" class="font-weight-bold" style="width: 20%"><spring:message code="wifi.poller.client.ip" /></label>
					<input type="text" id="query_ClientIp" class="input-ip" style="width: 70%">
				</div>
			  	<div class="col-lg-2 group-field-other">
					<label for="query_ApName" class="font-weight-bold" style="width: 30%"><spring:message code="wifi.poller.ap.name" /></label>
					<input type="text" id="query_ApName" class="" style="width: 60%">
				</div>
		  		<div class="col-lg-2 group-field-other">
					<label for="query_Ssid" class="font-weight-bold" style="width: 35%"><spring:message code="wifi.poller.ssid" /></label>
					<input type="text" id="query_Ssid" class="" style="width: 60%">
				</div>
	      	 </div>
	      	</div>
		</form>
      </div>
      <!-- [END]查詢欄位bar -->
    </div>
    <!-- [END]查詢欄位&操作按鈕 for 大型解析度螢幕 -->
    
    <!-- 查詢欄位 for 中小型解析度螢幕 -->
    <div id="search-bar-small-btn" class="row search-bar-small-btn">
  	  <button id="mobileMenuBtn" class="btn btn-success col-sm-12" type="button" data-toggle="collapse" data-target="#collapseExample" aria-expanded="false" aria-controls="collapseExample">
	     	<spring:message code="query.condition" /> ▼
	  </button>
	</div>
	<div class="row search-bar-small">
	  <div class="col-sm-12 collapse" id="collapseExample" style="padding-top: 10px">
		  <div class="card card-body">
		  	<div class="col-12">
		  		<form>
		  		  <!--
		      	  <div class="form-group row">
		      	  	<label for="queryGroup_mobile" class="col-sm-2 col-form-label"><spring:message code="group.name" /></label>
		      	  	<form:select path="queryGroup" id="queryGroup_mobile" class="col-sm-10 form-control form-control-sm">
                        <c:if test="${fn:length(groupList) gt 1}">
                        	<form:option value="" label="=== ALL ===" />
                        </c:if>
                        <form:options items="${groupList}" />
                    </form:select>
		    	  </div>
		    	    -->
		    	  <div class="form-group row">
		    	  	<label for="query_Date_mobile" class="col-sm-2 col-form-label"><spring:message code="wifi.poller.date.conn" /></label>
		    	  	<input type="date" class="form-control form-control-sm" id="query_Date_mobile">
				  </div>
				  <div class="form-group row">
		    	  	<label for="query_ClientMac_mobile" class="col-sm-2 col-form-label"><spring:message code="net.flow.destination.ip" /></label>
		    	  	<input type="text" class="col-sm-10 form-control form-control-sm" id="query_ClientMac_mobile">
				  </div>
				  <div class="form-group row">
		    	  	<label for="query_ClientIp_mobile" class="col-sm-2 col-form-label"><spring:message code="net.flow.destination.port" /></label>
		    	  	<input type="text" class="col-sm-10 form-control form-control-sm input-port" id="query_ClientIp_mobile">
				  </div>
				  <div class="form-group row">
		    	  	<label for="query_ApName_mobile" class="col-sm-2 col-form-label"><spring:message code="net.flow.sender.ip" /></label>
		    	  	<input type="text" class="col-sm-10 form-control form-control-sm" id="query_ApName_mobile">
				  </div>
				  <div class="form-group row">
		    	  	<label for="query_Ssid_mobile" class="col-sm-2 col-form-label"><spring:message code="net.flow.mac" /></label>
		    	  	<input type="text" class="col-sm-10 form-control form-control-sm" id="query_Ssid_mobile">
				  </div>
				  <div class="form-group row">
		    	    <div class="col-sm-12">
				      <button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_mobile"><spring:message code="inquiry" /></button>
				    </div>
				  </div>
				</form>
		  	</div>
		  </div>
	  </div>
	</div>
	
	<!-- 查詢結果TABLE區塊 -->
	<div class="row">
	  <div class="col-sm-12 myTableSection" style="display:none;">
		<table id="resultTable" class="dataTable myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
		  <thead class="center">
		    <tr>
		      <th scope="col" nowrap="nowrap"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="wifi.poller.client.mac" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="wifi.poller.start.time" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="wifi.poller.end.time" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="wifi.poller.client.ip" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="wifi.poller.ap.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="wifi.poller.ssid" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="total.traffic" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="upload.traffic" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="download.traffic" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="wifi.poller.conn.history" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
  </div>
  
</section>
<!-- Modal [ViewWifiDetail] start -->
<div class="modal fade" id="viewWifiDetailModal" tabindex="-1" role="dialog" aria-labelledby="viewWifiDetailLabel" aria-hidden="true">
  <div class="modal-dialog modal-mid" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="viewWifiDetailLabel"><span id="msgModal_title">Wifi連線明細資料</span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
     	<div class="form-group row">
        	<label for="viewWifiDetailModal_groupName" class="col-md-2 col-sm-12 col-form-label"><spring:message code="group.name" /> :</label>
    		<div class="form-control form-control-sm col-md-10 col-sm-12" id="viewWifiDetailModal_groupName"></div>
        </div>
        <div class="form-group row">
        	<label for="viewWifiDetailModal_clientMac" class="col-md-2 col-sm-12 col-form-label"><spring:message code="wifi.poller.client.mac" /> :</label>
    		<div class="form-control form-control-sm col-md-10 col-sm-12" id="viewWifiDetailModal_clientMac"></div>
        </div>
        <div class="form-group row">
        	<label for="viewWifiDetailModal_clientIp" class="col-md-2 col-sm-12 col-form-label"><spring:message code="wifi.poller.client.ip" /> :</label>
    		<div class="form-control form-control-sm col-md-10 col-sm-12" id="viewWifiDetailModal_clientIp"></div>
        </div>
        <div class="form-group row">
        	<label for="viewWifiDetailModal_pollingTime" class="col-md-2 col-sm-12 col-form-label"><spring:message code="wifi.poller.polling.time" /> :</label>
    		<div class="form-control form-control-sm col-md-10 col-sm-12" id="viewWifiDetailModal_pollingTime"></div>
        </div>
        <div class="form-group row">
        	<label for="viewWifiDetailModal_trafficData" class="col-md-2 col-sm-12 col-form-label"><spring:message code="wifi.poller.traffic.chart" /> :</label>
    		<div class="form-control form-control-sm col-md-10 col-sm-12" id="viewWifiDetailModal_trafficData"></div>
        </div>
        <div class="form-group row">
        	<label for="viewWifiDetailModal_qualityData" class="col-md-2 col-sm-12 col-form-label"><spring:message code="wifi.poller.quality.chart" /> :</label>
    		<div class="form-control form-control-sm col-md-10 col-sm-12" id="viewWifiDetailModal_qualityData"></div>
        </div>
      </div>
      <div class="modal-footer">
      </div>
    </div>
  </div>
</div>
<!-- Modal [ViewWifiDetail] end -->
<script>
	var msg_chooseGroup = '<spring:message code="please.choose" /><spring:message code="group.name" />';
	var msg_chooseDate = '<spring:message code="please.choose" /><spring:message code="date" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.wifi.poller.min.js"></script>
<style>
	canvas {
		-moz-user-select: none;
		-webkit-user-select: none;
		-ms-user-select: none;
	}
</style>
