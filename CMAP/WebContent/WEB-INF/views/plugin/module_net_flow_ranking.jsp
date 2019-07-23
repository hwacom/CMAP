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
	    	    	<label for="queryGroup" class="font-weight-bold must" style="width: 20%"><spring:message code="group.name" /></label>
	    	    	<form:select path="queryGroup" id="queryGroup" style="width: 75%">
                        <c:if test="${fn:length(groupList) gt 1}">
                        	<form:option value="" label="=== ALL ===" />
                        </c:if>
                        <form:options items="${groupList}" />
                    </form:select>
	    	    </div>
	    	    <div class="col-lg-2" style="padding-top: 5px;">
	    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_1_web">
	    	    		1日
	    	    	</button>
	    	    </div>
	    	    <div class="col-lg-2" style="padding-top: 5px;">
	    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_3_web">
	    	    		3日
	    	    	</button>
	    	    </div>
	    	    <div class="col-lg-2" style="padding-top: 5px;">
	    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_7_web">
	    	    		7日
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
				      <button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_1_mobile">1日</button>
				    </div>
				  </div>
				  <div class="form-group row">
		    	    <div class="col-sm-12">
				      <button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_3_mobile">3日</button>
				    </div>
				  </div>
				  <div class="form-group row">
		    	    <div class="col-sm-12">
				      <button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_7_mobile">7日</button>
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
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.address" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap">百分比</th>
		      <th scope="col" nowrap="nowrap">總流量</th>
		      <th scope="col" nowrap="nowrap">上傳流量</th>
		      <th scope="col" nowrap="nowrap">下載流量</th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
  </div>
  
</section>

<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.net.flow.ranking.min.js"></script>
