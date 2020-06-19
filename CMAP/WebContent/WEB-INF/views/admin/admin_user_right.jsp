<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<section>

  <div class="container-fluid">
    <!-- [START]查詢欄位&操作按鈕 for 大型解析度螢幕 -->
	<div class="row search-bar-large">
	  <!-- [START]查詢欄位bar -->
      <!-- <div class="col-12 search-bar">
		<div class="container-fluid">
      	  <div class="form-group row">
			<div class="col-lg-2" style="padding-top: 5px;">
    	    	<button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_web"><spring:message code="inquiry" /></button>
    	    </div>
    	    <div class="col-lg-4" style="padding-top: 5px;">
    	    	<span id="diffMsg" style="color: red; font-weight: bold; background-color: yellow;"></span>
    	    </div>
      	  </div>
      	</div> 

      </div>-->
      <!-- [END]查詢欄位bar -->
      <!-- [START]操作按鈕bar -->
      <div class="col-12 action-btn-bar">
        <div class="container-fluid">
        	<div id="defaultActionBar" class="row">
        		<c:if test="${isAdmin eq true}">
			  	    <div class="col-lg-2 action-btn-bar-style" align="center">
			  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnAdd"><spring:message code="btn.add" /></button>
			  	    </div>
		  	    </c:if>
		  	    <div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnModify"><spring:message code="btn.modify" /></button>
		  	    </div>
		  	    <c:if test="${isAdmin eq true}">
			  	    <div class="col-lg-2 action-btn-bar-style" align="center">
			  	    	<button type="button" class="btn btn-danger btn-sm" style="width: 100%" id="btnDelete"><spring:message code="btn.delete" /></button>
			  	    </div>
		  	    </c:if>
        	</div>
        	<div id="modifyActionBar" class="row" style="display: none">
		  	    <div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-success btn-sm" style="width: 100%" id="btnModifySubmit"><spring:message code="btn.submit" /></button>
		  	    </div>
		  	    <div class="col-lg-2 action-btn-bar-style" align="center">
		  	    	<button type="button" class="btn btn-dark btn-sm" style="width: 100%" id="btnModifyCancel"><spring:message code="btn.cancel" /></button>
		  	    </div>
        	</div>
        </div>
      </div>
      <!-- [END]操作按鈕bar -->
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
			  <div class="form-group row">
	    	    <div class="col-sm-12">
			      <button type="button" class="btn btn-primary btn-sm" style="width: 100%" id="btnSearch_mobile"><spring:message code="inquiry" /></button>
			    </div>
			  </div>
		  	</div>
		  </div>
	  </div>
	</div> -->
	<!-- [END]查詢欄位 for 中小型解析度螢幕 -->
	
	<!-- 查詢結果TABLE區塊 -->
	<div class="row">
	  <div class="col-sm-12 myTableSection">
		<table id="resultTable" class="dataTable myTable table-striped table-hover table-sm table-responsive-sm nowrap" style="width:100%;">
		  <thead class="center">
		    <tr>
		      <th scope="col" nowrap="nowrap"><spring:message code="action" />&nbsp;<input type="checkbox" id="checkAll" name="checkAll" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="seq" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="user.account" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="user.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="login.password" /></th>		      
		      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="group.name" /></th>		       
		      <th scope="col" nowrap="nowrap"><spring:message code="login.mode" /></th>
		      <th scope="col" nowrap="nowrap"><spring:message code="user.right.isadmin" /></th>
		      <th scope="col" nowrap="nowrap">CREATE_TIME</th>
		      <th scope="col" nowrap="nowrap">CREATE_BY</th>
		      <th scope="col" nowrap="nowrap">UPDATE_TIME</th>
		      <th scope="col" nowrap="nowrap">UPDATE_BY</th>
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
      
        <h5 class="modal-title" id="exampleModalLabel"><span id="msgModal_title">新增/維護使用者授權</span></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        
      </div>
      <div class="modal-body">
        <form role="form" id="formEdit" name="formEdit">
        	<div class="card card-body">
        	  <div class="col-12">
	           	<div class="form-group row">
	            	<label for="addAccount" class="col-md-2 col-sm-3 col-form-label"><spring:message code="user.account" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	            		<input type="text" class="form-control form-control-sm checkRequired" id="addAccount" name="addAccount" placeholder="user account" >
	            	</div>
	            </div>
	            <div class="form-group row">
	            	<label for="addUserName" class="col-md-2 col-sm-3 col-form-label"><spring:message code="user.name" /></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="text" class="form-control form-control-sm" id="addUserName" name="addUserName" placeholder="使用者" >
	                </div>
	            </div>
	            <div class="form-group row">
	            	<label for="addPassword" class="col-md-2 col-sm-3 col-form-label"><spring:message code="login.password" /><span class="pull-right" style="color: red;">＊ </span></label>
	            	<div class="col-md-10 col-sm-9">
	                	<input type="password" class="form-control form-control-sm" id="addPassword" name="addPassword" placeholder="password" >
	                </div>
	            </div>	            
	            <div class="form-group row">
                	<label for="addUserGroup" class="col-md-2 col-sm-3 col-form-label"><spring:message code="group.name" /><spring:message code="type" /><span class="pull-right" style="color: red;">＊ </span></label>
                  	<div class="col-md-10 col-sm-9">
                  		<form:select path="userGroupList" id="addUserGroup" class="form-control form-control-sm" style="min-width: 120px">
			               <form:options items="${userGroupList}" />
			            </form:select> 
                  	</div>
                </div>
	            <div class="form-group row">
	            	<label for="addIsAdmin" class="col-md-2 col-sm-3 col-form-label"><spring:message code="user.right.isadmin" /></label>
	            	<div class="col-md-10 col-sm-9">
					    <select id="addIsAdmin" name="addIsAdmin" class="form-control form-control-sm" style="min-width: 40px" >
							<option value="Y" label="Y" />
			                 <option value="N" label="N" />                   
					    </select>
	                </div>
	            </div>
	            <div class="form-group row">
                	<label for="addLoginMode" class="col-md-2 col-sm-3 col-form-label"><spring:message code="login.mode" /><span class="pull-right" style="color: red;">＊ </span></label>
                  	<div class="col-md-10 col-sm-9">
                  		<form:select path="loginModeList" id="addLoginMode" class="form-control form-control-sm" style="min-width: 40px">
			               <form:options items="${loginModeList}" />
			            </form:select> 
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

<script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.admin.user.right.min.js"></script>