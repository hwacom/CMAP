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
		    	    <div class="col-lg-9 group-field-other">
		    	    	<label for="queryDateBegin" class="font-weight-bold must" style="width: 6%"><spring:message code="time" /></label>
		    	    	<input type="date" id="queryDateBegin" style="width: 18%">
		    	    	<input type="time" id="queryTimeBegin" style="width: 18%">
		    	    	~
		    	    	<input type="date" id="queryDateEnd" style="width: 18%">
		    	    	<input type="time" id="queryTimeEnd" style="width: 18%">
		    	    </div>
		    	    <div class="col-lg-3 group-field-other" style="padding-top: 5px;">
		    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 60%" id="btnSearch_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
		    	    		<spring:message code="btn.query" />
		    	    	</button>
		    	    </div>
		    	</div>
		    	<div class="form-group row" style="margin-bottom: -.4rem; margin-top: -.5rem;">
		    	    <div class="col-lg-3 group-field-other">
						<label for="queryUserAccount" class="font-weight-bold" style="width: 35%"><spring:message code="user.account" /></label>
						<input type="text" id="queryUserAccount" style="width: 60%">
					</div>
					<div class="col-lg-6 group-field-other" style="padding-top: 5px;">
		    	    	<input type="hidden" id="timeoutMsg" style="width: 100%">
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
		      <th scope="col" nowrap="nowrap"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="sessionId" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.address" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="user.account" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="user.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="login.time" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="logout.time" /></th>
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
<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.admin.login.info.min.js"></script>
