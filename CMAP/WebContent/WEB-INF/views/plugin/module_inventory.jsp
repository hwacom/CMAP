<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<section>
  <input type="hidden" id="pageLength" name="pageLength" value="${pageLength }" />
  <input type="hidden" id="selectionclassUse" name="selectionclassUse" value="${inputInvDepartment }" />
  <input type="hidden" id="selectionidcId" name="selectionidcId" value="${inputInvDepartment }" />
  <input type="hidden" id="selectionstorageType" name="selectionstorageType" value="${inputInvDepartment }" />
  
  <div id="content" class="container-fluid">
    <!-- [START]查詢欄位&操作按鈕 for 大型解析度螢幕 -->
	<div id="search-bar-large" class="row search-bar-large">
	  <!-- [START]查詢欄位bar -->
      <div class="col-12 search-bar">
      	<form>
      		<div class="container-fluid">
      			<div class="form-group row">
		    	    <div class="col-lg-3 group-field-other">
		    	    	<span class="font-weight-bold" style="width: 20%"><spring:message code="group.name" /></span>
		    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryGroup" id="queryGroup" onchange="changeDeviceMenu('queryDevice', this.value)">
	                        <form:option value="" label="== ALL ==" />
	                        <form:options items="${groupList}" />
	                    </form:select>
		    	    </div>
		    	    <div class="col-lg-3 group-field-other">
						<span class="font-weight-bold" style="width: 20%"><spring:message code="device.name" /></span>
		    	    	<form:select class="selectpicker" data-live-search="true" data-width="75%" path="queryDevice" id="queryDevice">
	                        <form:option value="" label="== ALL ==" />
	                        <form:options items="${deviceList}" />
	                    </form:select>
					</div>
					<div class="col-lg-2" style="padding-top: 10px;width: 20%">
		    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 90%" id="btnSearch_web"><spring:message code="inquiry" /></button>
		    	    </div>
		    	    <div class="col-lg-2" style="padding-top: 10px;width: 20%">
		    	    	<button type="button" class="btn btn-info btn-sm" style="width: 90%" id="btnExport_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
		    	    		<spring:message code="btn.export" />
		    	    	</button>
		    	    </div>
		    	    <div class="col-lg-2" style="padding-top: 10px;width: 20%">		    	    	
		    	    	<button type="button" class="btn btn-warning btn-sm" style="width: 90%" id="btnImport_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
		    	    		<spring:message code="btn.import" />
		    	    	</button>
		    	    </div>
	      	  	</div>
	      		<div class="form-group row" style="margin-bottom: -.2rem;">
		    	    <div class="col-lg-3 group-field-other">
						<label for="queryProbe" class="font-weight-bold" style="width: 35%"><spring:message code="func.plugin.inventory.probe" /></label>
						<input type="text" id="queryProbe" style="width: 50%">
					</div>
					<div class="col-lg-3 group-field-other">
						<label for="queryDeviceName" class="font-weight-bold" style="width: 35%"><spring:message code="ip.trace.poller.device.name" /></label>
						<input type="text" id="queryDeviceName" style="width: 50%">
					</div>
					<div class="col-lg-3 group-field-other">
		                <label for="queryDeviceType" class="font-weight-bold" style="width: 35%"><spring:message code="func.plugin.inventory.type" /></label>
		    	    	<form:select  data-width="100%" path="inputInvDeviceType" id="queryDeviceType">
	                        <form:option value="" label="== ALL ==" />
	                        <form:options items="${inputInvDeviceType}" />
	                    </form:select>
		            </div>
					<div class="col-lg-3 group-field-other">
						<input type="checkbox" id="queryDiffOnly" name="queryDiffOnly" />
	    	    		<label for="queryDiffOnly" class="font-weight-bold"><spring:message code="func.plugin.inventory.query.diff.only" /></label>  
					</div>					
		    	</div>
		    	<div class="form-group row" style="margin-bottom: -.4rem; margin-top: -.5rem;">
		    	    <div class="col-lg-3 group-field-other">
						<label for="queryIP" class="font-weight-bold" style="width: 35%"><spring:message code="ip.address" /></label>
						<input type="text" id="queryIP" style="width: 50%">
					</div>
					<div class="col-lg-3 group-field-other">
						<label for="queryModel" class="font-weight-bold" style="width: 35%"><spring:message code="device.model" /></label>
						<input type="text" id="queryModel" style="width: 50%">
					</div>					
		    	    <div class="col-lg-3 group-field-other" style="padding-top: 5px;">
		    	    	<label for="queryDeviceType" class="font-weight-bold" style="width: 35%">自訂<spring:message code="group.name" /></label>
						<input type="text" id="queryGroupName" style="width: 50%">
		    	    </div>
		    	    <div class="col-lg-3 group-field-other" style="padding-top: 5px;">
		    	    	<input type="hidden" id="timeoutMsg" style="width: 20%">
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
		  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnAdd"><spring:message code="btn.add" /></button>
		  	    </div>
		  	    <div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnModify"><spring:message code="btn.modify" /></button>
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
		      <th scope="col" nowrap="nowrap"><spring:message code="action" />&nbsp;<input type="checkbox" id="checkAll" name="checkAll" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="device.id" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.probe" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.trace.poller.device.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="ip.address" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.type" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.brand" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="device.model" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="system.version" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.serial.number" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.manufacture.date" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="status" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.sync.flag" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.diffrence.comparison" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.upload.time" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.custodian" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.department" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.user" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.north.flag" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="remark" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.inventory.showDetail" /></th>
		    </tr>
		  </thead>
		</table>
	  </div>
	</div>
	
  </div>
  
