package synergyviewcore.sharing.model;

public class SvnServerInfo {
	
	public static final String PROP_SERVER_URL = "serverUrl";
	private String serverUrl;
	public static final String PROP_USERNAME = "userName";
	private String userName;
	public static final String PROP_PASSWORD = "password";
	private char[] password;

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public char[] getPassword() {
		return password;
	}
}
