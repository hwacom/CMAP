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
		  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnModify"><spring:message code="btn.modify" /></button>
		  	    </div>
        	</div>
        </div>
      </div>
      <!-- [END]操作按鈕bar -->
    </div>
    <!-- [END]查詢欄位&操作按鈕 for 大型解析度螢幕 -->
    
    <!-- [START]查詢欄位 for 中小型解析度螢幕 -->
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
                        <c:if test="${fn:length(groupList) gt 1}">
                        	<form:option value="" label="=== ALL ===" />
                        </c:if>
                        <form:options items="${groupList}" />
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
	<!-- [END]查詢欄位 for 中小型解析度螢幕 -->
	
	<!-- 查詢結果TABLE區塊 -->
	<div class="row">
	  <div class="col-sm-12 myTableSection">
		<table id="resultTable" class="dataTable myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
		  <thead class="center">
		    <tr>
		      <th scope="col" nowrap="nowrap"><spring:message code="action" />&nbsp;<input type="checkbox" id="checkAll" name="checkAll" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="dev.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.admin.device.login.info.connect.mode" /></th>	
		      <th scope="col" nowrap="nowrap"><spring:message code="login.account" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="login.password" /></th>		      
		      <th scope="col" nowrap="nowrap">enable <spring:message code="login.password" /></th>
		      <th scope="col" nowrap="nowrap">enable backup</th>	      	       
		      <th scope="col" nowrap="nowrap">community</th>
		      <th scope="col" nowrap="nowrap">UDP Port</th>
		      <th scope="col" nowrap="nowrap"><spring:message code="update.time" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="update.by" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
	
  </div>

</section>

<!-- Modal [Modify] start -->
<div class="modal fade" id="modifyModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="exampleModalLabel"><span id="msgModal_title"><spring:message code="func.admin.device.login.info.modify.title" /></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formEdit" name="formEdit">
        	<div class="card card-body">
        	  <div class="col-12">
        	  	<input type="hidden" class="form-control form-control-sm checkRequired" id="modifyIds" name="modifyIds" >
	        	  <div class="form-group row">
	        	  	<label for="modifyRemark" class="col-md-2 col-sm-3 col-form-label">設備名稱</label>
	            	<div class="col-md-12 col-sm-3">
	            		<textarea rows="5" class="form-control form-control-sm" id="modifyRemark" name="modifyRemark" disabled></textarea>
	                </div>
	        	  </div>
        	  	<div class="form-group row">
                	<label for="modifyConnectionMode" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.admin.device.login.info.connect.mode" /><span class="pull-right" style="color: red;">＊ </span></label>
                  	<div class="col-md-10 col-sm-9">
                  		<form:select path="connectModeList" id="modifyConnectionMode" class="form-control form-control-sm" style="min-width: 25px">
			               <form:options items="${connectModeList}" />
			            </form:select> 
                  	</div>
                </div>
                <div class="form-group row">
	            	<label for="modifyLoginAccount" class="col-md-2 col-sm-3 col-form-label"><spring:message code="user.account" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm checkRequired" id="modifyLoginAccount" name="modifyLoginAccount" placeholder="user account" >
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="modifyLoginPassword" class="col-md-2 col-sm-3 col-form-label"><spring:message code="login.password" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="password" class="form-control form-control-sm" id="modifyLoginPassword" name="modifyLoginPassword" placeholder="password" >
	                </div>
	            </div>
	            <div class="form-group row">
	            	<label for="modifyEnablePassword" class="col-md-2 col-sm-3 col-form-label">enable <spring:message code="login.password" /></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="password" class="form-control form-control-sm" id="modifyEnablePassword" name="modifyEnablePassword" placeholder="enable password" >
	                </div>
	            </div>
	            <div class="form-group row">
                	<label for="modifyEnableBackup" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.admin.device.login.info.enable.backup" /><span class="pull-right" style="color: red;">＊ </span></label>
                  	<div class="col-md-10 col-sm-9">
                  		<form:select path="enableBackupList" id="modifyEnableBackup" class="form-control form-control-sm" style="min-width: 25px">
			               <form:options items="${enableBackupList}" />
			            </form:select> 
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
<!-- Modal [Modify] end -->

<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.admin.device.login.info.min.js"></script>