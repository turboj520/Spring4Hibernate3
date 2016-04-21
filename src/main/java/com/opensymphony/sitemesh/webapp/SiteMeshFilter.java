/**============================================================
 * 版权：Autohome 版权所有 (c) 2014 - 2014
 * 文件：com.opensymphony.sitemesh.webapp.SiteMeshFilter.java
 * 所含类:SiteMeshFilter.java
 * 修改记录：
 * 日期                           作者                            内容
 * =============================================================
 * 2014年5月29日 上午9:18:16    姜强强       创建文件，实现基本功能
 * ============================================================*/
package com.opensymphony.sitemesh.webapp;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.compatability.DecoratorMapper2DecoratorSelector;
import com.opensymphony.sitemesh.compatability.PageParser2ContentProcessor;

/**
 * <p> SiteMeshFilter</p>
 * 
 * <p> 说明  </p>
 * 
 * <p> Copyright: 版权所有 (c) 2014 - 2014 </p>
 * <p> Company:Autohome </p>
 * 
 * @author Administrator
 * @version 1.0
 */
public class SiteMeshFilter implements Filter
{
	private FilterConfig filterConfig;
	private ContainerTweaks containerTweaks;
	private static final String ALREADY_APPLIED_KEY = "com.opensymphony.sitemesh.APPLIED_ONCE";

	public void init(FilterConfig filterConfig)
	{
		this.filterConfig = filterConfig;
		this.containerTweaks = new ContainerTweaks();
	}

	public void destroy()
	{
		this.filterConfig = null;
		this.containerTweaks = null;
	}

	public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) rq;
		HttpServletResponse response = (HttpServletResponse) rs;
		ServletContext servletContext = this.filterConfig.getServletContext();

		SiteMeshWebAppContext webAppContext = new SiteMeshWebAppContext(request, response, servletContext);

		ContentProcessor contentProcessor = initContentProcessor(webAppContext);
		DecoratorSelector decoratorSelector = initDecoratorSelector(webAppContext);

		if (filterAlreadyAppliedForRequest(request))
		{
			chain.doFilter(request, response);
			return;
		}

		if (!(contentProcessor.handles(webAppContext)))
		{
			chain.doFilter(request, response);
			return;
		}

		if (this.containerTweaks.shouldAutoCreateSession())
		{
			request.getSession(true);
		}

		try
		{
			Content content = obtainContent(contentProcessor, webAppContext, request, response, chain);

			if (content == null)
			{
				return;
			}

			Decorator decorator = decoratorSelector.selectDecorator(content, webAppContext);
			decorator.render(content, webAppContext);
		} catch (IllegalStateException e)
		{
			if (!(this.containerTweaks.shouldIgnoreIllegalStateExceptionOnErrorPage()))
				throw e;
		} catch (RuntimeException e)
		{
			if (this.containerTweaks.shouldLogUnhandledExceptions())
			{
				servletContext.log("Unhandled exception occurred whilst decorating page", e);
			}
			throw e;
		} catch (ServletException e)
		{
			request.setAttribute("com.opensymphony.sitemesh.APPLIED_ONCE", null);
			throw e;
		}
	}

	protected ContentProcessor initContentProcessor(SiteMeshWebAppContext webAppContext)
	{
		Factory factory = Factory.getInstance(new Config(this.filterConfig));
		factory.refresh();
		return new PageParser2ContentProcessor(factory);
	}

	protected DecoratorSelector initDecoratorSelector(SiteMeshWebAppContext webAppContext)
	{
		Factory factory = Factory.getInstance(new Config(this.filterConfig));
		factory.refresh();
		return new DecoratorMapper2DecoratorSelector(factory.getDecoratorMapper());
	}

	private Content obtainContent(ContentProcessor contentProcessor, SiteMeshWebAppContext webAppContext, HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		ContentBufferingResponse contentBufferingResponse = new ContentBufferingResponse(response, contentProcessor, webAppContext);
		chain.doFilter(request, contentBufferingResponse);

		webAppContext.setUsingStream(contentBufferingResponse.isUsingStream());
		return contentBufferingResponse.getContent();
	}

	private boolean filterAlreadyAppliedForRequest(HttpServletRequest request)
	{
		if (request.getAttribute("com.opensymphony.sitemesh.APPLIED_ONCE") == Boolean.TRUE)
		{
			return true;
		}
		request.setAttribute("com.opensymphony.sitemesh.APPLIED_ONCE", Boolean.TRUE);
		return false;
	}
}