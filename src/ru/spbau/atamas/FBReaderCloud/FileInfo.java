package ru.spbau.atamas.FBReaderCloud;

public class FileInfo {
    private String title;
    private Boolean folder;
    public FileInfo(String name, Boolean folder){
            this.title = name;
            this.folder = folder;
    }
    
    public String getName(){
            return this.title;
    }
    
    public Boolean isFolder(){
            return this.folder;
    }
}
