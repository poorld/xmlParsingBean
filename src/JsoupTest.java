import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class JsoupTest {

	public static void main(String[] args) {
		Student student = (Student) getBean("student");
		System.out.println(student);
		
		Dog dog = (Dog) getBean("dog");
		System.out.println(dog);
	}
	
	public static Object getBean(String id){
		Map<String, Object> beans = analysisXml();
		Object object = beans.get(id);
		if (object != null){
			return object;
		}
		return null;
	}

	/**
	 * 解析xml
	 * 
	 * @param path
	 * @return
	 */
	public static Map<String, Object> analysisXml() {
		String path = JsoupTest.class.getResource("beans.xml").getFile();
		Map<String, Object> map = new HashMap<String, Object>();
		File file = new File(path);
		Element bean = null;
		try {
			Document dom = Jsoup.parse(file, "utf-8");
			// 获取<beans>节点
			Elements beansElements = dom.getElementsByTag("beans");
			// bean节点是否为空，为空则return null
			if (beansElements.isEmpty())
				return null;

			// 获取第一个<beans>
			Element beansElement = beansElements.get(0);
			// 获取bean节点
			Elements beanElements = beansElement.getElementsByTag("bean");
			if (beanElements.isEmpty())
				return null;

			int beanElementsSize = beanElements.size();

			for (int i = 0; i < beanElementsSize; i++) {
				// 使用map来装对象的属性值
				Map<String, String> props = null;
				bean = beanElements.get(i);
				// 是否有id属性
				if (!bean.hasAttr("id")) {
					System.out.println("第" + (i + 1) + "个<bean>节点没有id属性,请检查");
					continue;
				}

				// 是否有class属性
				if (!bean.hasAttr("class")) {
					System.out.println("第" + (i + 1) + "个<bean>节点没有class属性,请检查");
					continue;
				}

				// 获取id值
				String id = bean.attr("id");
				// 获取class值
				String className = bean.attr("class");

				// 是否有property节点,有则使用内省给对象赋值
				Elements propertyElements = bean.getElementsByTag("property");
				if (!propertyElements.isEmpty()) {
					props = analysisProperty(propertyElements);
				}
				
				Class clazz = Class.forName(className);
				Object obj = clazz.newInstance();

				map.put(id, obj);

				// 给对象赋值，没有property节点则跳过
				if (props == null)
					continue;

				Field[] fields = clazz.getDeclaredFields();
				for (int k = 0; k < fields.length; k++) {
					String name = fields[k].getName();
					String value = props.get(name);
					if (value != null) {
						Type genericType = fields[k].getGenericType();
						String typeName = genericType.getTypeName();
						Object val = typeTransformation(typeName,value);
						// 使用内省api操作bean属性
						// PropertyDescriptor pd = new PropertyDescriptor("name", obj.getClass());
						PropertyDescriptor pd = new PropertyDescriptor(name, obj.getClass());
						// 获取set方法
						Method writeMethod = pd.getWriteMethod();
						writeMethod.setAccessible(true);
						// 赋值
						writeMethod.invoke(obj, val);
					}
				}
				map.put(id, obj);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println(bean);
			System.out.println("无法找到"+e.getMessage()+"这个类");
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 类型转换
	 * @param type
	 * @param value
	 * @return
	 */
	public static Object typeTransformation(String type, String value) {
		Object val = null;
		switch (type) {
		case "java.lang.Integer":
			val = Integer.parseInt(value);
			break;
		case "java.lang.Long":
			val = Long.parseLong(value);
			break;
		case "java.lang.Double":
			val = Double.parseDouble(value);	
			break;
		case "java.lang.Float":
			val = Float.parseFloat(value);	
			break;	
		case "java.lang.Boolean":
			val = Boolean.parseBoolean(value);	
			break;
		case "java.lang.String":
			val = value;
		default:
			break;
		}
		return val;
	}

	/**
	 * 解析<property>节点获取属性
	 * 
	 * @param elements
	 * @return
	 */
	public static Map<String, String> analysisProperty(Elements elements) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < elements.size(); i++) {
			Element propertyElement = elements.get(i);
			// 是否有name
			if (propertyElement.hasAttr("name")) {
				// 是否有value
				if (propertyElement.hasAttr("value")) {
					String name = propertyElement.attr("name");
					String value = propertyElement.attr("value");
					map.put(name, value);
				}
			}
		}
		return map;
	}

}
