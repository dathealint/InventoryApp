package datnguyen.com.inventoryapp;

/**
 * Created by datnguyen on 12/28/16.
 */

public class CustomError {

	public static final int CODE_TEXT_EMPTY = 100;
	public static final int CODE_NUMBER_NEGATIVE = 200;

	private int errorCode;
	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public CustomError(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
