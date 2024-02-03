package net.acesinc.data.json.generator;

public class FileModel {
	
		private String name;
		private String data;
		
		public FileModel () {}
		
		public FileModel(String name,String data) {
			this.name=name;
			this.data=data;
		}
	    public String getName() {
	    	return name;
	    }
	
	    public String getData() {
	    	return data;
	    }
}
