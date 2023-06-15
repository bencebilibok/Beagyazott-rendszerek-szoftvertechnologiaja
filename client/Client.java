//package client;
//import java.net.InetSocketAddress;
//import java.nio.BufferUnderflowException;
//import java.nio.ByteBuffer;
//import java.nio.channels.DatagramChannel;
//import java.util.HashMap;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingDeque;
//import java.util.concurrent.LinkedBlockingDeque;
//
//
//public class Client{
//	// server est initialisé dans le readBufferWaitPlayerServer et utile dans launchSpeaker
//	private InetSocketAddress server;
//	// créée dans launchListener et utile pour launchDisplayer, gridJobs contient les serpents envoye par le serveur, la liste est partagée entre le client listener aui la remplit et le dManagement de la grid qui la vide
//	private ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> gridJobs;
//	// créée dans launchDisplayer et utile pour launchSpeaker, directionIdJobs est remplie par le dManagement de direction et vidée par le sender
//	private BlockingDeque<Pair<Byte,Byte>> directionIdJobs;
//	// pour envoyer le message je veux joueur tant qu'on ne recoit pas de message du serveur
//	protected volatile boolean receivedPortStep = true;
//	private String serverName;
//	private Gestiondirection gest;
//	private ManagementDisplay window = null;
//
//	// on recupere sur le port 5656, le serveur et le port avec lequel on
//	// communique avec le serveur, on dit au serveur de nous parler sur 5959
//	public Client() throws Exception{
//			launchListener((short) 5959, readBufferWaitPlayerServer(5656));
//	}
//
//	// On lance un client listener sur le port listeningPort et on envoie au serveur le port sur lequel on va ecouter
//	private void launchListener(short listeningPort, short sendingPort) throws Exception {
//		gridJobs = new ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>>(1);
//		new Thread(new Client_listener(gridJobs, listeningPort, this)).start();
//		sendServer(listeningPort, sendingPort, server);
//	}
//
//	// appelé par le Client_listener
//	void launchDisplayer(byte number) {
//		directionIdJobs = new LinkedBlockingDeque<Pair<Byte,Byte>>(5);
//		ArrayBlockingQueue<Byte> directionJobs = new ArrayBlockingQueue<Byte>(5);
//		gest = new Gestiondirection(directionIdJobs, directionJobs);
//		window = new ManagementDisplay(serverName, number, directionJobs);
//		new Thread(new BackGridManagement(gridJobs, window, number, gest)).start();
//	}
//
//	// appelé par le Client_listener, on lance un speaker sur le port gamePort
//	void launchSpeaker(byte number, short gamePort) {
//		new Thread(new Client_sender(server, directionIdJobs, gamePort, number)).start();
//	}
//
//	void launchManagerDirection(){
//		new Thread(gest).start();
//	}
//
//	public void print(String string) {
//		//text.setText(string);
//		if(window!=null)
//			window.print(string);
//	}
//
//	private void sendServer(short listeningPort, short portConnection,
//			InetSocketAddress server) throws Exception {
//		// on ouvre une nouvelle connexion avec le serveur sur le port de connexion
//		DatagramChannel speakerChannel = DatagramChannel.open();
//		speakerChannel.socket().bind(new InetSocketAddress(0));
//		InetSocketAddress remote = new InetSocketAddress(server.getAddress(), portConnection);
//
//		// on envoie le port de jeu du client
//		ByteBuffer iWantToPlay = clientConnection(listeningPort);
//		while(receivedPortStep){
//			// On envoie un message je veux jouer sur le port portConnection
//			speakerChannel.send(iWantToPlay, remote);
//			// permet de reenvoyer le buffer
//			iWantToPlay.position(0);
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//			}
//		}
//		// on ferme la communication
//		speakerChannel.close();
//	}
//
//	private ByteBuffer clientConnection(short listeningPort) {
//		ByteBuffer res = ByteBuffer.allocate(3);
//		res.put((byte) 0);
//		res.putShort(listeningPort);
//		res.flip();
//		return res;
//	}
//
//	private short readBufferWaitPlayerServer(int portServer)
//			throws Exception {
//		// on ouvre une communication avec le serveur sur le port indiqué dans la rfc (5656)
//		DatagramChannel clientSocket = DatagramChannel.open();
//		InetSocketAddress local = new InetSocketAddress(portServer);
//		clientSocket.socket().bind(local);
//		// on cree un buffer pour recevoir le message, on attend une réponse du serveur
//		ByteBuffer buffer = ByteBuffer.allocate(1024);
//		// on recupere l'adresse du serveur
//		server = (InetSocketAddress) clientSocket
//				.receive(buffer);
//		buffer.flip();
//		try {
//			// on a déjà le nom du serveur codé comme pour DNS
//			byte nbChar = buffer.get();
//			serverName = "";
//			for (int i = 0; i < nbChar; i++)
//				serverName += (char) buffer.get();
//			// on récupère le port de connexion
//			short portConnection = buffer.getShort();
//			// On joue sur serverName nom qui contient nbChar caracteres. On se connecte sur portConnection
//			// on ferme la connexion avec le serveur sur le port 5656
//			clientSocket.close();
//			return portConnection;
//		} catch (BufferUnderflowException e) {
//			clientSocket.close();
//			throw new Exception("Le message du serveur est corrompu");
//		}
//	}
//
//}
//
//class Pair<E,V>{
//	E a;
//	V b;
//	Pair(E a1, V b1){
//		a = a1;
//		b = b1;
//	}
//}

