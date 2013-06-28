package com.vdbs;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;

public class FSFuncs {

	public static List<String> getFileNamesOfDir(String dir, String ext, boolean flagRemoveExt) {
		try {

			List<String> files = new ArrayList<String>();
			File folder = new File(dir);
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					if (ext != null && ext != "*" && !FSFuncs.getFileExtension(listOfFiles[i]).equals(ext))
						continue;

					String tmpFileName = listOfFiles[i].getName();

					if (flagRemoveExt)
						tmpFileName = FSFuncs.removeExt(tmpFileName, ext);

					files.add(tmpFileName);
				}
			}

			return files;
		}
		catch(Exception e) {
			Common.error("Ощибка", "Чтение директории невозможно.");
			return null;
		}
	}


 	private static String getFileExtension(File file) {
        if (file == null) {
            return null;
        }

        String name = file.getName();
        int extIndex = name.lastIndexOf(".");

        if (extIndex == -1) {
            return null;
        } else {
            return name.substring(extIndex + 1);
        }
    }


    private static String removeExt(String filename, String ext) {
    	return filename.substring(0, filename.length() - (ext.length() + 1));
    }

    public static boolean dropProject(String name) {
    	try {
	    	File fileToRemove = new File(Common.PROJECTS_DATA_PATH + name + Common.PROJECT_DOT_EXT);
	    	if (fileToRemove.exists()) {
	    		fileToRemove.delete();
	    		return true;
	    	}
	    	else
	    		throw new Exception("Файл не существует.");
	    }
	    catch(Exception e) {
	    	Common.error("Ошибка", e.getMessage());
    		return false;
	    }
    }


	public static File getFile(String filename) {
		try {
			File tmpFile = new File(Common.PROJECTS_DATA_PATH + filename + Common.PROJECT_DOT_EXT);
			if (tmpFile.exists()) {
				return tmpFile;
			}
	    	else
	    		throw new Exception("Файл не существует.");
		}
		catch(Exception e) {
			Common.error("Ошибка", e.getMessage());
			return null;
		}
	}

	public static boolean renameProjectFiles(String oldName, String newName) {
		File oldFile = FSFuncs.getFile(oldName);
	    // File (or directory) with new name
	    String path = Common.PROJECTS_DATA_PATH + newName + Common.PROJECT_DOT_EXT;
	    File newFile = new File(path);
	    // Rename file (or directory)
	    return oldFile.renameTo(newFile);
	}

}