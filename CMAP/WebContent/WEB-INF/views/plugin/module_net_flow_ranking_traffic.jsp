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
	      	  <div class="form-group row" style="margin-bottom: -.6rem; margin-top: -.6rem;">
	    	    <div class="col-lg-3 group-field-other">
	    	    	<label for="queryGroup" class="font-weight-bold must" style="width: 20%"><spring:message code="group.name" /></label>
					<!-- Group List為空的例外處理 btn 增加disabled attr -->
	    	    	<c:if test="${fn:length(groupList) ge 1}">
	    	    			<script> 
                        		var is_btnDisabled = "false";
                        	</script>
                    </c:if>
                    <c:if test="${fn:length(groupList) lt 1}">
	    	    			<script> 
	    	    				var msg_errorGroupList = '<spring:message code="ERROR.grouplist.access.fail" />';
                    			alert(msg_errorGroupList);
                    			var is_btnDisabled = "true";
                        	</script>
                    </c:if>
                    <!-- Group List為空的例外處理 -->
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryGroup" id="queryGroup">
	    	    		<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
                        	<c:if test="${fn:length(groupList) ge 1}">
                        		<form:option value="" label="=== ALL ===" />
                        	</c:if>
                        </sec:authorize>
                        <form:options items="${groupList}" />
                    </form:select>
	    	    </div>
	    	  </div>
	    	  <div class="form-group row">
	    	  	 <div class="col-lg-4 group-field-other">
	    	    	<label for="queryDateBegin" class="font-weight-bold must" style="width: 15%"><spring:message code="date" /></label>
	    	    	<input type="date" id="queryDateBegin"  class="input-date-begin"  style="width: 35%">
	    	    	<span class="font-weight-bold center" style="width: 5%">~</span>
	    	    	<input type="date" id="queryDateEnd"  class="input-date-end"  style="width: 35%">
	    	    </div>
	    	    <div class="col-lg-1 action-btn-bar-style" align="center">
	    	    	<button type="button" class="btn btn-secondary btn-sm" style="width: 100%" id="btn_1d_web">
	    	    		<spring:message code="btn.1.day" />
	    	    	</button>
	    	    </div>
	    	    <div class="col-lg-1 action-btn-bar-style" align="center">
	    	    	<button type="button" class="btn btn-secondary btn-sm" style="width: 100%" id="btn_3d_web">
	    	    		<spring:message code="btn.3.day" />
	    	    	</button>
	    	    </div>
	    	    <div class="col-lg-1 action-btn-bar-style" align="center">
	    	    	<button type="button" class="btn btn-secondary btn-sm" style="width: 100%" id="btn_7d_web">
	    	    		<spring:message code="btn.7.day" />
	    	    	</button>
	    	    </div>
	    	    <div class="offset-lg-1 col-lg-2"  style="padding-top: 10px;">
	    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
	    	    		<spring:message code="btn.query" />
	    	    	</button>
	    	    </div>
	    	    <div class="col-lg-2 " style="padding-top: 10px;">
	    	    	<button type="button" class="btn btn-info btn-sm" style="width: 100%" id="btnExport_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
	    	    		<spring:message code="btn.export" />
	    	    	</button>
	    	    </div>
        	  </div>
        	</div>
        </form>
   	  </div>
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
		      	  	<!-- Group List為空的例外處理 btn 增加disabled attr -->
	    	    	<c:if test="${fn:length(groupList) ge 1}">
	    	    			<script> 
                        		var is_btnDisabled = "false";
                        	</script>
                    </c:if>
                    <c:if test="${fn:length(groupList) lt 1}">
	    	    			<script> 
	    	    				var msg_errorGroupList = '<spring:message code="ERROR.grouplist.access.fail" />';
                    			alert(msg_errorGroupList);
                    			var is_btnDisabled = "true";
                        	</script>
                    </c:if>
                    <!-- Group List為空的例外處理 -->
		      	  	<form:select path="queryGroup" id="queryGroup_mobile" class="col-sm-10 form-control form-control-sm">
	    	    		<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
                        	<c:if test="${fn:length(groupList) ge 1}">
                        		<form:option value="" label="=== ALL ===" />
                        	</c:if>
                        </sec:authorize>
                        <form:options items="${groupList}" />
                    </form:select>
		    	  </div>
		    	  <div class="form-group row">
	    	    	<label for="queryDateBegin_mobile" class="col-sm-2 col-form-label" style="width: 15%"><spring:message code="date" /></label>
	    	    	<input type="date" id="queryDateBegin_mobile"  class="input-date-begin-mobile" style="width: 35%">
	    	    	<span class="font-weight-bold center" style="width: 5%">~</span>
	    	    	<input type="date" id="queryDateEnd_mobile"  class="input-date-end-mobile"  style="width: 35%">
	    	      </div>	      
				  <div class="form-group row">
				  	<div class="col-sm-3 action-btn-bar-style" align="center">
				      	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btn_1d_mobile">
				      		<spring:message code="btn.1.day" />
				      	</button>
				    </div>
				    <div class="col-sm-3 offset-sm-1 action-btn-bar-style" align="center">
				      <button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btn_3d_mobile">
				      	<spring:message code="btn.3.day" />
					  </button>
					</div>
					<div class="col-sm-3 offset-sm-1 action-btn-bar-style" align="center">
				      <button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btn_7d_mobile">
				      	<spring:message code="btn.7.day" />
					  </button>
					</div>
				  </div>
		    	  <div class="form-group row">
	    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_mobile"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
	    	    		<spring:message code="btn.query" />
	    	    	</button>
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
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.address" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.remark" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="percentage" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="total.traffic" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="upload.traffic" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="download.traffic" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
  </div>
</section>
<script>
	//var msg_chooseGroup = '<spring:message code="please.choose" /><spring:message code="group.name" />';
	var msg_chooseDate = '<spring:message code="please.choose" /><spring:message code="date" />';
	//var msg_chooseOne = '<spring:message code="please.choose" /><spring:message code="query.condition" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.net.flow.ranking.traffic.min.js"></script>
