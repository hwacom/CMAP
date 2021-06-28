<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<!-- <style type="text/css">
table.dataTable tr th.select-checkbox.selected::after {
    content: "✔";
    margin-top: -11px;
    margin-left: -4px;
    text-align: center;
    text-shadow: rgb(176, 190, 217) 1px 1px, rgb(176, 190, 217) -1px -1px, rgb(176, 190, 217) 1px -1px, rgb(176, 190, 217) -1px 1px;
}

</style> -->
<c:set var="__SHOW__" value="Y" scope="request"/>
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
	      			<div class="col-md-4 group-field-other">
						<label for="querySensorType" class="font-weight-bold" style="width: 18%"><spring:message code="func.plugin.alarm.summary.sensor.type" /></label>
						<form:select path="inputQuerySensorType" id="querySensorType" style="width: 40%">
							<form:option value="" label="== ALL ==" />
	                        <form:options items="${inputQuerySensorType}" />
	                    </form:select>
					</div>
					<div class="col-md-4 group-field-other">
						<label for="queryStatus" class="font-weight-bold" style="width: 18%"><spring:message code="func.plugin.alarm.summary.alarm.status" /></label>
						<form:select path="inputQueryStatus" id="queryStatus" style="width: 40%">
							<form:option value="" label="== ALL ==" />
	                        <form:options items="${inputQueryStatus}" />
	                    </form:select>
					</div>
		    	    <div class="col-md-2 float-md-right" style="padding-top: 10px;width: 10%">
		    	    	<button type="button" class="btn btn-success" style="width: 100%" id="btnUpdateStatus"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
		    	    		<spring:message code="action" />
		    	    	</button>
		    	    </div>
		    	    <div class="col-md col-lg-2" style="padding-top: 10px;width: 10%">
		    	    	<button type="button" class="btn btn-primary btn-sm float-md-right" style="width: 100%" id="btnSearch_web"  ${is_btnDisabled eq 'true' ? 'disabled' : '' }>
		    	    		<spring:message code="btn.query" />
		    	    	</button>
		    	    </div>
		    	</div>
		    	<div class="form-group row" style="margin-bottom: -.2rem;">
		    	    <div class="col-md-8 group-field-other">
		    	    	<label for="queryDateBegin" class="font-weight-bold must" style="width: 5%"><spring:message code="time" /></label>
		    	    	<input type="date" id="queryDateBegin"><input type="time" id="queryTimeBegin"> ~ <input type="date" id="queryDateEnd"><input type="time" id="queryTimeEnd">
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
	<nav>
		<div class="nav nav-pills my-2" id="nav-tab" role="tablist">
		    <a class="nav-item nav-link active" id="tab1" data-toggle="tab" href="#nav-tab1" role="tab" aria-controls="tab1" aria-expanded="true">active</a>
		    <a class="nav-item nav-link" id="tab2" onclick="findHistoryData()" data-toggle="tab" href="#nav-tab2" role="tab" aria-controls="tab2"><spring:message code="func.plugin.alarm.summary.alarm.data.status.finish" /></a>
		</div>
	</nav>
	<div class="tab-content" id="nav-tabContent">
	  <div class="tab-pane fade show active" id="nav-tab1" role="tabpanel" aria-labelledby="tab1">
		  <div class="col-sm-12 myTableSection" id="tableSection1">
		  	<table id="resultTable" class="dataTable dataTable_1 myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
			  <thead class="center">
			    <tr>
			      <th scope="col" nowrap="nowrap"><input type="checkbox" id="chkAllBox1" name="chkAllBox1" class="select-checkbox"></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.sensor.name" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.sensor.type" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="device.name" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.alarm.status" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.alarm.time" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.close.time" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.last.value" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="message" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.ticket.priority" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="remark" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.log" /></th>
			    </tr>
			  </thead>
			</table>
		  </div>
	  </div>
	  <div class="tab-pane fade" id="nav-tab2" role="tabpanel" aria-labelledby="tab2">
	  	<div class="col-sm-12 myTableSection" id="tableSection2">
		  	<table id="resultTable2" class="dataTable dataTable_2 myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%">
			  <thead class="center">
			    <tr>
			      <th scope="col" nowrap="nowrap"><input type="checkbox" id="chkAllBox2" name="chkAllBox2" class="select-checkbox"></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.sensor.name" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.sensor.type" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="device.name" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.alarm.status" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.alarm.time" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.close.time" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.last.value" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="message" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.ticket.priority" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="remark" /></th>
			      <th scope="col" nowrap="nowrap"><spring:message code="func.plugin.alarm.summary.log" /></th>
			    </tr>
			  </thead>
			</table>
		</div>
	  </div>
	</div>
  </div>
  
</section>

