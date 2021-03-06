<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglib.jsp" %>
<!doctype html>
<html>
  <head>
    <meta charset="utf-8">
    <!-- <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"> -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- Hwacom -->
    <link rel="icon" sizes="16x16" href="${pageContext.request.contextPath}/resources/images/hwacom_icon.ico">
    
    <!-- 台灣大哥大 -->
    <!-- 
    <link rel="icon" sizes="16x16" href="${pageContext.request.contextPath}/resources/images/icon_taiwan_mobile.ico">
	 -->
	  
	<!-- 桃機 -->
	<!-- 
	<link rel="icon" sizes="16x16" href="${pageContext.request.contextPath}/resources/images/icon_t3.ico">
	 -->
	 
	<!-- 亞太 -->
	<!-- 
	<link rel="icon" sizes="16x16" href="${pageContext.request.contextPath}/resources/images/icon_apt.ico">
	 -->
	 
    <title><spring:message code="cmap.title" /></title>

    <!-- Bootstrap core CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
	<link href="${pageContext.request.contextPath}/resources/css/signin.css" rel="stylesheet">
	
	<script src="${pageContext.request.contextPath}/resources/js/jquery/jquery-3.5.1.min.js"></script>
	
  <body class="text-center">
  	<!-- Container fluid  -->
    <!-- ============================================================== -->
    <div class="container" style="margin-bottom: 50px">
    	<!-- ============================================================== -->
        <!-- Start Page Content -->
        <!-- ============================================================== -->
        <div class="row">
            <div class="col-12">
	  			<div class="row">
			  		<div class="col-md-6 col-sm-12 offset-md-3 m-t-5 login-title">
			  			<!-- Hwacom -->
			  			<img class="img" src="${pageContext.request.contextPath}/resources/images/hwacom.png" width="auto" height="40" style="padding-top: 3px" />
			  			
			  			<!-- Innolux 群創 -->
			  			<!-- 
			  			<img class="img" src="${pageContext.request.contextPath}/resources/images/innolux_logo.png" width="auto" height="30" style="padding-top: 3px" />
			  			-->
			  			
			  			<!-- 亞太 -->
			  			<!-- 
			  			<img class="img" src="${pageContext.request.contextPath}/resources/images/aptg_logo_icon.png" width="auto" height="30" style="padding-top: 3px" />
      					<img class="img web-only" src="${pageContext.request.contextPath}/resources/images/aptg_logo_word.png" width="auto" height="23" style="padding-top: 3px" />
			  			 -->
			  			 
			  			<!-- 桃機 -->
			  			<!-- 
			  			<img class="img" src="${pageContext.request.contextPath}/resources/images/logo_new_icon.png" width="auto" height="40" style="padding-top: 3px" />
      					<img class="img web-only" src="${pageContext.request.contextPath}/resources/images/logo_new_word_short.png" width="auto" height="40" style="padding-top: 3px" />
			  			-->
			  			
			  			<!-- 台灣大哥大 -->
			  			<!--
				      	<img class="img" src="${pageContext.request.contextPath}/resources/images/logo_taiwan_mobile_icon.png" width="auto" height="40" style="padding-top: 3px" />
				  		<img class="img web-only" src="${pageContext.request.contextPath}/resources/images/logo_taiwan_mobile_word.png" width="auto" height="30" style="padding-top: 3px" />
			  			-->
			  			
			  			<span class="h3" style="color: rgb(82, 82, 82); border-bottom: 1px; border-bottom-color: black; border-bottom-style: dashed; font-family: 游ゴシック;"><spring:message code="cmap.title" /></span>			
			  		</div>
			  	</div>
			  	<div class="row">
			  		<div class="col-md-6 col-sm-12 offset-md-3 m-t-5 login-form">
				  		<form class="form-signin" name="f" method='POST'>
				  			<!-- 2021-01-12 Alvin modified 支援LDAP登入未知帳號顯示客製化錯誤訊息 -->
				  		  <c:if test="${not empty LOGIN_EXCEPTION}">
		  					<div class="col-md col-sm center">
		  						<span class="red">
						        	<spring:message code="${LOGIN_EXCEPTION}" />
						      	</span>
		  					</div>
						  </c:if>
						  <c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION && empty LOGIN_EXCEPTION }">
						      <span class="red">
						        	<spring:message code="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
						      </span>
						  </c:if>
				  		  <div class="form-group row" style="vertical-align: middle;">
				  		  	<div class="col-3">
				  		  		<span class="h5" style="color: black"><spring:message code="login.account" />:</span>
				  		  	</div>
				  		  	<div class="col-9">
				  		  		<label for="inputAccount" class="sr-only"><spring:message code="login.account" /></label>
				  		  		<input type="text" name="username" id="inputAccount" class="form-control" placeholder="<spring:message code="login.account" />" required autofocus>
				  		  	</div>
				  		  </div>
					      <div class="form-group row">
					      	<div class="col-3">
					      		<span class="h5" style="color: black"><spring:message code="login.password" />:</span>
				  		  	</div>
				  		  	<div class="col-9">
				  		  		<label for="inputPassword" class="sr-only"><spring:message code="login.password" /></label>
				  		  		<input type="password" name="password" id="inputPassword" class="form-control" placeholder="<spring:message code="login.password" />" required>
				  		  	</div>
				  		  </div>
				  		   <input type="hidden" name="previousPage" value="<%=request.getSession().getAttribute("PREVIOUS_URL") %>">
					      <div class="row">
				  		  	<div class="col-sm-12">
				  		  		<button class="btn btn-block btn-success" type="submit"><spring:message code="login" /></button> <!-- Sign in -->
				  		  	</div>
				  		  </div>
					    </form>
					</div>
			  	</div>
	  		</div>
		</div>
		<div class="row">
            <div class="col-12">
            	<span class="font-weight-bold copyright" style="color: gray">Copyright &copy; <spring:message code="copyright" /></span>	
            </div>
        </div>
	</div>
  </body>
</html>
