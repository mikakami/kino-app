package ws.softlabs.kino.server.tools;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.listener.StoreCallback;

import ws.softlabs.lib.kino.dao.server.impl.pmf.PMFDAOUtils;
import ws.softlabs.lib.kino.dao.server.model.pmf.PTheater;
import ws.softlabs.lib.kino.model.client.Theater;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ScheduleLog implements StoreCallback {
		
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Key key;	
	@Persistent
	public Date lastRun; 	//
	@Persistent
	public long loadTime;	//
	@Persistent
	public Key  theaterKey;	//
	
	public ScheduleLog() {
	}
	public ScheduleLog(Theater theater) {
		this.loadTime = 0L;
		this.lastRun  = new Date();
		PTheater ptheater = PMFDAOUtils.getPTheater(theater);
		if (ptheater != null)
			this.theaterKey = ptheater.getKey();
	}
	
	@Override
	public void jdoPreStore() {
	}

	public Date getLastRun() {
		return lastRun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	public long getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(long loadTime) {
		this.loadTime = loadTime;
	}

	public Theater getTheater() {
		PTheater ptheater = PMFDAOUtils.getPTheater(theaterKey);
		if (ptheater != null)
			return ptheater.asTheater();
		else 
			return null;
	}

	public void setTheater(Theater theater) {
		PTheater ptheater = PMFDAOUtils.getPTheater(theater);
		if (ptheater != null)
			this.theaterKey = ptheater.getKey();
	}

	
	
}
