<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=8,IE=9,IE=10" />
<meta http-equiv="Expires" content="0">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Cache-Control" content="no-store">

<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link href="${basePath }media/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/style-metro.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/style.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/style-responsive.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/default.css" rel="stylesheet" type="text/css" id="style_color" />
<link href="${basePath }media/css/uniform.default.css" rel="stylesheet" type="text/css" />
<!-- END GLOBAL MANDATORY STYLES -->

<!-- BEGIN PAGE LEVEL STYLES -->
<link href="${basePath }media/css/jquery.gritter.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/daterangepicker.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/fullcalendar.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/jqvmap.css" rel="stylesheet" type="text/css" media="screen" />
<link href="${basePath }media/css/jquery.easy-pie-chart.css" rel="stylesheet" type="text/css" media="screen" />
<!-- END PAGE LEVEL STYLES -->

<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link href="${basePath }media/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/style-metro.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/style.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/style-responsive.css" rel="stylesheet" type="text/css" />
<link href="${basePath }media/css/default.css" rel="stylesheet" type="text/css" id="style_color" />
<link href="${basePath }media/css/uniform.default.css" rel="stylesheet" type="text/css" />
<!-- END GLOBAL MANDATORY STYLES -->


<!-- END PAGE LEVEL STYLES -->
<link rel="shortcut icon" href="${basePath }media/image/favicon.ico" />


<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<!-- BEGIN CORE PLUGINS -->
<script src="${basePath }media/js/jquery-1.10.1.min.js" type="text/javascript"></script>
<script src="${basePath }media/js/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
<!-- IMPORTANT! Load jquery-ui-1.10.1.custom.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
<script src="${basePath }media/js/jquery-ui-1.10.1.custom.min.js" type="text/javascript"></script>
<script src="${basePath }media/js/bootstrap.min.js" type="text/javascript"></script>
<!--[if lt IE 9]>

<script src="media/js/excanvas.min.js"></script>

<script src="media/js/respond.min.js"></script>  

<![endif]-->
<script src="${basePath }media/js/jquery.slimscroll.min.js" type="text/javascript"></script>
<script src="${basePath }media/js/jquery.blockui.min.js" type="text/javascript"></script>
<script src="${basePath }media/js/jquery.cookie.min.js" type="text/javascript"></script>
<script src="${basePath }media/js/jquery.uniform.min.js" type="text/javascript"></script>
<!-- END CORE PLUGINS -->
<script src="${basePath }media/js/app.js"></script>
<script>
    jQuery(document).ready(function() {
    	console.log("App.init");
    	App.init();
    });
</script>

