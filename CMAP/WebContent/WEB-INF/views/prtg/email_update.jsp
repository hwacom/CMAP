<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
  
   <style>
  	 body {
  		 overflow: hidden;
  	 }
   </style>
   
   <iframe id="prtgFrame" class="scrollbar-macosx" width=100% style="height:calc(100vh - 55px)" frameborder="0" src="${IFRAME_URI }">
  	 Failed to open PRTG main page.
   </iframe>

   <!-- 
   <div id="uriFrame" style="width: 100%; height: 450px;" >&nbsp;</div>
    -->
   
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/prtg/cmap.prtg.common.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/prtg/cmap.prtg.emailUpdate.min.js"></script>