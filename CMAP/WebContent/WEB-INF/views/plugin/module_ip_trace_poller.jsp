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
	    	    <div class="col-lg-3 group-field-other">
	    	    	<label for="queryGroup" class="font-weight-bold must" style="width: 20%"><spring:message code="group.name" /></label>
					<!-- Group List為空的例外處理 btn 增加disabled attr -->
	    	    	<c:if test="${fn:length(groupList) gt 1}">
	    	    			<script> 
                        		var is_btnDisabled = "false";
                        	</script>
                    </c:if>
                    <c:if test="${fn:length(groupList) lt 1}">
	    	    			<script> 
	    	    				var msg_errorGroupList = '<spring:message code="ERROR.grouplist.access.fail" />';
                    			alert(msg_errorGroupList);
                    			var is_btnDisabled = "true";
                        	</script>
                    </c:if>
                    <!-- Group List為空的例外處理 -->
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryGroup" id="queryGroup">    	    		 
                        <!-- ToDo 根據User身分限制只有super user才可使用ALL查詢 -->
                        <c:if test="${fn:length(groupList) gt 1}">
                        	<form:option value="" label="=== ALL ===" />
                        </c:if>
                        <form:options items="${groupList}" />
                    </form:select>
	    	    </div>
	    	    <!-- 
	    	    <div class="col-lg-3 group-field-other">
					<span class="font-weight-bold" style="width: 25%"><spring:message code="device.name" /></span>
					// TODO
				</div>
				 -->
				<div class="col-lg-3 group-field-other">
					<label for="query_ClientMac" class="font-weight-bold" style="width: 25%"><spring:message code="ip.trace.poller.client.mac" /></label>
					<input type="text" id="query_ClientMac" class="" style="width: 70%">
				</div>
				<div class="col-lg-3 group-field-other">
					<label for="query_ClientIp" class="font-weight-bold" style="width: 20%"><spring:message code="ip.trace.poller.client.ip" /></label>
					<input type="text" id="query_ClientIp" class="input-ip" style="width: 70%">
				</div>
	    	   	<div class="col-lg-2 offset-lg-1 group-field-other">
	    	    	<input type="text" id="timeoutMsg" disabled="disabled" style="width: 100%">
	    	    </div>
	    	  </div>
	    	  <div class="form-group row">
	    	    <div class="col-lg-8 group-field-other">
	    	    	<label for="query_DateBegin" class="font-weight-bold" style="width: 10%"><spring:message code="ip.trace.poller.start.time" /></label>
	    	    	<input type="date" id="query_DateBegin" style="width: 20%"/>
	    	    	<input type="time" id="query_TimeBegin" style="width: 20%"/>
					<span class="font-weight-bold center" style="width: 5%">~</span>
	    	    	<input type="date" id="query_DateEnd" style="width: 20%"/>
	    	    	<input type="time" id="query_TimeEnd" style="width: 20%"/>
	    	    </div>
	    	    
				<div class="col-lg-2" style="padding-top: 5px;">
	   	    		<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' } ><spring:message code="inquiry" /></button>
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
		      	  <div class="form-group row">
		      	  	<label for="queryGroup_mobile" class="col-sm-2 col-form-label"><spring:message code="group.name" /></label>
		      	  	<form:select path="queryGroup" id="queryGroup_mobile" class="col-sm-10 form-control form-control-sm">
                        <!-- ToDo 根據User身分限制只有super user才可使用ALL查詢 -->
                        <c:if test="${fn:length(groupList) ge 1}">
                        	<form:option value="" label="=== ALL ===" />
                        </c:if>
                        <form:options items="${groupList}" />
                    </form:select>
		    	  </div>
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
		    	    <div class="col-sm-12">
				      <button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_mobile" ${is_btnDisabled eq 'true' ? 'disabled' : '' }><spring:message code="inquiry" /></button>
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
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.trace.poller.client.ip" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.trace.poller.start.time" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.trace.poller.end.time" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.trace.poller.client.mac" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.trace.poller.group.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.trace.poller.device.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.trace.poller.port.name" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
  </div>
  
</section>
<script>
	//var msg_chooseGroup = '<spring:message code="please.choose" /><spring:message code="group.name" />';
	var msg_chooseDate = '<spring:message code="please.choose" /><spring:message code="ip.trace.poller.full.date.time" />';
	var msg_chooseOne = '<spring:message code="please.choose" /><spring:message code="query.condition" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.ip.trace.poller.min.js"></script>
