import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Elevator extends Thread{
	private String id;
	private int status, startingFloor, currentFloor, nextDestFloor, idleTime;
	private MainControlSystem mainSys;
	private Timer idleTimer;
	public boolean[] isMoveToOtherFloor;
	private boolean isDoorClose;
	private int openDoorTime,ClientEnterTime,closeDoorTime,movingEachFloorTime;
	private ArrayList<Integer> goingFloor;
	//status = 0 - idle
	//status = 1 - up
	//status = 2 - down
	
	public Elevator(int numOfFloors, int idleTime){
		startingFloor = -1; //Handling elevator currently stops and access a new order 
		status = 0;
		currentFloor = 0;
		nextDestFloor = 0;
		openDoorTime =1000;
		closeDoorTime=1000;
		ClientEnterTime=5000;
		movingEachFloorTime = 500;
		isDoorClose = true;
		this.idleTime = idleTime;
		goingFloor = new ArrayList<Integer>();
		isMoveToOtherFloor = new boolean[numOfFloors];
		start();
	}
	
	public void run(){
		while(true){
			//Get next destination
			if(startingFloor!=-1){
				nextDestFloor=startingFloor;
			}
			else{
				for(int i=0;i<isMoveToOtherFloor.length;i++){
					if(isMoveToOtherFloor[i]){
						if(status==0){
							nextDestFloor=i;
							isMoveToOtherFloor[i] = false;
							break;
						}else if(status==1){
							if(i>currentFloor&&i<nextDestFloor){
								nextDestFloor=i;
								isMoveToOtherFloor[i] = false;
								break;
							}
						}else{
							if(i<currentFloor&&i>nextDestFloor){
								nextDestFloor=i;
								isMoveToOtherFloor[i] = false;
								break;
							}
						}
					}
				}
			}
			
			//Check move or stay
			if(nextDestFloor==currentFloor&&startingFloor==-1){
				status = 0;
				runIdleCountTimer();	
				try {
					sleep(500);
				}
				catch(InterruptedException e) {
				}
			}else{
				System.out.println("**Elevator "+id+" start to move from "+currentFloor+" to "+nextDestFloor);		
				//Move to next destination
				moveTo(nextDestFloor);
				while(currentFloor!=nextDestFloor){
					moving(movingEachFloorTime);
				}
				//Finish moving, change currentFloor
				
				if(currentFloor == nextDestFloor){
				
				
					System.out.println("**Elevator "+id+" "+currentFloor);		
					openDoor(openDoorTime);
					waitClientEnter(ClientEnterTime);
					if(isDoorClose == false){
						closeDoor(closeDoorTime);
					}
					if(startingFloor!=-1){		
						startingFloor = -1;
					}
				}
			}
			
		}
	}
	public int getDestFloor(){
		return nextDestFloor;
	}
	public void setID(int elevatorID){
		this.id = String.valueOf(elevatorID);
	}
	
	public String getID(){
		return this.id;
	}
	
	public void moveTo(int floorNumber){
		nextDestFloor = floorNumber;
		if(nextDestFloor>currentFloor){
			status=1;
		}else{
			status=2;
		}
		
	}
	public void moving(int movingEachFloorTime){
		try {
			sleep(movingEachFloorTime);
		}
		catch(InterruptedException e) {
		}
		if(status==1){
			currentFloor +=1;
		}else{
			currentFloor -=1;
		}
		
	}
	public int delayTimeMoving(){
		int delayTime = Math.abs(currentFloor-nextDestFloor)*movingEachFloorTime;
		return delayTime;
	}
	public int delayTimeDoor(){
		int delayTime = openDoorTime+closeDoorTime+ClientEnterTime;
		return delayTime;
	}
	public void runIdleCountTimer(){
		if(currentFloor == 0){
			return;
		}else{
			if(idleTimer==null){
				idleTimer = new Timer();
				idleTimer.schedule(new TimerTask(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						System.out.println("**Idle time is up, elevator "+id+" move to 0 floor");
						System.out.println("**Elevator "+id+" start to move from "+currentFloor+" to 0");	
						//Move to next destination
						moveTo(0);
						
						//Finish moving, change currentFloor
						currentFloor = 0;
						
						idleTimer = null;
					}
				}, idleTime*1000);
				
			}
		}
	}
	
	public Elevator getElevator(){
		return this;
	}
	
	public String getStatus(){
		switch(status){
			case 1:{
				return "up";
			}
			case 2:{
				return "down";
			}
			default:{
				return "idle";
			}
		}
	}
	public void openDoor(int openDoorTime){
		isDoorClose = false;
		System.out.println("**Elevator "+id+" doors open");
		try {
			sleep(openDoorTime);
		}
		catch(InterruptedException e) {
		}
		System.out.println("**Elevator "+id+" opened");
		
	}
	public void waitClientEnter(int ClientEnterTime){
		System.out.println("**Elevator "+id+" Wait Client Enter");
		try {
			sleep(ClientEnterTime);
		}
		catch(InterruptedException e) {
		}
	}
	public void closeDoor(int closeDoorTime){
		isDoorClose = true;
		System.out.println("**Elevator "+id+" close door");
		try {
			sleep(closeDoorTime);
		}
		catch(InterruptedException e) {
		}
		System.out.println("**Elevator "+id+" closed");
	}
	public int getCurrentFloor(){
		return currentFloor;
	}
	
	public void setSystem(MainControlSystem s){
		mainSys = s;
	}
	
	public void setStartFloor(int floor){
		startingFloor = floor;
	}
}
