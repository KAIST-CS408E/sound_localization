package DataStructure;
import java.io.Serializable;


public class AudioChunk implements Serializable{
	private static final long serialVersionUID = 1L;
	public String id;
	public byte[] data;
	public long arrivalTime;
	public boolean flag;//This will be used to check if this chunk has bytes that cross the threshold.
	public  int dataType;
	public AudioChunk(byte[] d, long time,String id) {
		this.data=d;
		this.arrivalTime=time;
		this.flag=false;
		this.id=id;

	}

}