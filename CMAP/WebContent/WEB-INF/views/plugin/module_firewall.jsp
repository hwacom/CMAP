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
	      	  <div class="form-group row" style="margin-bottom: -.5rem;">
	    	    <div class="col-lg-3 group-field-other">
	    	    	<label for="queryType" class="font-weight-bold must" style="width: 20%"><spring:message code="firewall.type" /></label>
	    	    	<form:select path="queryType" id="queryType" style="width: 75%">
                        <form:options items="${typeList}" />
                    </form:select>
	    	    </div>
	    	    <div class="col-lg-4 group-field-other">
	    	    	<label for="queryDevName" class="font-weight-bold must" style="width: 30%"><spring:message code="dev.name" /></label>
	    	    	<form:select path="queryDevName" id="queryDevName" style="width: 60%">
                        <form:option value="" label="=== ALL ===" />
                        <form:options items="${devNameList}" />
                    </form:select>
	    	    </div>
	    	    <div class="col-lg-2" style="padding-top: 5px;">
	    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_web">
	    	    		<spring:message code="btn.query" />
	    	    	</button>
	    	    </div>
	    	    <div class="col-lg-2 group-field-other">
	    	    	<input type="text" id="timeoutMsg" disabled="disabled" style="width: 100%">
	    	    </div>
	    	  </div>
	    	  <div class="form-group row" style="margin-bottom: -.2rem;">
	    	    <div class="col-lg-12 group-field-other">
	    	    	<label for="queryDateBegin" class="font-weight-bold must" style="width: 5%"><spring:message code="time" /></label>
	    	    	<input type="date" id="queryDateBegin" style="width: 12%">
	    	    	<input type="time" id="queryTimeBegin" style="width: 10%">
	    	    	~
	    	    	<input type="date" id="queryDateEnd" style="width: 12%">
	    	    	<input type="time" id="queryTimeEnd" style="width: 10%">
	    	    </div>
	    	  </div>
	    	  <div class="form-group row" style="margin-bottom: -.2rem;">
	    	    <div class="col-lg-4 group-field-other">
					<label for="query_SrcIp" class="font-weight-bold" style="width: 15%"><spring:message code="firewall.src.ip" /></label>
					<input type="text" id="query_SrcIp" class="input-ip" style="width: 40%">
					&nbsp;
					<label for="query_SrcPort" class="font-weight-bold" style="width: 18%"><spring:message code="firewall.src.port" /></label>
					<input type="text" id="query_SrcPort" class="input-port" style="width: 20%">
				</div>
				<div class="col-lg-4 group-field-other">
					<label for="query_DstIp" class="font-weight-bold" style="width: 15%"><spring:message code="firewall.dst.ip" /></label>
					<input type="text" id="query_DstIp" class="input-ip" style="width: 40%">
					&nbsp;
					<label for="query_DstPort" class="font-weight-bold" style="width: 18%"><spring:message code="firewall.dst.port" /></label>
					<input type="text" id="query_DstPort" class="input-port" style="width: 20%">
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
		    	  	<label for="query_Type_mobile" class="col-sm-2 col-form-label"><spring:message code="firewall.type" /></label>
		    	  	<form:select path="queryTypeMobile" id="query_Type_mobile" style="width: 75%">
                        <form:options items="${typeList}" />
                    </form:select>
				  </div>
				  <div class="form-group row">
		    	  	<label for="query_DevName_mobile" class="col-sm-2 col-form-label"><spring:message code="dev.name" /></label>
		    	  	<form:select path="queryDevNameMobile" id="query_DevName_mobile" style="width: 75%">
                        <form:option value="" label="=== ALL ===" />
                        <form:options items="${devNameList}" />
                    </form:select>
				  </div>
		    	  <div class="form-group row">
		    	  	<label for="query_SrcIp_mobile" class="col-sm-2 col-form-label"><spring:message code="firewall.src.ip" /></label>
		    	  	<input type="text" class="col-sm-10 form-control form-control-sm" id="query_SrcIp_mobile">
				  </div>
				  <div class="form-group row">
		    	  	<label for="query_SrcPort_mobile" class="col-sm-2 col-form-label"><spring:message code="firewall.src.port" /></label>
		    	  	<input type="text" class="col-sm-10 form-control form-control-sm input-port" id="query_SrcPort_mobile">
				  </div>
				  <div class="form-group row">
		    	  	<label for="query_DstIp_mobile" class="col-sm-2 col-form-label"><spring:message code="firewall.dst.ip" /></label>
		    	  	<input type="text" class="col-sm-10 form-control form-control-sm" id="query_DstIp_mobile">
				  </div>
				  <div class="form-group row">
		    	  	<label for="query_DstPort_mobile" class="col-sm-2 col-form-label"><spring:message code="firewall.dst.port" /></label>
		    	  	<input type="text" class="col-sm-10 form-control form-control-sm input-port" id="query_DstPort_mobile">
				  </div>
				  <div class="form-group row">
				  	<label for="queryDateBegin_mobile" class="col-sm-2 col-form-label"><spring:message code="date" /><spring:message code="time" /></label>
				  	<div class="col-sm-4">
				      <input type="date" class="form-control form-control-sm" id="queryDateBegin_mobile">
				    </div>
				    <div class="col-sm-4">
				      <input type="time" class="form-control form-control-sm" id="queryTimeBegin_mobile">
				    </div>
		    	  </div>
		    	  <div class="form-group row">
		    	    <div class="col-sm-1">~</div>
		    	  </div>
		    	  <div class="form-group row">
				  	<div class="col-sm-4">
				      <input type="date" class="form-control form-control-sm" id="queryDateEnd_mobile">
				    </div>
				    <div class="col-sm-4">
				      <input type="time" class="form-control form-control-sm" id="queryTimeBegin_mobile">
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
		<table id="resutTable" class="dataTable myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
		  <thead class="center">
		    <tr>
		      <th scope="col" nowrap="nowrap"><spring:message code="seq" /></th>
		      <!-- <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th> -->
		      <c:forEach var="fName" items="${TABLE_FIELD}">
		        <th scope="col" nowrap="nowrap">${fName }</th>
		      </c:forEach>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
  </div>
  
</section>
	
<c:set var="val"><spring:message code="group.name"/></c:set>

<script>
	var msg_chooseGroup = '<spring:message code="please.choose" /><spring:message code="group.name" />';
	var msg_chooseDate = '<spring:message code="please.choose" /><spring:message code="date" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.firewall.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/node-ip-master/ip.min.js"></script>