package client;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


public class Client {
	// A szerver inicializálódik a readBufferWaitingPlayerServer() metódusban és a launchSpeaker() metódusban
	private InetSocketAddress server;
	// Az ArrayBlockingQueue-et a launchListener() metódusban hozzuk létre és a launchDisplay()-ben használjuk.
	// A gridJobs tartalmazza a szervertől kapott kígyókat.
	// A lista megosztott a kliens hallgató és a rácskezelő között, a hallgató tölti fel, a rácskezelő pedig üríti.
	private ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> gridJobs;
	// A BlockingDeque-et a launchDisplay() metódusban hozzuk létre és a launchSpeaker()-ben használjuk.
	// A directionIdJobs-t a direction handler tölti fel és a küldő üríti.
	private BlockingDeque<Pair<Byte, Byte>> directionIdJobs;
	// Ahhoz, hogy üzenetet küldjek, szeretném, ha addig játszanék, amíg nem kapunk üzenetet a szerverről
	protected volatile boolean receivedPortStep = true;
	private String serverName;
	private DirectionManagement gest;
	private ManagementDisplay window = null;

	// 5656-os porton várjuk az adatokat, és a szerverrel 5959-es porton kommunikálunk
	public Client() throws Exception {
		launchListener((short) 5959, readBufferWaitPlayerServer(5656));
	}

	// Indítunk egy kliens hallgatót a listeningPort porton, és elküldjük a szervernek a kommunikációra használt portot
	private void launchListener(short listeningPort, short sendingPort) throws Exception {
		gridJobs = new ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>>(1);
		new Thread(new Client_listener(gridJobs, listeningPort, this)).start();
		sendServer(listeningPort, sendingPort, server);
	}

	// A Client_listener hívja meg
	void launchDisplayer(byte number) {
		directionIdJobs = new LinkedBlockingDeque<Pair<Byte, Byte>>(5);
		ArrayBlockingQueue<Byte> directionJobs = new ArrayBlockingQueue<Byte>(5);
		gest = new DirectionManagement(directionIdJobs, directionJobs);
		window = new ManagementDisplay(serverName, number, directionJobs);
		new Thread(new BackGridManagement(gridJobs, window, number, gest)).start();
	}

	// A Client_listener hívja meg, indítunk egy beszélőt a gamePort porton
	void launchSpeaker(byte number, short gamePort) {
		new Thread(new Client_sender(server, directionIdJobs, gamePort, number)).start();
	}

	void launchManagerDirection() {
		new Thread(gest).start();
	}

	public void print(String string) {
		if (window != null)
			window.print(string);
	}

	private void sendServer(short listeningPort, short portConnection,
							  InetSocketAddress server) throws Exception {
		// Új kommunikációt indítunk a szerverrel a portConnection porton
		DatagramChannel speakerChannel = DatagramChannel.open();
		speakerChannel.socket().bind(new InetSocketAddress(0));
		InetSocketAddress remote = new InetSocketAddress(server.getAddress(), portConnection);

		// Elküldjük a kliens játékportját
		ByteBuffer iWantToPlay = clientConnection(listeningPort);
		while (receivedPortStep) {
			// Elküldünk egy üzenetet, hogy szeretnénk játszani a portConnection porton keresztül
			speakerChannel.send(iWantToPlay, remote);
			// Reseteljük a buffert, hogy újra küldhessük
			iWantToPlay.position(0);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		// Bezárjuk a kommunikációt
		speakerChannel.close();
	}

	private ByteBuffer clientConnection(short listeningPort) {
		ByteBuffer res = ByteBuffer.allocate(3);
		res.put((byte) 0);
		res.putShort(listeningPort);
		res.flip();
		return res;
	}

	private short readBufferWaitPlayerServer(int portServer)
			throws Exception {
		// Kapcsolatot nyitunk a szerverrel a RFC szerinti (5656-os) porton
		DatagramChannel clientSocket = DatagramChannel.open();
		InetSocketAddress local = new InetSocketAddress(portServer);
		clientSocket.socket().bind(local);
		// Létrehozunk egy buffert az üzenet fogadásához, és várnunk kell a választ a szerverről
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		// Kapjuk meg a szerver címét
		server = (InetSocketAddress) clientSocket
				.receive(buffer);
		buffer.flip();
		try {
			// Már van a szerver neve, DNS-hez hasonlóan kódolva
			byte nbChar = buffer.get();
			serverName = "";
			for (int i = 0; i < nbChar; i++)
				serverName += (char) buffer.get();
			// Kapjuk meg a kapcsolódási portot
			short portConnection = buffer.getShort();
			// Játsszunk a serverName nevű szerveren. Csatlakozzunk a portConnection-hoz
			// Bezárjuk a kapcsolatot a 5656-os porton a szerverrel
			clientSocket.close();
			return portConnection;
		} catch (BufferUnderflowException e) {
			clientSocket.close();
			throw new Exception("A szerver üzenete sérült");
		}
	}

}

class Pair<E, V> {
	E a;
	V b;

	Pair(E a1, V b1) {
		a = a1;
		b = b1;
	}
}
