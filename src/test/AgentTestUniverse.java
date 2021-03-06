package test;

import internetz.Agent;
import internetz.AgentInternals;
import internetz.Experience;
import internetz.Skill;
import internetz.SkillFactory;

import java.util.ArrayList;

import logger.PjiitOutputter;

public class AgentTestUniverse {

	public static ArrayList<Agent> DATASET = new ArrayList<Agent>();
	private static SkillFactory skillFactory = new SkillFactory();

	public static void init() {
		DATASET.clear();
		
		Agent agent1 = new Agent("Joe", "Smith", "Java newbie-1");
		say("Initializing agent 1..");
		Skill skill11 = skillFactory.getSkill("Java");
		Experience experience11 = new Experience(3, 18);
		Skill skill12 = skillFactory.getSkill("C");
		Experience experience12 = new Experience(1, 10);
		Skill skill13 = skillFactory.getSkill("XML");
		Experience experience13 = new Experience(0, 10);
		agent1.addSkill("Java", new AgentInternals(skill11, experience11));
		agent1.addSkill("C", new AgentInternals(skill12, experience12));
		agent1.addSkill("XML", new AgentInternals(skill13, experience13));
		
		Agent agent2 = new Agent("John", "Dereck", "C expert-1");
		say("Initializing agent 2..");
		Skill skill21 = skillFactory.getSkill("Java");
		Experience experience21 = new Experience(2, 18);
		Skill skill22 = skillFactory.getSkill("C");
		Experience experience22 = new Experience(9, 18);
		Skill skill23 = skillFactory.getSkill("XML");
		Experience experience23 = new Experience(1, 10);
		agent2.addSkill("Java", new AgentInternals(skill21, experience21));
		agent2.addSkill("C", new AgentInternals(skill22, experience22));
		agent2.addSkill("XML", new AgentInternals(skill23, experience23));
		
		Agent agent3 = new Agent("Adam", "Smith", "Newbie");
		say("Initializing agent 3..");
		Skill skill31 = skillFactory.getSkill("Java");
		Experience experience31 = new Experience(0, 10);
		Skill skill32 = skillFactory.getSkill("C");
		Experience experience32 = new Experience(0, 10);
		Skill skill33 = skillFactory.getSkill("XML");
		Experience experience33 = new Experience(1, 20);
		agent3.addSkill("Java", new AgentInternals(skill31, experience31));
		agent3.addSkill("C", new AgentInternals(skill32, experience32));
		agent3.addSkill("XML", new AgentInternals(skill33, experience33));
		
		DATASET.add(agent1);
		DATASET.add(agent2);
		DATASET.add(agent3);
	}
	
	private static void say(String s) {
		PjiitOutputter.say(s);
	}
	
}
