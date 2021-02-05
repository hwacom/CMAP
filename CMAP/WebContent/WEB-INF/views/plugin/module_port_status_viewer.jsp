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
	    	    	<span class="font-weight-bold" style="width: 25%"><spring:message code="group.name" /></span>
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="70%"  path="queryGroup" id="queryGroup" onchange="changeDeviceMenu('queryDevice', this.value)">
                        <form:options items="${groupList}" />
                    </form:select>
	    	    </div>
	    	    <div class="col-lg-3 group-field-other">
	    	    	<span class="font-weight-bold" style="width: 25%"><spring:message code="device.name" /></span>
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="70%"  path="queryDevice" id="queryDevice">
                        <form:options items="${deviceList}" />
                    </form:select>
	    	    </div>
	    	    <div class="col-lg-2" style="padding-top: 10px;">
	    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_web"><spring:message code="inquiry" /></button>
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
                        <form:options items="${groupList}" />
                    </form:select>
		    	  </div>
		    	  <div class="form-group row">
		      	  	<label for="queryDevice_mobile" class="col-sm-2 col-form-label"><spring:message code="device.name" /></label>
		      	  	<form:select path="queryDevice" id="queryDevice_mobile" class="col-sm-10 form-control form-control-sm">
                        <form:options items="${deviceList}" />
                    </form:select>
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
		      <th scope="col" nowrap="nowrap" style="width: 5%;"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap" style="width: 15%;"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap" style="width: 20%;"><spring:message code="device.name" /></th>
		      <th scope="col" nowrap="nowrap" style="width: 20%;"><spring:message code="port.name" /></th>
		      <th scope="col" nowrap="nowrap" style="width: 15%;"><spring:message code="port.admin.status" /></th>
		      <th scope="col" nowrap="nowrap" style="width: 15%;"><spring:message code="port.oper.status" /></th>
		      <th scope="col" nowrap="nowrap" style="width: 10%;"><spring:message code="port.speed" />(/M)</th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
  </div>
  
</section>

<script>
	var msg_chooseGroup = '<spring:message code="please.choose" /><spring:message code="group.name" />';
	var msg_chooseDevice = '<spring:message code="please.choose" /><spring:message code="device.name" />';
</script>

<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.port.status.viewer.min.js"></script>
