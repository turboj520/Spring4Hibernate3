<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!-- BEGIN CONTAINER -->
<div class="page-container row-fluid">
    <!-- BEGIN HORIZONTAL MENU PAGE SIDEBAR1 -->
    <div class="page-sidebar nav-collapse collapse">
        <ul class="page-sidebar-menu hidden-phone hidden-tablet">
            <li>
                <!-- BEGIN SIDEBAR TOGGLER BUTTON -->
                <div class="sidebar-toggler hidden-phone"></div>
                <!-- BEGIN SIDEBAR TOGGLER BUTTON -->
            </li>
            <li>
                <!-- BEGIN RESPONSIVE QUICK SEARCH FORM -->
                <form class="sidebar-search">
                    <div class="input-box">
                        <a href="javascript:;" class="remove"></a>
                        <input type="text" placeholder="Search..." />
                        <input type="button" class="submit" value=" " />
                    </div>
                </form>
                <!-- END RESPONSIVE QUICK SEARCH FORM -->
            </li>
            <li class="root start open">
                <a href="javascript:;">
                    <i class="icon-cogs"></i>
                    <span class="title">权限管理</span>
                    <span class="selected"></span>
                    <span class="arrow open"></span>
                </a>
                <ul class="sub-menu" style="display: block;">
                    <li>
                        <a href="javascript:;">用户管理</a>
                    </li>
                    <li>
                        <a href="javascript:;">角色管理</a>
                    </li>
                    <li>
                        <a href="javascript:;">
                            模块管理
                            <span class="arrow "></span>
                        </a>
                        <ul class="sub-menu">
                            <li>
                                <a href="javascript:;">模块1配置</a>
                            </li>
                            <li>
                                <a href="javascript:;">模块2配置</a>
                            </li>
                            <li>
                                <a href="javascript:;">
                                    模块5配置
                                    <span class="arrow "></span>
                                </a>
                                <ul class="sub-menu">
                                    <li>
                                        <a href="javascript:;"> 模块 5 - 1 </a>
                                    </li>
                                    <li>
                                        <a href="javascript:;"> 模块 5 - 2 </a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="layout_horizontal_menu2.html"> Horzontal Menu 2 </a>
                    </li>
                    <li>
                        <a href="javascript:;"> Promo Page </a>
                    </li>
                    <li>
                        <a href="javascript:;"> Email Templates </a>
                    </li>
                    <li>
                        <a href="javascript:;"> Content Loading via Ajax</a>
                    </li>
                    <li>
                        <a href="layout_sidebar_closed.html"> Sidebar Closed Page </a>
                    </li>
                    <li>
                        <a href="layout_blank_page.html"> Blank Page </a>
                    </li>
                    <li>
                        <a href="layout_boxed_page.html">Boxed Page</a>
                    </li>
                    <li>
                        <a href="layout_boxed_not_responsive.html"> Non-Responsive Boxed Layout </a>
                    </li>
                </ul>
            </li>
            <li class="root">
                <a href="javascript:;">
                    <i class="icon-th-list"></i>
                    <span class="title">运营后台</span>
                    <span class="selected"></span>
                    <span class="arrow"></span>
                </a>
                <ul class="sub-menu">
                    <li>
                        <a href="javascript:;">
                            贴膜管理
                            <span class="arrow "></span>
                        </a>
                        <ul class="sub-menu">
                            <li>
                                <a href="javascript:;"> 前档贴膜 </a>
                            </li>
                            <li>
                                <a href="javascript:;"> 侧挡贴膜 </a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="javascript:;"> Draggable Portlets </a>
                    </li>
                </ul>
            </li>
            <li class="root">
                <a href="javascript:;">
                    <i class="icon-map-marker"></i>
                    <span class="title">Maps</span>
                    <span class="selected"></span>
                    <span class="arrow "></span>
                </a>
                <ul class="sub-menu">
                    <li>
                        <a href="maps_google.html"> Google Maps </a>
                    </li>
                    <li>
                        <a href="maps_vector.html"> Vector Maps </a>
                    </li>
                </ul>
            </li>
            <li class="root">
                <a href="javascript:;">
                    <i class="icon-bar-chart"></i>
                    <span class="title">Visual Charts</span>
                    <span class="selected"></span>
                </a>
            </li>
            <li class="last">
                <a href="login.html">
                    <i class="icon-user"></i>
                    <span class="title">Login Page</span>
                    <span class="selected"></span>
                </a>
            </li>
        </ul>
        <!--HORIZONTAL AND SIDEBAR MENU FOR MOBILE & TABLETS-->
        <ul class="page-sidebar-menu visible-phone visible-tablet">
            <li>
                <!-- BEGIN RESPONSIVE QUICK SEARCH FORM -->
                <form class="sidebar-search">
                    <div class="input-box">
                        <a href="javascript:;" class="remove"></a>
                        <input type="text" placeholder="Search..." />
                        <input type="button" class="submit" value=" " />
                    </div>
                </form>
                <!-- END RESPONSIVE QUICK SEARCH FORM -->
            </li>
            <li class="active">
                <a href="index.html">
                    Dashboard
                    <span class="selected"></span>
                    <span class="arrow open"></span>
                </a>
                <ul class="sub-menu">
                    <li class="active">
                        <a href="javascript:;">
                            <i class="icon-cogs"></i>
                            <span class="title">Layouts</span>
                            <span class="arrow open"></span>
                        </a>
                        <ul class="sub-menu">
                            <li class="active">
                                <a href="layout_horizontal_sidebar_menu.html"> Horzontal & Sidebar Menu </a>
                            </li>
                            <li>
                                <a href="layout_horizontal_menu1.html"> Horzontal Menu 1 </a>
                            </li>
                            <li>
                                <a href="layout_horizontal_menu2.html"> Horzontal Menu 2 </a>
                            </li>
                            <li>
                                <a href="layout_promo.html"> Promo Page </a>
                            </li>
                            <li>
                                <a href="layout_email.html"> Email Templates </a>
                            </li>
                            <li>
                                <a href="layout_sidebar_closed.html"> Sidebar Closed Page </a>
                            </li>
                            <li>
                                <a href="layout_blank_page.html"> Blank Page </a>
                            </li>
                            <li>
                                <a href="layout_boxed_page.html">Boxed Page</a>
                            </li>
                            <li>
                                <a href="layout_boxed_not_responsive.html"> Non-Responsive Boxed Layout </a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="javascript:;">
                            <i class="icon-th-list"></i>
                            <span class="title"> 运营管理 </span>
                            <span class="arrow "></span>
                        </a>
                        <ul class="sub-menu">
                            <li>
                                <a href="javascript:;"> 贴膜管理 </a>
                            </li>
                            <li>
                                <a href="javascript:;"> 保养管理 </a>
                            </li>
                            <li>
                                <a href="javascript:;"> 封釉管理 </a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="javascript:;">
                            <i class="icon-map-marker"></i>
                            <span class="title">Maps</span>
                            <span class="arrow "></span>
                        </a>
                        <ul class="sub-menu">
                            <li>
                                <a href="maps_google.html"> Google Maps </a>
                            </li>
                            <li>
                                <a href="maps_vector.html"> Vector Maps </a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="charts.html">
                            <i class="icon-bar-chart"></i>
                            <span class="title">Visual Charts</span>
                        </a>
                    </li>
                    <li>
                        <a href="login.html">
                            <i class="icon-user"></i>
                            <span class="title">Login Page</span>
                        </a>
                    </li>
                </ul>
            </li>
            <li>
                <a href="javascript:;">
                    Sections
                    <span class="arrow"></span>
                </a>
                <ul class="sub-menu">
                    <li>
                        <a href="javascript:;">Section 1</a>
                    </li>
                    <li>
                        <a href="javascript:;">Section 2</a>
                    </li>
                    <li>
                        <a href="javascript:;">Section 3</a>
                    </li>
                    <li>
                        <a href="javascript:;">Section 4</a>
                    </li>
                    <li>
                        <a href="javascript:;">Section 5</a>
                    </li>
                </ul>
            </li>
            <li>
                <a href="">Tables</a>
            </li>
            <li>
                <a href="">
                    Extra
                    <span class="arrow"></span>
                </a>
                <ul class="sub-menu">
                    <li>
                        <a href="index.html">About Us</a>
                    </li>
                    <li>
                        <a href="index.html">Services</a>
                    </li>
                    <li>
                        <a href="index.html">Pricing</a>
                    </li>
                    <li>
                        <a href="index.html">FAQs</a>
                    </li>
                    <li>
                        <a href="index.html">Gallery</a>
                    </li>
                    <li>
                        <a href="index.html">Registration</a>
                    </li>
                </ul>
            </li>
        </ul>
    </div>