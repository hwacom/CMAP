<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>

<style>
  .ui-dialog {
    width: 700px !important;
    left: 418px !important;
  }
</style>

<section>

  <div class="container-fluid">
  	<!-- [START]操作按鈕bar -->
    <div class="col-12 action-btn-bar">
    	<div class="container-fluid">
       		<div class="row">
       			<div class="col-lg-2 action-btn-bar-style" align="center">
	  	    		<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnDelivery"><spring:message code="btn.delivery" /></button>
	  	    	</div>
       		</div>
       	</div>
    </div>
    <!-- [END]操作按鈕bar -->
    
  	<div class="container-fluid">
		<!-- 查詢結果TABLE區塊 -->
		<div class="row">
	  	<div class="col-sm-12 myTableSection mainTable" style="display:none;">
			<table id="resultTable" class="dataTable dataTable_1 myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
		  	<thead class="center">
		    	<tr>
			      <th scope="col" nowrap="nowrap"><spring:message code="action" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="seq" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="script.name" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="type" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="system.version" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="execute.script.content" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="execute.script.remark" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="check.script.content" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="check.script.remark" /></th>
			    </tr>
			  </thead>
			</table>
	  	</div>
		</div>
  	</div>

	<div class="row">
		<div class="col-12">
			<div id="divBlockedTitle" style="width: 100%; padding-top: 50px; color: #ee2525; font-weight: bold; font-size: 1rem;">
				<spring:message code="blocked.mac.list.sub.title" />:&nbsp;&nbsp;&nbsp;
				
				<!-- 若使用者擁有多群組權限則show出群組選單；否則不顯示 -->
				<c:if test="${fn:length(groupList) gt 1}">
		            <c:choose>
					   <c:when test="${showSyncAction}">
						   <form:select class="selectpicker" data-live-search="true" data-width="200px" path="group" id="queryGroup" onchange="findBlockedMacRecordData('S')">
				               	<form:option value="" label="== ALL ==" />
				                <form:options items="${groupList}" />
				            </form:select>
				            &nbsp;&nbsp;&nbsp;
					   </c:when>
					   <c:otherwise>
					   		<form:select class="selectpicker" data-live-search="true" data-width="200px" path="group" id="queryGroup" onchange="findBlockedMacRecordData('B')">
				               	<form:option value="" label="== ALL ==" />
				                <form:options items="${groupList}" />
				            </form:select>
				            &nbsp;&nbsp;&nbsp;
					   </c:otherwise>
					</c:choose>
	            </c:if>
	            
	            <!-- 解鎖Button -->
				<button type="button" style="width: 100px;" class="btn btn-primary btn-sm" id="btnOpen" disabled="disabled"><spring:message code="btn.mac.open" /></button>
				<c:if test="${showSyncAction}">
					<button type="button"  style="width: 210px" class="btn btn-primary btn-sm"  id="btnSync_record">
	    	    		<spring:message code="synchronize.switch.mac" />
	    	    	</button>
				</c:if>
			</div>
		</div>
	</div>
  	<!-- MAC封鎖紀錄 -->
  	<div id="divBlockedMacRecord" class="container-fluid">
		<!-- 查詢結果TABLE區塊 -->
		<div class="row">
		  	<div class="col-sm-12 myTableSection" style="display:none;">
				<table id="resultTable_blockedMacRecord" class="dataTable dataTable_2 myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
			  	<thead class="center">
			    	<tr>
				      <th scope="col" nowrap="nowrap"><spring:message code="action" /></th>
				      <th scope="col" nowrap="nowrap" data-field="seq"><spring:message code="seq" /></th>
				      <th scope="col" nowrap="nowrap" data-field="groupName"><spring:message code="group.name" /></th>
				      <th scope="col" nowrap="nowrap" data-field="macAddress"><spring:message code="mac.address" /></th>
				      <th scope="col" nowrap="nowrap" data-field="status"><spring:message code="status" /></th>
				      <th scope="col" nowrap="nowrap" data-field="blockTime"><spring:message code="block.time" /></th>
				      <th scope="col" nowrap="nowrap" data-field="openTime"><spring:message code="open.time" /></th>
				      <th scope="col" nowrap="nowrap" data-field="blockReason"><spring:message code="block.reason" /></th>
				      <th scope="col" nowrap="nowrap" data-field="openReason"><spring:message code="open.reason" /></th>
				      <th scope="col" nowrap="nowrap" data-field="blockBy"><spring:message code="block.by" /></th>
				      <th scope="col" nowrap="nowrap" data-field="openBy"><spring:message code="open.by" /></th>
				    </tr>
				  </thead>
				</table>
		  	</div>
		</div>
  	</div>
  	
  </div>

</section>

