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
	      		<div class="form-group row" style="margin-bottom: -.2rem;">
		    	    <div class="col-lg-3 group-field-other">
						<label for="queryProbe" class="font-weight-bold" style="width: 25%"><spring:message code="inventory.probe" /></label>
						<input type="text" id="queryProbe" style="width: 40%">
					</div>
					<div class="col-lg-3 group-field-other">
						<label for="queryDeviceName" class="font-weight-bold" style="width: 20%"><spring:message code="ip.trace.poller.device.name" /></label>
						<input type="text" id="queryDeviceName" style="width: 60%">
					</div>
					<div class="col-lg-3 group-field-other">
						<label for="queryDeviceType" class="font-weight-bold" style="width: 20%"><spring:message code="inventory.type" /></label>
						<input type="text" id=""queryDeviceType"" style="width: 40%">
					</div>
		    	    <div class="col-lg-3 group-field-other" style="padding-top: 5px;">
		    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 60%" id="btnSearch_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
		    	    		<spring:message code="btn.query" />
		    	    	</button>
		    	    </div>
		    	</div>
		    	<div class="form-group row" style="margin-bottom: -.4rem; margin-top: -.5rem;">
		    	    <div class="col-lg-3 group-field-other">
						<label for="queryBrand" class="font-weight-bold" style="width: 20%"><spring:message code="inventory.brand" /></label>
						<input type="text" id="queryBrand" style="width: 40%">
					</div>
					<div class="col-lg-3 group-field-other">
						<label for="queryModel" class="font-weight-bold" style="width: 20%"><spring:message code="device.model" /></label>
						<input type="text" id="queryModel" style="width: 40%">
					</div>
					<div class="col-lg-3 group-field-other" style="padding-top: 5px;">
		    	    	<input type="hidden" id="timeoutMsg" style="width: 60%">
		    	    </div>
					<div class="col-lg-3 group-field-other" style="padding-top: 5px;">
		    	    	<button type="button" class="btn btn-info btn-sm" style="width: 60%" id="btnExport_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
		    	    		<spring:message code="btn.export" />
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
		      <th scope="col" nowrap="nowrap"><spring:message code="device.id" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="inventory.probe" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.trace.poller.device.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.address" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="inventory.type" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="inventory.brand" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="device.model" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="system.version" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="inventory.serial.number" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="inventory.manufacture.date" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
	
  </div>
  
</section>


<script>
	var msg_chooseDate = '<spring:message code="please.choose" /><spring:message code="date" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.admin.inventory.min.js"></script>
