<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<div id="mainContainer">
    
</div>
<!-- END CONTAINER -->
<script type="text/javascript">
<!--

//-->
$(document).ready(function(){
	console.log("index.jsp | ready");
});
$("#mainContainer").load("/Spring4Hibernate3/a/mainContainer.do");
</script>
