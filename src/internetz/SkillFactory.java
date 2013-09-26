package internetz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import au.com.bytecode.opencsv.CSVReader;

/***
 * Here ALL skills from GitHub are read and hold in ArrayList
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 */
public class SkillFactory {
	
	private static String filename = "all-languages.csv";
	public static ArrayList<Skill> skills = new ArrayList<Skill>();

	public SkillFactory() {
		say("SkillFactory object created");
	}
	
	public Skill getSkill(String name){
		for(Skill skill : skills){
			if (skill.getName().toLowerCase().equals(name.toLowerCase())) {
				return skill;
			}
		}
		return null;
	}
	
	public Skill getRandomSkill(){
		Random generator = new Random();
		int i = generator.nextInt(skills.size());
		return skills.get(i);
	}

	public void parse_csv_all_skills() throws IOException,
			FileNotFoundException {
		say("Searching for file in: " + new File(".").getAbsolutePath());
		CSVReader reader = new CSVReader(new FileReader(filename));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Skill skill = new Skill(nextLine[0], skills.size() + 1);
			skills.add(skill);
			say("Skill " + skill.getId() + ": " + skill.getName()
					+ " added to factory");
		}
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}