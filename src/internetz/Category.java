package internetz;

import logger.PjiitOutputter;

public class Category {
	
	private CategoryType type;
	
	public Category(){
		say("Category initialized");
	}
	
	public Category(CategoryType type){
		say("Category initialized with type: " + type);
		this.type = type;
	}
	
	public Category(String name){
		say("Category initialized with name: " + name);
		this.type = parseType(name);
	}
	
	private static CategoryType parseType(String name){
		if (name.toLowerCase().equals("programming")){
			return CategoryType.PROGRAMMING;
		}
		if (name.toLowerCase().equals("data")){
			return CategoryType.DATA;
		}
		if (name.toLowerCase().equals("markup")){
			return CategoryType.MARKUP;
		}
		if (name.toLowerCase().equals("unknown/other")){
			return CategoryType.UNKNOWN;
		}
		return CategoryType.UNKNOWN;
	}

	public CategoryType getType() {
		return type;
	}

	public void setType(CategoryType type) {
		this.type = type;
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}
	
	@Override
	public String toString() {
		switch (type) {
			case PROGRAMMING: {
				return "programming";
			}
			case MARKUP: {
				return "markup";
			}
			case DATA: {
				return "data";
			}
			default:
				break;
		}
		return "other/unknown";
	}

}
