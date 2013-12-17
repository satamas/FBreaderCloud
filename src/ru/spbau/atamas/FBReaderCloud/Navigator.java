package ru.spbau.atamas.FBReaderCloud;

import java.io.InputStream;
import java.util.List;




public abstract class Navigator{
	public void setSelectedAccountName(String accountName){};
	public abstract Boolean inRoot();
	public abstract void addParent(String title);
	public abstract void removeParent();
	public abstract List<FileInfo> getDriveContents();
	public abstract InputStream getFileStream(String title);
}

