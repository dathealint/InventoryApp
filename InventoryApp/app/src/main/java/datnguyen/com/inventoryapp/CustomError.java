package datnguyen.com.inventoryapp;

/**
 * Created by datnguyen on 12/28/16.
 */

public class CustomError {

	public static final int CODE_TEXT_EMPTY = 100;
	public static final int CODE_TEXT_LENGTH = 101;

	public static final int CODE_NUMBER_NEGATIVE = 200;
	public static final int CODE_NUMBER_INVALID = 201;

	private int errorCode;
	private String errorMessage;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public CustomError(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
