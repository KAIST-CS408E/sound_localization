import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


import DataStructure.AudioChunk;
import Utils.CSVUtils;

public class Main {

	private static ServerSocket[] serverSocket = new ServerSocket[4];
	public static ArrayList<String> clientList = new ArrayList<String>();
	public static ArrayList<String> emptyId = new ArrayList<String>();
	public static payLoad pLoad = new payLoad();
	public static DatagramSocket[] d = new DatagramSocket[4];
	public static void printArray(byte[][] x) {
		/* Helper function just to print 2D Arrays */
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[i].length; j++) {
				System.out.print(String.format("%20s", x[i][j]));
			}
			System.out.println("");
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		int[] port = { 3352, 3353, 3354, 3355 };

		System.out.println("main started");

		/*new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket[0] = new ServerSocket(port[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}

				while (true) {
					
					 * This loop keeps on accepting the incoming socket connections. Every time data
					 * is send to the server new connection is made. The source of the data is
					 * identified by using the IP address of the incoming socket
					 
					// System.out.println("Waiting for Clients...");
					try {

						Client client = new Client(serverSocket[0].accept());
						client.start();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket[1] = new ServerSocket(port[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}

				while (true) {
					
					 * This loop keeps on accepting the incoming socket connections. Every time data
					 * is send to the server new connection is made. The source of the data is
					 * identified by using the IP address of the incoming socket
					 
					// System.out.println("Waiting for Clients...");
					try {

						Client client = new Client(serverSocket[1].accept());
						client.start();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket[2] = new ServerSocket(port[2]);
				} catch (Exception e) {
					e.printStackTrace();
				}

				while (true) {
					
					 * This loop keeps on accepting the incoming socket connections. Every time data
					 * is send to the server new connection is made. The source of the data is
					 * identified by using the IP address of the incoming socket
					 
					// System.out.println("Waiting for Clients...");
					try {

						Client client = new Client(serverSocket[2].accept());
						client.start();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket[3] = new ServerSocket(port[3]);
				} catch (Exception e) {
					e.printStackTrace();
				}

				while (true) {
					
					 * This loop keeps on accepting the incoming socket connections. Every time data
					 * is send to the server new connection is made. The source of the data is
					 * identified by using the IP address of the incoming socket
					 
					// System.out.println("Waiting for Clients...");
					try {

						Client client = new Client(serverSocket[3].accept());
						client.start();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}).start();

*/	
	
	
	
//	Client cc = new Client(new Socket());


	Client cc = new Client(new DatagramSocket(3352));
	cc.start();
	
	Client cc2 = new Client(new DatagramSocket(3353));
	cc2.start();

	Client cc3 = new Client(new DatagramSocket(3354));
	cc3.start();

