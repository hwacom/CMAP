<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<section>

  <div class="container-fluid">
    <!-- [START]查詢欄位&操作按鈕 for 大型解析度螢幕 -->
	<div class="row search-bar-large">
	  <!-- [START]查詢欄位bar -->
      <div class="col-12 search-bar">
      	<form>
      		<div class="container-fluid">
	      	  <div class="form-group row">
	    	    <div class="col-lg-3 group-field-other">
	    	    	<span class="font-weight-bold" style="width: 20%"><spring:message code="group.name" /></span>
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryGroup1" id="queryGroup" onchange="changeDeviceMenu('queryDevice', this.value)">
                        <form:option value="" label="== ALL ==" />
                        <form:options items="${group1List}" />
                    </form:select>
	    	    </div>
	    	    <div class="col-lg-3 group-field-other">
					<span class="font-weight-bold" style="width: 20%"><spring:message code="device.name" /></span>
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryDevice1" id="queryDevice">
                        <form:option value="" label="== ALL ==" />
                        <form:options items="${device1List}" />
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
      <!-- [START]操作按鈕bar -->
      <div class="col-12 action-btn-bar">
        <div class="container-fluid">
        	<div class="row">
        		<div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnBackup"><spring:message code="backup" /></button>
		  	    </div>
		  	    <div class="col-lg-1 group-field-other">
                    <select id="exportDay" name="exportDay" class="form-control form-control-sm" style="min-width: 5px" >
						<option value="1" label="1天" />
		                <option value="3" label="3天" />                
		                <option value="7" label="7天" />
				    </select>
	    	    </div>
		  	    <div class="col-lg-2 action-btn-bar-style" align="center">
	    	    	<button type="button" class="btn btn-info btn-sm" style="width: 100%" id="btnExport_web"><spring:message code="btn.sys.error.log" /><spring:message code="btn.export" /></button>
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
		      	  	<label for="group_1" class="col-sm-2 col-form-label"><spring:message code="group.name" /></label>
				    <div class="col-sm-10">
				      <form:select path="queryGroup1" id="queryGroup_mobile" class="form-control form-control-sm" onchange="changeDeviceMenu('queryDevice_mobile', this.value)">
	                  	<form:option value="" label="=== ALL ===" />
	                    <form:options items="${group1List}" />
	                  </form:select>
				    </div>
		    	  </div>
		    	  <div class="form-group row">
		    	  	<label for="device_1" class="col-sm-2 col-form-label"><spring:message code="device.name" /></label>
		    	    <div class="col-sm-10">
		    	      <form:select path="queryDevice1" id="queryDevice_mobile" class="form-control form-control-sm">
                        <form:option value="" label="=== ALL ===" />
                        <form:options items="${device1List}" />
                      </form:select>
				    </div>
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
		      <th scope="col" nowrap="nowrap"><spring:message code="action" />&nbsp;<input type="checkbox" id="checkAll" name="checkAll" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="device.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="device.model" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="last.backup.version" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="backup.type" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="last.backup.time" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
	
  </div>

</section>

<!-- Modal [Backup_dialog] start -->
<div class="modal fade" id="backupDialogModal" tabindex="-1" role="dialog" aria-labelledby="backupDialogModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="backupDialogModalLabel"><span id="msgModal_title"><spring:message code="action" /><spring:message code="confirm" /></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formEdit" name="formEdit">
            <div class="box-body">
            	<div class="form-group">
            	  <!-- 
                  <label for="queryConfigType" class="control-label">請選擇要備份的組態檔類型:<span class="pull-right" style="color: red;">＊ </span></label>
                  <form:select path="queryConfigType" id="queryConfigType">
                      <form:option value="" label="兩者都要" />
                      <form:options items="${configTypeList}" />
                  </form:select>
                   -->
                  <span><spring:message code="backup.confirm.msg.1" />(Configuration)<br><spring:message code="backup.confirm.msg.2" />?</span>
                </div>                              
            </div>
        </form>
      </div>
      <div class="modal-footer center">
   		<div class="col-4">
   			<button type="button" class="btn btn-secondary" id="btnClose" data-dismiss="modal" style="width: 100%"><spring:message code="close" /></button>
   		</div>
   		<div class="col-4">
   			<button type="button" class="btn btn-success" id="btnConfirm" style="width: 100%"><spring:message code="confirm" /></button>
   		</div>
	  </div>
    </div>
  </div>
</div>
<!-- Modal [Backup_dialog] end -->

<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.version.backup.min.js"></script>