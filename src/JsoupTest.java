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
		String path = JsoupTest.class.getResource("beans.xml").getFile();
		Map<String, Object> beans = getBean(path);
		// ������ж���
		System.out.println(beans);
		//��ȡStudent
		Student stu = (Student) beans.get("student");
		System.out.println(stu);
	}

	/**
	 * ����xml
	 * 
	 * @param path
	 * @return
	 */
	public static Map<String, Object> getBean(String path) {
		Map<String, Object> map = new HashMap<String, Object>();
		File file = new File(path);
		Element bean = null;
		try {
			Document dom = Jsoup.parse(file, "utf-8");
			// ��ȡ<beans>�ڵ�
			Elements beansElements = dom.getElementsByTag("beans");
			// bean�ڵ��Ƿ�Ϊ�գ�Ϊ����return null
			if (beansElements.isEmpty())
				return null;

			// ��ȡ��һ��<beans>
			Element beansElement = beansElements.get(0);
			// ��ȡbean�ڵ�
			Elements beanElements = beansElement.getElementsByTag("bean");
			if (beanElements.isEmpty())
				return null;

			int beanElementsSize = beanElements.size();

			for (int i = 0; i < beanElementsSize; i++) {
				// ʹ��map��װ���������ֵ
				Map<String, String> props = null;
				bean = beanElements.get(i);
				// �Ƿ���id����
				if (!bean.hasAttr("id")) {
					System.out.println("��" + (i + 1) + "��<bean>�ڵ�û��id����,����");
					continue;
				}

				// �Ƿ���class����
				if (!bean.hasAttr("class")) {
					System.out.println("��" + (i + 1) + "��<bean>�ڵ�û��class����,����");
					continue;
				}

				// ��ȡidֵ
				String id = bean.attr("id");
				// ��ȡclassֵ
				String className = bean.attr("class");

				// �Ƿ���property�ڵ�,����ʹ����ʡ������ֵ
				Elements propertyElements = bean.getElementsByTag("property");
				if (!propertyElements.isEmpty()) {
					props = analysisProperty(propertyElements);
				}
				
				Class clazz = Class.forName(className);
				Object obj = clazz.newInstance();

				map.put(id, obj);

				// ������ֵ��û��property�ڵ�������
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
						// ʹ����ʡapi����bean����
						// PropertyDescriptor pd = new PropertyDescriptor("name", obj.getClass());
						PropertyDescriptor pd = new PropertyDescriptor(name, obj.getClass());
						// ��ȡset����
						Method writeMethod = pd.getWriteMethod();
						writeMethod.setAccessible(true);
						// ��ֵ
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
			System.out.println("�޷��ҵ�"+e.getMessage()+"�����");
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
	 * ����ת��
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
	 * ����<property>�ڵ��ȡ����
	 * 
	 * @param elements
	 * @return
	 */
	public static Map<String, String> analysisProperty(Elements elements) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < elements.size(); i++) {
			Element propertyElement = elements.get(i);
			// �Ƿ���name
			if (propertyElement.hasAttr("name")) {
				// �Ƿ���value
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
