package datnguyen.com.inventoryapp.data;

import java.util.Date;

/**
 * Created by datnguyen on 12/27/16.
 */

public class Product {

	private long id;
	private String name;
	private boolean completed;
	private int remindTime;
	private int repeatMode;
	private int repeatModeValue;
	private Date createdDate;
	private Date completedDate;

	private String thumnailPath = null;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public int getRemindTime() {
		return remindTime;
	}

	public void setRemindTime(int remindTime) {
		this.remindTime = remindTime;
	}

	public int getRepeatMode() {
		return repeatMode;
	}

	public void setRepeatMode(int repeatMode) {
		this.repeatMode = repeatMode;
	}

	public int getRepeatModeValue() {
		return repeatModeValue;
	}

	public void setRepeatModeValue(int repeatModeValue) {
		this.repeatModeValue = repeatModeValue;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public String getThumnailPath() {
		return thumnailPath;
	}

	public void setThumnailPath(String thumnailPath) {
		this.thumnailPath = thumnailPath;
	}
}
