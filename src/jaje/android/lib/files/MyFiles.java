package jaje.android.lib.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyFiles {

	public static boolean delete(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (int i = 0; i < children.length; i++) {
				boolean success = delete(children[i]);
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static boolean copyDir(File source, File destination, boolean overwriteExisting) {
		if (source.exists() && source.isDirectory()) {
			destination.mkdirs();
			for (File f : source.listFiles()) {
				File dest = new File(new StringBuilder(destination.getPath())
						.append(File.separator).append(f.getName()).toString());
				if (f.isDirectory()) {
					if (!copyDir(f, dest, overwriteExisting)) {
						return false;
					}
				} else {
					if (!copyFile(f, dest, overwriteExisting)) {
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean copyFile(File source, File destination, boolean overwriteExisting) {
		boolean result;
		boolean sourceCond = source.exists() && source.isFile();
		boolean destCond = destination.exists() ? overwriteExisting : true;
		if (sourceCond && destCond) {
			try {
				InputStream in = new FileInputStream(source);
				try {
					OutputStream out = new FileOutputStream(destination);
					try {
						byte[] buffer = new byte[1024];
						int length;
						while ((length = in.read(buffer)) > 0) {
							out.write(buffer, 0, length);
						}
						result = true;
					} catch (IOException e) {
						e.printStackTrace();
						result = false;
					} finally {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				result = false;
			}
			return result;
		} else {
			return false;
		}
	}
}
