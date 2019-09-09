<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<section>

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
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryGroup" id="queryGroup" onchange="changeDeviceMenu('queryDevice', this.value)">
                        <c:if test="${fn:length(groupList) gt 1}">
                        	<form:option value="" label="== ALL ==" />
                        </c:if>
                        <form:options items="${groupList}" />
                    </form:select>
	    	    </div>
	    	    <div class="col-lg-3 group-field-other">
	    	    	<label for="queryDevice" class="font-weight-bold must" style="width: 20%"><spring:message code="device.name" /></label>
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryDevice" id="queryDevice">
                        <form:option value="" label="== ALL ==" />
                        <form:options items="${deviceList}" />
                    </form:select>
				</div>
	    	    <div class="col-lg-2" style="padding-top: 10px;">
	    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_record_web">
	    	    		<spring:message code="btn.query" />
	    	    	</button>
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
		      	  	<form:select path="queryGroup" id="queryGroup_mobile" class="col-sm-10 form-control form-control-sm" onchange="changeDeviceMenu('queryDevice_mobile', this.value)">
                        <c:if test="${fn:length(groupList) gt 1}">
                        	<form:option value="" label="=== ALL ===" />
                        </c:if>
                        <form:options items="${groupList}" />
                    </form:select>
		    	  </div>
		    	  <div class="form-group row">
		    	  	<label for="queryDevice_mobile" class="col-sm-2 col-form-label"><spring:message code="device.name" /></label>
	    	        <form:select path="queryDevice" id="queryDevice_mobile" class="form-control form-control-sm">
                       <form:option value="" label="=== ALL ===" />
                       <form:options items="${deviceList}" />
                    </form:select>
				  </div>
				  <div class="form-group row">
		    	    <div class="col-sm-12">
				      <button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_record_mobile"><spring:message code="btn.query" /></button>
				    </div>
				  </div>
				</form>
		  	</div>
		  </div>
	  </div>
	</div>
	
	<!-- 查詢結果TABLE區塊 -->
	<div class="row">
	  <div id="divBlockedPortRecord" class="col-sm-12 myTableSection" style="display:none;">
		<table id="resultTable_blockedPortRecord" class="dataTable myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
		  <thead class="center">
		    <tr>
		      <th scope="col" nowrap="nowrap" data-field="action"><spring:message code="action" />&nbsp;<input type="checkbox" id="checkAll" name="checkAll" /></th>
		      <th scope="col" nowrap="nowrap" data-field="seq"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap" data-field="groupName"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap" data-field="deviceName"><spring:message code="device.name" /></th>
			  <th scope="col" nowrap="nowrap" data-field="portName"><spring:message code="port.name" /></th>
		      <th scope="col" nowrap="nowrap" data-field="status"><spring:message code="status" /></th>
		      <th scope="col" nowrap="nowrap" data-field="blockTime"><spring:message code="block.time" /></th>
		      <th scope="col" nowrap="nowrap" data-field="blockReason"><spring:message code="block.reason" /></th>
		      <th scope="col" nowrap="nowrap" data-field="blockBy"><spring:message code="block.by" /></th>
		      <th scope="col" nowrap="nowrap" data-field="openTime"><spring:message code="open.time" /></th>
		      <th scope="col" nowrap="nowrap" data-field="openReason"><spring:message code="open.reason" /></th>
		      <th scope="col" nowrap="nowrap" data-field="openBy"><spring:message code="open.by" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
  </div>
  
</section>

<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.switch.port.record.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.switch.port.min.js"></script>
