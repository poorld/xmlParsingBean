
public class Student {
	private Integer id;
	private String name;
	private Float height;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Float getHeight() {
		return height;
	}
	public void setHeight(Float height) {
		this.height = height;
	}
	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", height=" + height + "]";
	}
	
	
	
	
}
