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
	    	    	<span class="font-weight-bold" style="width: 30%"><spring:message code="script" /><spring:message code="type" /></span>
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="65%" path="scriptType" id="queryScriptType">
                        <form:option value="" label="== ALL ==" />
                        <form:options items="${scriptTypeList}" />
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
      
      <c:if test="${enableModify}">
      	  <!-- [START]操作按鈕bar -->
	      <div class="col-12 action-btn-bar">
	        <div class="container-fluid">
	        	<div class="row">
	        		<div class="col-lg-2 action-btn-bar-style center">
			  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnAdd"><spring:message code="script.add" /></button>
			  	    </div>
			  	    <div class="col-lg-2 action-btn-bar-style center">
			  	    	<button type="button" class="btn btn-info btn-sm" style="width: 100%" id="btnModify"><spring:message code="script.modify" /></button>
			  	    </div>
			  	    <div class="col-lg-2 action-btn-bar-style center">
			  	    	<button type="button" class="btn btn-danger btn-sm" style="width: 100%" id="btnDelete"><spring:message code="script.delete" /></button>
			  	    </div>
			  	    <div class="center" style="width: 3%">
			  	    	<span style="font-size: 1.5rem">|</span>
			  	    </div>
			  	    <div class="col-lg-2 action-btn-bar-style center">
			  	    	<button type="button" class="btn btn-secondary btn-sm" style="width: 100%" id="btnCompare"><spring:message code="variable.modify" /></button>
			  	    </div>
			  	    <div class="col-lg-2 action-btn-bar-style center">
			  	    	<button type="button" class="btn btn-secondary btn-sm" style="width: 100%" id="btnCompare"><spring:message code="script.type.modify" /></button>
			  	    </div>
	        	</div>
	        </div>
	      </div>
	      <!-- [END]操作按鈕bar -->
      </c:if>
    </div>
    <!-- [END]查詢欄位&操作按鈕 for 大型解析度螢幕 -->
    
    <!-- 查詢結果TABLE區塊 -->
	<div class="row">
	  <div class="col-sm-12 myTableSection" style="display:none;">
		<table id="resultTable" class="dataTable myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
		  <thead class="center">
		    <tr>
		      <c:if test="${enableModify}">
		      	<!-- 有開放可以修改時才顯示操作欄位 -->
		      	<th scope="col" nowrap="nowrap"><spring:message code="action" /></th>
		      </c:if>
		      <c:if test="${not enableModify}">
		      	<th scope="col" nowrap="nowrap" style="display: none"><spring:message code="action" /></th>
		      </c:if>
		      <th scope="col" nowrap="nowrap"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="script.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="type" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="device.model" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="execute.script.content" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="execute.script.remark" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="check.script.content" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="check.script.remark" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="create.time" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="last.update.time" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
	
  </div>

<!-- Modal [Step] start -->
<div class="modal fade" id="stepModal" tabindex="-1" role="dialog" aria-labelledby="stepModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="stepModalLabel"><span id="msgModal_title"></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<!-- [START] Step.1 -->
      	<div id="stepModal_scroll">
	      	<div id="step1_section" style="display: inline">
		     	<div class="form-group row">
	            	<label for="scriptCode" class="col-md-2 col-sm-3 col-form-label"><spring:message code='script' /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="scriptCode" name="input_var" placeholder="<spring:message code='script' />" required="required" >
	            	</div>
	            </div>      
	            <div class="form-group row">
	            	<label for="scriptName" class="col-md-2 col-sm-3 col-form-label"><spring:message code="script.name" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="scriptName" name="input_var" placeholder="<spring:message code='script.name' />" required="required" >
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="deviceModel" class="col-md-2 col-sm-3 col-form-label"><spring:message code="device.model" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="deviceModel" name="input_var" placeholder="<spring:message code='device.model' />" required="required" >
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="scriptContent" class="col-md-2 col-sm-3 col-form-label"><spring:message code="script.content" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<textarea id="scriptContent" name="input_var" style="width: 100%; resize: none;" rows="10" required="required" >
    					</textarea>
    				</div>
	            </div>
	            <div class="form-group row">
	            	<label for="scriptRemark" class="col-md-2 col-sm-3 col-form-label"><spring:message code="execute.script.remark" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="scriptRemark" name="scriptRemark" placeholder="<spring:message code='execute.script.remark' />">
	            	</div>
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
		      	  		<td class="delivery-var-title">ip_address</td>
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
</section>
<script>
	var msg_chooseType = '<spring:message code="please.choose" /><spring:message code="script" /><spring:message code="type" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.script.main.min.js"></script>