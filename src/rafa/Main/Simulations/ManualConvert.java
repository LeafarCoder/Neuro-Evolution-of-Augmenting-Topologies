package rafa.Main.Simulations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

import rafa.Main.Simulations.Created.FlappyBird.FlappyBird_Sim;
import rafa.Main.Simulations.Created.TicTacToe.TicTacToe_Sim;
import rafa.Main.Simulations.Created.XOR.XOR_Sim;

public class ManualConvert {

	public static void main(String[] args) {

		
		/*
		new File("src/resources/simulations/XOR").mkdirs();
		File objDir = new File("src/resources/simulations/XOR/XOR.sim");
		
		XOR_Sim sim_obj = new XOR_Sim();
		*/
		
		new File("src/resources/simulations/FlappyBird").mkdirs();
		File objDir = new File("src/resources/simulations/FlappyBird/FlappyBird.sim");
		
		FlappyBird_Sim sim_obj = new FlappyBird_Sim();
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(objDir);
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(fos);
			oos.writeObject(sim_obj);
			oos.close();
		} catch (NotSerializableException er) {
			er.printStackTrace();
		} catch (FileNotFoundException er) {
			er.printStackTrace();
		} catch (IOException er) {
			er.printStackTrace();
		}
		
		
	}

}
