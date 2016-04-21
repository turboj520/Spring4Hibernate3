<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="/WEB-INF/views/include/head.jsp" />
<title><sitemesh:title default="SITEMESH 默认装饰器" /></title>
<sitemesh:head />
</head>
<body class="page-header-fixed">
    <jsp:include page="/WEB-INF/views/include/navbar.jsp" />
    <jsp:include page="/WEB-INF/views/include/sidebar.jsp" />
    <sitemesh:body />
    <jsp:include page="/WEB-INF/views/include/footer.jsp" />
</body>
</html>