<!-- Modal [Step] start -->
<div class="modal fade" id="stepModal" tabindex="-1" role="dialog" aria-labelledby="stepModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="stepModalLabel"><span id="msgModal_title"><spring:message code="provision.delivery" /></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<div class="row center">
      		<div class="step-img col-3 step step-current">
      			<span>Step 1.</span><br><span><spring:message code="please.choose" /><spring:message code="device.name" /></span>
      		</div>
      		<div class="col-1 step-arrow">
      			<i class="fas fa-angle-double-right step-arrow-img"></i>
      		</div>
      		<div class="step-img col-3 step">
      			<span>Step 2.</span><br><span><spring:message code="variable.enter" /></span>
      		</div>
      		<div class="col-1 step-arrow">
      			<i class="fas fa-angle-double-right step-arrow-img"></i>
      		</div>
      		<div class="step-img col-3 step">
      			<span>Step 3.</span><br><span><spring:message code="delivery.confirm" /></span>
      		</div>
      	</div>
      	<div class="row center">
      		<div class="col-12">
      			<hr>
      		</div>
      	</div>
      	
      	<!-- [START] Step.1 -->
      	<div id="stepModal_scroll">
	      	<div id="step1_section" style="display: inline;">
	      	  	<div class="form-group row">
		        	<label for="stepModal_chooseDevice" class="col-md-4 col-sm-6 col-form-label bold"><spring:message code="please.choose" /><spring:message code="device.name" />(按住Ctrl可複選) :</label>
		        	<label for="stepModal_searchDevice" class="col-md-1 col-sm-1 col-form-label bold right"><spring:message code="search" /></label>
		    		<input type="text" class="form-control form-control-sm col-md-5 col-sm-" id="stepModal_searchDevice">
		    		<img src="${pageContext.request.contextPath}/resources/images/loading.gif" id="stepModal_searchWaiting" alt="loading...">
		        </div>
		        <div class="form-group row">
		        	<select class="multi-select col-md-12 col-sm-12" id="stepModal_chooseDevice" size="10" multiple="multiple">
		    		</select>
		        </div>
		        <div class="form-group row">
		        	<label for="stepModal_remark" class="col-md-12 col-sm-12 col-form-label">
		        		<span id="stepModal_variable_description"></span>
		        		<span id="stepModal_variable_show"></span>
		        	</label>
		        </div>
		        <div class="form-group row">
		        	<label for="stepModal_reason" class="col-md-2 col-sm-12 col-form-label bold"><spring:message code="provision.reason" /> :</label>
		        	<input type="text" class="form-control form-control-sm col-md-10 col-sm-12" id="stepModal_reason" name="stepModal_reason">
		        </div>
	      	</div>
	      	<!-- [END] Step.1 -->
	      	
	      	<!-- [START] Step.2 -->
	      	<div id="step2_section" style="display: none;">
	      	  <div class="form-group row">
		      	<label for="stepModal_enterVarRemark" class="col-md-12 col-sm-2 col-form-label bold">
		      		<spring:message code="please.confirm.target.and.enter.variable.value" /> :
		      	</label>
		      </div>
		      <div id="step2_target_section">
		      	<table id="step2_target_table" class="myTable">
		      	  <thead class="center bold">
		      	  	<tr>
			      	  	<th rowspan="3" width="3%"><spring:message code="seq" /></th>
			      	  	<th rowspan="3" width="12%"><spring:message code="group.name" /></th>
			      	  	<th rowspan="3" width="25%"><spring:message code="device.name" /></th>
			      	  	<th colspan="1" width="60%" id="step2_varKey_td"><spring:message code="variable.value" /></th>
		      	  	</tr>
		      	  	<tr>
		      	  		<td class="delivery-var-title">mac_address</td>
		      	  	</tr>
		      	  </thead>
		      	  <tbody>
		      	  	<!-- 依據前一步驟勾選的設備動態增長 -->
		      	  </tbody>
		      	</table>
		      </div>
		      
		      <div class="row">
		      	<hr class="col-12">
		      </div>

	      	</div>
	      	<!-- [END] Step.2 -->
	      	
	      	<!-- [START] Step.3 -->
	      	<div id="step3_section" style="display: none;">
	      		<div class="form-group row">
		      		<div class="col-md-12 col-sm-12" id="stepModal_preview">
		      		  <!-- 派送前預覽區 -->
		      		</div>
		     	</div>
	      	</div>
	      	<!-- [END] Step.3 -->
      	</div>
      	
      </div>
      <div class="modal-footer">
      	<div class="col-12 row center">
      		<div class="col-2">
	      		<button type="button" class="btn btn-secondary" id="btnStepGoPrev" style="width: 100%;"><spring:message code="btn.step.back" /></button>
	      	</div>
	      	<div class="col-1"></div>
	      	<div class="col-2">
	      		<button type="button" class="btn btn-success" id="btnStepGoNext" style="width: 100%;"><spring:message code="btn.step.next" /></button>
	      		<button type="button" class="btn btn-success" id="btnStepGoFire" style="width: 100%;"><spring:message code="btn.delivery.confirm" /></button>
	      	</div>
	      	<div class="col-1"></div>
	      	<div class="col-2">
	      		<button type="button" class="btn btn-info" id="btnCancel" style="width: 100%;" data-dismiss="modal" aria-label="Close"><spring:message code="btn.cancel" /></button>
	      	</div>
      	</div>
      </div>
    </div>
  </div>
</div>
<!-- Modal [View] end -->

<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.delivery.main.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.mac.open.block.min.js"></script>
