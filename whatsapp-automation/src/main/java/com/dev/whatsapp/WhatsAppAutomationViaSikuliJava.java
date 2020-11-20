package com.dev.whatsapp;

import org.sikuli.script.FindFailed;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WhatsAppAutomationViaSikuliJava {
	public static final String SIKULI_HOME_FOLDER_FIELD = "sikuli_home_folder";
	public static final String SIKULI_IMAGES_SUB_FOLDER_FIELD = "sikuli_images_sub_folder";
	public static final String PICTURES_FOLDER_FIELD = "system_default_pictures_folder";
	public static final String WHATSAPP_DESKTOP_EXE_FIELD= "whatsapp_desktop_exe";
	public static final String RUN_DATE_FIELD= "run_date";
	public static final String JSON_INPUT_DATA_FIELD= "input_data";
	
	public static final String PROCESS_DONE_TOUCH_FILE= "SikuliProcessDone.txt";
	public static final String[] PICTURE_FILE_TYPE_EXTENSIONS= {".png", ".gif", ".jpg", ".jpeg"};
	public static final String TEXT_FILE_TYPE_SUFFIX= ".txt";
	public static final String PROFILE_IMAGE_TYPE_PREFIX= "DP";	
	public static final String PROFILE_IMAGE_TYPE_PREFIX_DEFAULT= "SELF";
	public static final String PROFILE_DP_UPDATED_MARKER_FILE= "dpUpdated.txt";
	public static final String PROFILE_DP_UPDATED_MARKER_FILE_DEFAULT= "dpUpdatedSelf.txt";
	public static final String ARCHIVE_FOLDER_FOR_PICS= "archive";
	public static final int NO_OF_EMOJIS= 5;	
	
	//Images used
	public static final String NEW_BROWSER_TAB = "NewTabIcon.PNG";
	public static final String SEARCH_OR_START_NEW_CHAT = "SearchOrStartNewChat.PNG";
	public static final String PROFILE_MENU = "menu.PNG";
	public static final String PROFILE_MENU_1 = "menu_1.PNG";
	public static final String PROFILE_OPTION = "profile.PNG";
	public static final String PROFILE_REGION = "ProfileRegion.PNG";	
	public static final String CHANGE_PROGILE_PHOTO = "uploadPhoto.PNG";
	public static final String WIN_PATH_LOC = "WinPathLoc.PNG";
	public static final String FILE_NAME_LOC = "FileNameLoc.PNG";
	public static final String FILE_OPEN_BUTTON = "FileOpenButton.PNG";
	public static final String UPLOAD_ACTION = "uploadAction.PNG";
	public static final String UPLOAD_DONE_BACK_ACTION = "ProfileUpdateDoneBack.PNG";
	public static final String TYPE_A_MESSAGE_LOC = "TypeAMsgOrAttchment.PNG";
	public static final String EMOJI_ICON = "emojiIcon.PNG";
	public static final String TEXT_MSG_SEND_ACTION = "MsgSendIcon.PNG";
	public static final String WHATSAPP_MINIMIZE = "WhatsAppMinimize.PNG";
	public static final String WHATSAPP_OPEN_RETRY = "RETRY.png";	
	public static final String TEXT_MSG_SEND_ACTION_1 = "MsgSendIconAlternate.PNG";	
	public static final String PROFILE_PIC_SET = "ProfilePhotoSet.PNG";		
	public static final String WHATSAPP_ATTACH = "whatsAppAttach.PNG";
	public static final String WHATSAPP_PHOTOS_AND_VIDEOS = "photosAndVideos.PNG";
	public static final String ADD_CAPTION = "addCaption.PNG";
	public static final String SEND_ACTION_IN_ATTACHMENTS = "sendIconInAttachments.PNG";
	public static final String WIN_RUN_CMD_OPEN = "WinRunCmdOpen.PNG";
	public static final String DP_IMAGE_SCALE_DOWN = "ImageScaleDown.PNG";
	
	
	//CSV File Columns
	public static final String INPUT_COLUMN_TO = "To";
	public static final String INPUT_COLUMN_OCCASION = "Occasion";
	public static final String INPUT_COLUMN_MESSAGE = "Message";
	public static final String INPUT_COLUMN_SENDMSG = "SendMsg";
	public static final String INPUT_COLUMN_SENDIMG = "SendImage";	
	public static final String INPUT_COLUMN_WHATSAPP_UNIQUE_IDS = "WhatsAppUniqueDestinations";	
	public static final String INPUT_COLUMN_WHATSAPP_UNIQUE_IDS_DELIM = "\\|";	// Pipe delimiter needs this escape
	
	static Pattern ptrn = null;
	static Screen s = new Screen();
	
	static String sikulixHomeFolderPath = null;
	static String sikulixImagesSubFolderPath = null;
	static String systemDefaultPicturesFolderPath = null;
	static String picturesFolderPathForRunDate = null;
	static String whatsappDesktopExe = null;
	static String runDate = null;
	static JSONArray inputDataJsonArray = null;
	static FileWriter myWriter = null;
	static StringBuffer sb = null;
	static File profileDpUpdatedMarkerFileFromPreviousRun = null;
	static File profileDefaultDpUpdatedMarkerFileFromPreviousRun = null;
	
	public static void updateProfilePictureAndSendGreeting(String inutData) throws Exception {
		try {
			JSONObject inputObj = new JSONObject(inutData);
			
			inputDataJsonArray = inputObj.getJSONArray(JSON_INPUT_DATA_FIELD);
			sikulixHomeFolderPath = inputObj.getString(SIKULI_HOME_FOLDER_FIELD);
			sikulixImagesSubFolderPath = inputObj.getString(SIKULI_IMAGES_SUB_FOLDER_FIELD);
			systemDefaultPicturesFolderPath = inputObj.getString(PICTURES_FOLDER_FIELD);
			whatsappDesktopExe = inputObj.getString(WHATSAPP_DESKTOP_EXE_FIELD);
			runDate = inputObj.getString(RUN_DATE_FIELD);
			
			myWriter = new FileWriter(sikulixHomeFolderPath + "\\" + runDate + TEXT_FILE_TYPE_SUFFIX);
			
			profileDpUpdatedMarkerFileFromPreviousRun = new File(sikulixHomeFolderPath + "\\" + PROFILE_DP_UPDATED_MARKER_FILE);
			profileDefaultDpUpdatedMarkerFileFromPreviousRun = new File(sikulixHomeFolderPath + "\\" + PROFILE_DP_UPDATED_MARKER_FILE_DEFAULT);
			
			writeLog("Run Date: " + runDate);
			
			//Determine if there is data to process before WhatsApp Desktop App is opened
			String profileImageFileName = getImageFileName(PROFILE_IMAGE_TYPE_PREFIX, runDate.substring(0, 4));
			
			if (profileImageFileName != null || dataForGreetingsExists()) {
				// Open WhatsApp Desktop Exe
				common(s);
		
				writeLog("Whatsapp opened"); // You need to add your device to whatsapp web for this to work
				
				//Read CSV input file and send Greetings
				if (dataForGreetingsExists()) {
					sendGreetings(inputDataJsonArray);
				} else {
					writeLog("No data for greetings");
				}
				
				//Update profile picture if for the given rundate, a profile picture exists
				if (profileImageFileName != null) {
					updateProfilePic(profileImageFileName);
					moveToArchiveAndSetMarkerFile(profileImageFileName);
				} else {
					writeLog("No profile picture to update");
				}
				
				writeLog("WhatsApp update done");
				
				try {
					click(WHATSAPP_MINIMIZE,3000);
				} catch (FindFailed fe) {
					s.type(Key.TAB, Key.ALT);
				}
				
			} else {
				writeLog("No Data To Process");
			}
			myWriter.close();
			
		} catch (Exception e) {
			myWriter.close();
			throw e;
		}
	
	}
	
	private static void common(Screen s) throws FindFailed {
		
		ImagePath.setBundlePath(sikulixHomeFolderPath + "\\" + sikulixImagesSubFolderPath);
		
		s.type("r", Key.WIN);
		s.wait(new Pattern(WIN_RUN_CMD_OPEN),2000);
		s.type("a", Key.CTRL);
		s.type(whatsappDesktopExe + Key.ENTER);
		
	}
	
	private static String getImageFileName(String imageType, String picturesSubFolder) throws IOException {
		
		picturesFolderPathForRunDate =  systemDefaultPicturesFolderPath + "\\" + picturesSubFolder;
		File picsFolderPathForRunDate = new File(picturesFolderPathForRunDate);
		String result = null;
		File[] files = null;
		files = picsFolderPathForRunDate.listFiles();
		
		if (picsFolderPathForRunDate.exists() && picsFolderPathForRunDate.isDirectory()) {
			result = getFileNameOfLastEligibleImageFile(files, imageType);
			
			if (PROFILE_IMAGE_TYPE_PREFIX.equals(imageType) && result == null) {
				if (profileDpUpdatedMarkerFileFromPreviousRun.exists()) {
					result = getFileNameOfLastEligibleImageFile(files, PROFILE_IMAGE_TYPE_PREFIX_DEFAULT);
				} else if (profileDefaultDpUpdatedMarkerFileFromPreviousRun.exists()) {
					result = null;
				}
			}
		} else {
			result = null;
		}
		
		if (result != null) writeLog("Choosing this image for update: " + result);
		return result;

	}
	
	private static String getFileNameOfLastEligibleImageFile(File[] files, String imageType) throws IOException {
		
		String result = null;
		
		for (int i = 0; i < files.length; i++) {
			for (int j = 0; j < PICTURE_FILE_TYPE_EXTENSIONS.length ; j++) {
				if (files[i].isFile() && 
						files[i].getName().toLowerCase().startsWith(imageType.toLowerCase()) && 
						files[i].getName().toLowerCase().endsWith(PICTURE_FILE_TYPE_EXTENSIONS[j])) {
							result = files[i].getName();
							writeLog("Image found for image type: " + imageType + ":" + result);
				}
			}		
		}
		return result;
	}
	
	private static void sendGreetings(JSONArray inputDataJsonArray) throws FindFailed, JSONException, IOException, InterruptedException {
		
		String whatsAppUniqueIdsStr = null;
		String to = null;
		String occasion = null;
		String message = null;
		String sendMsg = null;
		String sendImage = null;
		String[] whatsAppUniqueIdsArr = null;
		String picturesFolderPathForRunDate = systemDefaultPicturesFolderPath + "\\" + runDate.substring(0, 4);

		for (int inputDataJsonArrayIdx = 0; inputDataJsonArrayIdx < inputDataJsonArray.length(); inputDataJsonArrayIdx++) {
			JSONObject objEntry = (JSONObject) inputDataJsonArray.get(inputDataJsonArrayIdx);

			to = objEntry.getString(INPUT_COLUMN_TO);
			occasion = objEntry.getString(INPUT_COLUMN_OCCASION);
			message = objEntry.getString(INPUT_COLUMN_MESSAGE);
			sendMsg = objEntry.getString(INPUT_COLUMN_SENDMSG);
			sendImage = objEntry.getString(INPUT_COLUMN_SENDIMG);
			whatsAppUniqueIdsStr = objEntry.getString(INPUT_COLUMN_WHATSAPP_UNIQUE_IDS);

			whatsAppUniqueIdsArr = whatsAppUniqueIdsStr.split(INPUT_COLUMN_WHATSAPP_UNIQUE_IDS_DELIM);

			String occasionImageFileName = getImageFileName(occasion, runDate.substring(0, 4));

			for (int whatsAppUniqueIdIndex = 0; whatsAppUniqueIdIndex < whatsAppUniqueIdsArr.length; whatsAppUniqueIdIndex++) {

				sb = new StringBuffer("\n***** Sending Greetings|").append(to).append("|").append(occasion).append("|")
						.append(whatsAppUniqueIdsArr[whatsAppUniqueIdIndex]).append("|Send Image:").append(sendImage);

				writeLog(sb.toString());

				/*
				 * Search WhatsApp Id/Grp If Send Image is true from input file, then attach
				 * image and associated message as the caption If no image, then just send
				 * greeting along with predetermined emojs based on occasion
				 */
				click(SEARCH_OR_START_NEW_CHAT, 60000);
		
				s.type(whatsAppUniqueIdsArr[whatsAppUniqueIdIndex]);
				Thread.sleep(2000);
				s.type(Key.ENTER);

				if ("Y".equalsIgnoreCase(sendImage) && occasionImageFileName != null) {
					s.click(new Pattern(WHATSAPP_ATTACH));
					click(WHATSAPP_PHOTOS_AND_VIDEOS, 2000);
					click(WIN_PATH_LOC, 5000, -32, 0);

					s.type(picturesFolderPathForRunDate + Key.ENTER);

					click(FILE_NAME_LOC, 5000, 23, -3);

					s.type(occasionImageFileName);

					s.click(new Pattern(FILE_OPEN_BUTTON));
					
					// In some cases, you may want to just send an image without any caption
					if ("Y".equalsIgnoreCase(sendMsg)) {

						click(ADD_CAPTION, 5000);

						s.type("*_" + message + " " + to + "_*");
					}

					click(SEND_ACTION_IN_ATTACHMENTS, 5000);

				} else {

					click(TYPE_A_MESSAGE_LOC, 5000, 53, 1);

					s.type("*_" + message + " " + to + "_*");

					if ("BIRTHDAY".equalsIgnoreCase(occasion) || "ANNIVERSARY".equalsIgnoreCase(occasion)) {

						for (int oCnt = 1; oCnt <= NO_OF_EMOJIS; oCnt++) {
							s.type(":" + occasion);
							for (int iCnt = 1; iCnt <= oCnt; iCnt++) {
								s.type(Key.RIGHT);
							}
							s.type(Key.ENTER);
						}
					}

					try {
						s.click(new Pattern(TEXT_MSG_SEND_ACTION));
					} catch (FindFailed e) {
						s.click(new Pattern(TEXT_MSG_SEND_ACTION_1));
					}
				}
			}
		}

	}
	
	private static void updateProfilePic(String profileImageFile) throws FindFailed, IOException {
		
		String picturesFolderPathForRunDate = systemDefaultPicturesFolderPath + "\\" + runDate.substring(0, 4);

		sb = new StringBuffer("\n***** Setting Profile Picture|")
				.append(picturesFolderPathForRunDate).append("|")
				.append(profileImageFile);

		writeLog(sb.toString());
		/*
		 * Find profile menu and update the picture with the picture found in the folder
		 * for the given rundate
		 */

		try {
			s.click(new Pattern(PROFILE_MENU));
		} catch (FindFailed ff) {
			click(PROFILE_MENU_1, 22, -1);
		}
		s.click(new Pattern(PROFILE_OPTION));

		click(PROFILE_REGION, 1, 153);

		s.click(new Pattern(CHANGE_PROGILE_PHOTO));

		click(WIN_PATH_LOC, 5000, -32, 0);

		s.type(picturesFolderPathForRunDate + Key.ENTER);

		click(FILE_NAME_LOC, 5000, 23, -3);

		s.type(profileImageFile);

		s.click(new Pattern(FILE_OPEN_BUTTON));
		
		for (int i = 0; i < 5; i++) {
			s.click(new Pattern(DP_IMAGE_SCALE_DOWN));
		}

		click(UPLOAD_ACTION, 5000);

		try {
			s.wait(new Pattern(PROFILE_PIC_SET), 15000);
		} catch (FindFailed e) {
			// Do-nothing. Even if it's not set in 15 seconds, it will be set, eventually.
			// In a way, adding 15 seconds wait this way
		}

		s.wait(new Pattern(UPLOAD_DONE_BACK_ACTION), 1000);

		s.click(new Pattern(UPLOAD_DONE_BACK_ACTION));

	}
	
	private static boolean dataForGreetingsExists() {
		
		return inputDataJsonArray.length() > 0;
	}	

	private static void moveToArchiveAndSetMarkerFile(String profileImageFileName) throws IOException {
		
		String archiveFolderForPicsStr =  systemDefaultPicturesFolderPath + "\\" + ARCHIVE_FOLDER_FOR_PICS;
		
		FileWriter fileWriter = null;
		
		if (profileDpUpdatedMarkerFileFromPreviousRun.exists()) profileDpUpdatedMarkerFileFromPreviousRun.delete();
		if (profileDefaultDpUpdatedMarkerFileFromPreviousRun.exists()) profileDefaultDpUpdatedMarkerFileFromPreviousRun.delete();
		
		if (profileImageFileName.toUpperCase().startsWith(PROFILE_IMAGE_TYPE_PREFIX)) {
			/* Archive DP file and set the DP updated marker file. 
			 * Next run, in the absence of a DP file, should revert it to a default DP
			 */
			File dpFileName = new File(archiveFolderForPicsStr + "\\" + profileImageFileName);
			
			if (dpFileName.exists()) dpFileName.delete();
			Files.move(Paths.get(systemDefaultPicturesFolderPath + "\\" + runDate.substring(0, 4) + "\\" + profileImageFileName),
					Paths.get(archiveFolderForPicsStr + "\\" + profileImageFileName));
			
			fileWriter = new FileWriter(profileDpUpdatedMarkerFileFromPreviousRun);
			fileWriter.write("DP Updated Marker File");
		} else if (profileImageFileName.toUpperCase().startsWith(PROFILE_IMAGE_TYPE_PREFIX_DEFAULT)) {
			fileWriter = new FileWriter(profileDefaultDpUpdatedMarkerFileFromPreviousRun);
			fileWriter.write("DP DEFAULT Updated Marker File");			
		}
		fileWriter.close();
	}
	
	private static void writeLog(String logStr) throws IOException {
		System.out.println("***** " + logStr);
		myWriter.write("\n***** " + logStr);		
	}
			

	
	/*public static void main(String[] args) throws FindFailed {
		//In line testing logic goes here
	}*/

	
	/*Overloaded Methods for clicking an image. 1) With timeout 2) With timeout & offset 3) With Offset
	 * Offset is found using Sikuli desktop IDE
	 */
	private static void click(String imageName, double timeout) throws FindFailed {
		ptrn = new Pattern(imageName);
		s.wait(ptrn,timeout);
		s.click(ptrn);
	}
	
	private static void click(String imageName, double timeout, int dx, int dy) throws FindFailed {
		ptrn = new Pattern(imageName);
		s.wait(ptrn,timeout);
		ptrn = ptrn.targetOffset(dx, dy);
		s.click(ptrn);
	}
	
	private static void click(String imageName, int dx, int dy) throws FindFailed {
		ptrn = new Pattern(imageName);
		ptrn = ptrn.targetOffset(dx, dy);
		s.click(ptrn);
	}	
}

