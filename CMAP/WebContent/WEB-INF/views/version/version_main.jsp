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
	      	  <div class="form-group row">
	      	  	<div class="col-lg-1 group-field-other center">
	      	  		<span class="font-weight-bold group-title"><spring:message code="group.first" /></span>
	      	  	</div>
	    	    <div class="col-lg-2 group-field-left">
	    	    	<span class="font-weight-bold" style="width: 25%"><spring:message code="group.name" /></span>
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="70%" path="queryGroup1" id="queryGroup1" onchange="changeDeviceMenu('queryDevice1', this.value)">
                        <form:option value="" label="== ALL ==" />
                        <form:options items="${group1List}" />
                    </form:select>
	    	    </div>
	    	    <div class="col-lg-2 group-field-middle">
					<span class="font-weight-bold" style="width: 25%"><spring:message code="device.name" /></span>
					<form:select class="selectpicker" data-live-search="true" data-width="70%" path="queryDevice1" id="queryDevice1">
                        <form:option value="" label="== ALL ==" />
                        <form:options items="${device1List}" />
                    </form:select>
				</div>
	    	    <div class="col-lg-4 group-field-right">
	    	    	<span class="font-weight-bold" style="width: 20%"><spring:message code="backup.date" /></span>
	    	    	<input type="date" id="queryExcuteDateBegin1" style="width: 35%" placeholder="yyyy-mm-dd">
	    	    	<span class="font-weight-bold center" style="width: 5%">~</span>
	    	    	<input type="date" id="queryExcuteDateEnd1" style="width: 35%" placeholder="yyyy-mm-dd">
	    	    </div>
	    	    <div class="col-lg-2 group-field-other">
	    	    	<span class="font-weight-bold" style="width: 25%"><spring:message code="category" /></span>
					<form:select path="queryConfigType" id="queryConfigType" style="width: 70%">
                        <form:option value="" label="== ALL ==" />
                        <form:options items="${configTypeList}" />
                    </form:select>
	    	    </div>
	    	    <div class="col-lg-1" style="padding-top: 5px;">
	    	    	<input type="checkbox" id="queryNewChkbox" value="" style="vertical-align:middle;" checked="checked">&nbsp;
	    	    	<label for="queryNewChkbox" class="font-weight-bold"><spring:message code="last.version" /></label>
	    	    </div>
	      	  </div>
	      	  
	      	  <div class="form-group row">
	      	  	<div class="col-lg-1 group-field-other center">
	      	  		<span class="font-weight-bold group-title"><spring:message code="group.second" /></span>
	      	  	</div>
	    	    <div class="col-lg-2 group-field-left">
	    	    	<span class="font-weight-bold" style="width: 25%"><spring:message code="group.name" /></span>
	    	    	<form:select class="selectpicker" data-live-search="true" data-width="70%" path="queryGroup2" id="queryGroup2" onchange="changeDeviceMenu('queryDevice2', this.value)">
                        <form:option value="" label="== ALL ==" />
                        <form:options items="${group2List}" />
                    </form:select>
	    	    </div>
	    	    <div class="col-lg-2 group-field-middle">
					<span class="font-weight-bold" style="width: 25%"><spring:message code="device.name" /></span>
					<form:select class="selectpicker" data-live-search="true" data-width="70%" path="queryDevice2" id="queryDevice2">
                        <form:option value="" label="== ALL ==" />
                        <form:options items="${device2List}" />
                    </form:select>
				</div>
	    	    <div class="col-lg-4 group-field-right">
	    	    	<span class="font-weight-bold" style="width: 20%"><spring:message code="backup.date" /></span>
	    	    	<input type="date" id="queryExcuteDateBegin2" style="width: 35%" placeholder="yyyy-mm-dd">
	    	    	<span class="font-weight-bold center" style="width: 5%">~</span>
	    	    	<input type="date" id="queryExcuteDateEnd2" style="width: 35%" placeholder="yyyy-mm-dd">
	    	    </div>
	    	    <div class="col-lg-2" style="padding-top: 10px;">
	    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_web"><spring:message code="inquiry" /></button>
	    	    </div>
	      	  </div>
	      	</div>
		</form>
      </div>
      <!-- [END]查詢欄位bar -->
      <!-- [START]操作按鈕bar -->
      <div class="col-12 action-btn-bar">
        <div class="container-fluid">
        	<div class="row">
        		<div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnCompare"><spring:message code="btn.compare" /></button>
		  	    </div>
		  	    <div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-danger btn-sm" style="width: 100%" id="btnDelete"><spring:message code="btn.delete" /></button>
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
		      	  	<div class="col-sm-12">
		      	  		<span class="font-weight-bold group-title"><spring:message code="group.first" /></span>
		      	  	</div>
		      	  </div>
		      	  <div class="form-group row">
		      	  	<label for="group_1" class="col-sm-2 col-form-label"><spring:message code="group.name" /></label>
				    <div class="col-sm-10">
				      <form:select path="queryGroup1" id="queryGroup1_mobile" class="form-control form-control-sm" onchange="changeDeviceMenu('queryDevice1_mobile', this.value)">
	                  	<form:option value="" label="=== ALL ===" />
	                    <form:options items="${group1List}" />
	                  </form:select>
				    </div>
		    	  </div>
		    	  <div class="form-group row">
		    	  	<label for="device_1" class="col-sm-2 col-form-label"><spring:message code="device.name" /></label>
		    	    <div class="col-sm-10">
		    	      <form:select path="queryDevice1" id="queryDevice1_mobile" class="form-control form-control-sm">
                        <form:option value="" label="=== ALL ===" />
                        <form:options items="${device1List}" />
                      </form:select>
				    </div>
				  </div>
				  <div class="form-group row">
				  	<label for="bkdate_begin_1" class="col-sm-2 col-form-label"><spring:message code="backup.date" /></label>
				  	<div class="col-sm-4">
				      <input type="date" class="form-control form-control-sm" id="queryExcuteDateBegin1_mobile">
				    </div>
				    <div class="col-sm-1">~</div>
				    <div class="col-sm-4">
				      <input type="date" class="form-control form-control-sm" id="queryExcuteDateEnd1_mobile">
				    </div>
		    	  </div>
		    	  <div class="form-group row">
		      	  	<div class="col-sm-12">
		      	  		<span class="font-weight-bold group-title"><spring:message code="group.second" /></span>
		      	  	</div>
		      	  </div>
		      	  <div class="form-group row">
		      	  	<label for="group_2" class="col-sm-2 col-form-label"><spring:message code="group.name" /></label>
				    <div class="col-sm-10">
				      <form:select path="queryGroup2" id="queryGroup2_mobile" class="form-control form-control-sm" onchange="changeDeviceMenu('queryDevice2_mobile', this.value)">
	                  	<form:option value="" label="=== ALL ===" />
	                    <form:options items="${group2List}" />
	                  </form:select>
				    </div>
		    	  </div>
		    	  <div class="form-group row">
		    	  	<label for="device_2" class="col-sm-2 col-form-label"><spring:message code="device.name" /></label>
		    	    <div class="col-sm-10">
		    	      <form:select path="queryDevice2" id="queryDevice2_mobile" class="form-control form-control-sm">
                        <form:option value="" label="=== ALL ===" />
                        <form:options items="${device2List}" />
                      </form:select>
				    </div>
				  </div>
				  <div class="form-group row">
				  	<label for="bkdate_begin_2" class="col-sm-2 col-form-label"><spring:message code="backup.date" /></label>
				  	<div class="col-sm-4">
				      <input type="date" class="form-control form-control-sm" id="queryExcuteDateBegin2_mobile">
				    </div>
				    <div class="col-sm-1">~</div>
				    <div class="col-sm-4">
				      <input type="date" class="form-control form-control-sm" id="queryExcuteDateEnd2_mobile">
				    </div>
		    	  </div>
		    	  <div class="form-group row">
		    	    <label for="device_2" class="col-sm-2 col-form-label"><spring:message code="category" /></label>
		    	    <div class="col-sm-10">
		    	      <form:select path="queryConfigType" id="queryConfigType_mobile" class="form-control form-control-sm">
                        <form:option value="" label="=== ALL ===" />
                        <form:options items="${configTypeList}" />
                      </form:select>
				    </div>
				  </div>
				  <div class="form-group row">
					<div class="col-sm-12">
					  <input id="queryNewChkbox_mobile" type="checkbox" value="" style="vertical-align:middle;" checked="checked"> <spring:message code="last.version" />
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
		<table id="resultTable" class="dataTable myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
		  <thead class="center">
		    <tr>
		      <th scope="col" nowrap="nowrap"><spring:message code="action" />&nbsp;<input type="checkbox" id="checkAll" name="checkAll" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="device.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="device.model" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="config.type" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="config.version" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="backup.time" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
  </div>
  
