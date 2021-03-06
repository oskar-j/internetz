package internetz;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import logger.PjiitOutputter;
import repast.simphony.annotate.AgentAnnot;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import strategies.Strategy;
import strategies.Strategy.SkillChoice;
import tasks.CentralAssignmentOrders;
import argonauts.GranularityType;
import argonauts.GranulatedChoice;
import argonauts.PersistJobDone;
import argonauts.PersistRewiring;

@AgentAnnot(displayName = "Agent")
public class Agent {

	private static final SkillFactory skillFactory = new SkillFactory();
	public static int totalAgents = 0;
	private static double time = 0;

	private Map<String, AgentInternals> skills = new HashMap<String, AgentInternals>();
	private Strategy strategy;

	private final int id = ++totalAgents;;
	private String firstName;
	private String lastName;
	private String nick;

	private CentralAssignmentOrders centralAssignmentOrders;

	public Agent() {
		this("Undefined name", "Undefined", "Agent_");
	}

	public Agent(String firstName, String lastName, String nick) {
		say("Agent constructor called");
		// this.id = ++totalAgents;
		AgentSkillsPool.fillWithSkills(this);
		this.firstName = firstName;
		this.lastName = lastName;
		this.nick = nick + this.id;
	}

	public void addSkill(String key, AgentInternals agentInternals) {
		skills.put(key, agentInternals);
	}

	/**
	 * Actually used very rarely, as far as I know - only when experience after
	 * decayExp operation returns 0 Assertion that agent possess this skill
	 * before removal
	 * 
	 * @param key
	 *            - name of the Skill to remove
	 */
	public void removeSkill(String key, boolean skipAssertion) {
		assert skipAssertion ? true : skills.containsKey(key);
		skills.remove(key);
	}

	public void removeSkill(Skill key, boolean skipAssertion) {
		removeSkill(key.getName(), skipAssertion);
	}

	public Collection<AgentInternals> getAgentInternals() {
		return skills.values();
	}

	public AgentInternals getAgentInternals(String key) {
		return skills.get(key);
	}

	public AgentInternals getAgentInternalsOrCreate(String key) {
		AgentInternals result = null;
		if (skills.get(key) == null) {
			result = (new AgentInternals(skillFactory.getSkill(key),
					new Experience(true)));
			skills.put(key, result);
			result = skills.get(key);
		} else {
			result = skills.get(key);
		}
		return result;
	}

	public Collection<Skill> getSkills() {
		ArrayList<Skill> skillCollection = new ArrayList<Skill>();
		Collection<AgentInternals> internals = this.getAgentInternals();
		for (AgentInternals ai : internals) {
			skillCollection.add(ai.getSkill());
		}
		return skillCollection;
	}

