package synergyviewcore.workspace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class WorkspaceLocationPreferenceHelper {
    private static Preferences preferences = Preferences.userNodeForPackage(WorkspaceLocationPreferenceHelper.class);
    private static final String WORKSPACE_ROOT_DIR_KEY   = "covantoWSRootDir";
    private static final String WORKSPACE_LOCATION_HISTORY_STRINGS_KEY = "covantoLastUsedWorkspaces";
    private static final int MAX_WORKSPACE_LOCATION_HISTORY = 20;
    private static final String WORKSPACE_LOCATION_HISTORY_PREFERENCE_SPLIT_CHAR = ";";
    private static final String WS_IDENTIFIER  = ".covantoprojects.xml";
    public static String getLastSetWorkspaceDirectory() {
        return preferences.get(WORKSPACE_ROOT_DIR_KEY, null);
    }
    
    public static void addWorkspaceLocationHistory(String newWorkspaceLocation) {
    	List<String> currentWorkspaceLocationHistoryList = getWorkspaceLocationHistory();
    	if (currentWorkspaceLocationHistoryList.contains(newWorkspaceLocation))
    		return;
    	if (currentWorkspaceLocationHistoryList.size() == MAX_WORKSPACE_LOCATION_HISTORY) //Deals with MAX list
    		currentWorkspaceLocationHistoryList.remove(0);
    	StringBuffer workspaceLocationHistoryStrings = listToWorkspaceLocationHistoryString(currentWorkspaceLocationHistoryList);
    	workspaceLocationHistoryStrings.append(newWorkspaceLocation);
        preferences.put(WORKSPACE_LOCATION_HISTORY_STRINGS_KEY, workspaceLocationHistoryStrings.toString());
    }
    

	private static StringBuffer listToWorkspaceLocationHistoryString(
			List<String> lastUsedWorkspaceLocationList) {
		StringBuffer lastUsedWorkspaceLocations = new StringBuffer();
		for (String workspaceLocationHistoryEntry : lastUsedWorkspaceLocationList) {
			lastUsedWorkspaceLocations.append(workspaceLocationHistoryEntry);
			lastUsedWorkspaceLocations.append(WORKSPACE_LOCATION_HISTORY_PREFERENCE_SPLIT_CHAR);
		}
		return lastUsedWorkspaceLocations;
	}

	public static List<String> getWorkspaceLocationHistory() {
    	String lastUsedWorkspaceLocations = preferences.get(WORKSPACE_LOCATION_HISTORY_STRINGS_KEY, "");
        List<String>currentWorkspaceLocationHistoryList = new ArrayList<String>();
        if (lastUsedWorkspaceLocations.isEmpty()) 
        	return currentWorkspaceLocationHistoryList;
        String[] lastUsedWorkspaceLocation = lastUsedWorkspaceLocations.split(WORKSPACE_LOCATION_HISTORY_PREFERENCE_SPLIT_CHAR);
        for(String workspaceLocationHistoryEntry : lastUsedWorkspaceLocation) {
        	currentWorkspaceLocationHistoryList.add(workspaceLocationHistoryEntry);
        }
        return currentWorkspaceLocationHistoryList;
    }
	
    // suggests a path based on the user.home/temp directory location
    public static String getWorkspacePathSuggestion() {
        String uHome = System.getProperty("user.home");
        if (uHome == null) 
            return null;
        StringBuffer buf = new StringBuffer();
        buf.append(uHome);
        buf.append(File.separator);
        buf.append("VocantoData");
        return buf.toString();
    }
    
    public static boolean createWorkspace(File workspaceDirectory) {
    	File wsDot = new File(workspaceDirectory, WS_IDENTIFIER);
        try {
			return wsDot.createNewFile();
		} catch (IOException e) {
			return false;
		}
    }
    
    public static boolean isValidWorkspace(File workspaceRootDirectory) {
    	File dotFile = new File(workspaceRootDirectory, WS_IDENTIFIER);
    	return dotFile.exists();
    }
    
    public static boolean isWorkspaceCreateable(File workspaceRootDirectory) {
    	return workspaceRootDirectory.canWrite();
    		
    }
}
