package database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

public interface DatabaseClient {
	
	byte[] get(String key);
	
	String post(byte[] image, long timestamp);
	
	public void put(String key, byte[] image, long timestamp);
	
	public void delete(String key);

	long getCurrentTimeStamp();

	List<Record> getNewEntries(long staleTimestamp);

	void putEntries(List<Record> list);

	List<Record> getAllEntries();

	void post(String key, byte[] image, long timestamp);

	ResultSetMetaData getMessage(String key);

	List getMessages(String fromId, String destId );

	void postMessage(String message, String toId,String fromId);

	void createDb();
	
}
