<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<section>
  <input type="hidden" id="pageLength" name="pageLength" value="${pageLength }" />
  <input type="hidden" id="userAccount" name="userAccount" value="${userAccount }" />
  <input type="hidden" id="userGroup" name="userGroup" value="${userGroup }" />
  
  <div id="content" class="container-fluid">
    <!-- [START]查詢欄位&操作按鈕 for 大型解析度螢幕 -->
	<div id="search-bar-large" class="row search-bar-large">
	  <!-- [START]查詢欄位bar -->
      <div class="col-12 search-bar">
      	<form>
      		<div class="container-fluid">
	      		<div class="form-group row" style="margin-bottom: -.2rem;">
		    	    <div class="col-md group-field-other">
						<label for="queryOwner" class="font-weight-bold" style="width: 20%"><spring:message code="func.plugin.ticket.owner" /></label>
	                    <form:select path="inputQueryOwner" id="queryOwner" style="width: 55%">
	                    	<form:option value="" label="== ALL ==" />
							<optgroup label="<spring:message code="group.name" />">
								<form:options items="${inputQueryGroup}" />
							</optgroup>
	                        <optgroup label="<spring:message code="user" />">
								<form:options items="${inputQueryOwner}" />
							</optgroup>
	                    </form:select>
					</div>
					<div class="col-md group-field-other">
						<label for="queryStatus" class="font-weight-bold" style="width: 10%"><spring:message code="status" /></label>
						<form:select path="inputQueryStatus" id="queryStatus" style="width: 30%">
							<form:option value="" label="== ALL ==" />
	                        <form:options items="${inputQueryStatus}" />
	                    </form:select>
					</div>
					
		    	    <div class="col-md">
		    	    	<button type="button" class="btn btn-primary btn-sm float-md-right" style="width: 40%" id="btnSearch_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
		    	    		<spring:message code="btn.query" />
		    	    	</button>
		    	    </div>
		    	</div>
		    	<div class="form-group row" style="margin-bottom: -.2rem;">
		    	    <div class="col-md-8 group-field-other">
		    	    	<label for="queryDateBegin" class="font-weight-bold must" style="width: 10%"><spring:message code="time" /></label>
		    	    	<input type="date" id="queryDateBegin">
		    	    	<input type="time" id="queryTimeBegin">
		    	    	~
		    	    	<input type="date" id="queryDateEnd">
		    	    	<input type="time" id="queryTimeEnd">
		    	    </div>
					<div class="col-md group-field-other" style="padding-top: 5px;">
		    	    	<input type="hidden" id="timeoutMsg" style="width: 100%">
		    	    </div>
					<div class="col-md float-md-right">
		    	    	<button type="button" class="btn btn-primary btn-sm float-md-right" style="width: 85%" id="btnAdd"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
		    	    		<spring:message code="btn.add" />
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
				  	<label for="queryDateBegin_mobile" class="col-sm-2 col-form-label"><spring:message code="execute.date" /></label>
				  	<div class="col-sm-4">
				      <input type="date" class="form-control form-control-sm" id="queryDateBegin_mobile">
				    </div>
				    <div class="col-sm-1">~</div>
				    <div class="col-sm-4">
				      <input type="date" class="form-control form-control-sm" id="queryDateEnd_mobile">
				    </div>
		    	  </div>
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
		      <th scope="col" nowrap="nowrap"><spring:message code="update.time" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.ticket.priority" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.ticket.listId" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.ticket.subject" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.ticket.owner" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="status" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="remark" /></th>
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
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.tickets.min.js"></script>
