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
	      	  <div class="form-group row" style="margin-top: -.3rem; margin-bottom: -.3rem;">
	    	    <div class="col-lg-3 group-field-other">
	    	    	<label for="queryGroup" class="font-weight-bold" style="width: 35%"><spring:message code="group.name" /></label>
	    	    	<form:select path="queryGroup" id="queryGroup" style="width: 60%">
                        <c:if test="${fn:length(groupList) gt 1}">
                        	<form:option value="" label="=== ALL ===" />
                        </c:if>
                        <form:options items="${groupList}" />
                    </form:select>
	    	    </div>
	    	  </div>
	      	</div>
		</form>
      </div>
      <!-- [END]查詢欄位bar -->
      <!-- [START]操作按鈕bar -->
      <div class="col-12 action-btn-bar">
        <div class="container-fluid">
        	<div id="defaultActionBar" class="row">
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
        	<div id="modifyActionBar" class="row" style="display: none">
		  	    <div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnModifySubmit"><spring:message code="btn.submit" /></button>
		  	    </div>
		  	    <div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-dark btn-sm" style="width: 100%" id="btnModifyCancel"><spring:message code="btn.cancel" /></button>
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
		      	  	<label for="queryGroup_mobile" class="col-sm-2 col-form-label"><spring:message code="group.name" /></label>
		      	  	<form:select path="queryGroup" id="queryGroup_mobile" class="col-sm-10 form-control form-control-sm">
                        <c:if test="${fn:length(groupList) gt 1}">
                        	<form:option value="" label="=== ALL ===" />
                        </c:if>
                        <form:options items="${groupList}" />
                    </form:select>
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
		      <th scope="col" nowrap="nowrap" style="width: 5%;"><spring:message code="action" />&nbsp;<input type="checkbox" id="checkAll" name="checkAll" /></th>
		      <th scope="col" nowrap="nowrap" style="width: 10%;"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap" style="width: 20%;"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap" style="width: 25%;"><spring:message code="ip.address" /></th>
		      <th scope="col" nowrap="nowrap" style="width: 40%;"><spring:message code="ip.remark" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
  </div>
  
</section>

<!-- Modal [資料匯入] start -->
<div class="modal fade" id="ipDataImportModal" tabindex="-1" role="dialog" aria-labelledby="ipDataImportLabel" aria-hidden="true">
  <div class="modal-dialog modal-xs" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="ipDataImportLabel"><span id="msgModal_title">資料匯入</span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <div class="form-group row">
        	<div class="form-control form-control-sm col-12 export-description">
	        	說明:<br>
	        	<ol style="padding-left: 15px;">
	        	  <li>一行代表一筆資料，以「逗號(,)」串接欄位</li>
	        	  <li>欄位由左至右依序為: IP_Address > IP備註</li>
	        	</ol>
	        	範例:<br>
	        	192.168.1.100,IP備註1<br>
	        	192.168.1.200,IP備註2<br>
	        	...
        	</div>
        </div>
     	<div id="div_edit_panel" class="form-group row">
        	<label for="ipDataImportModal_dataSet" class="col-12 col-form-label"><spring:message code="please.paste.data" /> :</label>
    		<div class="form-control form-control-sm col-12">
    			<textarea id="ipDataImportModal_dataSet" style="width: 100%; resize: none;" rows="10" cols="50">
    			</textarea>
    		</div>
        </div>
        <div id="div_confirm_panel" class="form-group row" style="display: none;">
    		<div class="form-control form-control-sm col-12">
    			<table id="confirm_panel_table" style="width: 100%;">
    				<thead>
    				  <tr>
    					<th class="var-td" style="width: 45%; color: blue !important;"><spring:message code="ip.address" /></th>
    					<th class="var-td" style="width: 55%; color: blue !important;"><spring:message code="ip.remark" /></th>
    				  </tr>
    				</thead>
    				<tbody>
    				</tbody>
    			</table>
    		</div>
        </div>
      </div>
      <div class="modal-footer center">
     		<div class="col-4">
     			<button type="button" class="btn btn-secondary" id="btnClose" data-dismiss="modal" style="width: 100%;"><spring:message code="close" /></button>
     		</div>
     		<div class="col-4" style="display: none;">
     			<button type="button" class="btn btn-secondary" id="btnIpDataImportBackStep" style="width: 100%;"><spring:message code="btn.step.back" /></button>
     		</div>
     		<div class="col-4">
     			<button type="button" class="btn btn-success" id="btnIpDataImportNextStep" style="width: 100%;"><spring:message code="btn.step.next" /></button>
     		</div>
     		<div class="col-4" style="display: none;">
     			<button type="button" class="btn btn-success" id="btnIpDataImportConfirm" style="width: 100%;"><spring:message code="btn.confirm" /></button>
     		</div>
	  </div>
	  <input type="hidden" id="ipDataImportModal_var1" name="ipDataImportModal_var1" value="" />
    </div>
  </div>
</div>
<!-- Modal [資料匯入] end -->

<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.ip.maintain.min.js"></script>