	public void setId(int id) {
		throw new UnsupportedOperationException();
		// its a final field, so please dont touch it :)
		// this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 100)
	public void step() {

		say("Step(" + time + ") of Agent " + this.id
				+ " scheduled method launched.");

		/**************************************************
		 * Gets current tick, for logging purpose only
		 **************************************************/
		time = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

		/********************************************************************
		 * Granularity starts here
		 ********************************************************************/
		if (SimulationParameters.granularity) {
			GranulatedChoice granulated = PersistRewiring
					.getGranulatedChoice(this);

			if (granulated != null) {
				// randomize decision
				double __d = RandomHelper.nextDoubleFromTo(0d, 100d);
				if (__d <= (double) (SimulationParameters.granularityObstinacy)) {
					say("Step(" + time + ") of Agent " + this.id
							+ " continuuing granularity");
					// continue work on the same skill
					// but check if the is any work left in this particular task
					// !
					Boolean workDone = granulated.getTaskChosen()
							.workOnTaskFromContinuum(this, granulated,
									this.strategy.skillChoice);
					if (!workDone) {
						// chose new task for granulated choice !
						Task taskToWork = TaskPool.chooseTask(this,
								this.strategy.taskChoice);
						executeJob(taskToWork);
						if (taskToWork != null) {
							switch (GranularityType
									.desc(SimulationParameters.granularityType)) {
							case TASKANDSKILL:
								// PersistRewiring.setOccupation(this,
								// taskToWork, PersistJobDone.);
								break;
							case TASKONLY:
								PersistRewiring.setOccupation(this, taskToWork);
								break;
							default:
								break;
							}
						}
					}
					// EnvironmentEquilibrium.setActivity(true);
				} else {
					say("Step(" + time + ") of Agent " + this.id
							+ " choosing new task for granulated choice");
					// chose new task for granulated choice !
					Task taskToWork = TaskPool.chooseTask(this,
							this.strategy.taskChoice);
					executeJob(taskToWork);
					if (taskToWork != null) {
						switch (GranularityType
								.desc(SimulationParameters.granularityType)) {
						case TASKANDSKILL:
							// PersistRewiring.setOccupation(this, taskToWork,
							// PersistJobDone.);
							break;
						case TASKONLY:
							PersistRewiring.setOccupation(this, taskToWork);
							break;
						default:
							break;
						}
					}
				}
			} else {
				say("Step("
						+ time
						+ ") of Agent "
						+ this.id
						+ " first run, chose new task and assign granulated choice");
				// first run
				// chose new task and assign granulated choice !
				Task taskToWork = TaskPool.chooseTask(this,
						this.strategy.taskChoice);
				executeJob(taskToWork);
				if (taskToWork != null) {
					switch (GranularityType
							.desc(SimulationParameters.granularityType)) {
					case TASKANDSKILL:
						// PersistRewiring.setOccupation(this, taskToWork,
						// PersistJobDone.);
						break;
					case TASKONLY:
						PersistRewiring.setOccupation(this, taskToWork);
						break;
					default:
						break;
					}
				}
			}
			/******************************************************************
			 * Granularity ends here
			 ******************************************************************/
		} else { // block without granularity
			// Agent Aj uses Aj {strategy for choosing tasks}
			// and chooses a task to work on
			Task taskToWork = TaskPool.chooseTask(this,
					this.strategy.taskChoice);
			// TO DO: make a good assertion to prevent nulls !!
			executeJob(taskToWork);
		}
	}

	private void executeJob(Task taskToWork) {
		// Agent Aj works on Ti
		if ((taskToWork != null) && (taskToWork.getTaskInternals().size() > 0)) {

			assert taskToWork.getTaskInternals().size() > 0;
			say("Agent " + this.id + " will work on task " + taskToWork.getId());
			if ((this.getCentralAssignmentOrders() != null)
					&& (this.getCentralAssignmentOrders()
							.getChosenTask()
							.getTaskInternals(
									this.getCentralAssignmentOrders()
											.getChosenSkillName()) != null)) {
				taskToWork.workOnTaskCentrallyControlled(this);
			} else
				taskToWork.workOnTask(this, this.strategy.skillChoice);
			EnvironmentEquilibrium.setActivity(true);

		} else {

			if (SimulationParameters.allwaysChooseTask
					&& TaskPool.stillNonEmptyTasks()) {
				Task randomTaskToWork = TaskPool.chooseTask(this,
						Strategy.TaskChoice.RANDOM);
				assert randomTaskToWork.getTaskInternals().size() > 0;
				say("Agent " + this.id + " will work on task "
						+ randomTaskToWork.getId());
				if ((this.getCentralAssignmentOrders() != null)
						&& (this.getCentralAssignmentOrders()
								.getChosenTask()
								.getTaskInternals(
										this.getCentralAssignmentOrders()
												.getChosenSkillName()) != null)) {
					randomTaskToWork.workOnTaskCentrallyControlled(this);
				} else
					randomTaskToWork.workOnTask(this, SkillChoice.RANDOM);
				EnvironmentEquilibrium.setActivity(true);
			} else {
				say("Agent " + this.id + " didn't work on anything");
				sanity("Agent " + this.id
						+ " don't have a task to work on in step " + time);
			}
		}

	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		say("Agent's login set to: " + nick);
		this.nick = nick;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public CentralAssignmentOrders getCentralAssignmentOrders() {
		return centralAssignmentOrders;
	}

	public void setCentralAssignmentOrders(
			CentralAssignmentOrders centralAssignmentOrders) {
		if (centralAssignmentOrders != null) {
			say("Agent " + this.nick + " got an order to work on "
					+ centralAssignmentOrders);
		}
		this.centralAssignmentOrders = centralAssignmentOrders;
	}

	public String describeExperience() {
		Collection<AgentInternals> internals = this.getAgentInternals();
		Map<String, String> deltaE = new HashMap<String, String>();
		for (AgentInternals ai : internals) {
			deltaE.put(ai.getSkill().getName(), (new DecimalFormat("#.######"))
					.format(ai.getExperience().getDelta()));
		}
		return deltaE.entrySet().toString();
	}

	public double describeExperience(Skill skill) {
		if (skills.get(skill.getName()) == null) {
			AgentInternals result = (new AgentInternals(
					skillFactory.getSkill(skill.getName()),
					new Experience(true)));
			skills.put(skill.getName(), result);
		}
		return skills.get(skill.getName()).getExperience().getDelta();
	}

	@Override
	public String toString() {
		return getNick();
	}

	@Override
	public int hashCode() {
		return nick.hashCode() * id;
	}

	@Override
	public boolean equals(Object obj) {
		if ((this.id == ((Agent) obj).id)
				&& (this.nick.toLowerCase().equals((((Agent) obj).nick
						.toLowerCase()))))
			return true;
		else
			return false;
	}

	public boolean wasWorkingOnAnything() {
		return PersistJobDone.getJobDone().containsKey(this.getNick());
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void sanity(String s) {
		PjiitOutputter.sanity(s);
	}
}

class EnvironmentEquilibrium {

	private static boolean activity = false;

	public static synchronized boolean getActivity() {
		return activity;
	}

	public static synchronized void setActivity(boolean defineActivity) {
		activity = defineActivity;
	}

}