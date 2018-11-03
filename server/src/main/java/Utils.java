import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import org.apache.commons.math3.linear.Array2DRowFieldMatrix;

public class Utils {
//	This class is used just to store to save the data into wav file.
	private static final byte RECORDER_BPP = 16;
	private static final int RECORDER_CHANNELS = 1;
	private int RECORDER_SAMPLERATE = 192000;
	String inFilename, outFilename;

	FileOutputStream s1, ds1;

	public void ByteFileToWav(String inFilename, String outFilename) {
		this.inFilename = inFilename;
		this.outFilename = outFilename;

		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = RECORDER_CHANNELS;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

		byte[] data = new byte[1500];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen,
			long longSampleRate, int channels, long byteRate) throws IOException {
		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) RECORDER_CHANNELS; // block align
		header[33] = 0;
		header[34] = RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

	public synchronized void writeByteToFile(byte[] data, String fileName) throws IOException, InterruptedException {
		//fileName="/matlab"+fileName;
		try {
			ds1 = new FileOutputStream(new File(fileName + "deb1"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Thread s1t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ds1.write(data, 0, data.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});

		s1t.start();

		s1t.join();

		ByteFileToWav(fileName + "deb1", fileName + ".wav");
		System.out.println("Saved the File ~~~~~~");
		File file = new File(fileName + "deb1");
		file.delete();

	}

	
	
	 public ArrayList<Object> recvObjFrom(DatagramSocket dSock) {
	        try {

	            byte[] recvBuf = new byte[5000];
	            DatagramPacket packet = new DatagramPacket(recvBuf,
	                    recvBuf.length);
	            dSock.receive(packet);
	            int byteCount = packet.getLength();
	            ByteArrayInputStream byteStream = new
	                    ByteArrayInputStream(recvBuf);
	            ObjectInputStream is = new
	                    ObjectInputStream(new BufferedInputStream(byteStream));
	            Object o = is.readObject();
	            is.close();
	            
	            String s = packet.getAddress().getHostAddress();
	            
	            ArrayList<Object> odd = new ArrayList<Object>();
	            odd.add(o);
	            odd.add(s);
	            return odd;
	        } catch (IOException e) {
	            System.err.println("Exception:  " + e);
	            e.printStackTrace();
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	        return (null);
	    }
}