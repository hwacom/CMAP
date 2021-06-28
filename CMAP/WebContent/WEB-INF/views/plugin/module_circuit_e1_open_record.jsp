<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<section>

  <div id="content" class="container-fluid">
    <!-- [START]查詢欄位&操作按鈕 for 大型解析度螢幕 -->
	<div id="search-bar-large" class="row search-bar-large">
	  <!-- [START]查詢欄位bar -->
      <!-- <div class="col-12 search-bar">
      	<form>
      		<div class="container-fluid">
	      	  <div class="form-group row">
	    	    
	    	    <div class="col-lg-2" style="padding-top: 10px;">
	    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_record_web">
	    	    		<spring:message code="btn.query" />
	    	    	</button>
	    	    </div>
	    	  </div>
	      	</div>
		</form>
      </div> -->
      <!-- [END]查詢欄位bar -->
    </div>
    <!-- [END]查詢欄位&操作按鈕 for 大型解析度螢幕 -->
    
    <!-- 查詢欄位 for 中小型解析度螢幕 -->
    <!-- <div id="search-bar-small-btn" class="row search-bar-small-btn">
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
		    	    <div class="col-sm-12">
				      <button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_record_mobile"><spring:message code="btn.query" /></button>
				    </div>
				  </div>
				</form>
		  	</div>
		  </div>
	  </div>
	</div>-->
	
	<!-- 查詢結果TABLE區塊 -->
	<div class="row">
	  <div class="col-sm-12 myTableSection" style="display:none;">
		<table id="resultTable" class="dataTable myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
		  <thead class="center">
		    <tr>
			    <th scope="col" nowrap="nowrap"></th>
			    <th scope="col" nowrap="nowrap"><spring:message code="device.id" /></th>
			    <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.circleName" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.stationName" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.stationEngName" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.neName" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.srcSid" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.dstSid" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.localCsrIp" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.remoteCsrIp" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.srcE1gwIp" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.dstE1gwIp" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.scrPortNumber" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="func.plugin.circuit.e1.open.dstPortNumber" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="remark" /></th>
				<th scope="col" nowrap="nowrap"><spring:message code="update.time" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
  
  </div>
  
</section>

<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.circuit.e1.open.record.min.js"></script>
