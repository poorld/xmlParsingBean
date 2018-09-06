
public class Dog {
	private String name;
	private String color;
	private Double weight;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	@Override
	public String toString() {
		return "Dog [name=" + name + ", color=" + color + ", weight=" + weight + "]";
	}
	
	

	
	
}	