<!-- Modal [Update_Status_dialog] start -->
<div class="modal fade" id="updateStatusDialogModal" tabindex="-1" role="dialog" aria-labelledby="updateStatusDialogModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="updateStatusDialogModalLabel"><span id="msgModal_title"><spring:message code="action" /><spring:message code="confirm" /></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formUpdateStatus" name="formUpdateStatus">
            <div class="box-body">
                <div class="form-group row">
                	<label for="updateStatus" class="col-md-2 col-sm-3 col-form-label"><spring:message code="action" /></label>
                  	<div class="col-md-10 col-sm-9">
		                <select id="updateStatus" name="updateStatus" class="form-control form-control-sm">
							<option value="active" label=<spring:message code="func.plugin.alarm.summary.alarm.data.status.active" /> />
							<c:if test="${ticketFlag eq __SHOW__}">
								<option value="doing" label=<spring:message code="func.plugin.alarm.summary.alarm.data.status.doing" /> />
							</c:if>							
							<option value="finish" label=<spring:message code="func.plugin.alarm.summary.alarm.data.status.finish" /> />
					    </select>
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
   			<button type="button" class="btn btn-success" id="btnConfirmUpdate" style="width: 100%"><spring:message code="confirm" /></button>
   		</div>
	  </div>
    </div>
  </div>
</div>
<!-- Modal [Update_Status_dialog] end -->

<!-- Modal [addNewTicket_dialog] start -->
<div class="modal fade" id="addNewTicketDialogModal" tabindex="-1" role="dialog" aria-labelledby="addNewTicketDialogModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="addNewTicketDialogModalLabel"><span id="msgModal_title"><spring:message code="action" /><spring:message code="confirm" /></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formAddNewTicket" name="formAddNewTicket">
            <div class="box-body">
                <div class="col-md group-field-other">
					<label for="inputOwner" class="font-weight-bold" style="width: 20%"><spring:message code="func.plugin.ticket.owner" /></label>
                    <form:select path="inputQueryOwner" id="inputOwner" style="width: 55%">
						<optgroup label="<spring:message code="group.name" />">
							<form:options items="${inputQueryGroup}" />
						</optgroup>
                        <optgroup label="<spring:message code="user" />">
							<form:options items="${inputQueryOwner}" />
						</optgroup>
                    </form:select>
				</div>
            </div>
        </form>
      </div>
      <div class="modal-footer center">
   		<div class="col-4">
   			<button type="button" class="btn btn-secondary" id="btnClose" data-dismiss="modal" style="width: 100%"><spring:message code="close" /></button>
   		</div>
   		<div class="col-4">
   			<button type="button" class="btn btn-success" id="btnConfirmCreate" style="width: 100%"><spring:message code="confirm" /></button>
   		</div>
	  </div>
    </div>
  </div>
</div>
<!-- Modal [addNewTicket_dialog] end -->

<!-- Modal [alarm_dialog] start -->
<div class="modal fade" id="alarmDialogModal" tabindex="-1" role="dialog" aria-labelledby="alarmDialogModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="alarmDialogModalLabel"><spring:message code="func.plugin.alarm.summary.sensor.name" />：<span id="msgModal_title" class="alarmTitle"></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formAlarm" name="formAlarm">
            <div class="box-body">
				
			</div>
			<div class="refreshable-initialized clock-keeper-container">
				<div class="tab-container ticket-history-main-container">
					<div class="overviewsmalldata">
						<div class="overview-block">
							<spring:message code="func.plugin.ticket.priority" />：<span class="overview-data alarmPriority"></span>
						</div>
						<div class="overview-block">
							<spring:message code="func.plugin.alarm.summary.sensor.type" />：<span class="overview-data alarmSensorType"></span>
						</div>
						<div class="overview-block">
							<spring:message code="func.plugin.alarm.summary.alarm.status" />：<span class="overview-data alarmStatus"></span>
						</div>
						<div class="overview-block">
							<spring:message code="func.plugin.alarm.summary.alarm.time" />：<span class="overview-data alarmTimeStr"></span>
						</div>
						<div class="overview-block">
							<spring:message code="func.plugin.alarm.summary.last.value" />：<span class="overview-data alarmLastValue"></span>
						</div>
						<div class="overview-block">
							<spring:message code="message" />：<span class="overview-data alarmMessage"></span>
						</div>
						<div class="overview-block">
							<spring:message code="remark" />：<span class="overview-data alarmRemark"></span>
						</div>
					</div>
				</div>
			</div>
        </form>
      </div>
      <div class="modal-footer center">
   		<div class="col-4">
   			<button type="button" class="btn btn-secondary" id="btnClose" data-dismiss="modal" style="width: 100%"><spring:message code="close" /></button>
   		</div>
	  </div>
    </div>
  </div>
</div>
<!-- Modal [alarm_dialog] end -->


<!-- Modal [alarm_log_dialog] start -->
<div class="modal fade" id="alarmLogDialogModal" tabindex="-1" role="dialog" aria-labelledby="alarmLogDialogModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
      
        <h5 class="modal-title" id="alarmLogDialogModalLabel"><spring:message code="func.plugin.alarm.summary.log" />#<span id="msgModal_title" class="alarmLogTitle"></span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formAlarmLog" name="formAlarmLog">
            <div id="alarmLogBody" class="box-body">
				<div id="alarmLogDiv">
				
				</div>
			</div>
        </form>
      </div>
      <div class="modal-footer center">
   		<div class="col-4">
   			<button type="button" class="btn btn-secondary" id="btnClose" data-dismiss="modal" style="width: 100%"><spring:message code="close" /></button>
   		</div>
	  </div>
    </div>
  </div>
</div>
<!-- Modal [alarm_log_dialog] end -->

<script>
	var msg_chooseDate = '<spring:message code="please.choose" /><spring:message code="date" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.alarmSummary.min.js"></script>
