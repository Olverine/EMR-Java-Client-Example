package org.notima.energyintelligence;

import java.util.Scanner;

class Main {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.print("Device address: ");
        String deviceAddress = in.next();
        new EnergyPanel().addDevice(deviceAddress);
        
        Thread.currentThread().join();
    }
}