</section>

<!-- Modal [Add/Modify] start -->
<div class="modal fade" id="addModifyModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="exampleModalLabel"><span id="msgModal_title">新增/維護設備資產</span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formEdit" name="formEdit">
        	<div class="card card-body">
        	  <div class="col-12">
	           	<div class="form-group row selectOneInfo">
	            	<label for="addProbe" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.plugin.inventory.probe" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm infoInput" id="addProbe" name="inputmodifyProbe" >
	            	</div>
	            </div>
	            <div id="addGroupDiv">
			      	<div id="addGroupBodyDiv" class="form-group row selectOneInfo">
			      		<!-- <label for="addGroup" class="col-md-2 col-sm-3 col-form-label"><spring:message code="group.name" /></label>
		            	<div class="col-md-10 col-sm-9" id="inputAddGroupDiv" style="width: 80%">
		            		<input type="text" class="form-control form-control-sm" id="addGroup" name="name="modifyGroup" onchange="showValue()">
		            	</div>
		            	<button type="button" class="btn btn-primary btn-sm" onclick="divAddInputText('')">+</button>
		            	<div class="col-md-10 col-sm-9">
		            		<b><font class="blue">群組名稱：</font><span id="showGroupName"></span></b>
		            	</div> -->            	
			      	</div>
		        </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addDeviceName" class="col-md-2 col-sm-3 col-form-label"><spring:message code="ip.trace.poller.device.name" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm infoInput" id="addDeviceName" name="inputmodifyDeviceName" >
	            	</div>
	            </div>	            
	            <div class="form-group row selectOneInfo">
	            	<label for="addDeviceIp" class="col-md-2 col-sm-3 col-form-label"><spring:message code="ip.address" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm infoInput" id="addDeviceIp" name="inputmodifyDeviceIp" >
	            	</div>
	            </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addDeviceType" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.plugin.inventory.type" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm infoInput" id="addDeviceType" name="inputmodifyDeviceType" >
	            	</div>
	            </div>
	            
	            <div class="form-group row selectOneInfo">
	            	<label for="addBrand" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.plugin.inventory.brand" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm infoInput" id="addBrand" name="inputmodifyBrand" >
	            	</div>
	            </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addModel" class="col-md-2 col-sm-3 col-form-label"><spring:message code="device.model" /></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm infoInput" id="addModel" name="inputmodifyModel" >
	            	</div>
	            </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addSystemVersion" class="col-md-2 col-sm-3 col-form-label"><spring:message code="system.version" /></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="text" class="form-control form-control-sm infoInput" id="addSystemVersion" name="inputmodifySystemVersion" >
	                </div>
	            </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addSerialNumber" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.plugin.inventory.serial.number" /></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="text" class="form-control form-control-sm infoInput" id="addSerialNumber" name="inputmodifySerialNumber" >
	                </div>
	            </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addManufactureDate" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.plugin.inventory.manufacture.date" /></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="text" class="form-control form-control-sm infoInput" id="addManufactureDate" name="inputmodifyManufactureDate" >
	                </div>
	            </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addStatus" class="col-md-2 col-sm-3 col-form-label"><spring:message code="status" /></label>
	            	<div class="col-md-10 col-sm-9 addStatus">
		                <form:select path="inputInvStatus" id="addStatus" name="inputmodifyStatus" class="form-control form-control-sm infoInput">
		                  	<form:options items="${inputInvStatus}" />
		                </form:select>
	                </div>
	            </div>
	            <div class="form-group row">
	            	<label for="addSyncFlag" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.plugin.inventory.sync.flag" /></label>
	            	<div class="col-md-10 col-sm-9 addSyncFlag">
		            	<select id="addSyncFlag" name="inputmodifySyncFlag" class="form-control form-control-sm infoInput">
							<option value="N" label="N" />
							<option value="Y" label="Y" />
					    </select>
				    </div>
	            </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addCustodian" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.plugin.inventory.custodian" /></label>
	            	<div class="col-md-10 col-sm-9 addCustodian">
		            	<form:select path="inputInvUsers" id="addCustodian" name="inputmodifyCustodian" class="form-control form-control-sm infoInput">
		                  	<form:options items="${inputInvUsers}" />
		                </form:select>
	                </div>
	            </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addDepartment" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.plugin.inventory.department" /></label>
	            	<div class="col-md-10 col-sm-9 addDepartment">
		            	<form:select path="inputInvDepartment" id="addDepartment" name="inputmodifyDepartment" class="form-control form-control-sm infoInput">
		                  	<form:options items="${inputInvDepartment}" />
		                </form:select>
	                </div>
	            </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addUser" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.plugin.inventory.user" /></label>
	            	<div class="col-md-10 col-sm-9 addUser">
		            	<form:select path="inputInvUsers" id="addUser" name="inputmodifyUser" class="form-control form-control-sm infoInput">
		                  	<form:options items="${inputInvUsers}" />
		                </form:select>
	                </div>
	            </div>
	            <div class="form-group row">
	            	<label for="addNorthFlag" class="col-md-2 col-sm-3 col-form-label"><spring:message code="func.plugin.inventory.north.flag" /></label>
	            	<div class="col-md-10 col-sm-9 addNorthFlag">
		            	<select id="addNorthFlag" name="inputmodifyNorthFlag" class="form-control form-control-sm infoInput">
							<option value="N" label="N" />
							<option value="Y" label="Y" />
					    </select>
				    </div>
	            </div>
	            <div class="form-group row selectOneInfo">
	            	<label for="addRemark" class="col-md-2 col-sm-3 col-form-label"><spring:message code="remark" /></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="text" class="form-control form-control-sm infoInput" id="addRemark" name="inputmodifyRemark" >
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

