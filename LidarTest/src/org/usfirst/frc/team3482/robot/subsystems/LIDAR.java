package org.usfirst.frc.team3482.robot.subsystems;

import java.util.TimerTask;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.hal.I2CJNI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LIDAR extends Subsystem{

	I2C i2c;
	private static byte[] distance;
	private java.util.Timer updater;
	private LIDARUpdater task;
	
	private final int LIDAR_ADDR = 0x62;
	private final int LIDAR_CONFIG_REGISTER = 0x00;
	private final int LIDAR_STATUS_REGISTER = 0x01;
	private final int LIDAR_DISTANCE_REGISTER = 0x8f;
	

	
	public LIDAR(Port port) {
		i2c = new I2C(port, LIDAR_ADDR);
		distance = new byte[2];

		task = new LIDARUpdater();
		updater = new java.util.Timer();
	}
	// Distance in cm...
	public boolean getAddress()
	{
		//returns false if there's a device responding to this address i hope
		return i2c.addressOnly();
	}
	
	public static double getDistance() {
		int distanceCM = (int)Integer.toUnsignedLong(distance[0] << 8) + Byte.toUnsignedInt(distance[1]);
		return (distanceCM /2.54);
	}
 
	public double pidGet() {
		return getDistance();
	}
	
	// Start 10Hz polling
	public void start() {
		updater.scheduleAtFixedRate(task, 0, 20);
	}
	
	// Start polling for period in milliseconds
	public void start(int period) {
		updater.scheduleAtFixedRate(task, 0, period);
	}
	
	public void stop() {
		updater.cancel();
	}
	
	// Update distance variable
	public void write() 
	{
		i2c.write(LIDAR_CONFIG_REGISTER, 0x04);
	}
	
	public void read()
	{
		byte[] status = new byte[1];
		status[0] = 1;
		int statusBit = 1;
		while (statusBit == 1)
		{
				I2CJNI.i2CReadB(0, (byte)LIDAR_STATUS_REGISTER, status, (byte)1);
				statusBit = (status[0] & 1);
				System.out.println(status[0] + " Full Status -- " + statusBit);
		}    
		I2CJNI.i2CReadB(0, (byte)0x8f, distance, (byte)2);
		for(byte b : distance) {
			System.out.print(b + " Data ");
		}
		System.out.println();
	}
	
	public void update() {
		i2c.write(LIDAR_CONFIG_REGISTER, 0x04); // Initiate measurement
		Timer.delay(0.04); // Delay for measurement to be taken
		read(); // Read in measurement
		Timer.delay(0.01); // Delay to prevent over polling
	}
	
	
	//CODE YAY
	private class LIDARUpdater extends TimerTask {
		public void run() {
			while(true) {
				update();
				
				if(getDistance() < 90 && getDistance() > 84){
					SmartDashboard.putBoolean("Correct distance from human feeder", true);
				}
				else{
					SmartDashboard.putBoolean("Correct distance from human feeder", false);
				}
				
				if(getDistance() < 80 && getDistance() > 70){
					SmartDashboard.putBoolean("Correct distance to stacks", true);
				}
				else{
					SmartDashboard.putBoolean("Correct distance to stacks", false);
				}
				SmartDashboard.putNumber("LIDAR distance Inches", (getDistance() / 2.54));
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		
		}
	}
	
	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
}
