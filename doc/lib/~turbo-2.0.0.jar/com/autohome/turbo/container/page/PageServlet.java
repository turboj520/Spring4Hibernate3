// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   PageServlet.java

package com.autohome.turbo.container.page;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.StringUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

// Referenced classes of package com.autohome.turbo.container.page:
//			PageHandler, Menu, MenuComparator, Page

public class PageServlet extends HttpServlet
{

	private static final long serialVersionUID = 0x8bd6acf13483f78bL;
	protected static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/container/page/PageServlet);
	protected final Random random = new Random();
	protected final Map pages = new ConcurrentHashMap();
	protected final List menus = new ArrayList();
	private static PageServlet INSTANCE;

	public PageServlet()
	{
	}

	public static PageServlet getInstance()
	{
		return INSTANCE;
	}

	public List getMenus()
	{
		return Collections.unmodifiableList(menus);
	}

	public void init()
		throws ServletException
	{
		super.init();
		INSTANCE = this;
		String config = getServletConfig().getInitParameter("pages");
		Collection names;
		if (config != null && config.length() > 0)
			names = Arrays.asList(Constants.COMMA_SPLIT_PATTERN.split(config));
		else
			names = ExtensionLoader.getExtensionLoader(com/autohome/turbo/container/page/PageHandler).getSupportedExtensions();
		Iterator i$ = names.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			String name = (String)i$.next();
			PageHandler handler = (PageHandler)ExtensionLoader.getExtensionLoader(com/autohome/turbo/container/page/PageHandler).getExtension(name);
			pages.put(ExtensionLoader.getExtensionLoader(com/autohome/turbo/container/page/PageHandler).getExtensionName(handler), handler);
			Menu menu = (Menu)handler.getClass().getAnnotation(com/autohome/turbo/container/page/Menu);
			if (menu != null)
				menus.add(handler);
		} while (true);
		Collections.sort(menus, new MenuComparator());
	}

	protected final void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		doPost(request, response);
	}

	protected final void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		if (!response.isCommitted())
		{
			PrintWriter writer = response.getWriter();
			String uri = request.getRequestURI();
			boolean isHtml = false;
			if (uri == null || uri.length() == 0 || "/".equals(uri))
			{
				uri = "index";
				isHtml = true;
			} else
			{
				if (uri.startsWith("/"))
					uri = uri.substring(1);
				if (uri.endsWith(".html"))
				{
					uri = uri.substring(0, uri.length() - ".html".length());
					isHtml = true;
				}
			}
			if (uri.endsWith("favicon.ico"))
			{
				response.sendError(404);
				return;
			}
			ExtensionLoader pageHandlerLoader = ExtensionLoader.getExtensionLoader(com/autohome/turbo/container/page/PageHandler);
			PageHandler pageHandler = pageHandlerLoader.hasExtension(uri) ? (PageHandler)pageHandlerLoader.getExtension(uri) : null;
			if (isHtml)
			{
				writer.println("<html><head><title>Dubbo</title>");
				writer.println("<style type=\"text/css\">html, body {margin: 10;padding: 0;background-color: #6D838C;font-family: Arial, Verdana;font-size: 12px;color: #FFFFFF;text-align: center;vertical-align: middle;word-break: break-all; } table {width: 90%; margin: 0px auto;border-collapse: collapse;border: 8px solid #FFFFFF; } thead tr {background-color: #253c46; } tbody tr {background-color: #8da5af; } th {padding-top: 4px;padding-bottom: 4px;font-size: 14px;height: 20px; } td {margin: 3px;padding: 3px;border: 2px solid #FFFFFF;font-size: 14px;height: 25px; } a {color: #FFFFFF;cursor: pointer;text-decoration: underline; } a:hover {text-decoration: none; }</style>");
				writer.println("</head><body>");
			}
			if (pageHandler != null)
			{
				Page page = null;
				try
				{
					String query = request.getQueryString();
					page = pageHandler.handle(URL.valueOf((new StringBuilder()).append(request.getRequestURL().toString()).append(query != null && query.length() != 0 ? (new StringBuilder()).append("?").append(query).toString() : "").toString()));
				}
				catch (Throwable t)
				{
					logger.warn(t.getMessage(), t);
					String msg = t.getMessage();
					if (msg == null)
						msg = StringUtils.toString(t);
					if (isHtml)
					{
						writer.println("<table>");
						writer.println("<thead>");
						writer.println("    <tr>");
						writer.println("        <th>Error</th>");
						writer.println("    </tr>");
						writer.println("</thead>");
						writer.println("<tbody>");
						writer.println("    <tr>");
						writer.println("        <td>");
						writer.println((new StringBuilder()).append("            ").append(msg.replace("<", "&lt;").replace(">", "&lt;").replace("\n", "<br/>")).toString());
						writer.println("        </td>");
						writer.println("    </tr>");
						writer.println("</tbody>");
						writer.println("</table>");
						writer.println("<br/>");
					} else
					{
						writer.println(msg);
					}
				}
				if (page != null)
					if (isHtml)
					{
						String nav = page.getNavigation();
						if (nav == null || nav.length() == 0)
						{
							nav = ExtensionLoader.getExtensionLoader(com/autohome/turbo/container/page/PageHandler).getExtensionName(pageHandler);
							nav = (new StringBuilder()).append(nav.substring(0, 1).toUpperCase()).append(nav.substring(1)).toString();
						}
						if (!"index".equals(uri))
							nav = (new StringBuilder()).append("<a href=\"/\">Home</a> &gt; ").append(nav).toString();
						writeMenu(request, writer, nav);
						writeTable(writer, page.getTitle(), page.getColumns(), page.getRows());
					} else
					if (page.getRows().size() > 0 && ((List)page.getRows().get(0)).size() > 0)
						writer.println((String)((List)page.getRows().get(0)).get(0));
			} else
			if (isHtml)
			{
				writer.println("<table>");
				writer.println("<thead>");
				writer.println("    <tr>");
				writer.println("        <th>Error</th>");
				writer.println("    </tr>");
				writer.println("</thead>");
				writer.println("<tbody>");
				writer.println("    <tr>");
				writer.println("        <td>");
				writer.println((new StringBuilder()).append("            Not found ").append(uri).append(" page. Please goto <a href=\"/\">Home</a> page.").toString());
				writer.println("        </td>");
				writer.println("    </tr>");
				writer.println("</tbody>");
				writer.println("</table>");
				writer.println("<br/>");
			} else
			{
				writer.println((new StringBuilder()).append("Not found ").append(uri).append(" page.").toString());
			}
			if (isHtml)
				writer.println("</body></html>");
			writer.flush();
		}
	}

	protected final void writeMenu(HttpServletRequest request, PrintWriter writer, String nav)
	{
		writer.println("<table>");
		writer.println("<thead>");
		writer.println("    <tr>");
		String uri;
		Menu menu;
		for (Iterator i$ = menus.iterator(); i$.hasNext(); writer.println((new StringBuilder()).append("        <th><a href=\"").append(uri).append(".html\">").append(menu.name()).append("</a></th>").toString()))
		{
			PageHandler handler = (PageHandler)i$.next();
			uri = ExtensionLoader.getExtensionLoader(com/autohome/turbo/container/page/PageHandler).getExtensionName(handler);
			menu = (Menu)handler.getClass().getAnnotation(com/autohome/turbo/container/page/Menu);
		}

		writer.println("    </tr>");
		writer.println("</thead>");
		writer.println("<tbody>");
		writer.println("    <tr>");
		writer.println((new StringBuilder()).append("        <td style=\"text-align: left\" colspan=\"").append(menus.size()).append("\">").toString());
		writer.println(nav);
		writer.println("        </td>");
		writer.println("    </tr>");
		writer.println("</tbody>");
		writer.println("</table>");
		writer.println("<br/>");
	}

	protected final void writeTable(PrintWriter writer, String title, List columns, List rows)
	{
		int n = random.nextInt();
		int c = columns != null ? columns.size() : rows != null && rows.size() != 0 ? ((List)rows.get(0)).size() : 0;
		int r = rows != null ? rows.size() : 0;
		writer.println("<table>");
		writer.println("<thead>");
		writer.println("    <tr>");
		writer.println((new StringBuilder()).append("        <th colspan=\"").append(c).append("\">").append(title).append("</th>").toString());
		writer.println("    </tr>");
		if (columns != null && columns.size() > 0)
		{
			writer.println("    <tr>");
			for (int i = 0; i < columns.size(); i++)
			{
				String col = (String)columns.get(i);
				if (col.endsWith(":"))
					col = (new StringBuilder()).append(col).append(" <input type=\"text\" id=\"in_").append(n).append("_").append(i).append("\" onkeyup=\"for (var i = 0; i < ").append(r).append("; i ++) { var m = true; for (var j = 0; j < ").append(columns.size()).append("; j ++) { if (document.getElementById('in_").append(n).append("_' + j)) { var iv = document.getElementById('in_").append(n).append("_' + j).value; var tv = document.getElementById('td_").append(n).append("_' + i + '_' + j).innerHTML; if (iv.length > 0 && (tv.length < iv.length || tv.indexOf(iv) == -1)) { m = false; break; } } } document.getElementById('tr_").append(n).append("_' + i).style.display = (m ? '' : 'none');}\" sytle=\"width: 100%\" />").toString();
				writer.println((new StringBuilder()).append("        <td>").append(col).append("</td>").toString());
			}

			writer.println("    </tr>");
		}
		writer.println("</thead>");
		if (rows != null && rows.size() > 0)
		{
			writer.println("<tbody>");
			int i = 0;
			for (Iterator i$ = rows.iterator(); i$.hasNext();)
			{
				Collection row = (Collection)i$.next();
				writer.println((new StringBuilder()).append("    <tr id=\"tr_").append(n).append("_").append(i).append("\">").toString());
				int j = 0;
				for (Iterator i$ = row.iterator(); i$.hasNext();)
				{
					String col = (String)i$.next();
					writer.println((new StringBuilder()).append("        <td id=\"td_").append(n).append("_").append(i).append("_").append(j).append("\" style=\"display: ;\">").append(col).append("</td>").toString());
					j++;
				}

				writer.println("    </tr>");
				i++;
			}

			writer.println("</tbody>");
		}
		writer.println("</table>");
		writer.println("<br/>");
	}

}