<!-- Modal [資料匯入] start -->
<div class="modal fade" id="inventoryDataImportModal" tabindex="-1" role="dialog" aria-labelledby="inventoryDataImportLabel" aria-hidden="true">
  <div class="modal-dialog modal-mid" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="inventoryDataImportLabel"><span id="msgModal_title">資料匯入</span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <div class="form-group row">
        	<div class="form-control form-control-sm col-12 export-description">
	        	說明:<br>
	        	<ol style="padding-left: 15px;">
	        	  <li>請以<font class="blue">CSV檔案格式</font>貼上/輸入資料。一行代表一筆資料，<font class="blue">第一行為表頭</font>，以「<font class="blue">逗號(,)</font>」串接欄位</li>
	        	  <li>欄位由左至右依序為: <font class="blue"><spring:message code="func.plugin.inventory.probe" /> > <spring:message code="group.name" />1 
	        	  > <spring:message code="group.name" />2 > <spring:message code="group.name" />3 > <spring:message code="group.name" />4 > <spring:message code="group.name" />5 
	        	  										  > <spring:message code="ip.trace.poller.device.name" />
											        	  > <spring:message code="ip.address" /> > <spring:message code="func.plugin.inventory.type" />
											        	  > <spring:message code="func.plugin.inventory.brand" /> > <spring:message code="device.model" />
											        	  > <spring:message code="system.version" /> > <spring:message code="func.plugin.inventory.serial.number" /> 
											        	  > <spring:message code="func.plugin.inventory.manufacture.date" />
											        	  > <spring:message code="remark" /></font></li>
	        	  <li>若備註內容含有「<font class="blue">逗號(,)</font>」，請以「<font class="blue">雙引號(")</font>」<font class="blue">前後包夾整段備註</font>(參照第2條範例)</li>
	        	  <li>若備註內容含有「<font class="blue">雙引號(")</font>」，請以<font class="blue">兩個「雙引號(")</font>」<font class="blue">替代</font>，並且以「<font class="blue">雙引號(")</font>」<font class="blue">前後包夾整段備註</font>(參照第3條範例)</li>
	        	  <li>(上述末2點為CSV檔針對保留字元處理作法，<font style="text-decoration: underline;">若您是透過文字編輯器開啟CSV檔複製內容，則可忽略上述兩點</font>)</li>
	        	</ol>
	        	範例:<br>
	        	column1,column2,column3<br>
	        	column1,column2,<font class="red">"</font>備註內容有逗號,用雙引號包夾<font class="red">"</font><br>
	        	column1,column2,<font class="red">"</font>前後雙引號包夾,<font class="blue">""</font>內容有雙引號則再多加一個<font class="blue">""</font><font class="red">"</font><br>
	        	...
        	</div>
        	<ol style="padding-left: 15px;">
	        	  <li><a href="#" onclick="javascript:window.open('${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/${pageContext.request.contextPath}resource/download/inventoryImportSample','_blank')">
		            	<span><spring:message code="func.plugin.inventory.sample.download" />(Server)</span></a>
		          </li>
		          <li><a href="#" onclick="javascript:window.open('${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/${pageContext.request.contextPath}resource/download/inventoryImportSample','_blank')">
	            	<span><spring:message code="func.plugin.inventory.sample.download" />(VM)</span></a>
            	  </li>
            	  <li>
            	  	<a href="#" onclick="javascript:window.open('${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/${pageContext.request.contextPath}resource/download/inventoryImportSample','_blank')">
		            	<span><spring:message code="func.plugin.inventory.sample.download" />(Storage)</span>
		            </a>
            	  </li>
            	  <li>
            	  	<a href="#" onclick="javascript:window.open('${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/${pageContext.request.contextPath}resource/download/inventoryImportSample','_blank')">
		            	<span><spring:message code="func.plugin.inventory.sample.download" />(UPS)</span>
		            </a>
            	  </li>
	       	</ol>
        </div>
     	<div id="div_edit_panel" class="form-group row">
        	<label for="importFileName" class="col-12 col-form-label"><spring:message code="please.choose" /> :</label>
    		<div class="form-control form-control-sm col-12">    			
		    	<input type="file" id="importFileName" onchange="checkData(this.files[0])">
    		</div>
        </div>
        <div class="modal-footer center">
      		<button type="button" class="btn btn-secondary" id="btnClose" data-dismiss="modal"><spring:message code="btn.close" /></button>
      	</div>
      </div>
    </div>
  </div>
</div>
<!-- Modal [資料匯入] end -->

<!-- Modal [View Detail] start -->
<div class="modal fade" id="saveDetailModal" tabindex="-1" role="dialog" aria-labelledby="saveDetailLabel" aria-hidden="true">
  <div class="modal-dialog modal-mid" role="document">
    <div class="modal-content" id="saveDetailModalContentDiv">
      <div class="modal-header">
        <h5 class="modal-title" id="saveDetailLabel"><span id="msgModal_title"></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body" id="saveDetailModalDiv">
      	<input type="hidden" id="detailDeviceId" name="detailDeviceId" />
      	<input type="hidden" id="detailDeviceType" name="detailDeviceType" />
      	<div id="saveDetailModalBodyDiv">
      	</div>
      </div>
      <div class="modal-footer center">
      		<button type="button" class="btn btn-secondary" id="btnClose" data-dismiss="modal"><spring:message code="btn.close" /></button>
      		<button type="button" class="btn btn-success" id="btnSaveDetail"><spring:message code="btn.save" /></button>
      </div>
    </div>
  </div>
</div>
<!-- Modal [View Detail] end -->

<!-- Modal [delete_dialog] start -->
<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="deleteModalLabel"><span id="msgModal_title"><spring:message code="action" /><spring:message code="confirm" /></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formDelete" name="formDelete">
            <div class="box-body">
                <div class="form-group row">
	            	<label for="deleteRsn" class="col-md-3 col-sm-4 col-form-label"><spring:message code="func.plugin.inventory.manufacture.deleteRsn" /></label>
	            	<div class="col-md-12 col-sm-10">
	                	<input type="text" class="form-control form-control-sm" id="deleteRsn" name="inputDeleteRsn" >
	                </div>
	            </div>     
            </div>
        </form>
      </div>
      <div class="modal-footer center">
   		<div class="col-4">
   			<button type="button" class="btn btn-secondary" id="btnClose" data-dismiss="modal" style="width: 100%"><spring:message code="close" /></button>
   		</div>
   		<div class="col-4">
   			<button type="button" class="btn btn-success" id="btnConfirmDelete" style="width: 100%"><spring:message code="confirm" /></button>
   		</div>
	  </div>
    </div>
  </div>
</div>
<!-- Modal [Update_Status_dialog] end -->

<script>
	var msg_chooseDate = '<spring:message code="please.choose" /><spring:message code="date" />';
	var springGroupName = '<spring:message code="group.name" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.inventory.min.js"></script>
