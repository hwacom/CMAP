<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<section>

  <div id="content" class="container-fluid">
  	<img src="${pageContext.request.contextPath}/resources/images/working.png" />
  </div>
  
  <input type="hidden" value="${isAll}" id="isAll"/>
  
</section>
<script src="${pageContext.request.contextPath}/resources/js/custom/min/plugin/module/cmap.module.net.flow.current.ranking.min.js"></script>
