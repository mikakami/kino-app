package ws.softlabs.kino.server.schedule;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.listener.StoreCallback;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ScheduleLog implements StoreCallback {
		
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Key key;	
	@Persistent
	public long lastRunTS;
	@Persistent
	public long lastTimeTS;
	@Persistent
	public Date date;
	
	public ScheduleLog() {
	}
	@Override
	public void jdoPreStore() {
		date = new Date(lastRunTS);
	}		
}
