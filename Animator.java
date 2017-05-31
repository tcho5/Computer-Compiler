package project;
import javax.swing.Timer;

public class Animator {
	static final int TICK = 500;
	private boolean autoStepOn = false;
	private Timer timer;
	private ViewsOrganizer view;
	 
	 public Animator(ViewsOrganizer view){
		 this.view = view;
	 }
	public boolean isAutoStepOn() {
		return autoStepOn;
	}
	public void setAutoStepOn(boolean autoStepOn) {
		this.autoStepOn = autoStepOn;
	}
	public void toggleAutoStep(){
		if (autoStepOn == true) {
			autoStepOn = false;
		}else{
			autoStepOn = true;
		}
	}
	public void setPeriod(int period){
		//which calls the setDelay(period) of timer.
		timer.setDelay(period);
	}
	public void start() {
		timer = new Timer(TICK, e -> {if(autoStepOn) view.step();});
		timer.start();
	}
}

