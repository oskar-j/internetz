package strategies;

import internetz.TaskInternals;
import internetz.WorkUnit;

public class ProportionalTimeDivision extends EmergenceStrategy {
	
	public static void increment(TaskInternals __skill, int n, double experience){
		WorkUnit wd = __skill.getWorkDone();
		WorkUnit wu = __skill.getWorkUnits();
		
		wd.increment();
	}
	
}