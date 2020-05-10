package segsoft.utils;

import segsoft.exception.ResponseException;

public class ConfigData implements Data {

	public String host;
    public Integer port;
	//public String database;
	
	public ConfigData() {}
	
	public ConfigData(String host, Integer port) {
		this.host = host;
        this.port = port;
    }
	
	public void validateData() throws ResponseException {
		if(this.host == null || this.port == null)
			throw new ResponseException("Invalid Config File");
	}

}
