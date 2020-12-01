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
		    	    	<span class="font-weight-bold" style="width: 20%"><spring:message code="group.name" /></span>
		    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryGroup" id="queryGroup" onchange="changeDeviceMenu('queryDevice', this.value)">
	                        <form:option value="" label="== ALL ==" />
	                        <form:options items="${groupList}" />
	                    </form:select>
		    	    </div>
		    	    <div class="col-lg-3 group-field-other">
						<span class="font-weight-bold" style="width: 20%"><spring:message code="device.name" /></span>
		    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryDevice" id="queryDevice">
	                        <form:option value="" label="== ALL ==" />
	                        <form:options items="${deviceList}" />
	                    </form:select>
					</div>
					<div class="col-lg-2" style="padding-top: 10px;">
		    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_web"><spring:message code="inquiry" /></button>
		    	    </div>
		    	    <div class="col-lg-3 group-field-other" style="padding-top: 5px;">
		    	    	<button type="button" class="btn btn-info btn-sm" style="width: 60%" id="btnExport_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
		    	    		<spring:message code="btn.export" />
		    	    	</button>
		    	    </div>
	      	  	</div>
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
						<input type="text" id="queryDeviceType" style="width: 40%">
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
			  </div>
	      	</div>
		</form>
      </div>
      <!-- [END]查詢欄位bar -->
      <!-- [START]操作按鈕bar -->
      <div class="col-12 action-btn-bar">
        <div class="container-fluid">
        	<div class="row">
        		<div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnAdd"><spring:message code="btn.add" /></button>
		  	    </div>
		  	    <div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnModify"><spring:message code="btn.modify" /></button>
		  	    </div>
		  	    <div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-danger btn-sm" style="width: 100%" id="btnDelete"><spring:message code="btn.delete" /></button>
		  	    </div>
        	</div>
        </div>
      </div>
      <!-- [END]操作按鈕bar -->
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
		      <th scope="col" nowrap="nowrap"><spring:message code="action" />&nbsp;<input type="checkbox" id="checkAll" name="checkAll" /></th>
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

<!-- Modal [Add/Modify] start -->
<div class="modal fade" id="addModifyModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="exampleModalLabel"><span id="msgModal_title">新增/維護設備資產</span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formEdit" name="formEdit">
        	<div class="card card-body">
        	  <div class="col-12">
	           	<div class="form-group row">
	            	<label for="addProbe" class="col-md-2 col-sm-3 col-form-label"><spring:message code="inventory.probe" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="addProbe" name="inputAddProbe" >
	            	</div>
	            </div>	            
	            <div class="form-group row">
	            	<label for="addGroup" class="col-md-2 col-sm-3 col-form-label"><spring:message code="group.name" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="addGroup" name="inputAddGroup" >
	            	</div>
	            </div>	            
	            <div class="form-group row">
	            	<label for="addDeviceName" class="col-md-2 col-sm-3 col-form-label"><spring:message code="ip.trace.poller.device.name" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="addDeviceName" name="inputAddDeviceName" >
	            	</div>
	            </div>	            
	            <div class="form-group row">
	            	<label for="addDeviceIp" class="col-md-2 col-sm-3 col-form-label"><spring:message code="ip.address" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="addDeviceIp" name="inputAddDeviceIp" >
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="addDeviceType" class="col-md-2 col-sm-3 col-form-label"><spring:message code="inventory.type" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="addDeviceType" name="inputAddDeviceType" >
	            	</div>
	            </div>
	            
	            <div class="form-group row">
	            	<label for="addBrand" class="col-md-2 col-sm-3 col-form-label"><spring:message code="inventory.brand" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="addBrand" name="inputAddBrand" >
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="addModel" class="col-md-2 col-sm-3 col-form-label"><spring:message code="device.model" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="addModel" name="inputAddModel" >
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="addSystemVersion" class="col-md-2 col-sm-3 col-form-label"><spring:message code="system.version" /></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="text" class="form-control form-control-sm" id="addSystemVersion" name="inputAddSystemVersion" >
	                </div>
	            </div>
	            <div class="form-group row">
	            	<label for="addSerialNumber" class="col-md-2 col-sm-3 col-form-label"><spring:message code="inventory.serial.number" /></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="text" class="form-control form-control-sm" id="addSerialNumber" name="inputAddSerialNumber" >
	                </div>
	            </div>
	            <div class="form-group row">
	            	<label for="addManufactureDate" class="col-md-2 col-sm-3 col-form-label"><spring:message code="inventory.manufacture.date" /></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="text" class="form-control form-control-sm" id="addManufactureDate" name="inputAddManufactureDate" >
	                </div>
	            </div>                
              </div>
			</div>
			<div class="modal-footer">
        		<button type="button" class="btn btn-secondary" id="btnClose" data-dismiss="modal"><spring:message code="btn.close" /></button>
        		<button type="button" class="btn btn-success" id="btnSave"><spring:message code="btn.save" /></button>
			</div>
        </form>
      </div>
    </div>
  </div>
</div>
<!-- Modal [Add/Modify] end -->

<script>
	var msg_chooseDate = '<spring:message code="please.choose" /><spring:message code="date" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.admin.inventory.min.js"></script>
