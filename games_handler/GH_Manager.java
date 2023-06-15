package games_handler;

import game.Game;

import java.io.IOException;
//import java.util.HashSet;
//import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

//import utilities.Client;
import utilities.Job;
import utilities.Runnable_Input;

public class GH_Manager implements Runnable{
	 //kezeli az új játékosokat, akik játszani szeretnének

	// Az Output Thread > üzenetet küld a lejátszónak, hogy megkérje, hogy játsszon egy adott porton
	private Thread output;

	// Input thread > a játékosok üzeneteket küldenek rá
	// Az üzenetek munkákká alakulnak, és az in_communicatoron keresztül elküldik ennek az osztálynak
	private Thread input;
	private ArrayBlockingQueue<Job> in_communicator;
	private int nextPortToUseForGame, nbPlayers;
	
	
	public GH_Manager(int inputPort, int outputPort, String serverName, long broadcastTimeInterval, int nbP) throws IOException{

		//A GameHandler_Manager:
		//figyel a bemeneti porton (1 szál használatával)
		//ennek a bemeneti portnak és a kiszolgálónak a sugárzása minden broadcastTimeInterval (ms)
		// neve a kimeneti porton (másik szál használatával)
		nbPlayers = Math.max(1, Math.min(nbP, 4));

		in_communicator=new ArrayBlockingQueue<Job>(100);
		input=new Thread(new Runnable_Input(inputPort, in_communicator, "GH"));

		output=new Thread(new GH_Output(outputPort, serverName, broadcastTimeInterval, inputPort));

		nextPortToUseForGame=30000;
	}

	@Override
	public void run() {
		input.start();
		output.start();
		
		while(true){
			//várunk az új játékosra aki csatlakozni szeretne
			try {
				Job j=in_communicator.take();
				switch(j.type()){
				case WANT_TO_PLAY:
					Game g= Game.getGameForANewPlayer();
					if(g==null && nextPortToUseForGame<32000){
						//nincs elérhető játék most
						g=new Game(nbPlayers, nextPortToUseForGame++);
						//új játékot kezdünk a maxPlayer, inputPort
						g.start();
					}
					g.addClient(j.address(), j.port());
					break;
					
				default:
					break;
				
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
