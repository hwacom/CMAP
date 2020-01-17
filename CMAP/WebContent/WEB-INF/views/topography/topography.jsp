<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../../common/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Title</title>
  
  <style>
    body{
      overflow: hidden;
    }
    #togo{
      width: 2000px;
      height:1500px;
      border:1px solid #ccc;
      user-select: none;
    }
    #togo text{
      font-size:10px;/*和js里保持一致*/
      fill:#1A2C3F;
      text-anchor: middle;
    }
    #togo .node-other{
 
      text-anchor: start;
    }
    #togo .health1{
      stroke:#92E1A2;
    }
    #togo .health2{
      stroke:orange;
    }
    #togo .health3{
      stroke:red;
    }
    #togo .link{
      stroke:#E4E8ED;
    }
    #togo .node-title{
      font-size: 14px;
    }
    #togo .node-code circle{
      fill:#3F86F5;
    }
    #togo .node-code text{
      fill:#fff;
    }
    #togo .node-bg{
      fill:#fff;
    }
    #togo .arrow{
      fill:#E4E8ED;
    }
  </style>
  <script src="data.js"></script>
</head>
<body>
 <svg id="togo" width="1000" height="600">
 
 </svg>
 <script src="${pageContext.request.contextPath}/resources/js/custom/min/cmap.topography.min.js"></script>
 <script>

 let __options={
		  data:[{
		   type:'app',
		   name: 'monitor-web-server',
		   time: 30,
		   rpm: 40,
		   epm: 50,
		   active: 3,
		   total: 5,
		   code: 'java',
		   health: 1,
		   lineProtocol: 'http',
		   lineTime: 12,
		   lineRpm: 34,
		  }, {
		   type:'database',
		   name: 'Mysql',
		   time: 30,
		   rpm: 40,
		   epm: 50,
		   active: 3,
		   total: 5,
		   code: 'java',
		   health: 2,
		   lineProtocol: 'http',
		   lineTime: 12,
		   lineRpm: 34,
		  
		  },
		   {
		    type:'app',
		    name: 'Redis',
		    time: 30,
		    rpm: 40,
		    epm: 50,
		    active: 3,
		    total: 5,
		    code: 'java',
		    health: 3,
		    lineProtocol: 'http',
		    lineTime: 12,
		    lineRpm: 34,
		  
		   }, {
		    type:'cloud',
		    name: 'ES',
		    time: 30,
		    rpm: 40,
		    epm: 50,
		    active: 3,
		    total: 5,
		    code: 'java',
		    health: 1,
		    lineProtocol: 'http',
		    lineTime: 12,
		    lineRpm: 34,
		    value: 100
		   }
		  ],
		  edges: [
		    {
		    source: 3,
		    target: 0,
		   }, {
		    source: 1,
		    target: 2,
		   }
		   , {
		    source: 1,
		    target: 3,
		   },
		   {
		    source: 0,
		    target: 1,
		   },
		   {
		    source: 0,
		    target: 2,
		   }
		   // {
		   //  source: 3,
		   //  target: 2,
		   // },
		  ]
		 }
 
  let t=new Togo('#togo',__options);
  t.render();
  
 </script>
</body>
</html>
<!-- <script type="text/javascript" src="http://d3js.org/d3.v5.min.js">
</script> -->
  