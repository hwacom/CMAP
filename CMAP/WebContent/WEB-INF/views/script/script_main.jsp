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
			  	    <!-- <div class="col-lg-2 action-btn-bar-style center">
			  	    	<button type="button" class="btn btn-secondary btn-sm" style="width: 100%" id="btnCompare"><spring:message code="variable.modify" /></button>
			  	    </div> -->
			  	    <div class="col-lg-2 action-btn-bar-style center">
			  	    	<button type="button" class="btn btn-secondary btn-sm" style="width: 100%" id="btnTypeModify"><spring:message code="script.type.modify" /></button>
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
	            	<label for="scriptCode" class="col-md-2 col-sm-3 col-form-label"><spring:message code='script.code' /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="addScriptCode" name="input_var" placeholder="<spring:message code='script.code' />EX:TMP_001" required="required" >
	            	</div>
	            </div>      
	            <div class="form-group row">
	            	<label for="scriptName" class="col-md-2 col-sm-3 col-form-label"><spring:message code="script.name" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="addScriptName" name="input_var" placeholder="<spring:message code='script.name' />" required="required" >
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="deviceModelList" class="col-md-2 col-sm-3 col-form-label"><spring:message code="device.model" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<!-- <input type="text" class="form-control form-control-sm" id="deviceModel" name="input_var" placeholder="<spring:message code='device.model' />" required="required" > -->
	            		<form:select path="deviceModelList" id="addDeviceModel" class="form-control form-control-sm" style="min-width: 120px">
			               <form:options items="${deviceModelList}" />
			            </form:select> 
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="scriptContent" class="col-md-2 col-sm-3 col-form-label"><spring:message code="script.content" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<textarea id="addScriptContent" name="input_var" style="width: 100%; resize: none;" rows="10" required="required" >
    					</textarea>
    				</div>
	            </div>
	            <div class="form-group row">
	            	<label for="scriptRemark" class="col-md-2 col-sm-3 col-form-label"><spring:message code="execute.script.remark" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="addScriptRemark" name="input_var" placeholder="<spring:message code='execute.script.remark' />">
	            	</div>
	            </div>
	        </div> 	
	      	<!-- [END] Step.1 -->
	      	
	      	<!-- [START] Step.2 -->
	      	<div id="step2_section" style="display: none;">
	      		<div class="form-group row">
	            	<label for="currentIndex" class="col-md-2 col-sm-3 col-form-label"><spring:message code='script.step.order' /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="currentIndex" readonly="readonly" >
	            	</div>
	            </div>      
	            <div class="form-group row">
	            	<label for="showContentValue" class="col-md-2 col-sm-3 col-form-label"><spring:message code="script.content" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="showContentValue" readonly="readonly" >
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="terminalSymbol" class="col-md-2 col-sm-3 col-form-label"><spring:message code="script.expect.terminal.symbol" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="terminalSymbol" name="input_var2" placeholder="<spring:message code='script.expect.terminal.symbol' /> (EX:#)" required="required">
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="errorSymbol" class="col-md-2 col-sm-3 col-form-label"><spring:message code="script.error.symbol" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm" id="errorSymbol" name="input_var2" placeholder="<spring:message code='script.error.symbol' /> (EX:Unknown command)">
	            	</div>
	            </div>
	      	</div>
	      	<!-- [END] Step.2 -->
	      	
	      	<!-- [START] Step.3 -->
	      	<div id="step3_section" style="display: none;">
	      		<div>
	            	<h1 style="color:red; text-align:center;">請確認是否執行?</h1> 
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


<!-- Modal [Add/Modify] start -->
<div class="modal fade" id="addModifyModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="exampleModalLabel"><span id="msgModal_title">新增/維護腳本類別</span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formEdit" name="formEdit">
        	<div class="card card-body">
        	  <div class="col-12">
	           	<div class="form-group row">
	            	<label for="addAccount" class="col-md-2 col-sm-3 col-form-label"><spring:message code="script.type.code" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm checkRequired" id="scriptTypeCode" name="scriptTypeCode" placeholder="<spring:message code="script.type.code" />" >
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="addUserName" class="col-md-2 col-sm-3 col-form-label"><spring:message code="script.type.name" /></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="text" class="form-control form-control-sm" id="scriptTypeName" name="scriptTypeName" placeholder="<spring:message code="script.type.name" />" >
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

</section>
<script>
	var msg_chooseType = '<spring:message code="please.choose" /><spring:message code="script" /><spring:message code="type" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.script.main.min.js"></script>