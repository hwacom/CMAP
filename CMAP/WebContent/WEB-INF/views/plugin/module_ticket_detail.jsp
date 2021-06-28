<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<style type="text/css">
.text-with-hr {
	text-align: left;
	position: relative;
	z-index: 2;
}
/*
横线，并通过z-index：-1将or附近的横线遮盖住
*/
.text-with-hr:before {
	position: absolute;
	content: '';
	top: 20px;
	left: 0px;
	width: 100%;
	border-bottom: 1px solid #d4d4d4;
	z-index: -1;
}
.text-with-hr span {
	display: inline-block;
	background: white;
	padding: 10px;
}
.ticket-main-container {
	display: block;
	background: white;
	padding: 20px;
}
</style>
<section>
  <input type="hidden" id="pageLength" name="pageLength" value="${pageLength }" />
  <input type="hidden" id="inputListId" name="inputListId" value="${ticket.listId }" />
  <input type="hidden" id="inputOwner" name="inputOwner" value="${ticket.owner }" />

	<div class="ticket-main-container" id="main"
		data-title="工單 #${ticket.listId }" data-contexthelpshow="false">
		<div id="content" class="limitedcontentwidth ticket-content">
			<div>
				<div style="text-align: left">
					<a href="${pageContext.request.contextPath}/plugin/module/tickets"><span data-feather="arrow-left-circle"></span><spring:message code="func.plugin.ticket.back" /></a>
				</div>
				<div style="text-align: right">
					<label for="queryOwner" class="font-weight-bold"><spring:message code="func.plugin.ticket.forward" />：</label>
					<form:select path="inputQueryOwner" id="queryOwner">
						<optgroup label="<spring:message code="group.name" />">
							<form:options items="${inputQueryGroup}" />
						</optgroup>
                        <optgroup label="<spring:message code="user" />">
							<form:options items="${inputQueryOwner}" />
						</optgroup>
                    </form:select>
                    <a href="#" onclick="confirmForward()"><span data-feather="fast-forward"></span></a><!-- '${pageContext.request.contextPath}/prtg/netFlowOutput/core' -->
				</div>
			</div>
			<br/>
			<div id="ticketheader"
				class="ticket-header refreshable-initialized clock-keeper-container">
				<div class="bigsensoricon ticket-status">
					<h1>待辦事項 工單 #${ticket.listId } ${ticket.subject }</h1>
				</div>
			</div>
			<div class="refreshable-initialized clock-keeper-container">
				<div id="tickethistory"
					class="tab-container ticket-history-main-container">
					<div class="overviewsmalldata overviewsmalldata-tickets">
						<div class="overview-block">
							<span class="overview-title">狀態:</span> <span
								class="overview-data">${ticket.status }</span>
						</div>
						<div class="overview-block" id="ticketOwner">
							<span class="overview-title"><spring:message code="func.plugin.ticket.owner" />:</span> 
							<span class="overview-data">
								<c:if test="${ticket.ownerType eq 'G'}">
									<spring:message code="group.name" /> - 
			                    </c:if>
			                    <c:if test="${ticket.ownerType eq 'U'}">
									<spring:message code="user" /> - 
			                    </c:if>${ticket.ownerStr }</span>
								<!-- <a href="/tickets.htm?filter_user=200"></a> -->
						</div>
						<div class="overview-block">
							<span class="overview-title"><spring:message code="func.plugin.ticket.listId" />:</span> <span
								class="overview-data">${ticket.listId }</span>
						</div>
						<br/>
						<div class="description-edit edit">
							<textarea
								class="field js-description-draft description" id="inputContent" style="width: 100%; resize: none;" rows="10" required="required" placeholder="新增更詳細的敘述…"></textarea>
						</div>
						<div class="col-2">
				      		<button type="button" class="btn btn-success" id="btnSave" style="width: 100%;">確認儲存</button>
				      	</div>
					</div>
					<br/><br/>
					<div>
						<h4><spring:message code="func.plugin.ticket.history" /></h4>
						${historyContent }
					</div>
				</div>
			</div>
		</div>
	</div>
</section>


<script>
	var msg_chooseDate = '<spring:message code="please.choose" /><spring:message code="date" />';
	var msg_reasign = '<spring:message code="func.plugin.ticket.forward" />';
</script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.tickets.min.js"></script>
