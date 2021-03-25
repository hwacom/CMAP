<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<section>
	<form role="form" id="formEdit">
       	<div class="card card-body">
       		<h5 class="modal-title" style="font-size: 50px;"><span>密碼到期請完成密碼變更</h5><br><br>
           	<div class="form-group row">
            	<label for="modeifyAccount" class="col-md-2 col-sm-3 col-form-label"><spring:message code="user.account" /></label>
            	<div class="col-md-10 col-sm-9">
            		<input type="text" class="form-control form-control-sm checkRequired" id="modeifyAccount" name="modeifyAccount" value="${userAccount}" disabled="disabled">
            	</div>
            </div>
            <div class="form-group row">
            	<label for="modifyUserName" class="col-md-2 col-sm-3 col-form-label"><spring:message code="user.name" /></label>
            	<div class="col-md-10 col-sm-9">
                	<input type="text" class="form-control form-control-sm" id="modifyUserName" name="modifyUserName" value="${userName}" disabled="disabled">
                </div>
            </div>
            <div class="form-group row">
            	<label for="modifyPassword" class="col-md-2 col-sm-3 col-form-label"><spring:message code="new.login.password" /><input type="checkbox" id="chkbox" name="chkbox" checked="checked" onclick="return false" value="${userId}"/></label>
            	<div class="col-md-10 col-sm-9">
                	<input type="text" class="form-control form-control-sm" id="modifyPassword" name="modifyPassword">
                </div>
            </div>
		</div>
		<div class="center" style="font-size:50px;height: 50px;">
       		<button type="button" class="btn btn-success" style="font-size:20px;" id="btnModifySubmit"><spring:message code="btn.save" /></button>
		</div>
   </form>

</section>

<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.admin.user.right.min.js"></script>