</section>

<!-- Modal [Compare] start -->
<div class="modal fade" id="compareModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-xxg" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="exampleModalLabel"><span id="msgModal_title"><spring:message code="version.compare" /></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <div class="form-group row">
        	<div class="col-1"></div>
        	<label for="viewModal_version" class="col-md-1 col-sm-12 col-form-label"><spring:message code="config.version.short" /> :</label>
    		<input type="text" class="form-control form-control-sm col-md-4 col-sm-12" id="viewModal_versionLeft" readonly>
    		<label for="viewModal_version" class="col-md-1 col-sm-12 col-form-label"><spring:message code="config.version.short" /> :</label>
    		<input type="text" class="form-control form-control-sm col-md-4 col-sm-12" id="viewModal_versionRight" readonly>
        </div>
        <div class="form-group row">
        	<div class="form-control form-control-sm col-1 compare-line script" id="compareModal_contentLineNum"></div>
        	<div class="form-control form-control-sm col-5 compare-content nowrap script" id="compareModal_contentLeft"></div>
        	<div class="form-control form-control-sm col-5 compare-content nowrap script" id="compareModal_contentRight"></div>
        	<div class="col-1">
        		<span data-feather="chevrons-up" class="feather-compare" id="jumpToTop"></span>
        		<span data-feather="chevron-up" class="feather-compare" id="jumpToPre"></span>
        		<span data-feather="chevron-down" class="feather-compare" id="jumpToNext"></span>
        		<span data-feather="chevrons-down" class="feather-compare" id="jumpToBottom"></span>
        	</div>
        	<div class="col-12 center">
        		<span id="compareModal_summary"></span>
        	</div>
        </div>
      </div>
      <div class="modal-footer">
        <!-- <button type="button" class="btn btn-secondary" data-dismiss="modal">關閉</button> -->
      </div>
    </div>
  </div>
</div>
<!-- Modal [Compare] end -->

<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.version.main.min.js"></script>
	