	Client cc4 = new Client(new DatagramSocket(3355));
	cc4.start();
	
	}

	public static void registerClient(String IP) {
		/* An android device must be registered before being used. */
		if (!clientList.contains(IP)) {
			clientList.add(IP);
			System.out.println(clientList.toString());
		}

	}

	public static void unSubscribeClient(String IP) {
		/* Function to unsubscribe the andorid device.. */
		if (clientList.contains(IP)) {
			clientList.remove(IP);
			System.out.println(clientList.toString());

		} else {
			System.out.println("NOT REGISTERED YET");
		}
	}

	public static String getIP(String Address) {
		return Address.substring(1, Address.indexOf(":"));
	}

	private static class payLoad {
		/*
		 * This class mimics the a payload that is to be send for further analysis in
		 * the matlab engine. For the payload to be valid following conditions should be
		 * met.
		 * 
		 * 1. Data should arrive from all the registered devices in between 1 sec. If
		 * any of the registered client fails to send the data, then the other data are
		 * also discarded.
		 * 
		 * 
		 */
		/*
		 * This should be changed to 4. 2 is being used here because we have been
		 * testing with 2 cell phones only.
		 */
		private static int noOfDevices = 4;
		private static byte[][] payload = new byte[noOfDevices][];
		private static long[] incomingTimeStamp = new long[noOfDevices];
		/*
		 * detail of payload
		 * 
		 * Registered device 1 -----> [ data from 1 ] Registered device 2 -----> [ data
		 * from 2 ] Registered device 3 -----> [ data from 3 ] Registered device 4
		 * -----> [ data from 4 ]
		 * 
		 * 
		 * 
		 */

		private static long timeChecker;// This variable is used to check if anydata is older than 1 sec in the payload.
		public static boolean emptyFlag = false;// This flag will be used to check if the payload is to be cleared or
												// not.
		static Utils utils = null;// This is a helper class and is used to write bytes data into wav file.

		Thread cleanerThread = new Thread() {
			/*
			 * This thread is responsible to removing non-eligible data from the payload.
			 * 
			 * Which data are "non-eligible" ?
			 * 
			 * --> if any registered device sent data at time x and is ready to be send for
			 * matlab analysis. However, other registered devices couldnt send the data for
			 * whatever reason. in this scenario, the data in the payload is non-eligible so
			 * that data also must be cleard.
			 * 
			 *
			 * This thread is responsible for doing this task.
			 * 
			 */

			public void run() {
				System.out.println("Thread Running");

				while (true) {
					if (System.currentTimeMillis() - timeChecker > 150) {
						empty();
					}
					try {
						Thread.sleep(10);// here blocking functions should have been made insted of Thread sleep. This
											// part is not efficient. This is to be fixed while going for production
											// code.
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		public payLoad() {
			/* Constructor for payLoad class */
			utils = new Utils();

			cleanerThread.start();
		}

		static synchronized boolean insert(int i, byte[] data) {
			/*
			 * This function inserts data into the payload. Based on the IP of the client,
			 * it is stored in the data. respectively in the array.
			 */
			incomingTimeStamp[i] = System.currentTimeMillis();
			if (payload[i] == null) {
				if (emptyFlag) {
					timeChecker = System.currentTimeMillis();
				}
				payload[i] = data;

				/* returns false if there is already data in the array. */
				if (checkFull()) {
					push();
				}

				return true;
			} else {
				return false;
			}
		}

		synchronized static void empty() {
			/* Clears the payload */
			int i = 0;
			while (i < noOfDevices) {
				payload[i] = null;

//				Clear the timestamps
				incomingTimeStamp[i] = 0;
				i++;

			}

			// payload[0]=null;
//			payload[1]=null;
//			
			emptyFlag = true;

		}

		synchronized static void push() {
			/*
			 * This function is only called whenever there is an data in the payload that is
			 * ready for the further analusis.
			 */
			// Push all the data for further processing.

			// printArray(payload);
//			System.out.println("\n\n\nYEAH DATA PUSHED !!!!\n"+ System.currentTimeMillis());

			fileWriter(incomingTimeStamp);
			try {

				/*
				 * CONSUME THE DATA HERE
				 * 
				 * 
				 * For example.
				 * 
				 * We have a matlab eninge class(MatLabEngine). Suppose that the engine has the
				 * static function eval. Then,
				 * 
				 * 
				 * ----------------------------------------------------------------- FXN CALL.
				 * -----------------------------------------------------------------
				 * MatLabEngine.eval(payload);
				 * -----------------------------------------------------------------
				 * 
				 */

				/* These two functions are for writing the data into file. */
//				utils.writeByteToFile(payload[0], clientList.get(0));
//				utils.writeByteToFile(payload[1], clientList.get(1));
				int i = 0;
				while (i < noOfDevices) {
					utils.writeByteToFile(payload[i], clientList.get(i));

					i++;

				}

			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

			empty();
		}

		synchronized static boolean checkFull() {

			for (int i = 0; i < noOfDevices; i++) {
				if (payload[i] == null) {
					return false;
				}
			}
			return true;
		}
	}

	private static class Client extends Thread {
//		private Socket clientSocket;
		private DatagramSocket clientSocket;
		private ObjectInputStream ois;
		private String IP;
		private boolean connected = true;

		public Client(DatagramSocket socket) {

			this.clientSocket = socket;
/*
			try {
				this.clientSocket.setTcpNoDelay(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.IP = Main.getIP(socket.getRemoteSocketAddress().toString());
*/
		}

		public void run() {
			try {

//				ois = new ObjectInputStream(clientSocket.getInputStream());
				while (this.connected) {
//					AudioChunk message = (AudioChunk) ois.readObject();
					ArrayList<Object> x = new Utils().recvObjFrom(this.clientSocket);
					AudioChunk message = (AudioChunk)x.get(0);
					this.IP = message.id;
					if (message.dataType == 1111) {

						Main.registerClient(this.IP);
						System.out.println("Register");
						this.connected = true;

					} else if (message.dataType == 1010) {
						Main.unSubscribeClient(this.IP);
						System.out.println("Unsubscribed");
						//this.connected = false;
					} else {
						if (clientList.contains(this.IP)) {
							payLoad.insert(clientList.indexOf(this.IP), message.data);
							System.out.println(this.IP + " Pay Load length -> " + message.data.length + "\n");
						} else {
							System.out.println("Data From Unregistered Client");
						}
					}
				}
				

			} catch (Exception e1) {
				e1.printStackTrace();
			} /*catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}*/

		}

	}

	public static void fileWriter(long[] timestamp) {

		ArrayList<String> list = new ArrayList<String>();

		long max = timestamp[0];
		long min = timestamp[0];
		for (long x : timestamp) {
			list.add(String.valueOf(x));

			if (max < x) {
				max = x;
			}
			if (min > x) {
				min = x;
			}
			System.out.println(x);

		}
		list.add(String.valueOf(max - min));
		System.out.println("\n" + String.valueOf(max - min));
		new Thread(new Runnable() {
			public void run() {
				try {
					String csvFile = "data.csv";
					FileWriter writer = new FileWriter(csvFile, true);
					CSVUtils.writeLine(writer, list);

					writer.flush();
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();

	}

}