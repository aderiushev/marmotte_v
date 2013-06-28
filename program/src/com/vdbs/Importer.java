package com.vdbs;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.Arrays;

public class Importer extends Base {
	private String delimeter = "#";
	private List<String> orderList;
	private File fileToImport;

	public Importer() {

	}

	public boolean fileChooser() {
		JFileChooser fileChooser = new JFileChooser(".");
		FileFilter filter = new ExtensionFileFilter("Текстовые форматы", new String[] {"txt"});
	    fileChooser.setFileFilter(filter);
	    
	    int status = fileChooser.showOpenDialog(null);
	    if (status == JFileChooser.APPROVE_OPTION) {
	    	this.setFileToImport(fileChooser.getSelectedFile());
	    	if (this.getFileToImport() != null)
	    		return true;

	    }
	    
	    return false;
		    
	}

	public void doImport() {
		List setData = this.getSetList(this.getFileToImport());
		if (setData.size() != 0) {
			for (int i = 0; i < setData.size(); i++) {
				List currentSet = (List)setData.get(i);
				String[] setArray = new String[] { String.valueOf(currentSet.get(0)), String.valueOf(currentSet.get(2)), String.valueOf(currentSet.get(1)) };
				this.getBi().getProject().addSet(setArray);
			}
		}
	}

	private List getSetList(File file) {
	    List result = new ArrayList();
		
		try {
	    	FileReader fReader = new FileReader(file);
	        BufferedReader bufRead = new BufferedReader(fReader);

	        while(bufRead.readLine() != null) {
	        	String[] setString = bufRead.readLine().split(this.getDelimeter());
	        	result.add(Arrays.asList(setString));
	        }

	        bufRead.close();
	   
	  	}
	  	catch(IOException e) {
	        Common.error("Exception", "Error on reading file");
	  	}

	  	return result;
	}

	public void setFileToImport(File val) {
		this.fileToImport = val;
	}

	public File getFileToImport() {
		return  this.fileToImport;
	}

	public void setDelimeter(String val) {
		this.delimeter = val;
	}

	public String getDelimeter() {
		return this.delimeter;
	}

	public void setOrderList(List<String> val) {
		this.orderList = val;
	}

}




class ExtensionFileFilter extends FileFilter {
  String description;

  String extensions[];

  public ExtensionFileFilter(String description, String extension) {
    this(description, new String[] { extension });
  }

  public ExtensionFileFilter(String description, String extensions[]) {
    if (description == null) {
      this.description = extensions[0];
    } else {
      this.description = description;
    }
    this.extensions = (String[]) extensions.clone();
    toLower(this.extensions);
  }

  private void toLower(String array[]) {
    for (int i = 0, n = array.length; i < n; i++) {
      array[i] = array[i].toLowerCase();
    }
  }

  public String getDescription() {
    return description;
  }

  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    } else {
      String path = file.getAbsolutePath().toLowerCase();
      for (int i = 0, n = extensions.length; i < n; i++) {
        String extension = extensions[i];
        if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
          return true;
        }
      }
    }
    return false;
  }
}
