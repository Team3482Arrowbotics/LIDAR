package org.usfirst.frc.team3482.robot.subsystems;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.command.Subsystem;

public class SimpleLIDAR extends Subsystem {
	I2C i2c;
	private static byte[] distance;
	private static byte[] status;
	private static byte[] test;
	private final int LIDAR_ADDR = 0x62;
	private final int LIDAR_CONFIG_REGISTER = 0x00;
	private final int LIDAR_STATUS_REGISTER = 0x01;
	private final int LIDAR_DISTANCE_REGISTER = 0x8f;

	public SimpleLIDAR(Port port) {
		i2c = new I2C(port, LIDAR_ADDR);
		distance = new byte[2];
		status = new byte[1];
		test = new byte[1];

	}

	public String bitByBitInt(int n) {
		String ret = "";
		for (int i = 7; i >= 0; i--) {
			ret += (n >> i) & 1;
		}
		ret += " - " + n;
		return ret;
	}

	public int getLastBit(int n) {
		return n & 1;
	}
	public void readAndPrint(int register) {
		byte[] a = new byte[1];
		System.out.println(i2c.read(register, 1, a));
		System.out.println(Integer.toHexString(register) + " " + Integer.toHexString(a[0]));
	}
	public void init() {
//		i2c.write(0x00, 0x00);
		readAndPrint(0x01);
//		readAndPrint(0x0e);
//		readAndPrint(0x0f);
//		readAndPrint(0x10);
//		readAndPrint(0x01);
//		readAndPrint(0x11);
	}

	public void update() {
		i2c.write(LIDAR_CONFIG_REGISTER, 0x04);
//		i2c.read(LIDAR_STATUS_REGISTER, 1, status);
//		System.out.println(bitByBitInt(status[0]));
//		while (getLastBit(status[0]) == 1) {
//			System.out.println("Device not ready!");
//			i2c.read(LIDAR_STATUS_REGISTER, 1, status);
//		}
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		i2c.read(LIDAR_DISTANCE_REGISTER, 2, distance);
		for (byte b : distance) {
			System.out.println(b);
		}
		System.out.println(getDistance());

	}

	public static double getDistance() {
		return (distance[0] << 8) + distance[1];

	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub

	}

}
